package us.ait.weatherappsimple.data
data class WeatherResponse(
    val weather: List<Weather>,
    val main: Main,
    val sys: Sys,
    val coord: Coord,
    val name: String
) {
    data class Weather(
        val id: Int,
        val main: String,
        val description: String,
        val icon: String
    )

    data class Main(
        val temp: Float,
        val temp_min: Float,
        val temp_max: Float,
        val humidity: Int
    )

    data class Sys(
        val country: String,
        val sunrise: Long,
        val sunset: Long
    )

    data class Coord(
        val lon: Double,
        val lat: Double
    )
}

data class ForecastResponse(
    val list: List<ForecastData>
) {
    data class ForecastData(
        val dt_txt: String,
        val main: Main,
        val weather: List<Weather>
    ) {
        data class Main(
            val temp: Float,
//            val temp_min: Float,
//            val temp_max: Float,
//            val humidity: Int
        )

        data class Weather(
//            val id: String,
//            val main: String,
            val description: String,
//            val icon: String
        )
    }
}
