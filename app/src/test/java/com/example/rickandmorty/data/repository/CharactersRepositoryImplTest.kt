package com.example.rickandmorty.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.rickandmorty.data.local.database.RickAndMortyDatabase
import com.example.rickandmorty.data.local.database.characters.CharactersDao
import com.example.rickandmorty.data.mapper.character.CharacterDtoToCharacterEntityMapper
import com.example.rickandmorty.data.models.character.CharacterDto
import com.example.rickandmorty.data.remote.characters.CharactersApi
import com.example.rickandmorty.domain.models.character.Character
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions
import org.mockito.Mockito
import org.mockito.kotlin.mock
import org.mockito.kotlin.times

class CharactersRepositoryImplTest {

    private val api = mock<CharactersApi>()
    private val database = mock<RickAndMortyDatabase>()
    private val dao = mock<CharactersDao>()

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun testGetCharacterById() {

        val mapperDtoToEntity = CharacterDtoToCharacterEntityMapper()

        val testId = 12345
        val testCharacterFromApi = CharacterDto(
            id = testId,
            created = "test_created",
            name = "test_name",
            status = "test_status",
            species = "test_species",
            type = "test_type",
            gender = "test_gender",
            origin = CharacterDto.LocationData(
                url = "test_origin_url_111",
                name = "test_origin_name"
            ),
            location = CharacterDto.LocationData(
                url = "test_location_url_222",
                name = "test_location_name"
            ),
            episode = listOf("1", "2", "3"),
            image = "test_image",
            url = "test_url_1"
        )

        val testCharacterEntity = mapperDtoToEntity.map(testCharacterFromApi)

        val expected = Character(
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

        runBlocking {
            Mockito.`when`(api.getCharacterById(testId)).thenReturn(testCharacterFromApi)
            Mockito.`when`(dao.getCharacterById(testId)).thenReturn(testCharacterEntity)
        }

        Mockito.`when`(database.charactersDao).thenReturn(dao)

        val characterRepositoryImpl = CharactersRepositoryImpl(
            api = api,
            database = database
        )

        runBlocking {
            val actualCharacterFromApi = api.getCharacterById(testId)
            dao.insertCharacter(mapperDtoToEntity.map(actualCharacterFromApi))

            val actual = characterRepositoryImpl.getCharacterById(testId)

            Assertions.assertEquals(expected, actual)
        }

        Mockito.verify(dao, times(1)).getCharacterById(testId)
    }
}