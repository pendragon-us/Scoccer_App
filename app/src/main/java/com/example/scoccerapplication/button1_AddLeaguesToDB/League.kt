package com.example.scoccerapplication.button1_AddLeaguesToDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class League(
    @PrimaryKey
    val idLeague: String,
    val strLeague: String,
    val strSport: String,
    val strLeagueAlternate: String
)