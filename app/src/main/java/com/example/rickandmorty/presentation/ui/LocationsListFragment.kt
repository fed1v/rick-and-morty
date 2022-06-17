package com.example.rickandmorty.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.rickandmorty.databinding.FragmentLocationsListBinding
import com.example.rickandmorty.presentation.ui.characters.CharactersListFragment

class LocationsListFragment : Fragment() {

    private lateinit var binding: FragmentLocationsListBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLocationsListBinding.inflate(inflater, container, false)


        return binding.root
    }

    companion object {
        fun newInstance(param1: String, param2: String) =
            CharactersListFragment().apply {
                arguments = Bundle().apply {
                    //        putString(ARG_PARAM1, param1)
                    //        putString(ARG_PARAM2, param2)
                }
            }
    }
}