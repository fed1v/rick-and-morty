package com.example.rickandmorty.presentation.ui.episodes.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rickandmorty.data.local.database.RickAndMortyDatabase
import com.example.rickandmorty.data.local.database.characters.CharactersDao
import com.example.rickandmorty.data.local.database.episodes.EpisodesDao
import com.example.rickandmorty.data.remote.characters.CharactersApi
import com.example.rickandmorty.data.remote.characters.CharactersApiBuilder
import com.example.rickandmorty.data.remote.episodes.EpisodesApi
import com.example.rickandmorty.data.remote.episodes.EpisodesApiBuilder
import com.example.rickandmorty.data.repository.CharactersRepositoryImpl
import com.example.rickandmorty.data.repository.EpisodesRepositoryImpl
import com.example.rickandmorty.databinding.FragmentEpisodeDetailsBinding
import com.example.rickandmorty.domain.repository.CharactersRepository
import com.example.rickandmorty.domain.repository.EpisodesRepository
import com.example.rickandmorty.domain.usecases.characters.GetCharactersByIdsUseCase
import com.example.rickandmorty.domain.usecases.episodes.GetEpisodeByIdUseCase
import com.example.rickandmorty.presentation.mapper.CharacterDomainToCharacterPresentationModelMapper
import com.example.rickandmorty.presentation.mapper.EpisodeDomainToEpisodePresentationModelMapper
import com.example.rickandmorty.presentation.models.CharacterPresentation
import com.example.rickandmorty.presentation.models.EpisodePresentation
import com.example.rickandmorty.presentation.ui.characters.adapters.CharactersAdapter
import com.example.rickandmorty.presentation.ui.characters.details.CharacterDetailsFragment
import com.example.rickandmorty.presentation.ui.hostActivity
import com.example.rickandmorty.util.status.Status

class EpisodeDetailsFragment : Fragment() {

    private lateinit var binding: FragmentEpisodeDetailsBinding
    private lateinit var episode: EpisodePresentation
    private lateinit var toolbar: Toolbar
    private lateinit var charactersAdapter: CharactersAdapter

    private lateinit var episodesApi: EpisodesApi
    private lateinit var charactersApi: CharactersApi

    private lateinit var episodesDao: EpisodesDao
    private lateinit var charactersDao: CharactersDao

    private lateinit var episodesRepository: EpisodesRepository
    private lateinit var charactersRepository: CharactersRepository

    private lateinit var getEpisodeByIdUseCase: GetEpisodeByIdUseCase
    private lateinit var getCharactersByIdsUseCase: GetCharactersByIdsUseCase

    private lateinit var viewModel: EpisodeDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        episode =
            arguments?.getParcelable("Episode") ?: EpisodePresentation(-1, "", "", "", listOf())
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEpisodeDetailsBinding.inflate(LayoutInflater.from(requireContext()))
        setBottomNavigationCheckedItem()
        initToolbar()
        initRecyclerView()

        initDependencies()
        initViewModel()

        setUpObservers(id = episode.id, episode.characters)

        return binding.root
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this, EpisodeDetailsViewModelFactory(
                getEpisodeByIdUseCase = getEpisodeByIdUseCase,
                getCharactersByIdsUseCase = getCharactersByIdsUseCase
            )
        ).get(EpisodeDetailsViewModel::class.java)
    }

    private fun initDependencies() {
        episodesApi = EpisodesApiBuilder.apiService
        charactersApi = CharactersApiBuilder.apiService

        episodesDao = RickAndMortyDatabase
            .getInstance(requireContext().applicationContext).episodesDao
        charactersDao = RickAndMortyDatabase
            .getInstance(requireContext().applicationContext).charactersDao

        episodesRepository = EpisodesRepositoryImpl(
            api = episodesApi,
            dao = episodesDao
        )
        charactersRepository = CharactersRepositoryImpl(
            api = charactersApi,
            dao = charactersDao
        )

        getEpisodeByIdUseCase = GetEpisodeByIdUseCase(episodesRepository)
        getCharactersByIdsUseCase = GetCharactersByIdsUseCase(charactersRepository)
    }

    private fun setUpObservers(id: Int, ids: List<Int?>) {
        setUpEpisodeDetailsObserver(id)
        setUpEpisodeCharactersObserver(ids)
    }

    private fun setUpEpisodeCharactersObserver(ids: List<Int?>) {
        viewModel.getEpisodeCharactersByIds(ids).observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    val mapper = CharacterDomainToCharacterPresentationModelMapper()
                    showEpisodeCharacters(resource.data?.map { mapper.map(it) } ?: listOf())
                    binding.recyclerViewProgressBar.visibility = View.GONE
                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                    binding.recyclerViewProgressBar.visibility = View.GONE
                }
                Status.LOADING -> {
                    binding.episodeDetailsProgressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setUpEpisodeDetailsObserver(id: Int) {
        viewModel.getEpisode(id).observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    val mapper = EpisodeDomainToEpisodePresentationModelMapper()
                    showEpisode(mapper.map(resource.data!!))
                    binding.episodeDetailsProgressBar.visibility = View.GONE
                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                    binding.episodeDetailsProgressBar.visibility = View.GONE
                }
                Status.LOADING -> {
                    binding.episodeDetailsProgressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun showEpisodeCharacters(characters: List<CharacterPresentation>) {
        charactersAdapter.charactersList = characters
    }

    private fun initRecyclerView() {
        charactersAdapter = CharactersAdapter { onCharacterClicked(it) }
        binding.rvEpisodeCharacters.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvEpisodeCharacters.adapter = charactersAdapter
    }

    private fun onCharacterClicked(character: CharacterPresentation) {
        hostActivity().openFragment(
            CharacterDetailsFragment.newInstance(character),
            "CharacterDetailsFragment"
        )
    }

    private fun initToolbar() {
        toolbar = binding.episodeToolbar
        hostActivity().setSupportActionBar(toolbar)
        hostActivity().supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                hostActivity().onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setBottomNavigationCheckedItem() {
        hostActivity().setBottomNavItemChecked(MENU_ITEM_NUMBER)
    }

    private fun showEpisode(episode: EpisodePresentation?) {
        if (episode == null) return
        binding.episodeAirDate.text = episode.airDate
        binding.episodeEpisode.text = episode.episode
        binding.episodeName.text = episode.name
    }


    companion object {

        private const val MENU_ITEM_NUMBER: Int = 2

        fun newInstance(episode: EpisodePresentation) = EpisodeDetailsFragment().apply {
            arguments = Bundle().apply {
                putParcelable("Episode", episode)
            }
        }
    }

}