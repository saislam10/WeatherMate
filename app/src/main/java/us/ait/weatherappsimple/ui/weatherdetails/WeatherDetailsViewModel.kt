package us.ait.weatherappsimple.ui.weatherdetails

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.State
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import us.ait.weatherappsimple.data.ForecastResponse.ForecastData
import us.ait.weatherappsimple.data.RetrofitInstance
import us.ait.weatherappsimple.data.WeatherApiService
import us.ait.weatherappsimple.data.WeatherResponse
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WeatherDetailsViewModel(private val apiService: WeatherApiService) : ViewModel() {
    private var _weatherResult = mutableStateOf<WeatherResponse?>(null)
    val weatherResult: State<WeatherResponse?> get() = _weatherResult
    val forecastResult = mutableStateOf<List<ForecastData>?>(null)

    val weatherIcon: MutableStateFlow<Bitmap?> = MutableStateFlow(null)


    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?>
        get() = _errorMessage

    fun fetchWeatherData(city: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getWeatherByCity(city)
                if (response.isSuccessful) {
                    _weatherResult.value = response.body()
                } else {
                    _errorMessage.value = "Error: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Exception: ${e.message}"
            }
        }
    }
    fun fetchForecastData(cityName: String) {
        viewModelScope.launch {
            val response = RetrofitInstance.apiService.getForecastByCity(cityName)
            if (response.isSuccessful) {
                val allForecasts = response.body()?.list
                val noonForecasts = allForecasts?.filter {
                    val dateTime = LocalDateTime.parse(it.dt_txt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    dateTime.hour == 12 && !dateTime.toLocalDate().isEqual(LocalDate.now())
                }
                forecastResult.value = noonForecasts?.map {
                    ForecastData(
                        dt_txt = it.dt_txt,
                        main = ForecastData.Main(it.main.temp),
                        weather = listOf(ForecastData.Weather(it.weather[0].description)),

                    )
                }
            }
        }
    }
//    fun fetchWeatherIcon(icon: String) {
//        viewModelScope.launch {
//            val bitmap = fetchImageAsBitmap("https://openweathermap.org/img/w/$icon.png")
//            weatherIcon.value = bitmap
//        }
//    }
//
//    private suspend fun fetchImageAsBitmap(url: String): Bitmap? {
//        val request = Request.Builder()
//            .url(url)
//            .build()
//
//        return withContext(Dispatchers.IO) {
//            OkHttpClient().newCall(request).execute().use { response ->
//                if (!response.isSuccessful) throw IOException("Unexpected code $response")
//
//                val inputStream = response.body?.byteStream()
//                val bitmap = BitmapFactory.decodeStream(inputStream)
//                inputStream?.close()
//
//                return@withContext bitmap
//            }
//        }
//    }







}
