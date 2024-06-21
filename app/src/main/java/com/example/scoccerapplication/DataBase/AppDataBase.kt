package com.example.scoccerapplication.DataBase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.scoccerapplication.button1_AddLeaguesToDB.League
import com.example.scoccerapplication.button1_AddLeaguesToDB.LeagueDao
import com.example.scoccerapplication.button2_SearchForClubsByLeague.Club
import com.example.scoccerapplication.button2_SearchForClubsByLeague.ClubDao

@Database(entities = [League::class, Club::class], version = 2, exportSchema = false)
abstract class AppDataBase : RoomDatabase() {
    abstract fun leagueDao(): LeagueDao
    abstract fun clubDao(): ClubDao
}
