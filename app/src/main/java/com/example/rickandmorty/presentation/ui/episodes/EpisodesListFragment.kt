package com.example.rickandmorty.presentation.ui.episodes

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rickandmorty.R
import com.example.rickandmorty.data.Episode
import com.example.rickandmorty.data.EpisodesProvider
import com.example.rickandmorty.databinding.FragmentEpisodesListBinding
import com.example.rickandmorty.presentation.ui.hostActivity
import com.example.rickandmorty.util.EpisodeFilter
import com.example.rickandmorty.util.EpisodesFiltersHelper


class EpisodesListFragment : Fragment() {

    private lateinit var binding: FragmentEpisodesListBinding
    private lateinit var episodesAdapter: EpisodesAdapter
    private lateinit var toolbar: Toolbar
    private var episodesFiltersHelper: EpisodesFiltersHelper? = null

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

        toolbar = binding.episodesToolbar
        hostActivity().setSupportActionBar(toolbar)

        episodesFiltersHelper = EpisodesFiltersHelper(requireContext()) { onFiltersApplied(it) }

        initRecyclerView()
        showEpisodes(EpisodesProvider.episodesList)

        return binding.root
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

    private fun showEpisodes(episodes: List<Episode>) {
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

    private fun onEpisodeClicked(episode: Episode) {
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