package com.example.scoccerapplication.button2_SearchForClubsByLeague

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ClubDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClub(club: Club)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(clubs: List<Club>)

    @Query("SELECT * FROM club")
    suspend fun getAllClubs(): List<Club>

    @Query("SELECT * FROM club WHERE LOWER(Name) LIKE LOWER(:search) OR LOWER(strLeague) LIKE LOWER(:search)")
    suspend fun searchClubs(search: String): List<Club>
}