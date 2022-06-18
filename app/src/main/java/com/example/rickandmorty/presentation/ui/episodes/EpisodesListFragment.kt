package com.example.rickandmorty.presentation.ui.episodes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rickandmorty.data.Episode
import com.example.rickandmorty.data.EpisodesProvider
import com.example.rickandmorty.databinding.FragmentEpisodesListBinding
import com.example.rickandmorty.presentation.ui.characters.CharactersListFragment
import com.example.rickandmorty.presentation.ui.hostActivity


class EpisodesListFragment : Fragment() {

    private lateinit var binding: FragmentEpisodesListBinding
    private lateinit var episodesAdapter: EpisodesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEpisodesListBinding.inflate(inflater, container, false)
        setBottomNavigationCheckedItem()

        initRecyclerView()
        showEpisodes(EpisodesProvider.episodesList)

        return binding.root
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
        hostActivity().openFragment(EpisodeDetailsFragment.newInstance(episode), "EpisodeDetailsFragment")
    }

    companion object {

        private const val MENU_ITEM_NUMBER: Int = 1

        fun newInstance(param1: String, param2: String) =
            CharactersListFragment().apply {
                arguments = Bundle().apply {
                    //        putString(ARG_PARAM1, param1)
                    //        putString(ARG_PARAM2, param2)
                }
            }
    }

}