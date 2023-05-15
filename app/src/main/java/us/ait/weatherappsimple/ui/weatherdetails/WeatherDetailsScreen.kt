package us.ait.weatherappsimple.ui.weatherdetails

import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import androidx.constraintlayout.compose.ConstraintLayout
import us.ait.weatherappsimple.data.ForecastResponse


import us.ait.weatherappsimple.data.RetrofitInstance
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import us.ait.weatherappsimple.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@Composable
fun WeatherDetailsScreen(
    navController: NavController,
    userCity: String?,
    viewModel: WeatherDetailsViewModel = viewModel(factory = WeatherDetailsViewModelFactory(
        RetrofitInstance.apiService)),
    onNavigateToCities: () -> Unit
) {
    val weatherResult by viewModel.weatherResult
    val forecastResult by viewModel.forecastResult

    println(weatherResult)

    LaunchedEffect(userCity) {
        userCity?.let {
            viewModel.fetchWeatherData(it)
            viewModel.fetchForecastData(it)
        }
    }

    if (weatherResult != null) {
        val cityCoordinates = LatLng(
            weatherResult!!.coord.lat,
            weatherResult!!.coord.lon
        ) // get coordinates of the city

        var showDialog by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colors.surface)
                    .border(2.dp, MaterialTheme.colors.primary, RoundedCornerShape(16.dp)),
                elevation = 8.dp,
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = weatherResult!!.name,
                        style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    println("https://openweathermap.org/img/w/${weatherResult!!.weather[0].icon}.png")
                    Image(
                        painter = rememberImagePainter(
                            data = "https://openweathermap.org/img/w/${weatherResult!!.weather[0].icon}.png"
                        ),
                        contentDescription = "Weather Icon",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(100.dp).clip(CircleShape)
                    )
                    Text(
                        text = "Temperature: ${weatherResult!!.main.temp}°C",
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Text(
                        text = "Description: ${weatherResult!!.weather[0].description}",
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (forecastResult != null) {
                LazyColumn {
                    itemsIndexed(forecastResult.orEmpty()) { index, forecast ->
                        ForecastCard(forecast = forecast)
                    }
                }
            } else {
                Text("Loading forecast data...")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                content = {
                    Button(
                        onClick = { showDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp)
                            .height(48.dp)
                            .clip(RoundedCornerShape(24.dp))
                    ) {
                        Text(
                            text = "View Map",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Button(
                        onClick = onNavigateToCities,
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp)
                            .height(48.dp)
                            .clip(RoundedCornerShape(24.dp))
                    ) {
                        Text(
                            text = "My Cities",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            )

            MapDialog(cityCoordinates = cityCoordinates, showDialog = showDialog) { showDialog = false }
        }

    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(48.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Your city may not exist, please go back and enter a valid city",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}



@Composable
fun MapDialog(cityCoordinates: LatLng, showDialog: Boolean, onDismiss: () -> Unit) {
    if (showDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Box(modifier = Modifier.fillMaxSize()) {
                val mapView = rememberMapViewWithLifecycle()
                AndroidView({ mapView }, Modifier.fillMaxSize()) { mapView ->
                    mapView.getMapAsync { googleMap ->
                        googleMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                cityCoordinates,
                                10f
                            )
                        )
                    }
                }

                IconButton(
                    onClick = { onDismiss() },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
            }
        }
    }
}

@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            // Pre-initialize the MapView here to avoid delay on first render
            onCreate(Bundle())
        }
    }

    DisposableEffect(Unit) {
        mapView.onStart()
        mapView.onResume()

        onDispose {
            mapView.onPause()
            mapView.onStop()
            mapView.onDestroy()
        }
    }

    return mapView
}


@Composable
fun ForecastCard(forecast: ForecastResponse.ForecastData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            val dateTime = LocalDateTime.parse(forecast.dt_txt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            val dayOfWeek = dateTime.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())

            Text(
                text = "$dayOfWeek's Midday Weather",
                style = MaterialTheme.typography.h6
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Temperature: ${forecast.main.temp}°C",
                style = MaterialTheme.typography.body1
            )
            Text(
                text = "Description: ${forecast.weather[0].description}",
                style = MaterialTheme.typography.body1
            )
        }
    }
}

