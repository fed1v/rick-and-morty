package com.example.rickandmorty.data.mapper.character

import com.example.rickandmorty.data.local.database.characters.CharacterEntity
import com.example.rickandmorty.domain.models.character.Character
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals

class CharacterEntityToCharacterDomainMapperTest {

    @Test
    fun `should map character from entity to domain correctly`() {
        val characterEntityTest = CharacterEntity(
            id = 12345,
            name = "test_name",
            status = "test_status",
            species = "test_species",
            type = "test_type",
            gender = "test_gender",
            origin = CharacterEntity.CharacterEntityLocation(
                id = 111,
                name = "test_origin_name"
            ),
            location = CharacterEntity.CharacterEntityLocation(
                id = 222,
                name = "test_location_name"
            ),
            episodes = listOf(1, 2, 3),
            image = "test_image"
        )

        val actual = CharacterEntityToCharacterDomainMapper().map(characterEntityTest)

        val expected = Character(
            id = 12345,
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

        assertEquals(expected, actual)
    }

    @Test
    fun `should map negative id to positive`() {
        val characterEntityTest = CharacterEntity(
            id = -12345,
            name = "test_name",
            status = "test_status",
            species = "test_species",
            type = "test_type",
            gender = "test_gender",
            origin = CharacterEntity.CharacterEntityLocation(
                id = 111,
                name = "test_origin_name"
            ),
            location = CharacterEntity.CharacterEntityLocation(
                id = 222,
                name = "test_location_name"
            ),
            episodes = listOf(1, 2, 3),
            image = "test_image"
        )

        val actual = CharacterEntityToCharacterDomainMapper().map(characterEntityTest)

        val expected = Character(
            id = 12345,
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

        assertEquals(expected, actual)
    }


}