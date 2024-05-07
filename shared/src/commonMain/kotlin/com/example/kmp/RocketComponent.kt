import co.touchlab.kermit.Logger
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.*
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json

class RocketComponent {
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    private suspend fun getDateOfLastSuccessfulLaunch(): String {
        val rockets: List<RocketLaunch> =
            httpClient.get("https://api.spacexdata.com/v4/launches").body()
        Logger.d { rockets.toString() }
        val lastSuccessLaunch = rockets.last { it.launchSuccess == true }
        Logger.i { lastSuccessLaunch.toString() }
        val date = Instant.parse(lastSuccessLaunch.launchDateUTC)
            .toLocalDateTime(TimeZone.currentSystemDefault())
        return "${date.month} ${date.dayOfMonth}, ${date.year}"
    }

    suspend fun launchPhrase(): String =
        try {
            "The last successful launch was on ${getDateOfLastSuccessfulLaunch()} 🚀"
        } catch (e: Exception) {
            Logger.e("Exception during getting the date of the last successful launch $e")
            "Error occurred"
        }

}


