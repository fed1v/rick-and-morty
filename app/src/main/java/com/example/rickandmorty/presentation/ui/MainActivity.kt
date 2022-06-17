package com.example.rickandmorty.presentation.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.rickandmorty.R
import com.example.rickandmorty.databinding.ActivityMainBinding
import com.example.rickandmorty.presentation.ui.characters.CharactersListFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_RickAndMorty)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCharacters.setOnClickListener {
            println("Characters")
            openFragment(CharactersListFragment(), "CharactersListFragment")
        }
        binding.btnEpisodes.setOnClickListener {
            println("Episodes")
            openFragment(EpisodesListFragment(), "EpisodesListFragment")
        }
        binding.btnLocations.setOnClickListener {
            println("Locations")
            openFragment(LocationsListFragment(), "LocationsListFragment")
        }

        if (savedInstanceState == null) {
            openCharactersListFragment()
        }

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

    private fun openFragment(fragment: Fragment, tag: String) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment, tag)
            .addToBackStack(tag)
            .commit()
    }

}