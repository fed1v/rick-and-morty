package com.example.rickandmorty.presentation.ui.episodes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.rickandmorty.data.Episode
import com.example.rickandmorty.databinding.FragmentEpisodeDetailsBinding
import com.example.rickandmorty.presentation.ui.hostActivity

class EpisodeDetailsFragment : Fragment() {

    private lateinit var binding: FragmentEpisodeDetailsBinding
    private lateinit var episode: Episode
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        episode = arguments?.getParcelable("Episode") ?: Episode(-1, "", "", "")
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
        showEpisode()

        return binding.root
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

    private fun showEpisode() {
        binding.episodeAirDate.text = episode.airDate
        binding.episodeEpisode.text = episode.episode
        binding.episodeName.text = episode.name
    }


    companion object {

        private const val MENU_ITEM_NUMBER: Int = 2

        fun newInstance(episode: Episode) = EpisodeDetailsFragment().apply {
            arguments = Bundle().apply {
                putParcelable("Episode", episode)
            }
        }
    }

}