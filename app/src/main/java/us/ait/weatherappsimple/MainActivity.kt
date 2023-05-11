package us.ait.weatherappsimple



import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleObserver
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import us.ait.weatherappsimple.data.user.UserViewModel
import us.ait.weatherappsimple.ui.citylist.CityListScreen
import us.ait.weatherappsimple.ui.weatherdetails.WeatherDetailsScreen
import us.ait.weatherappsimple.utils.LocationHelper
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity(), LifecycleObserver {
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private val userCity = mutableStateOf("cityList")
    private var isLoggedIn = false



    companion object {
        private const val RC_SIGN_IN = 9001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        isLoggedIn = currentUser != null

        setContentView(ComposeView(this).apply {

            setContent {
                if (isLoggedIn) {
                    // User is already logged in, continue with the app
                    checkAndRequestLocationPermission()
                    WeatherApp(onSignOut = { signOut() })
                } else {
                    // User is not logged in, show the login screen

                        val navController = rememberNavController()
                        LoginScreen(navController = navController)


                }
            }
        })

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    fetchUserLocation()
                } else {
                    Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show()
                }
            }
    }


    @Composable
    fun WeatherApp(onSignOut: () -> Unit) {
        val navController = rememberNavController()
        var isDarkTheme by remember { mutableStateOf(false) }



        MaterialTheme(
            colors = if (isDarkTheme) darkColors() else lightColors(),

        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Your Weather In One Place") },
                        actions = {
                            IconButton(onClick = { onSignOut() }) {
                                Icon(Icons.Filled.ExitToApp, contentDescription = "Logout")
                            }
                            IconButton(
                                onClick = { isDarkTheme = !isDarkTheme },
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Icon(
                                    imageVector = if (isDarkTheme) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                                    contentDescription = "Toggle Theme"
                                )
                            }
                        }
                    )
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = "cityList",
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable("cityList") {
                        CityListScreen(navController, userCity)
                    }
                    composable("weatherDetails/{city}") { backStackEntry ->
                        val city = backStackEntry.arguments?.getString("city")
                        WeatherDetailsScreen(
                            navController = navController,
                            userCity = city,
                            onNavigateToCities = { navController.navigate("citylist") }
                        )
                    }

                }
            }
        }
    }
    @Composable
    fun LoginScreen(navController: NavController) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.weather_logo),
                contentDescription = "Weather App Logo",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Discover Your Weather",
                style = MaterialTheme.typography.h5,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(bottom = 16.dp),
                shape = MaterialTheme.shapes.medium,
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Sign in with Google",
                        style = MaterialTheme.typography.subtitle1,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Button(
                        onClick = { signIn() },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.primary
                        )
                    ) {
                        Text("Sign In")
                    }
                }
            }

//            Card(
//                modifier = Modifier.fillMaxWidth(0.8f),
//                shape = MaterialTheme.shapes.medium,
//                elevation = 4.dp
//            ) {
//                Column(
//                    modifier = Modifier.padding(16.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text(
//                        text = "Sign is as guest",
//                        style = MaterialTheme.typography.subtitle1,
//                        modifier = Modifier.padding(bottom = 16.dp)
//                    )
//                    Button(
//                        onClick = { navController.navigate("weatherDetails/Paris") },
//                        colors = ButtonDefaults.buttonColors(
//                            backgroundColor = MaterialTheme.colors.primary
//                        )
//                    ) {
//                        Text("Guest Login")
//                    }
//                }
//            }
//
//            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "We take your privacy seriously. Your data will not be shared with any third parties.",
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    }



    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)

    }

    private fun signOut() {
        // Firebase sign out
        auth.signOut()

        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this) {
            updateUI(null)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed
                Log.w(TAG, "Google sign in failed", e)
                // Show an error message to the user
                Toast.makeText(this, "Sign in failed. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    userViewModel.saveUserData(user) // Save user data via the UserViewModel
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }


    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            // User is signed in
            Toast.makeText(this, "User signed in: ${user.email}", Toast.LENGTH_SHORT).show()
            isLoggedIn = true
            checkAndRequestLocationPermission()
            setContentView(ComposeView(this).apply {
                setContent {
                    WeatherApp { signOut() }
                }
            })
        } else {
            // User is signed out
            Toast.makeText(this, "User not signed in.", Toast.LENGTH_SHORT).show()
            isLoggedIn = false
            setContentView(ComposeView(this).apply {
                setContent {
                    val navController = rememberNavController()
                    LoginScreen(navController = navController)
                }

            })
        }
    }


    private fun checkAndRequestLocationPermission() {
    when {
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED -> {
            fetchUserLocation()
        }
        shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
            showPermissionExplanationDialog()
        }
        else -> {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}

private fun showPermissionExplanationDialog() {
    AlertDialog.Builder(this)
        .setTitle("Location Permission Required")
        .setMessage("This app requires location permission to show the weather for your current location.")
        .setPositiveButton("OK") { _, _ ->
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        .setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        .show()
}

private fun fetchUserLocation() {
    val locationHelper = LocationHelper(this, this.lifecycle) { location ->
        if (location != null) {
            Log.d(
                "LocationUpdate",
                "Latitude: ${location.latitude}, Longitude: ${location.longitude}"
            )

            getCityName(location.latitude, location.longitude) { cityName ->
                if (cityName != null) {
                    // Set the user city value
                    userCity.value = cityName
                } else {
                    // Handle the case when the city name is not available
                    Toast.makeText(this, "City name not available.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // Handle the case when the location is not available
            Toast.makeText(this, "Location not available.", Toast.LENGTH_SHORT).show()
        }
    }

    lifecycle.addObserver(locationHelper)
    locationHelper.start()
}
    private fun getCityName(latitude: Double, longitude: Double, onCityNameReceived: (String?) -> Unit) {
    val geocoder = Geocoder(this, Locale.getDefault())
    try {
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        if (addresses != null && addresses.isNotEmpty()) {
            val cityName = addresses[0].locality
            onCityNameReceived(cityName)
        } else {
            onCityNameReceived(null)
        }
    } catch (e: IOException) {
        e.printStackTrace()
        onCityNameReceived(null)
    }
}
}



