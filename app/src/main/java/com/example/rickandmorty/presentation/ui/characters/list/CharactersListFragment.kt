package com.example.rickandmorty.presentation.ui.characters.list

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
import com.example.rickandmorty.data.local.database.RickAndMortyDatabase
import com.example.rickandmorty.data.local.database.characters.CharactersDao
import com.example.rickandmorty.data.remote.characters.CharactersApi
import com.example.rickandmorty.data.remote.characters.CharactersApiBuilder
import com.example.rickandmorty.data.repository.CharactersRepositoryImpl
import com.example.rickandmorty.databinding.FragmentCharactersListBinding
import com.example.rickandmorty.domain.models.character.CharacterFilter
import com.example.rickandmorty.domain.repository.CharactersRepository
import com.example.rickandmorty.domain.usecases.characters.GetCharactersByFiltersUseCase
import com.example.rickandmorty.domain.usecases.characters.GetCharactersFiltersUseCase
import com.example.rickandmorty.domain.usecases.characters.GetCharactersUseCase
import com.example.rickandmorty.presentation.mapper.CharacterDomainToCharacterPresentationModelMapper
import com.example.rickandmorty.presentation.models.CharacterPresentation
import com.example.rickandmorty.presentation.ui.characters.adapters.CharactersAdapter
import com.example.rickandmorty.presentation.ui.characters.details.CharacterDetailsFragment
import com.example.rickandmorty.presentation.ui.hostActivity
import com.example.rickandmorty.util.filters.CharactersFiltersHelper
import com.example.rickandmorty.util.resource.Status


class CharactersListFragment : Fragment() {

    private lateinit var binding: FragmentCharactersListBinding
    private lateinit var charactersAdapter: CharactersAdapter
    private lateinit var toolbar: Toolbar
    private var charactersFiltersHelper: CharactersFiltersHelper? = null

    private var appliedFilters: CharacterFilter = CharacterFilter()

    private lateinit var api: CharactersApi
    private lateinit var repository: CharactersRepository

    private lateinit var charactersDao: CharactersDao

    private lateinit var getCharactersUseCase: GetCharactersUseCase
    private lateinit var getCharactersByFiltersUseCase: GetCharactersByFiltersUseCase
    private lateinit var getCharactersFiltersUseCase: GetCharactersFiltersUseCase

    private lateinit var viewModel: CharactersViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharactersListBinding.inflate(inflater, container, false)
        setBottomNavigationCheckedItem()
        initToolbar()
        initRecyclerView()


        initDependencies()
        initViewModel()
        initFilters()

        setUpObservers()

        return binding.root
    }

    private fun initFilters() {
        charactersFiltersHelper = CharactersFiltersHelper(
            context = requireContext(),
            applyCallback = { onFiltersApplied(it) },
            resetCallback = { onResetClicked() }
        )
        viewModel.getFilters().observe(viewLifecycleOwner) { filter ->
            when (filter.first) {
                "species" -> {
                    charactersFiltersHelper!!.speciesArray = filter.second.toTypedArray()
                }
                "type" -> {
                    charactersFiltersHelper!!.typesArray = filter.second.toTypedArray()
                }
            }
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            owner = this,
            factory = CharactersViewModelFactory(
                getCharactersUseCase = getCharactersUseCase,
                getCharactersByFiltersUseCase = getCharactersByFiltersUseCase,
                getCharactersFiltersUseCase = getCharactersFiltersUseCase
            )
        ).get(CharactersViewModel::class.java)
    }

    private fun initDependencies() {
        api = CharactersApiBuilder.apiService
        charactersDao =
            RickAndMortyDatabase.getInstance(requireContext().applicationContext).charactersDao

        repository = CharactersRepositoryImpl(
            api = api,
            dao = charactersDao
        )
        getCharactersUseCase = GetCharactersUseCase(repository)
        getCharactersByFiltersUseCase = GetCharactersByFiltersUseCase(repository)
        getCharactersFiltersUseCase = GetCharactersFiltersUseCase(repository)
    }

    private fun initToolbar() {
        toolbar = binding.charactersToolbar
        hostActivity().setSupportActionBar(toolbar)
    }

    private fun setUpObservers() {
        setUpCharactersObserver()
        //    setUpCharactersByFiltersObserver()
    }

    private fun setUpCharactersByFiltersObserver(filters: CharacterFilter) {
        viewModel.getCharactersByFilters(filters).observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    val mapper = CharacterDomainToCharacterPresentationModelMapper()
                    val result =
                        resource.data?.map { character -> mapper.map(character) } ?: listOf()
                    showCharacters(result)
                    binding.charactersProgressBar.visibility = View.GONE
                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                    binding.charactersProgressBar.visibility = View.GONE
                }
                Status.LOADING -> {
                    binding.charactersProgressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setUpCharactersObserver() {
        viewModel.getCharacters().observe(viewLifecycleOwner) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        val mapper = CharacterDomainToCharacterPresentationModelMapper()
                        val result =
                            resource.data?.map { character -> mapper.map(character) } ?: listOf()
                        showCharacters(result)
                        binding.charactersProgressBar.visibility = View.GONE
                    }
                    Status.ERROR -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        binding.charactersProgressBar.visibility = View.GONE
                    }
                    Status.LOADING -> {
                        binding.charactersProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun initRecyclerView() {
        charactersAdapter = CharactersAdapter { onCharacterClicked(it) }
        binding.rvCharacters.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvCharacters.adapter = charactersAdapter
    }

    private fun showCharacters(characters: List<CharacterPresentation>) {
        if (characters.isEmpty()) {
            Toast.makeText(requireContext(), "Nothing found", Toast.LENGTH_SHORT).show()
        }
        charactersAdapter.charactersList = characters
    }

    private fun setBottomNavigationCheckedItem() {
        hostActivity().setBottomNavItemChecked(MENU_ITEM_NUMBER)
    }

    private fun onCharacterClicked(character: CharacterPresentation) {
        hostActivity().openFragment(
            fragment = CharacterDetailsFragment.newInstance(character),
            tag = "CharacterDetailsFragment"
        )
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
        charactersFiltersHelper?.openFilters()
    }

    private fun onFiltersApplied(filters: CharacterFilter) {
        println("Filters applied: ${filters}")
        val name = filters.name ?: appliedFilters.name
        appliedFilters = filters.copy(name = name)
        setUpCharactersByFiltersObserver(appliedFilters)
    }

    private fun onResetClicked() {

        appliedFilters = CharacterFilter()
        setUpCharactersByFiltersObserver(appliedFilters)
    }


    private fun searchByQuery(query: String?) {
        println("Query: $query")
        appliedFilters.name = query
        setUpCharactersByFiltersObserver(appliedFilters)
    }

    override fun onDestroy() {
        super.onDestroy()
        charactersFiltersHelper = null
    }

    companion object {
        private const val MENU_ITEM_NUMBER: Int = 0
    }


}