package com.example.rickandmorty.presentation.ui.characters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rickandmorty.R
import com.example.rickandmorty.data.local.EpisodesProvider
import com.example.rickandmorty.databinding.FragmentCharacterDetailsBinding
import com.example.rickandmorty.presentation.ui.episodes.EpisodeDetailsFragment
import com.example.rickandmorty.presentation.ui.episodes.EpisodesAdapter
import com.example.rickandmorty.presentation.ui.hostActivity
import com.example.rickandmorty.presentation.ui.locations.LocationDetailsFragment
import com.example.rickandmorty.presentation.ui.models.CharacterPresentation
import com.example.rickandmorty.presentation.ui.models.EpisodePresentation
import com.example.rickandmorty.presentation.ui.models.LocationPresentation


class CharacterDetailsFragment : Fragment() {

    private lateinit var binding: FragmentCharacterDetailsBinding
    private lateinit var character: CharacterPresentation
    private lateinit var toolbar: Toolbar
    private lateinit var episodesAdapter: EpisodesAdapter

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
        showCharacter()
        initRecyclerView()
        showCharacterEpisodes(EpisodesProvider.episodesList)

        return binding.root
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