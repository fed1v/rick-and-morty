package com.example.rickandmorty.presentation.ui.characters.list

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
import com.example.rickandmorty.data.local.database.RickAndMortyDatabase
import com.example.rickandmorty.data.local.database.characters.CharactersDao
import com.example.rickandmorty.data.local.database.characters.remote_keys.CharactersRemoteKeysDao
import com.example.rickandmorty.data.remote.characters.CharactersApi
import com.example.rickandmorty.data.remote.characters.CharactersApiBuilder
import com.example.rickandmorty.data.repository.CharactersRepositoryImpl
import com.example.rickandmorty.databinding.FragmentCharactersListBinding
import com.example.rickandmorty.domain.models.character.CharacterFilter
import com.example.rickandmorty.domain.repository.CharactersRepository
import com.example.rickandmorty.domain.usecases.characters.*
import com.example.rickandmorty.presentation.models.CharacterPresentation
import com.example.rickandmorty.presentation.ui.characters.adapters.CharactersAdapter
import com.example.rickandmorty.presentation.ui.characters.adapters.CharactersPagedAdapter
import com.example.rickandmorty.presentation.ui.characters.details.CharacterDetailsFragment
import com.example.rickandmorty.presentation.ui.hostActivity
import com.example.rickandmorty.util.OnItemSelectedListener
import com.example.rickandmorty.util.filters.CharactersFiltersHelper
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class CharactersListFragment : Fragment() {

    private lateinit var binding: FragmentCharactersListBinding

    private lateinit var charactersPagedAdapter: CharactersPagedAdapter

    private val onCharacterSelectedListener =
        object : OnItemSelectedListener<CharacterPresentation> {
            override fun onSelectItem(item: CharacterPresentation) {
                hostActivity().openFragment(
                    fragment = CharacterDetailsFragment.newInstance(item),
                    tag = "CharacterDetailsFragment"
                )
            }
        }

    private lateinit var toolbar: Toolbar
    private var charactersFiltersHelper: CharactersFiltersHelper? = null

    private var appliedFilters: CharacterFilter = CharacterFilter()


    //   lateinit var api: CharactersApi
    //   private lateinit var repository: CharactersRepository

    //  lateinit var database: RickAndMortyDatabase
/*
    private lateinit var charactersDao: CharactersDao
    private lateinit var charactersRemoteKeysDao: CharactersRemoteKeysDao

    private lateinit var getCharactersUseCase: GetCharactersUseCase
    private lateinit var getCharactersByFiltersUseCase: GetCharactersByFiltersUseCase
    private lateinit var getCharactersFiltersUseCase: GetCharactersFiltersUseCase
    private lateinit var getCharactersWithPaginationUseCase: GetCharactersWithPaginationUseCase
    private lateinit var getCharactersByFiltersWithPaginationUseCase: GetCharactersByFiltersWithPaginationUseCase*/

    @Inject
    lateinit var viewModelFactory: CharactersViewModelFactory
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

        val appComponent = (requireContext().applicationContext as App).appComponent
        appComponent.inject(this)

        initDependencies()
        initViewModel()
        initFilters()

        setUpObservers()

        getCharacters()

        initSwipeRefreshListener()

        return binding.root
    }

    private fun initSwipeRefreshListener() {
        binding.swipeRefreshLayout.apply {
            setOnRefreshListener {
                isRefreshing = true
                getCharacters()
                binding.rvCharacters.scrollToPosition(0)
                isRefreshing = false
            }
        }
    }

    private fun setUpObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.charactersFlow.collectLatest {
                charactersPagedAdapter.submitData(it)
            }
        }
    }

    private fun getCharacters() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getCharactersWithPagination()
            viewModel.getCharactersWithPagination()
        }
    }

    private suspend fun getCharactersByFilters(filters: CharacterFilter) {
        viewModel.getCharactersByFiltersWithPagination(filters)
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
            factory = viewModelFactory
            /*CharactersViewModelFactory(
                getCharactersUseCase = getCharactersUseCase,
                getCharactersByFiltersUseCase = getCharactersByFiltersUseCase,
                getCharactersFiltersUseCase = getCharactersFiltersUseCase,
                getCharactersWithPaginationUseCase = getCharactersWithPaginationUseCase,
                getCharactersByFiltersWithPaginationUseCase = getCharactersByFiltersWithPaginationUseCase
            )*/
        ).get(CharactersViewModel::class.java)
    }

    private fun initDependencies() {
        //    api = CharactersApiBuilder.apiService

        //    database = RickAndMortyDatabase.getInstance(requireContext().applicationContext)

        /*charactersDao = database.charactersDao
        charactersRemoteKeysDao = database.charactersRemoteKeysDao

        repository = CharactersRepositoryImpl(
            api = api,
            database = database
        )

        getCharactersUseCase = GetCharactersUseCase(repository)
        getCharactersByFiltersUseCase = GetCharactersByFiltersUseCase(repository)
        getCharactersFiltersUseCase = GetCharactersFiltersUseCase(repository)
        getCharactersWithPaginationUseCase = GetCharactersWithPaginationUseCase(repository)
        getCharactersByFiltersWithPaginationUseCase =
            GetCharactersByFiltersWithPaginationUseCase(repository)*/
    }

    private fun initToolbar() {
        toolbar = binding.charactersToolbar
        hostActivity().setSupportActionBar(toolbar)
    }


    private fun initRecyclerView() {
        charactersPagedAdapter = CharactersPagedAdapter(
            onItemSelectedListener = onCharacterSelectedListener
        )

        viewLifecycleOwner.lifecycleScope.launch {
            charactersPagedAdapter.loadStateFlow.collect { state ->

                binding.charactersBottomProgressBar.isVisible = state.append is LoadState.Loading
                binding.charactersProgressBar.isVisible = state.refresh is LoadState.Loading

                if (state.source.refresh is LoadState.NotLoading
                    && state.refresh is LoadState.Error
                    && charactersPagedAdapter.itemCount == 0
                ) {
                    binding.charactersProgressBar.isVisible = false
                    Toast.makeText(requireContext(), "Nothing found", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.rvCharacters.itemAnimator = null
        binding.rvCharacters.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvCharacters.adapter = charactersPagedAdapter
    }


    private fun setBottomNavigationCheckedItem() {
        hostActivity().setBottomNavItemChecked(MENU_ITEM_NUMBER)
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
        val name = filters.name ?: appliedFilters.name
        appliedFilters = filters.copy(name = name)
        viewLifecycleOwner.lifecycleScope.launch {
            binding.charactersProgressBar.visibility = View.VISIBLE
            getCharactersByFilters(appliedFilters)
        }
    }

    private fun searchByQuery(query: String?) {
        appliedFilters.name = query
        viewLifecycleOwner.lifecycleScope.launch {
            binding.charactersProgressBar.visibility = View.VISIBLE
            getCharactersByFilters(appliedFilters)
        }
    }

    private fun onResetClicked() {
        appliedFilters = CharacterFilter()
        viewLifecycleOwner.lifecycleScope.launch {
            getCharacters()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        charactersFiltersHelper = null
    }

    companion object {
        private const val MENU_ITEM_NUMBER: Int = 0
    }
}