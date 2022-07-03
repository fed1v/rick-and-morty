package com.example.rickandmorty.presentation.ui.characters.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.example.rickandmorty.App
import com.example.rickandmorty.R
import com.example.rickandmorty.databinding.FragmentCharacterDetailsBinding
import com.example.rickandmorty.presentation.mapper.CharacterDomainToCharacterPresentationMapper
import com.example.rickandmorty.presentation.mapper.EpisodeDomainToEpisodePresentationMapper
import com.example.rickandmorty.presentation.mapper.LocationDomainToLocationPresentationMapper
import com.example.rickandmorty.presentation.models.CharacterPresentation
import com.example.rickandmorty.presentation.models.EpisodePresentation
import com.example.rickandmorty.presentation.models.LocationPresentation
import com.example.rickandmorty.presentation.ui.episodes.adapters.EpisodesAdapter
import com.example.rickandmorty.presentation.ui.episodes.details.EpisodeDetailsFragment
import com.example.rickandmorty.presentation.ui.hostActivity
import com.example.rickandmorty.presentation.ui.locations.details.LocationDetailsFragment
import com.example.rickandmorty.util.resource.Status
import javax.inject.Inject

@ExperimentalPagingApi
class CharacterDetailsFragment : Fragment() {

    private lateinit var binding: FragmentCharacterDetailsBinding
    private lateinit var character: CharacterPresentation
    private lateinit var toolbar: Toolbar
    private lateinit var episodesAdapter: EpisodesAdapter

    @Inject
    lateinit var viewModelFactory: CharacterDetailsViewModelFactory
    private lateinit var viewModel: CharacterDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        character = arguments?.getParcelable("Character") ?: CharacterPresentation(
            -1,
            "",
            "",
            "",
            "",
            "",
            LocationPresentation(-1, "", "", "", listOf()),
            LocationPresentation(-1, "", "", "", listOf()),
            listOf(),
            ""
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

        injectDependencies()

        initViewModel()

        setUpObservers(
            episodesIds = character.episodes,
            characterId = character.id,
            location = character.location,
            origin = character.origin
        )

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
        ).get(CharacterDetailsViewModel::class.java)
    }

    private fun setUpObservers(
        episodesIds: List<Int?>,
        characterId: Int,
        location: LocationPresentation,
        origin: LocationPresentation
    ) {
        setUpLocationsObserver(location = location)
        setUpOriginsObserver(origin = origin)
        setUpEpisodesObserver(ids = episodesIds)
        setUpCharacterObserver(id = characterId)
    }

    private fun setUpOriginsObserver(origin: LocationPresentation) {
        viewModel.getOrigin(origin = origin)
            .observe(viewLifecycleOwner) { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        val mapper = LocationDomainToLocationPresentationMapper()
                        val result = mapper.map(resource.data!!)
                        showOrigin(origin = result)
                        binding.characterDetailsProgressBar.visibility = View.GONE
                    }
                    Status.ERROR -> {
                        Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT)
                            .show()
                        binding.characterDetailsProgressBar.visibility = View.GONE
                    }
                    Status.LOADING -> {
                        binding.characterDetailsProgressBar.visibility = View.VISIBLE
                    }
                }
            }
    }

    private fun setUpLocationsObserver(
        location: LocationPresentation,
    ) {
        viewModel.getLocation(location = location)
            .observe(viewLifecycleOwner) { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        val mapper = LocationDomainToLocationPresentationMapper()
                        val result = mapper.map(resource.data!!)
                        showLocation(location = result)
                        binding.characterDetailsProgressBar.visibility = View.GONE
                    }
                    Status.ERROR -> {
                        Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT)
                            .show()
                        binding.characterDetailsProgressBar.visibility = View.GONE
                    }
                    Status.LOADING -> {
                        binding.characterDetailsProgressBar.visibility = View.VISIBLE
                    }
                }
            }
    }

    private fun setUpCharacterObserver(id: Int) {
        viewModel.getCharacterById(id).observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    val mapper = CharacterDomainToCharacterPresentationMapper()
                    showCharacter(mapper.map(resource.data!!))
                    binding.characterDetailsProgressBar.visibility = View.GONE
                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                    binding.characterDetailsProgressBar.visibility = View.GONE
                }
                Status.LOADING -> {
                    binding.characterDetailsProgressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setUpEpisodesObserver(ids: List<Int?>) {
        viewModel.getEpisodesByIds(ids).observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    val mapper = EpisodeDomainToEpisodePresentationMapper()
                    val result = resource.data?.map { mapper.map(it) } ?: listOf()
                    binding.episodesProgressBar.visibility = View.GONE
                    showCharacterEpisodes(result)
                }
                Status.ERROR -> {
                    binding.episodesProgressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
                Status.LOADING -> {
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

    private fun showOrigin(origin: LocationPresentation) {
        binding.characterOrigin.apply {
            when (origin.name) {
                "?" -> {
                    text = character.origin.name
                    setOnClickListener {
                        Toast.makeText(requireContext(), "Cannot open origin", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                "unknown_origin" -> {
                    text = requireContext().getString(R.string.unknown)
                    setOnClickListener {
                        Toast.makeText(requireContext(), "Origin is unknown", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                else -> {
                    text = origin.name
                    setOnClickListener {
                        openFragment(
                            LocationDetailsFragment.newInstance(origin),
                            "LocationDetailsFragment"
                        )
                    }
                }
            }
        }
    }


    private fun showLocation(location: LocationPresentation) {
        println("location: ${location}")
        binding.characterLocation.apply {
            when (location.name) {
                "?" -> {
                    text = character.location.name
                    setOnClickListener {
                        Toast.makeText(requireContext(), "Cannot open location", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                "unknown_location" -> {
                    text = requireContext().getString(R.string.unknown)
                    setOnClickListener {
                        Toast.makeText(requireContext(), "Location is unknown", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                else -> {
                    text = location.name
                    setOnClickListener {
                        openFragment(
                            LocationDetailsFragment.newInstance(location),
                            "LocationDetailsFragment"
                        )
                    }
                }
            }
        }
    }


    private fun showCharacter(character: CharacterPresentation?) {
        if (character == null) return

        Glide.with(requireContext())
            .load(character.image)
            .into(binding.characterImage)

        binding.characterName.text = character.name
        binding.characterStatus.text = character.status

        var speciesText = requireContext().getString(R.string.species)
        speciesText += if (character.species.isBlank()) {
            ": unknown"
        } else {
            ": ${character.species}"
        }
        binding.characterSpecies.text = speciesText

        val typeText = requireContext().getString(R.string.type) + ": ${character.type}"
        if (character.type.isBlank()) {
            binding.characterType.isVisible = false
        }
        binding.characterType.text = typeText

        val genderDrawable = when (character.gender) {
            "Male" -> ContextCompat.getDrawable(requireContext(), R.drawable.ic_male)
            "Female" -> ContextCompat.getDrawable(requireContext(), R.drawable.ic_female)
            "Genderless" -> ContextCompat.getDrawable(requireContext(), R.drawable.ic_genderless)
            else -> ContextCompat.getDrawable(requireContext(), R.drawable.ic_question_mark)
        }

        binding.iconGender.setImageDrawable(genderDrawable)
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