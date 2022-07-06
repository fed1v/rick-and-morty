package com.example.rickandmorty.presentation.ui.characters.details

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.viewModelScope
import com.example.rickandmorty.domain.models.character.Character
import com.example.rickandmorty.domain.usecases.characters.GetCharacterByIdUseCase
import com.example.rickandmorty.domain.usecases.episodes.GetEpisodesByIdsUseCase
import com.example.rickandmorty.domain.usecases.locations.GetLocationByIdUseCase
import com.example.rickandmorty.util.resource.Resource
import com.example.rickandmorty.util.resource.Status
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions
import org.mockito.Mockito
import org.mockito.kotlin.mock

class CharactersDetailsViewModelTest {

    private val getCharacterByIdUseCase = mock<GetCharacterByIdUseCase>()
    private val getEpisodesByIdsUseCase = mock<GetEpisodesByIdsUseCase>()
    private val getLocationByIdUseCase = mock<GetLocationByIdUseCase>()

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun testGetCharactersById() {

        val testId = 12345
        val expectedCharacter = Character(
            id = testId,
            name = "test_name",
            status = "test_status",
            species = "test_species",
            type = "test_type",
            gender = "test_gender",
            origin = Character.CharacterLocation(
                id = 111,
                name = "test_origin_name"
            ),
            location = Character.CharacterLocation(
                id = 222,
                name = "test_location_name"
            ),
            episodes = listOf(1, 2, 3),
            image = "test_image"
        )

        val expectedCharacter2 = Character(
            id = testId,
            name = "test_name2",
            status = "test_status",
            species = "test_species",
            type = "test_type",
            gender = "test_gender",
            origin = Character.CharacterLocation(
                id = 111,
                name = "test_origin_name"
            ),
            location = Character.CharacterLocation(
                id = 222,
                name = "test_location_name"
            ),
            episodes = listOf(1, 2, 3),
            image = "test_image"
        )

        runBlocking {
            Mockito.`when`(getCharacterByIdUseCase.execute(testId)).thenReturn(expectedCharacter)
        }

        val viewModel = CharacterDetailsViewModel(
            getCharacterByIdUseCase = getCharacterByIdUseCase,
            getEpisodesByIdsUseCase = getEpisodesByIdsUseCase,
            getLocationByIdUseCase = getLocationByIdUseCase
        )


        viewModel.getCharacterById(testId).observeForever { resource ->
            if(resource.status == Status.SUCCESS){
                Assertions.assertEquals(expectedCharacter, expectedCharacter2)
            }
        }

        viewModel.getCharacterById(testId)



    //    Assertions.assertEquals(expectedCharacter, actualCharacter)


    }
}