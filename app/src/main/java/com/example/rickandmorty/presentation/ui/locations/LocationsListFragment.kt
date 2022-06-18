package com.example.rickandmorty.presentation.ui.locations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rickandmorty.data.Location
import com.example.rickandmorty.data.LocationsProvider
import com.example.rickandmorty.databinding.FragmentLocationsListBinding
import com.example.rickandmorty.presentation.ui.characters.CharactersListFragment
import com.example.rickandmorty.presentation.ui.hostActivity

class LocationsListFragment : Fragment() {

    private lateinit var binding: FragmentLocationsListBinding
    private lateinit var locationsAdapter: LocationsAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLocationsListBinding.inflate(inflater, container, false)
        setBottomNavigationCheckedItem()

        initRecyclerView()
        showLocations(LocationsProvider.locationsList)

        return binding.root
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

        private const val MENU_ITEM_NUMBER: Int = 2

        fun newInstance(param1: String, param2: String) =
            CharactersListFragment().apply {
                arguments = Bundle().apply {
                    //        putString(ARG_PARAM1, param1)
                    //        putString(ARG_PARAM2, param2)
                }
            }
    }
}