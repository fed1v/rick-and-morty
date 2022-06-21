package com.example.rickandmorty.presentation.ui.characters.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rickandmorty.R
import com.example.rickandmorty.data.remote.EpisodesApi
import com.example.rickandmorty.data.remote.EpisodesApiBuilder
import com.example.rickandmorty.data.repository.EpisodesRepositoryImpl
import com.example.rickandmorty.databinding.FragmentCharacterDetailsBinding
import com.example.rickandmorty.domain.repository.EpisodesRepository
import com.example.rickandmorty.domain.usecases.episodes.GetEpisodesByIdsUseCase
import com.example.rickandmorty.presentation.mapper.EpisodeDomainToEpisodePresentationModelMapper
import com.example.rickandmorty.presentation.models.CharacterPresentation
import com.example.rickandmorty.presentation.models.EpisodePresentation
import com.example.rickandmorty.presentation.models.LocationPresentation
import com.example.rickandmorty.presentation.ui.episodes.EpisodeDetailsFragment
import com.example.rickandmorty.presentation.ui.episodes.EpisodesAdapter
import com.example.rickandmorty.presentation.ui.hostActivity
import com.example.rickandmorty.presentation.ui.locations.LocationDetailsFragment
import com.example.rickandmorty.util.status.Status


class CharacterDetailsFragment : Fragment() {

    private lateinit var binding: FragmentCharacterDetailsBinding
    private lateinit var character: CharacterPresentation
    private lateinit var toolbar: Toolbar
    private lateinit var episodesAdapter: EpisodesAdapter

    private lateinit var api: EpisodesApi
    private lateinit var repository: EpisodesRepository
    private lateinit var getEpisodesByIdsUseCase: GetEpisodesByIdsUseCase
    private lateinit var viewModel: CharacterDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        character = arguments?.getParcelable("Character") ?: CharacterPresentation(
            -1,
            "",
            "",
            "",
            "",
            LocationPresentation(-1, "", "", ""),
            LocationPresentation(-1, "", "", ""),
            listOf()
        )
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharacterDetailsBinding.inflate(inflater)
        setBottomNavigationCheckedItem()
        initToolbar()
        initRecyclerView()

        api = EpisodesApiBuilder.apiService
        repository = EpisodesRepositoryImpl(api)
        getEpisodesByIdsUseCase = GetEpisodesByIdsUseCase(repository)

        viewModel =
            ViewModelProvider(this, CharacterDetailsViewModelFactory(getEpisodesByIdsUseCase))
                .get(CharacterDetailsViewModel::class.java)

        setUpObservers(character.episodes)

        showCharacter()


        return binding.root
    }

    private fun setUpObservers(ids: List<Int?>) {
        viewModel.getEpisodesByIds(ids).observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    println("SUCCESS")
                    val mapper = EpisodeDomainToEpisodePresentationModelMapper()
                    val result = resource.data?.map { mapper.map(it) } ?: listOf()
                    println("Result:")
                    result.forEach {
                        println(it.name)
                    }
                    binding.episodesProgressBar.visibility = View.GONE
                    showCharacterEpisodes(result)
                }
                Status.ERROR -> {
                    println("ERROR")
                    binding.episodesProgressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
                Status.LOADING -> {
                    println("LOADING")
                    binding.episodesProgressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun showCharacterEpisodes(episodes: List<EpisodePresentation>) {
        episodesAdapter.episodesList = episodes
    }

    private fun initRecyclerView() {
        episodesAdapter = EpisodesAdapter { onEpisodeClicked(it) }
        binding.rvEpisodes.layoutManager = GridLayoutManager(requireContext(), 1)
        binding.rvEpisodes.adapter = episodesAdapter
    }

    private fun onEpisodeClicked(episode: EpisodePresentation) {
        hostActivity().openFragment(
            EpisodeDetailsFragment.newInstance(episode),
            "EpisodeDetailsFragment"
        )
    }

    private fun initToolbar() {
        toolbar = binding.characterToolbar
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

    private fun showCharacter() {
        binding.characterImage.setImageDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.rick_image
            )
        )
        binding.characterName.text = character.name
        binding.characterSpecies.text = character.species
        binding.characterStatus.text = character.status
        binding.characterGender.text = character.gender
        binding.characterLocation.apply {
            text = character.location.name
            setOnClickListener {
                openFragment(
                    LocationDetailsFragment.newInstance(character.location),
                    "LocationDetailsFragment"
                )
            }
        }
        binding.characterOrigin.apply {
            text = character.origin.name
            setOnClickListener {
                openFragment(
                    LocationDetailsFragment.newInstance(character.origin),
                    "LocationDetailsFragment"
                )
            }
        }
    }

    private fun openFragment(fragment: Fragment, tag: String) {
        hostActivity().openFragment(fragment, tag)
    }

    companion object {

        private const val MENU_ITEM_NUMBER: Int = 0

        fun newInstance(character: CharacterPresentation) =
            CharacterDetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("Character", character)
                }
            }
    }
}