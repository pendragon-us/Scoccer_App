package com.example.scoccerapplication.button_4

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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.scoccerapplication.R
import com.example.scoccerapplication.button2_SearchForClubsByLeague.Club
import com.example.scoccerapplication.button_4.ui.theme.ScoccerApplicationTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ButtonFourScreen : ComponentActivity() {

    private var jerseyUrlList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
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
        var scope = rememberCoroutineScope()

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
                        scope.launch {
                            isLoading = true
                            getCountryData(text)
                            isLoading = false
                        }
                    }
                ) {
                    Text(text = "Search", style = TextStyle(color = Color.Black))
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (isLoading) {
                    Text(text = "Loading...", color = Color.White, fontSize = 20.sp)
                } else {
                    LazyColumn {
                        items(jerseyUrlList) {url ->
                            val imageBitmap = loadImageFromUrl(url)
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

    private suspend fun getCountryData(partialName: String) {
        try {
            val countryDataApi = "https://www.thesportsdb.com/api/v1/json/3/all_countries.php"
            val url = URL(countryDataApi)
            val con: HttpURLConnection =
                withContext(Dispatchers.IO) {
                    url.openConnection()
                } as HttpURLConnection

            val stb = StringBuilder()

            withContext(Dispatchers.IO) {
                val bf = BufferedReader(InputStreamReader(con.inputStream))
                var line: String? = bf.readLine()
                while (line != null) {
                    stb.append(line + "\n")
                    line = bf.readLine()
                }
            }
            countryDataDivider(stb, partialName)
        } catch (_: Throwable) {
        }
    }

    private suspend fun countryDataDivider(stb: StringBuilder, partialName: String) {

        val json =
            JSONObject(stb.toString())

        val jsonArray: JSONArray = json.getJSONArray("countries")

        for (i in 0..<jsonArray.length()) {
            val teams: JSONObject = jsonArray[i] as JSONObject

            val teamID = teams["name_en"].toString()
            getFootballLeagueTeamData(
                "c=${teamID}&s=Soccer",
                partialName
            )
        }
    }

    private suspend fun getFootballLeagueTeamData(
        keyword: String,
        partialName: String
    ) {

        try {
            val teamDataApi =
                "https://www.thesportsdb.com/api/v1/json/3/search_all_teams.php?$keyword"
            val url = URL(teamDataApi)
            val con: HttpURLConnection =
                withContext(Dispatchers.IO) {
                    url.openConnection()
                } as HttpURLConnection

            val stb = StringBuilder()

            withContext(Dispatchers.IO) {
                val bf = BufferedReader(InputStreamReader(con.inputStream))
                var line: String? = bf.readLine()
                while (line != null) {
                    stb.append(line + "\n")
                    line = bf.readLine()
                }
            }
            jsonTeamDataDivider(stb, partialName)
        } catch (_: Throwable) {

        }
    }

    private suspend fun jsonTeamDataDivider(
        stb: StringBuilder,
        partialName: String
    ) {

        try {

            val json =
                JSONObject(stb.toString())


            val jsonArray: JSONArray = json.getJSONArray("teams")

            for (i in 0..<jsonArray.length()) {
                val teams: JSONObject = jsonArray[i] as JSONObject

                val teamID = teams["idTeam"].toString()
                val name = teams["strTeam"].toString()

                val jerseyUrl: MutableList<String>
                if (name.lowercase().contains(partialName.lowercase())) {
                    jerseyUrl = getClubJerseyData(teamID)
                    jerseyUrlList.clear()
                    jerseyUrlList.addAll(jerseyUrl)
                    for(jersey in jerseyUrlList){
                        Log.d("Jersey", jersey)
                    }
                }
            }
        } catch (_: Throwable) {

        }

    }

    private suspend fun getClubJerseyData(keyword: String): MutableList<String> {

        val emptyList = mutableListOf<String>()

        try {
            val teamEquipmentApi =
                "https://www.thesportsdb.com/api/v1/json/3/lookupequipment.php?id=$keyword"
            val url = URL(teamEquipmentApi)
            val con: HttpURLConnection =
                withContext(Dispatchers.IO) {
                    url.openConnection()
                } as HttpURLConnection

            val stb = StringBuilder()

            withContext(Dispatchers.IO) {
                val bf = BufferedReader(InputStreamReader(con.inputStream))
                var line: String? = bf.readLine()
                while (line != null) {
                    stb.append(line + "\n")
                    line = bf.readLine()

                }
            }
            return jsonJerseyDataDivider(stb)
        } catch (t: Throwable) {
            return emptyList
        }
    }

    private fun jsonJerseyDataDivider(stb: StringBuilder): MutableList<String> {

        val jerseyUrlList = mutableListOf<String>()

        try {

            val json =
                JSONObject(stb.toString())

            val jsonArray: JSONArray = json.getJSONArray("equipment")

            for (i in 0..<jsonArray.length()) {
                val equipmentArray: JSONObject =
                    jsonArray[i] as JSONObject

                val jerseyUrl = equipmentArray["strEquipment"].toString()
                jerseyUrlList.add(jerseyUrl)

            }
            return jerseyUrlList
        } catch (t: Throwable) {
            return jerseyUrlList
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
