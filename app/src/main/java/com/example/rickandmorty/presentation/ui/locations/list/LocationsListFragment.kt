package com.example.rickandmorty.presentation.ui.locations.list

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
import com.example.rickandmorty.data.remote.LocationsApi
import com.example.rickandmorty.data.remote.LocationsApiBuilder
import com.example.rickandmorty.data.repository.LocationsRepositoryImpl
import com.example.rickandmorty.databinding.FragmentLocationsListBinding
import com.example.rickandmorty.domain.repository.LocationsRepository
import com.example.rickandmorty.domain.usecases.locations.GetLocationsUseCase
import com.example.rickandmorty.presentation.mapper.LocationDomainToLocationPresentationMapper
import com.example.rickandmorty.presentation.models.LocationPresentation
import com.example.rickandmorty.presentation.ui.hostActivity
import com.example.rickandmorty.presentation.ui.locations.adapters.LocationsAdapter
import com.example.rickandmorty.presentation.ui.locations.details.LocationDetailsFragment
import com.example.rickandmorty.util.filters.LocationFilter
import com.example.rickandmorty.util.filters.LocationsFiltersHelper
import com.example.rickandmorty.util.status.Status

class LocationsListFragment : Fragment() {

    private lateinit var binding: FragmentLocationsListBinding
    private lateinit var locationsAdapter: LocationsAdapter
    private lateinit var toolbar: Toolbar
    private var locationsFiltersHelper: LocationsFiltersHelper? = null

    private lateinit var api: LocationsApi
    private lateinit var repository: LocationsRepository
    private lateinit var getEpisodesUseCase: GetLocationsUseCase

    private lateinit var viewModel: LocationsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLocationsListBinding.inflate(inflater, container, false)
        setBottomNavigationCheckedItem()
        initToolbar()
        initRecyclerView()
        locationsFiltersHelper = LocationsFiltersHelper(requireContext()) { onFiltersApplied(it) }

        initDependencies()
        initViewModel()

        setUpObservers()

        return binding.root
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this, LocationsViewModelFactory(getEpisodesUseCase))
            .get(LocationsViewModel::class.java)
    }

    private fun initDependencies() {
        api = LocationsApiBuilder.apiService
        repository = LocationsRepositoryImpl(api)
        getEpisodesUseCase = GetLocationsUseCase(repository)
    }

    private fun setUpObservers() {
        viewModel.getLocations().observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    val mapper = LocationDomainToLocationPresentationMapper()
                    val result = resource.data?.map { mapper.map(it) } ?: listOf()
                    binding.locationsProgressBar.visibility = View.GONE
                    showLocations(result)
                }
                Status.ERROR -> {
                    binding.locationsProgressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
                Status.LOADING -> {
                    binding.locationsProgressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun initToolbar() {
        toolbar = binding.locationsToolbar
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
        locationsFiltersHelper?.openFilters()
    }

    private fun searchByQuery(query: String?) {
        println("Query: $query")
        //TODO
    }

    private fun showLocations(locations: List<LocationPresentation>) {
        locationsAdapter.locationsList = locations
    }

    private fun initRecyclerView() {
        locationsAdapter = LocationsAdapter { onLocationClicked(it) }
        binding.rvLocations.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvLocations.adapter = locationsAdapter
    }

    private fun setBottomNavigationCheckedItem() {
        hostActivity().setBottomNavItemChecked(MENU_ITEM_NUMBER)
    }

    private fun onLocationClicked(location: LocationPresentation) {
        hostActivity().openFragment(
            LocationDetailsFragment.newInstance(location),
            "LocationDetailsFragment"
        )
    }

    private fun onFiltersApplied(filters: LocationFilter) {
        println("Filters applied: ${filters}")
    }

    override fun onDestroy() {
        super.onDestroy()
        locationsFiltersHelper = null
    }

    companion object {
        private const val MENU_ITEM_NUMBER: Int = 1
    }
}