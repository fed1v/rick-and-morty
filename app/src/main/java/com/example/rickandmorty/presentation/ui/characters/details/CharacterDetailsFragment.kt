package com.example.rickandmorty.presentation.ui.characters.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.rickandmorty.R
import com.example.rickandmorty.data.local.database.RickAndMortyDatabase
import com.example.rickandmorty.data.local.database.characters.CharactersDao
import com.example.rickandmorty.data.local.database.episodes.EpisodesDao
import com.example.rickandmorty.data.local.database.locations.LocationsDao
import com.example.rickandmorty.data.local.database.characters.remote_keys.CharactersRemoteKeysDao
import com.example.rickandmorty.data.remote.characters.CharactersApi
import com.example.rickandmorty.data.remote.characters.CharactersApiBuilder
import com.example.rickandmorty.data.remote.episodes.EpisodesApi
import com.example.rickandmorty.data.remote.episodes.EpisodesApiBuilder
import com.example.rickandmorty.data.remote.locations.LocationsApi
import com.example.rickandmorty.data.remote.locations.LocationsApiBuilder
import com.example.rickandmorty.data.repository.CharactersRepositoryImpl
import com.example.rickandmorty.data.repository.EpisodesRepositoryImpl
import com.example.rickandmorty.data.repository.LocationsRepositoryImpl
import com.example.rickandmorty.databinding.FragmentCharacterDetailsBinding
import com.example.rickandmorty.domain.repository.CharactersRepository
import com.example.rickandmorty.domain.repository.EpisodesRepository
import com.example.rickandmorty.domain.repository.LocationsRepository
import com.example.rickandmorty.domain.usecases.characters.GetCharacterByIdUseCase
import com.example.rickandmorty.domain.usecases.episodes.GetEpisodesByIdsUseCase
import com.example.rickandmorty.domain.usecases.locations.GetLocationByIdUseCase
import com.example.rickandmorty.presentation.mapper.CharacterDomainToCharacterPresentationModelMapper
import com.example.rickandmorty.presentation.mapper.EpisodeDomainToEpisodePresentationModelMapper
import com.example.rickandmorty.presentation.mapper.LocationDomainToLocationPresentationMapper
import com.example.rickandmorty.presentation.models.CharacterPresentation
import com.example.rickandmorty.presentation.models.EpisodePresentation
import com.example.rickandmorty.presentation.models.LocationPresentation
import com.example.rickandmorty.presentation.ui.episodes.details.EpisodeDetailsFragment
import com.example.rickandmorty.presentation.ui.episodes.adapters.EpisodesAdapter
import com.example.rickandmorty.presentation.ui.hostActivity
import com.example.rickandmorty.presentation.ui.locations.details.LocationDetailsFragment
import com.example.rickandmorty.util.resource.Status


class CharacterDetailsFragment : Fragment() {

    private lateinit var binding: FragmentCharacterDetailsBinding
    private lateinit var character: CharacterPresentation
    private lateinit var toolbar: Toolbar
    private lateinit var episodesAdapter: EpisodesAdapter

    private lateinit var episodesApi: EpisodesApi
    private lateinit var charactersApi: CharactersApi
    private lateinit var locationsApi: LocationsApi

    private lateinit var database: RickAndMortyDatabase

    private lateinit var charactersDao: CharactersDao
    private lateinit var episodesDao: EpisodesDao
    private lateinit var locationsDao: LocationsDao
    private lateinit var keysDao: CharactersRemoteKeysDao

    private lateinit var episodesRepository: EpisodesRepository
    private lateinit var charactersRepository: CharactersRepository
    private lateinit var locationsRepository: LocationsRepository

    private lateinit var getEpisodesByIdsUseCase: GetEpisodesByIdsUseCase
    private lateinit var getCharacterByIdUseCase: GetCharacterByIdUseCase
    private lateinit var getLocationByIdUseCase: GetLocationByIdUseCase

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

        initDependencies()
        initViewModel()

        setUpObservers(
            episodesIds = character.episodes,
            characterId = character.id,
            location = character.location,
            origin = character.origin
        )

        return binding.root
    }

    private fun initDependencies() {
        episodesApi = EpisodesApiBuilder.apiService
        charactersApi = CharactersApiBuilder.apiService
        locationsApi = LocationsApiBuilder.apiService

        database = RickAndMortyDatabase.getInstance(requireContext().applicationContext)

        charactersDao = database.charactersDao
        episodesDao = database.episodesDao
        locationsDao = database.locationDao
        keysDao = database.charactersRemoteKeysDao

        episodesRepository = EpisodesRepositoryImpl(
            api = episodesApi,
            database = database
        )
        charactersRepository = CharactersRepositoryImpl(
            api = charactersApi,
            database = RickAndMortyDatabase.getInstance(requireContext().applicationContext)
        )
        locationsRepository = LocationsRepositoryImpl(
            api = locationsApi,
            dao = locationsDao
        )

        getEpisodesByIdsUseCase = GetEpisodesByIdsUseCase(episodesRepository)
        getCharacterByIdUseCase = GetCharacterByIdUseCase(charactersRepository)
        getLocationByIdUseCase = GetLocationByIdUseCase(locationsRepository)
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            owner = this,
            factory = CharacterDetailsViewModelFactory(
                getEpisodesByIdsUseCase = getEpisodesByIdsUseCase,
                getCharacterByIdUseCase = getCharacterByIdUseCase,
                getLocationByIdUseCase = getLocationByIdUseCase
            )
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

    // Abradolf Lincler
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
                    val mapper = CharacterDomainToCharacterPresentationModelMapper()
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
                    val mapper = EpisodeDomainToEpisodePresentationModelMapper()
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
        binding.characterSpecies.text = character.species
        binding.characterStatus.text = character.status
        binding.characterGender.text = character.gender
        binding.characterType.text = character.type
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