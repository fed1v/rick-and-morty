package com.example.rickandmorty.presentation.ui.episodes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rickandmorty.data.local.CharactersProvider
import com.example.rickandmorty.databinding.FragmentEpisodeDetailsBinding
import com.example.rickandmorty.presentation.ui.characters.details.CharacterDetailsFragment
import com.example.rickandmorty.presentation.ui.characters.CharactersAdapter
import com.example.rickandmorty.presentation.ui.hostActivity
import com.example.rickandmorty.presentation.models.CharacterPresentation
import com.example.rickandmorty.presentation.models.EpisodePresentation

class EpisodeDetailsFragment : Fragment() {

    private lateinit var binding: FragmentEpisodeDetailsBinding
    private lateinit var episode: EpisodePresentation
    private lateinit var toolbar: Toolbar
    private lateinit var charactersAdapter: CharactersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        episode = arguments?.getParcelable("Episode") ?: EpisodePresentation(-1, "", "", "")
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
        initRecyclerView()
        showEpisodeCharacters(CharactersProvider.charactersList)

        return binding.root
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

    private fun showEpisode() {
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