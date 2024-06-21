package com.example.scoccerapplication.button2_SearchForClubsByLeague

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import com.example.scoccerapplication.DataBase.AppDataBase
import com.example.scoccerapplication.R
import com.example.scoccerapplication.button2_SearchForClubsByLeague.ui.theme.ScoccerApplicationTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class SearchForClubsByLeagueScreen : ComponentActivity() {

    // Define Room database and DAO
    private lateinit var appDataBase: AppDataBase
    private lateinit var clubDao: ClubDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScoccerApplicationTheme {
                DisplayTheUI()
            }
        }

        // Initialize Room database
        appDataBase = Room.databaseBuilder(applicationContext, AppDataBase::class.java, "app_database").build()
        clubDao = appDataBase.clubDao()
    }

    @Preview(showBackground = true)
    @Composable
    fun DisplayTheUI(){
        val snackBarHostState = SnackbarHostState()
        var detailsOfTheLeagues by remember { mutableStateOf("") }
        val scope = rememberCoroutineScope()
        var isSearchingForResult by remember { mutableStateOf(false) }
        val buttonColor =  ButtonDefaults.buttonColors(containerColor = Color.Yellow)
        var clubDetails by remember { mutableStateOf(listOf<Club>()) }


        Box(modifier = Modifier.fillMaxSize()){
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = R.drawable.football),
                contentDescription = "Soccer App",
                contentScale = ContentScale.FillBounds
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(50.dp))

                TextField(
                    value = detailsOfTheLeagues,
                    onValueChange = { detailsOfTheLeagues = it },
                    placeholder = { Text("Enter League here") }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ){
                    Button(
                        colors = buttonColor,
                        modifier = Modifier
                            .height(50.dp)
                            .width(170.dp),
                        onClick = {
                            scope.launch {
                                isSearchingForResult = true
                                clubDetails = fetchClubs(detailsOfTheLeagues)
                                Log.d("ClubDetails", clubDetails.toString())
                                isSearchingForResult = false
                            }
                        }
                    ) {
                        Text("Retrieve Clubs",style = TextStyle(color = Color.Black))
                    }

                    Button(
                        colors = buttonColor,
                        modifier = Modifier
                            .height(50.dp)
                            .width(170.dp),
                        onClick = {
                            scope.launch {
                                clubDao.insertAll(clubDetails)
                                withContext(Dispatchers.Main) {
                                    snackBarHostState.showSnackbar(
                                        message = "Data has been added successfully.",
                                        actionLabel = "Done"
                                    )
                                }
                            }
                        }
                    ) {
                        Text("Save clubs to DB",style = TextStyle(color = Color.Black))
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))

                if(isSearchingForResult){
                    Text(text = "Loading...", color = Color.White, fontSize = 20.sp)
                }else{
                    LazyColumn (
                        modifier = Modifier.padding(16.dp)
                    ){
                        items(clubDetails) { club ->
                            Text(text = "idTeam: ${club.idTeam},", color = Color.White)
                            Text(text = "Name: ${club.name},", color = Color.White)
                            Text(text = "strTeamShort: ${club.strTeamShort},", color = Color.White)
                            Text(text = "strAlternate: ${club.strAlternate},", color = Color.White)
                            Text(text = "intFormedYear: ${club.intFormedYear},", color = Color.White)
                            Text(text = "strLeague: ${club.strLeague},", color = Color.White)
                            Text(text = "idLeague: ${club.idLeague},", color = Color.White)
                            Text(text = "strStadium: ${club.strStadium},", color = Color.White)
                            Text(text = "strKeywords: ${club.strKeywords},", color = Color.White)
                            Text(text = "strStadiumThumb: ${club.strStadiumThumb},", color = Color.White)
                            Text(text = "strStadiumLocation: ${club.strStadiumLocation},", color = Color.White)
                            Text(text = "intStadiumCapacity: ${club.intStadiumCapacity},", color = Color.White)
                            Text(text = "strWebsite: ${club.strWebsite},", color = Color.White)
                            Text(text = "strTeamJersey: ${club.strTeamJersey},", color = Color.White)
                            Text(text = "strTeamLogo: ${club.strTeamLogo},", color = Color.White)
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }

                SnackbarHost(hostState = snackBarHostState)
            }
        }
    }
}

suspend fun fetchClubs(league: String): List<Club> = withContext(Dispatchers.IO) {
    val url = URL("https://www.thesportsdb.com/api/v1/json/3/search_all_teams.php?l=$league")
    (url.openConnection() as? HttpURLConnection)?.run {
        requestMethod = "GET"
        if (responseCode == HttpURLConnection.HTTP_OK) {
            inputStream.bufferedReader().use { reader ->
                val clubs = JSONObject(reader.readText()).getJSONArray("teams")
                List(clubs.length()) { i ->
                    clubs.getJSONObject(i).run {
                        Club(
                            idTeam = optString("idTeam"),
                            name = optString("strTeam"),
                            strTeamShort = optString("strTeamShort"),
                            strAlternate = optString("strAlternate"),
                            intFormedYear = optString("intFormedYear"),
                            strLeague = optString("strLeague"),
                            idLeague = optString("idLeague"),
                            strStadium = optString("strStadium"),
                            strKeywords = optString("strKeywords"),
                            strStadiumThumb = optString("strStadiumThumb"),
                            strStadiumLocation = optString("strStadiumLocation"),
                            intStadiumCapacity = optString("intStadiumCapacity"),
                            strWebsite = optString("strWebsite"),
                            strTeamJersey = optString("strTeamJersey"),
                            strTeamLogo = optString("strTeamLogo")
                        )
                    }
                }
            }
        } else {
            emptyList()
        }
    } ?: emptyList()
}