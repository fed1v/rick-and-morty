package com.example.rickandmorty.presentation.ui.episodes.list

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
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
import com.example.rickandmorty.domain.usecases.episodes.*
import com.example.rickandmorty.presentation.models.EpisodePresentation
import com.example.rickandmorty.presentation.ui.episodes.adapters.EpisodesAdapter
import com.example.rickandmorty.presentation.ui.episodes.adapters.EpisodesPagedAdapter
import com.example.rickandmorty.presentation.ui.episodes.details.EpisodeDetailsFragment
import com.example.rickandmorty.presentation.ui.hostActivity
import com.example.rickandmorty.util.OnItemSelectedListener
import com.example.rickandmorty.util.filters.EpisodesFiltersHelper
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EpisodesListFragment : Fragment() {

    private lateinit var binding: FragmentEpisodesListBinding

    private lateinit var episodesAdapter: EpisodesAdapter
    private lateinit var episodesPagedAdapter: EpisodesPagedAdapter

    @OptIn(ExperimentalPagingApi::class)
    private val onEpisodeSelectedListener =
        object : OnItemSelectedListener<EpisodePresentation> {
            override fun onSelectItem(item: EpisodePresentation) {
                hostActivity().openFragment(
                    fragment = EpisodeDetailsFragment.newInstance(item),
                    tag = "EpisodeDetailsFragment"
                )
            }
        }

    private lateinit var toolbar: Toolbar
    private var episodesFiltersHelper: EpisodesFiltersHelper? = null

    private var appliedFilters = EpisodeFilter()

    private lateinit var api: EpisodesApi

    private lateinit var database: RickAndMortyDatabase

    private lateinit var episodesDao: EpisodesDao

    private lateinit var repository: EpisodesRepository

    private lateinit var getEpisodesUseCase: GetEpisodesUseCase
    private lateinit var getEpisodesByFiltersUseCase: GetEpisodesByFiltersUseCase
    private lateinit var getEpisodesFiltersUseCase: GetEpisodesFiltersUseCase
    private lateinit var getEpisodesWithPaginationUseCase: GetEpisodesWithPaginationUseCase
    private lateinit var getEpisodesByFiltersWithPaginationUseCase: GetEpisodesByFiltersWithPaginationUseCase


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

        getCharacters()

        return binding.root
    }

    private fun setUpObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.episodesFlow.collectLatest { data ->
                episodesPagedAdapter.submitData(data)
            }
        }
    }

    private fun getCharacters() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getEpisodesWithPagination()
        }
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
                getEpisodesFiltersUseCase = getEpisodesFiltersUseCase,
                getEpisodesWithPaginationUseCase = getEpisodesWithPaginationUseCase,
                getEpisodesByFiltersWithPaginationUseCase = getEpisodesByFiltersWithPaginationUseCase
            )
        ).get(EpisodesViewModel::class.java)
    }

    private fun initDependencies() {
        api = EpisodesApiBuilder.apiService
        database = RickAndMortyDatabase.getInstance(requireContext().applicationContext)
        episodesDao = database.episodesDao
        repository = EpisodesRepositoryImpl(
            api = api,
            database = database
        )

        getEpisodesUseCase = GetEpisodesUseCase(repository)
        getEpisodesByFiltersUseCase = GetEpisodesByFiltersUseCase(repository)
        getEpisodesFiltersUseCase = GetEpisodesFiltersUseCase(repository)
        getEpisodesWithPaginationUseCase = GetEpisodesWithPaginationUseCase(repository)
        getEpisodesByFiltersWithPaginationUseCase =
            GetEpisodesByFiltersWithPaginationUseCase(repository)
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


    private fun initRecyclerView() {
        episodesPagedAdapter = EpisodesPagedAdapter(
            onItemSelectedListener = onEpisodeSelectedListener
        )

        viewLifecycleOwner.lifecycleScope.launch {
            episodesPagedAdapter.loadStateFlow.collect { state ->


                println("State: $state")

                if (state.append is LoadState.Loading) {
                    println("Loading")
                    binding.episodesBottomProgressBar.isVisible = true
                }
                if (state.append is LoadState.NotLoading
                    || state.append is LoadState.Error
                ) {
                    println("Not Loading")
                    binding.episodesBottomProgressBar.isVisible = false
                }

                if (state.refresh is LoadState.Loading) {
                    binding.episodesProgressBar.isVisible = true
                    println("Refresh: Loading")
                }

                if (state.refresh is LoadState.NotLoading
                    || state.refresh is LoadState.Error
                ) {
                    binding.episodesProgressBar.isVisible = false
                    println("Refresh: NotLoading")
                }



                if (state.source.refresh is LoadState.NotLoading
                    && state.refresh is LoadState.Error
                    && episodesPagedAdapter.itemCount == 0
                ) {
                    println("Nothing found")
                    binding.episodesProgressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Nothing found", Toast.LENGTH_SHORT).show()
                }

            }
        }

        binding.rvEpisodes.itemAnimator = null
        binding.rvEpisodes.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvEpisodes.adapter = episodesPagedAdapter
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

    private fun searchByQuery(query: String?) {
        appliedFilters.name = query
        viewLifecycleOwner.lifecycleScope.launch {
            binding.episodesProgressBar.visibility = View.VISIBLE
            viewModel.getEpisodesByFiltersWithPagination(appliedFilters)
        }
    }

    private fun onFiltersApplied(filters: EpisodeFilter) {
        val name = filters.name ?: appliedFilters.name
        appliedFilters = filters.copy(name = name)
        viewLifecycleOwner.lifecycleScope.launch {
            binding.episodesProgressBar.visibility = View.VISIBLE
            viewModel.getEpisodesByFiltersWithPagination(appliedFilters) // ?? TODO
        }
    }

    private fun onResetClicked() {
        appliedFilters = EpisodeFilter()
        viewLifecycleOwner.lifecycleScope.launch {
            binding.episodesProgressBar.visibility = View.VISIBLE
            viewModel.getEpisodesWithPagination()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        episodesFiltersHelper = null
    }

    companion object {
        private const val MENU_ITEM_NUMBER: Int = 2
    }

}