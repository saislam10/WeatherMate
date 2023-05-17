package us.ait.weatherappsimple.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("data/2.5/weather")
    suspend fun getWeatherByCity(
        @Query("q") cityName: String,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String = "75710d05bc5b9e7659059ed56998bee3"
    ): Response<WeatherResponse>
    @GET("data/2.5/forecast")
    suspend fun getForecastByCity(
        @Query("q") cityName: String,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String = "75710d05bc5b9e7659059ed56998bee3",
        @Query("cnt") count: Int = 32 // get forecast for next 3 days
    ): Response<ForecastResponse>

}

