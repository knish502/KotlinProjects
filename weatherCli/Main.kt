import java.net.URL
import java.net.HttpURLConnection
import java.io.InputStreamReader
import java.io.BufferedReader

fun main(args: Array<String>){
    val latitude = 34.7304
    val longitude = -86.5861

    val urlString = "https://api.weather.gov/points/$latitude,$longitude"
    val connection = URL(urlString).openConnection() as HttpURLConnection

    val token = "single_user_test"

    connection.requestMethod = "GET"
    connection.setRequestProperty("User-Agent", token)

    val responseCode = connection.getResponseCode()
    println("HTTP Response Code: $responseCode")

    if (responseCode == HttpURLConnection.HTTP_OK){

        val inputStream = connection.inputStream
        val reader = BufferedReader(InputStreamReader(inputStream))

        val response = reader.readText()
        reader.close()

        println("Response:\n$response")

        
    }

    connection.disconnect()

}