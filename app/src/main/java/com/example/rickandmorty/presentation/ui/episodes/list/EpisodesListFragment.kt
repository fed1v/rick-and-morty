package com.example.rickandmorty.presentation.ui.episodes.list

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rickandmorty.R
import com.example.rickandmorty.data.local.database.RickAndMortyDatabase
import com.example.rickandmorty.data.local.database.episodes.EpisodesDao
import com.example.rickandmorty.data.remote.episodes.EpisodesApi
import com.example.rickandmorty.data.remote.episodes.EpisodesApiBuilder
import com.example.rickandmorty.data.repository.EpisodesRepositoryImpl
import com.example.rickandmorty.databinding.FragmentEpisodesListBinding
import com.example.rickandmorty.domain.models.episode.EpisodeFilter
import com.example.rickandmorty.domain.repository.EpisodesRepository
import com.example.rickandmorty.domain.usecases.episodes.GetEpisodesByFiltersUseCase
import com.example.rickandmorty.domain.usecases.episodes.GetEpisodesFiltersUseCase
import com.example.rickandmorty.domain.usecases.episodes.GetEpisodesUseCase
import com.example.rickandmorty.presentation.mapper.EpisodeDomainToEpisodePresentationModelMapper
import com.example.rickandmorty.presentation.models.EpisodePresentation
import com.example.rickandmorty.presentation.ui.episodes.adapters.EpisodesAdapter
import com.example.rickandmorty.presentation.ui.episodes.details.EpisodeDetailsFragment
import com.example.rickandmorty.presentation.ui.hostActivity
import com.example.rickandmorty.util.filters.EpisodesFiltersHelper
import com.example.rickandmorty.util.resource.Status


class EpisodesListFragment : Fragment() {

    private lateinit var binding: FragmentEpisodesListBinding
    private lateinit var episodesAdapter: EpisodesAdapter
    private lateinit var toolbar: Toolbar
    private var episodesFiltersHelper: EpisodesFiltersHelper? = null

    private var appliedFilters = EpisodeFilter()

    private lateinit var api: EpisodesApi

    private lateinit var episodesDao: EpisodesDao

    private lateinit var repository: EpisodesRepository

    private lateinit var getEpisodesUseCase: GetEpisodesUseCase
    private lateinit var getEpisodesByFiltersUseCase: GetEpisodesByFiltersUseCase
    private lateinit var getEpisodesFiltersUseCase: GetEpisodesFiltersUseCase

    private lateinit var viewModel: EpisodesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEpisodesListBinding.inflate(inflater, container, false)
        setBottomNavigationCheckedItem()
        initToolbar()
        initRecyclerView()

        initDependencies()
        initViewModel()
        initFilters()

        setUpObservers()

        return binding.root
    }

    private fun initFilters() {
        episodesFiltersHelper = EpisodesFiltersHelper(
            context = requireContext(),
            applyCallback = { onFiltersApplied(it) },
            resetCallback = { onResetClicked() }
        )
        viewModel.getFilters().observe(viewLifecycleOwner) { filter ->
            when (filter.first) {
                "episode" -> {
                    episodesFiltersHelper!!.episodesArray = filter.second.toTypedArray()
                }
            }
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            owner = this,
            factory = EpisodesViewModelFactory(
                getEpisodesUseCase = getEpisodesUseCase,
                getEpisodesByFiltersUseCase = getEpisodesByFiltersUseCase,
                getEpisodesFiltersUseCase = getEpisodesFiltersUseCase
            )
        ).get(EpisodesViewModel::class.java)
    }

    private fun initDependencies() {
        api = EpisodesApiBuilder.apiService
        episodesDao = RickAndMortyDatabase
            .getInstance(requireContext().applicationContext).episodesDao
        repository = EpisodesRepositoryImpl(
            api = api,
            dao = episodesDao
        )

        getEpisodesUseCase = GetEpisodesUseCase(repository)
        getEpisodesByFiltersUseCase = GetEpisodesByFiltersUseCase(repository)
        getEpisodesFiltersUseCase = GetEpisodesFiltersUseCase(repository)
    }

    private fun setUpObservers() {
        setUpEpisodesObserver()
    }

    private fun setUpEpisodesObserver() {
        viewModel.getEpisodes().observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    val mapper = EpisodeDomainToEpisodePresentationModelMapper()
                    showEpisodes(resource.data?.map { mapper.map(it) } ?: listOf())
                    binding.episodesProgressBar.visibility = View.GONE
                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                    binding.episodesProgressBar.visibility = View.GONE
                }
                Status.LOADING -> {
                    binding.episodesProgressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setUpEpisodesByFiltersObserver(filters: EpisodeFilter) {
        viewModel.getEpisodesByFilters(filters).observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    val mapper = EpisodeDomainToEpisodePresentationModelMapper()
                    showEpisodes(resource.data?.map { mapper.map(it) } ?: listOf())
                    binding.episodesProgressBar.visibility = View.GONE
                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                    binding.episodesProgressBar.visibility = View.GONE
                }
                Status.LOADING -> {
                    binding.episodesProgressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun initToolbar() {
        toolbar = binding.episodesToolbar
        hostActivity().setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
        val searchItem = menu.findItem(R.id.item_search)
        val searchView = searchItem.actionView as? SearchView

        searchView?.queryHint = HtmlCompat.fromHtml(
            "<font color = #000000>" + resources.getString(R.string.hintSearchMessage) + "</font>",
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchByQuery(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_filter -> {
                openFilters()
                true
            }
            R.id.item_search -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    private fun openFilters() {
        episodesFiltersHelper?.openFilters()
    }

    private fun searchByQuery(query: String?) {
        println("Query: $query")
        appliedFilters.name = query
        setUpEpisodesByFiltersObserver(appliedFilters)
    }

    private fun showEpisodes(episodes: List<EpisodePresentation>) {
        if (episodes.isEmpty()) {
            Toast.makeText(requireContext(), "Nothing found", Toast.LENGTH_SHORT).show()
        }
        episodesAdapter.episodesList = episodes
    }

    private fun initRecyclerView() {
        episodesAdapter = EpisodesAdapter { onEpisodeClicked(it) }
        binding.rvEpisodes.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvEpisodes.adapter = episodesAdapter
    }

    private fun setBottomNavigationCheckedItem() {
        hostActivity().setBottomNavItemChecked(MENU_ITEM_NUMBER)
    }

    private fun onEpisodeClicked(episode: EpisodePresentation) {
        println("EpisodeDetailsFragment: ${episode.name}")
        hostActivity().openFragment(
            EpisodeDetailsFragment.newInstance(episode),
            "EpisodeDetailsFragment"
        )
    }

    private fun onFiltersApplied(filters: EpisodeFilter) {
        val name = filters.name ?: appliedFilters.name
        appliedFilters = filters.copy(name = name)
        setUpEpisodesByFiltersObserver(appliedFilters)
    }

    private fun onResetClicked() {
        appliedFilters = EpisodeFilter()
        setUpEpisodesByFiltersObserver(appliedFilters)
    }

    override fun onDestroy() {
        super.onDestroy()
        episodesFiltersHelper = null
    }

    companion object {
        private const val MENU_ITEM_NUMBER: Int = 2
    }

}