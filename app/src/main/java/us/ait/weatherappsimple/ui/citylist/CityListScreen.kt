package us.ait.weatherappsimple.ui.citylist


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Map
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import us.ait.weatherappsimple.R
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CityListScreen(navController: NavController, userCity: MutableState<String>) {
    val cityViewModel: CityViewModel = viewModel()
    var showDialog by remember { mutableStateOf(false) }
    var newCity by remember { mutableStateOf("") }

    Scaffold(
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (cityViewModel.cityList.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_city_added),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        style = MaterialTheme.typography.h5
                    )
                } else {
                    LazyColumn {
                        items(cityViewModel.cityList) { cityName ->
                            CityListItem(
                                cityName = cityName,
                                onDeleteClick = { cityViewModel.removeCity(cityName) },
                                onClick = { navController.navigate("weatherDetails/$cityName") }
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                FloatingActionButton(
                    onClick = { navController.navigate("weatherDetails/${userCity.value}") },
//                    backgroundColor = Color.Blue,
                    content = {
                        Icon(
                            Icons.Filled.Map,
                            contentDescription = stringResource(R.string.map),
//                            tint = Color.White
                        )
                    }
                )

                Spacer(modifier = Modifier.width(16.dp))
                FloatingActionButton(
                    onClick = { cityViewModel.clearCities() },
                    backgroundColor = Color.Red,
                    content = {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = stringResource(R.string.delete_all)
                        )
                    }
                )

                Spacer(modifier = Modifier.width(16.dp))
                FloatingActionButton(
                    onClick = { showDialog = true },
                    content = {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = stringResource(R.string.add_city)
                        )
                    }
                )
            }
        }
    )


    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.add_city),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            },
            text = {
                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)

                ) {
                    TextField(
                        value = newCity,
                        onValueChange = { newCity = it },
                        label = { Text(stringResource(R.string.enter_city_name)) },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            },
            buttons = {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Button(
                            onClick = { showDialog = false },
                            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary)
                        ) {
                            Text(text = stringResource(R.string.cancel))
                        }

                        Button(
                            onClick = {
                                cityViewModel.addCity(newCity)
                                newCity = ""
                                showDialog = false
                                cityViewModel.saveCityData() // Save the city data
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
                        ) {
                            Text(text = stringResource(R.string.add))
                        }
                    }
                }
            }
        )
    }
}


