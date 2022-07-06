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
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rickandmorty.App
import com.example.rickandmorty.R
import com.example.rickandmorty.databinding.FragmentEpisodeDetailsBinding
import com.example.rickandmorty.presentation.mapper.CharacterDomainToCharacterPresentationMapper
import com.example.rickandmorty.presentation.mapper.EpisodeDomainToEpisodePresentationMapper
import com.example.rickandmorty.presentation.models.CharacterPresentation
import com.example.rickandmorty.presentation.models.EpisodePresentation
import com.example.rickandmorty.presentation.ui.characters.adapters.CharactersAdapter
import com.example.rickandmorty.presentation.ui.characters.details.CharacterDetailsFragment
import com.example.rickandmorty.presentation.ui.episodes.details.viewmodel.EpisodeDetailsViewModel
import com.example.rickandmorty.presentation.ui.episodes.details.viewmodel.EpisodeDetailsViewModelFactory
import com.example.rickandmorty.presentation.ui.hostActivity
import com.example.rickandmorty.util.OnItemSelectedListener
import com.example.rickandmorty.util.resource.Status
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class EpisodeDetailsFragment : Fragment() {

    private lateinit var binding: FragmentEpisodeDetailsBinding
    private lateinit var episode: EpisodePresentation
    private lateinit var toolbar: Toolbar
    private lateinit var charactersAdapter: CharactersAdapter

    private val onCharacterSelectedListener =
        object : OnItemSelectedListener<CharacterPresentation> {
            override fun onSelectItem(item: CharacterPresentation) {
                hostActivity().openFragment(
                    fragment = CharacterDetailsFragment.newInstance(item),
                    tag = "CharacterDetailsFragment"
                )
            }
        }

    @Inject
    lateinit var viewModelFactory: EpisodeDetailsViewModelFactory
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

        injectDependencies()

        initViewModel()

        setUpObservers(id = episode.id, episode.characters)

        return binding.root
    }

    private fun injectDependencies() {
        val appComponent = (requireContext().applicationContext as App).appComponent
        appComponent.inject(this)
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            owner = this,
            factory = viewModelFactory
        ).get(EpisodeDetailsViewModel::class.java)
    }

    private fun setUpObservers(id: Int, ids: List<Int?>) {
        setUpEpisodeDetailsObserver(id)
        setUpEpisodeCharactersObserver(ids)
    }

    private fun setUpEpisodeCharactersObserver(ids: List<Int?>) {
        viewModel.getEpisodeCharactersByIds(ids).observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    val mapper = CharacterDomainToCharacterPresentationMapper()
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
                    val mapper = EpisodeDomainToEpisodePresentationMapper()
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
        charactersAdapter = CharactersAdapter(onCharacterSelectedListener)
        binding.rvEpisodeCharacters.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvEpisodeCharacters.adapter = charactersAdapter
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
        binding.episodeName.text = episode.name
        binding.episodeEpisode.text = episode.episode

        var airDateText = requireContext().getString(R.string.air_date)
        airDateText += ": ${episode.airDate}"
        binding.episodeAirDate.text = airDateText
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