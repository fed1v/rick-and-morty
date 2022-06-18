package com.example.rickandmorty.presentation.ui.locations

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rickandmorty.R
import com.example.rickandmorty.data.Location
import com.example.rickandmorty.data.LocationsProvider
import com.example.rickandmorty.databinding.FragmentLocationsListBinding
import com.example.rickandmorty.presentation.ui.characters.CharactersListFragment
import com.example.rickandmorty.presentation.ui.hostActivity

class LocationsListFragment : Fragment() {

    private lateinit var binding: FragmentLocationsListBinding
    private lateinit var locationsAdapter: LocationsAdapter
    private lateinit var toolbar: Toolbar

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

        toolbar = binding.locationsToolbar
        hostActivity().setSupportActionBar(toolbar)

        initRecyclerView()
        showLocations(LocationsProvider.locationsList)

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
        println("OpenFilters")
        //TODO
    }

    private fun searchByQuery(query: String?) {
        println("Query: $query")
        //TODO
    }

    private fun showLocations(locations: List<Location>) {
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

    private fun onLocationClicked(location: Location) {
        println("Clicked ${location.name}")
        hostActivity().openFragment(
            LocationDetailsFragment.newInstance(location),
            "LocationDetailsFragment"
        )
    }

    companion object {
        private const val MENU_ITEM_NUMBER: Int = 1
    }
}