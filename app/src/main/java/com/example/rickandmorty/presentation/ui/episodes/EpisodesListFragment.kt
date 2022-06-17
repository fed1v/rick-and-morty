package com.example.rickandmorty.presentation.ui.episodes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.rickandmorty.databinding.FragmentEpisodesListBinding
import com.example.rickandmorty.presentation.ui.characters.CharactersListFragment
import com.example.rickandmorty.presentation.ui.hostActivity


class EpisodesListFragment : Fragment() {

    private lateinit var binding: FragmentEpisodesListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEpisodesListBinding.inflate(inflater, container, false)
        setBottomNavigationCheckedItem()

        return binding.root
    }

    private fun setBottomNavigationCheckedItem() {
        hostActivity().setBottomNavItemChecked(MENU_ITEM_NUMBER)
    }

    companion object {

        private const val MENU_ITEM_NUMBER: Int = 1

        fun newInstance(param1: String, param2: String) =
            CharactersListFragment().apply {
                arguments = Bundle().apply {
                    //        putString(ARG_PARAM1, param1)
                    //        putString(ARG_PARAM2, param2)
                }
            }
    }

}