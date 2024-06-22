package com.example.scoccerapplication

import android.content.Intent
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.scoccerapplication.DataBase.AppDataBase
import com.example.scoccerapplication.button1_AddLeaguesToDB.leagues
import com.example.scoccerapplication.button2_SearchForClubsByLeague.SearchForClubsByLeagueScreen
import com.example.scoccerapplication.button3_SearchForClubs.SearchForClubsFromDBScreen
import com.example.scoccerapplication.button4_SearchForJerseys.SearchForJerseyScreen
import com.example.scoccerapplication.ui.theme.ScoccerApplicationTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private lateinit var appDataBase: AppDataBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScoccerApplicationTheme {
                DisplayMainMenu()
            }
        }
        appDataBase = Room.databaseBuilder(applicationContext, AppDataBase::class.java, "app_database").build()
    }

    /**
     * DisplayMainMenu is the main menu of the app. It contains buttons that allow the user to
     * add leagues to the database, search for clubs by league, search for clubs, and search for
     * jerseys.
     */
    @Preview(showBackground = true)
    @Composable
    fun DisplayMainMenu(){
        val buttonColor =  ButtonDefaults.buttonColors(containerColor = Color.Yellow)
        val lifecycleOwner = LocalLifecycleOwner.current
        val snackBarHostState = SnackbarHostState()
        val context = LocalContext.current

        Box(modifier = Modifier.fillMaxSize()) {
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
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Add Leagues to DB button
                Button(
                    colors = buttonColor,
                    modifier = Modifier
                        .height(60.dp)
                        .width(250.dp),
                    onClick = {
                        try {
                            lifecycleOwner.lifecycleScope.launch {
                                withContext(Dispatchers.IO) {
                                    for (league in leagues) {
                                        appDataBase.leagueDao().insertLeague(league)
                                    }
                                    withContext(Dispatchers.Main) {
                                        snackBarHostState.showSnackbar(
                                            message = "Data has been added successfully.",
                                            actionLabel = "Done"
                                        )
                                    }
                                }
                            }
                        }catch (e: Exception){
                            Log.d("Error", e.toString())
                        }
                    },
                ) {
                    Text(text = "Add Leagues to DB", style = TextStyle(color = Color.Black))
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search for Clubs By League button
                Button(
                    colors = buttonColor,
                    modifier = Modifier
                        .height(60.dp)
                        .width(250.dp),
                    onClick = {
                        val intent = Intent(context, SearchForClubsByLeagueScreen::class.java)
                        context.startActivity(intent)
                    }
                ) {
                    Text(text = "Search for Clubs By League",style = TextStyle(color = Color.Black))
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search for Clubs button
                Button(
                    colors = buttonColor,
                    modifier = Modifier
                        .height(60.dp)
                        .width(250.dp),
                    onClick = {
                        val intent = Intent(context, SearchForClubsFromDBScreen::class.java)
                        context.startActivity(intent)
                    }
                ) {
                    Text(text = "Search for Clubs",style = TextStyle(color = Color.Black))
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search for Jerseys button
                Button(
                    colors = buttonColor,
                    modifier = Modifier
                        .height(60.dp)
                        .width(250.dp),
                    onClick = {
                        val intent = Intent(context, SearchForJerseyScreen::class.java)
                        context.startActivity(intent)
                    }
                ) {
                    Text(text = "Search for jerseys",style = TextStyle(color = Color.Black))
                }
            }

            SnackbarHost(hostState = snackBarHostState)
        }
    }
}
