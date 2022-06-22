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
import com.example.rickandmorty.data.remote.episodes.EpisodesApi
import com.example.rickandmorty.data.remote.episodes.EpisodesApiBuilder
import com.example.rickandmorty.data.repository.EpisodesRepositoryImpl
import com.example.rickandmorty.databinding.FragmentEpisodesListBinding
import com.example.rickandmorty.domain.models.episode.EpisodeFilter
import com.example.rickandmorty.domain.repository.EpisodesRepository
import com.example.rickandmorty.domain.usecases.episodes.GetEpisodesUseCase
import com.example.rickandmorty.presentation.mapper.EpisodeDomainToEpisodePresentationModelMapper
import com.example.rickandmorty.presentation.models.EpisodePresentation
import com.example.rickandmorty.presentation.ui.episodes.adapters.EpisodesAdapter
import com.example.rickandmorty.presentation.ui.episodes.details.EpisodeDetailsFragment
import com.example.rickandmorty.presentation.ui.hostActivity
import com.example.rickandmorty.util.filters.EpisodesFiltersHelper
import com.example.rickandmorty.util.status.Status


class EpisodesListFragment : Fragment() {

    private lateinit var binding: FragmentEpisodesListBinding
    private lateinit var episodesAdapter: EpisodesAdapter
    private lateinit var toolbar: Toolbar
    private var episodesFiltersHelper: EpisodesFiltersHelper? = null

    private lateinit var api: EpisodesApi
    private lateinit var repository: EpisodesRepository
    private lateinit var getEpisodesUseCase: GetEpisodesUseCase

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
        episodesFiltersHelper = EpisodesFiltersHelper(requireContext()) { onFiltersApplied(it) }
        initRecyclerView()

        initDependencies()
        initViewModel()

        setUpObservers()

        return binding.root
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this, EpisodesViewModelFactory(
                getEpisodesUseCase = getEpisodesUseCase
            )
        ).get(EpisodesViewModel::class.java)
    }

    private fun initDependencies() {
        api = EpisodesApiBuilder.apiService
        repository = EpisodesRepositoryImpl(api)
        getEpisodesUseCase = GetEpisodesUseCase(repository)
    }

    private fun setUpObservers() {
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
        //TODO
    }

    private fun showEpisodes(episodes: List<EpisodePresentation>) {
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
        println("Filters applied: ${filters}")
    }

    override fun onDestroy() {
        super.onDestroy()
        episodesFiltersHelper = null
    }

    companion object {
        private const val MENU_ITEM_NUMBER: Int = 2
    }

}