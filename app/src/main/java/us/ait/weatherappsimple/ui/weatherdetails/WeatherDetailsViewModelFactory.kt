package us.ait.weatherappsimple.ui.weatherdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import us.ait.weatherappsimple.data.WeatherApiService

class WeatherDetailsViewModelFactory(private val apiService: WeatherApiService) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherDetailsViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
