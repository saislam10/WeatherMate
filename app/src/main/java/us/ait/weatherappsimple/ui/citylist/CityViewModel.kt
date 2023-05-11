package us.ait.weatherappsimple.ui.citylist

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class CityViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _cityList = mutableStateListOf<String>()
    val cityList: SnapshotStateList<String> get() = _cityList

    init {
        // Load city data from Firestore when the ViewModel is created
        loadCityData()
    }

    private fun loadCityData() {
        val user: FirebaseUser? = auth.currentUser
        val userId: String? = user?.uid

        if (userId != null) {
            db.collection("cities").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val cityData = document.get("cityList") as? List<String>
                        _cityList.clear()
                        _cityList.addAll(cityData ?: emptyList())
                    }
                }
                .addOnFailureListener { e ->
                    // Error occurred while loading data
                }
        }
    }

    fun addCity(cityName: String) {
        _cityList.add(cityName)
        saveCityData()
    }

    fun removeCity(cityName: String) {
        _cityList.remove(cityName)
        saveCityData()
    }

    fun clearCities() {
        _cityList.clear()
        saveCityData()
    }

    fun saveCityData() {
        val user: FirebaseUser? = auth.currentUser
        val userId: String? = user?.uid

        if (userId != null) {
            val userCityData = hashMapOf(
                "userId" to userId,
                "cityList" to _cityList.toList()
            )

            db.collection("cities").document(userId)
                .set(userCityData)
                .addOnSuccessListener {
                    // Data saved successfully
                }
                .addOnFailureListener { e ->
                    // Error occurred while saving data
                }
        }
    }
}
