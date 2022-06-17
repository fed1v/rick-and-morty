package com.example.rickandmorty.presentation.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.rickandmorty.R
import com.example.rickandmorty.databinding.ActivityMainBinding
import com.example.rickandmorty.presentation.ui.characters.CharactersListFragment
import com.example.rickandmorty.presentation.ui.episodes.EpisodesListFragment
import com.example.rickandmorty.presentation.ui.locations.LocationsListFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_RickAndMorty)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_characters -> {
                    println("Characters")
                    openFragment(CharactersListFragment(), "CharactersListFragment")
                    true
                }
                R.id.item_episodes -> {
                    println("Episodes")
                    openFragment(EpisodesListFragment(), "EpisodesListFragment")
                    true
                }
                R.id.item_locations -> {
                    println("Locations")
                    openFragment(LocationsListFragment(), "LocationsListFragment")
                    true
                }
                else -> false
            }
        }


        if (savedInstanceState == null) {
            openCharactersListFragment()
        }

    }

    fun setBottomNavItemChecked(id: Int) {
        binding
            .bottomNavigationView
            .menu
            .getItem(id)
            .isChecked = true
    }

    private fun openCharactersListFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, CharactersListFragment())
            .commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 0) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    fun openFragment(fragment: Fragment, tag: String) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment, tag)
            .addToBackStack(tag)
            .commit()
    }

}

fun Fragment.hostActivity() = activity as MainActivity