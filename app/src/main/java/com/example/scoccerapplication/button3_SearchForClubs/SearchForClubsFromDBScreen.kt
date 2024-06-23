package com.example.scoccerapplication.button3_SearchForClubs

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import com.example.scoccerapplication.DataBase.AppDataBase
import com.example.scoccerapplication.R
import com.example.scoccerapplication.button2_SearchForClubsByLeague.Club
import com.example.scoccerapplication.button3_SearchForClubs.ui.theme.ScoccerApplicationTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class SearchForClubsFromDBScreen : ComponentActivity() {

    private lateinit var db: AppDataBase
    override fun onCreate(savedInstanceState: Bundle?) {
        db = Room.databaseBuilder(applicationContext, AppDataBase::class.java, "app_database").build()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScoccerApplicationTheme {
                DisplayTheUI()
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DisplayTheUI() {
        var text by rememberSaveable { mutableStateOf("") }
        var clubs by rememberSaveable { mutableStateOf(listOf<Club>()) }
        var isLoading by rememberSaveable { mutableStateOf(false) }
        val buttonColor = ButtonDefaults.buttonColors(containerColor = Color.Yellow)

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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(50.dp))

                TextField(
                    value = text,
                    onValueChange = { newText -> text = newText },
                    label = { Text("Type here") }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    colors = buttonColor,
                    onClick = {
                        isLoading = true
                        CoroutineScope(Dispatchers.IO).launch {
                            clubs = db.clubDao().searchClubs("%$text%")
                            withContext(Dispatchers.Main) {
                                isLoading = false
                            }
                        }
                        Log.d("Clubs", clubs.toString())
                    }
                ) {
                    Text(text = "Search", style = TextStyle(color = Color.Black))
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (isLoading) {
                    Text(text = "Loading...", color = Color.White, fontSize = 20.sp)
                } else {
                    LazyColumn {
                        items(clubs) {club ->
                            Text(text = "Name: ${club.name}", color = Color.White)
                            Text(text = "League: ${club.strLeague}", color = Color.White)
                            Log.d("Link", club.strLogo)
                            val imageBitmap = loadImageFromUrl(club.strLogo)
                            imageBitmap?.let {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = "Club Logo",
                                    modifier = Modifier.size(200.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun loadImageFromUrl(url: String): Bitmap? {
        var bitmap by remember { mutableStateOf<Bitmap?>(null) }
        LaunchedEffect(url) {
            bitmap = withContext(Dispatchers.IO) {
                try {
                    val connection = URL(url).openConnection() as HttpURLConnection
                    connection.doInput = true
                    connection.connect()
                    val input = connection.inputStream
                    BitmapFactory.decodeStream(input)
                } catch (e: Exception) {
                    null
                }
            }
        }
        return bitmap
    }


}

