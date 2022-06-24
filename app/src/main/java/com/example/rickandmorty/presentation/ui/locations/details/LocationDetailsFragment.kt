package com.example.rickandmorty.presentation.ui.locations.details

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
import com.example.rickandmorty.data.local.database.RickAndMortyDatabase
import com.example.rickandmorty.data.local.database.characters.CharactersDao
import com.example.rickandmorty.data.local.database.locations.LocationsDao
import com.example.rickandmorty.data.remote.characters.CharactersApi
import com.example.rickandmorty.data.remote.characters.CharactersApiBuilder
import com.example.rickandmorty.data.remote.locations.LocationsApi
import com.example.rickandmorty.data.remote.locations.LocationsApiBuilder
import com.example.rickandmorty.data.repository.CharactersRepositoryImpl
import com.example.rickandmorty.data.repository.LocationsRepositoryImpl
import com.example.rickandmorty.databinding.FragmentLocationDetailsBinding
import com.example.rickandmorty.domain.repository.CharactersRepository
import com.example.rickandmorty.domain.repository.LocationsRepository
import com.example.rickandmorty.domain.usecases.characters.GetCharactersByIdsUseCase
import com.example.rickandmorty.domain.usecases.locations.GetLocationByIdUseCase
import com.example.rickandmorty.presentation.mapper.CharacterDomainToCharacterPresentationModelMapper
import com.example.rickandmorty.presentation.mapper.LocationDomainToLocationPresentationMapper
import com.example.rickandmorty.presentation.models.CharacterPresentation
import com.example.rickandmorty.presentation.models.LocationPresentation
import com.example.rickandmorty.presentation.ui.characters.adapters.CharactersAdapter
import com.example.rickandmorty.presentation.ui.characters.details.CharacterDetailsFragment
import com.example.rickandmorty.presentation.ui.hostActivity
import com.example.rickandmorty.util.resource.Status

class LocationDetailsFragment : Fragment() {

    private lateinit var binding: FragmentLocationDetailsBinding
    private lateinit var location: LocationPresentation
    private lateinit var toolbar: Toolbar
    private lateinit var charactersAdapter: CharactersAdapter

    private lateinit var locationsApi: LocationsApi
    private lateinit var charactersApi: CharactersApi

    private lateinit var charactersDao: CharactersDao
    private lateinit var locationsDao: LocationsDao

    private lateinit var locationsRepository: LocationsRepository
    private lateinit var charactersRepository: CharactersRepository

    private lateinit var getLocationByIdUseCase: GetLocationByIdUseCase
    private lateinit var getCharactersByIdsUseCase: GetCharactersByIdsUseCase

    private lateinit var viewModel: LocationDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        location =
            arguments?.getParcelable("Location") ?: LocationPresentation(-1, "", "", "", listOf())
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLocationDetailsBinding.inflate(LayoutInflater.from(requireContext()))
        setBottomNavigationCheckedItem()
        initToolbar()
        initRecyclerView()

        initDependencies()
        initViewModel()

        setUpObservers(id = location.id, ids = location.residents)

        return binding.root
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            owner = this,
            factory = LocationDetailsViewModelFactory(
                getLocationByIdUseCase = getLocationByIdUseCase,
                getCharactersByIdsUseCase = getCharactersByIdsUseCase
            )
        ).get(LocationDetailsViewModel::class.java)
    }

    private fun initDependencies() {
        locationsApi = LocationsApiBuilder.apiService
        charactersApi = CharactersApiBuilder.apiService

        charactersDao =
            RickAndMortyDatabase.getInstance(requireContext().applicationContext).charactersDao
        locationsDao =
            RickAndMortyDatabase.getInstance(requireContext().applicationContext).locationDao


        locationsRepository = LocationsRepositoryImpl(
            api = locationsApi,
            dao = locationsDao
        )
        charactersRepository = CharactersRepositoryImpl(
            api = charactersApi,
            dao = charactersDao
        )

        getLocationByIdUseCase = GetLocationByIdUseCase(locationsRepository)
        getCharactersByIdsUseCase = GetCharactersByIdsUseCase(charactersRepository)
    }

    private fun setUpObservers(id: Int, ids: List<Int?>) {
        setUpLocationDetailsObserver(id)
        setUpLocationResidentsObserver(ids)
    }

    private fun setUpLocationResidentsObserver(ids: List<Int?>) {
        viewModel.getResidents(ids).observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    val mapper = CharacterDomainToCharacterPresentationModelMapper()
                    val result = resource.data?.map { mapper.map(it) } ?: listOf()
                    location = location.copy(residents = result.map { it.id })
                    binding.recyclerViewProgressBar.visibility = View.GONE
                    showLocationResidents(result)
                }
                Status.ERROR -> {
                    binding.recyclerViewProgressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
                Status.LOADING -> {
                    binding.recyclerViewProgressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setUpLocationDetailsObserver(id: Int) {
        viewModel.getLocation(id).observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    val mapper = LocationDomainToLocationPresentationMapper()
                    val result = mapper.map(resource.data!!)
                    binding.locationDetailsProgressBar.visibility = View.GONE
                    showLocation(result)
                }
                Status.ERROR -> {
                    binding.locationDetailsProgressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
                Status.LOADING -> {
                    binding.locationDetailsProgressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun showLocationResidents(charactersList: List<CharacterPresentation>) {
        if (charactersList.isEmpty()) {
            binding.tvNoResidents.visibility = View.VISIBLE
        } else {
            binding.tvNoResidents.visibility = View.GONE
            charactersAdapter.charactersList = charactersList
        }
    }

    private fun initRecyclerView() {
        charactersAdapter = CharactersAdapter { onCharacterClicked(it) }
        binding.rvLocationResidents.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvLocationResidents.adapter = charactersAdapter
    }

    private fun onCharacterClicked(character: CharacterPresentation) {
        hostActivity().openFragment(
            CharacterDetailsFragment.newInstance(character),
            "CharacterDetailsFragment"
        )
    }

    private fun initToolbar() {
        toolbar = binding.locationToolbar
        hostActivity().setSupportActionBar(toolbar)
        hostActivity().supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                println("OnBackPressed")
                hostActivity().onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setBottomNavigationCheckedItem() {
        hostActivity().setBottomNavItemChecked(MENU_ITEM_NUMBER)
    }

    private fun showLocation(location: LocationPresentation) {
        binding.locationDimension.text = location.dimension
        binding.locationName.text = location.name
        binding.locationType.text = location.type
    }


    companion object {

        private const val MENU_ITEM_NUMBER: Int = 1

        fun newInstance(location: LocationPresentation) =
            LocationDetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("Location", location)
                }
            }
    }
}