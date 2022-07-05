package com.example.rickandmorty.presentation.ui.locations.list

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rickandmorty.App
import com.example.rickandmorty.R
import com.example.rickandmorty.databinding.FragmentLocationsListBinding
import com.example.rickandmorty.domain.models.location.LocationFilter
import com.example.rickandmorty.presentation.models.LocationPresentation
import com.example.rickandmorty.presentation.ui.characters.list.isInternetAvailable
import com.example.rickandmorty.presentation.ui.hostActivity
import com.example.rickandmorty.presentation.ui.locations.adapters.LocationsPagedAdapter
import com.example.rickandmorty.presentation.ui.locations.details.LocationDetailsFragment
import com.example.rickandmorty.util.OnItemSelectedListener
import com.example.rickandmorty.util.filters.LocationsFiltersHelper
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalPagingApi
class LocationsListFragment : Fragment() {

    private lateinit var binding: FragmentLocationsListBinding

    private lateinit var locationsPagedAdapter: LocationsPagedAdapter

    private val onLocationSelectedListener = object : OnItemSelectedListener<LocationPresentation> {
        override fun onSelectItem(item: LocationPresentation) {
            hostActivity().openFragment(
                fragment = LocationDetailsFragment.newInstance(item),
                tag = "LocationDetailsFragment"
            )
        }
    }

    private lateinit var toolbar: Toolbar
    private var locationsFiltersHelper: LocationsFiltersHelper? = null

    private var appliedFilters = LocationFilter()

    @Inject
    lateinit var viewModelFactory: LocationsViewModelFactory
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

        injectDependencies()

        initViewModel()
        initFilters()

        setUpObservers()
        getLocations()

        initSwipeRefreshListener()

        checkInternetConnection()

        return binding.root
    }

    private fun checkInternetConnection() {
        if (!isInternetAvailable(requireContext())) {
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
        }
    }

    private fun injectDependencies() {
        val appComponent = (requireContext().applicationContext as App).appComponent
        appComponent.inject(this)
    }

    private fun initSwipeRefreshListener() {
        binding.swipeRefreshLayout.apply {
            setOnRefreshListener {
                isRefreshing = true
                getLocations()
                binding.rvLocations.scrollToPosition(0)
                isRefreshing = false
            }
        }
    }

    private fun setUpObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.locationsFlow.collectLatest { data ->
                locationsPagedAdapter.submitData(data)
            }
        }
    }

    private fun getLocations() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getLocationsWithPagination()
            viewModel.getLocationsWithPagination()
        }
    }

    private suspend fun getLocationsByFilters(filters: LocationFilter) {
        viewModel.getLocationsByFiltersWithPagination(filters)
    }

    private fun initFilters() {
        locationsFiltersHelper = LocationsFiltersHelper(
            context = requireContext(),
            applyCallback = { onFiltersApplied(it) },
            resetCallback = { onResetClicked() }
        )
        viewModel.getFilters().observe(viewLifecycleOwner) { filter ->
            when (filter.first) {
                "type" -> {
                    locationsFiltersHelper!!.typesArray = filter.second.toTypedArray()
                }
                "dimension" -> {
                    locationsFiltersHelper!!.dimensionsArray = filter.second.toTypedArray()
                }
            }
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            owner = this,
            factory = viewModelFactory
        ).get(LocationsViewModel::class.java)
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

    private fun initRecyclerView() {
        locationsPagedAdapter = LocationsPagedAdapter(
            onItemSelectedListener = onLocationSelectedListener
        )

        viewLifecycleOwner.lifecycleScope.launch {
            locationsPagedAdapter.loadStateFlow.collect { state ->

                binding.locationsBottomProgressBar.isVisible = state.append is LoadState.Loading
                binding.locationsProgressBar.isVisible = state.refresh is LoadState.Loading

                if (state.source.refresh is LoadState.NotLoading
                    && state.refresh is LoadState.Error
                    && locationsPagedAdapter.itemCount == 0
                ) {
                    binding.locationsProgressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Nothing found", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.rvLocations.itemAnimator = null
        binding.rvLocations.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvLocations.adapter = locationsPagedAdapter
    }

    private fun setBottomNavigationCheckedItem() {
        hostActivity().setBottomNavItemChecked(MENU_ITEM_NUMBER)
    }


    private fun onFiltersApplied(filters: LocationFilter) {
        val name = filters.name ?: appliedFilters.name
        appliedFilters = filters.copy(name = name)
        viewLifecycleOwner.lifecycleScope.launch {
            binding.locationsProgressBar.visibility = View.VISIBLE
            getLocationsByFilters(appliedFilters)
        }
    }

    private fun searchByQuery(query: String?) {
        appliedFilters.name = query
        viewLifecycleOwner.lifecycleScope.launch {
            binding.locationsProgressBar.visibility = View.VISIBLE
            getLocationsByFilters(appliedFilters)
        }
    }

    private fun onResetClicked() {
        appliedFilters = LocationFilter()
        viewLifecycleOwner.lifecycleScope.launch {
            binding.locationsProgressBar.visibility = View.VISIBLE
            getLocations()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationsFiltersHelper = null
    }

    companion object {
        private const val MENU_ITEM_NUMBER: Int = 1
    }
}