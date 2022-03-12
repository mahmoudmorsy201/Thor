package com.example.thor.domain.use_case.get_one_call


import com.example.thor.data.remote.dto.toOneCallWeather
import com.example.thor.domain.model.OneCallWeather
import com.example.thor.domain.repository.WeatherRepositoryInterface
import com.example.thor.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetOneCallWeatherUseCase @Inject constructor(
    private val weatherRepositoryInterface: WeatherRepositoryInterface
) {

    operator fun invoke(
        lat: String,
        lon: String,
        language: String,
        units: String,
        apiKey: String
    ): Flow<Resource<OneCallWeather>> = flow {
        try {
            emit(Resource.Loading<OneCallWeather>())
            val oneCallWeather =
                weatherRepositoryInterface.getOneCallWeather(lat, lon, language, units, apiKey)
                    .toOneCallWeather()
            emit(Resource.Success<OneCallWeather>(oneCallWeather))

        } catch (e: HttpException) {
            emit(
                Resource.Error<OneCallWeather>(
                    e.localizedMessage ?: "An unexpected error occurred."
                )
            )
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }

}