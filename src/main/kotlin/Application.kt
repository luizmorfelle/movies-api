package com.univali

import com.google.gson.Gson
import com.univali.models.ApiResponse
import com.univali.models.omdb.OMDbResponseModel
import com.univali.models.tmdb.TmdbResponseModel
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

const val OMDB_URL = "http://www.omdbapi.com/"
const val OMDB_API_KEY = "3b58340e"

const val TMDB_URL = "https://api.themoviedb.org/3/search/movie"
const val TMDB_API_KEY =
    "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJlYzY3YjllNWZmOGM2MzZiZDhlYjNlODRiODVlNTFjMCIsIm5iZiI6MTcxMjY3NDkzMC4yNDg5OTk4LCJzdWIiOiI2NjE1NTg3MjMzYTUzMzAxN2Q4NzAwYzAiLCJzY29wZXMiOlsiYXBpX3JlYWQiXSwidmVyc2lvbiI6MX0.NPg3d2YwCGvzKTY6ALdgGBKz8JQRgZjfmf8wyGUaAF8"

val client = HttpClient(CIO)

val gson = Gson()

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    install(ContentNegotiation) {
        gson()
    }
    routing {
        get("/") {
            println("Starting service!")
            val movieParameter = call.queryParameters["movie"]
            val yearParameter = call.queryParameters["year"]

            if (movieParameter == null || yearParameter == null) {
                call.respond(HttpStatusCode.BadRequest, "Movie name and year is required")
            }

            val response = fetchApis(movieParameter!!, yearParameter!!)
            println("Finishing service!")
            call.respond(HttpStatusCode.OK, response)
        }
    }
}

suspend fun fetchApis(movie: String, year: String): ApiResponse = coroutineScope {
    val omdbDeferred = async { fetchOmdb(movie, year) }
    val tmdbDeferred = async { fetchTmdb(movie, year) }

    val omdbResponse: OMDbResponseModel = omdbDeferred.await()
    val tmdbResponse: TmdbResponseModel = tmdbDeferred.await()

    ApiResponse().apply {
        titulo = omdbResponse.title
        ano = omdbResponse.year?.toInt()
        sinopse = tmdbResponse.results.first().overview
        reviews = omdbResponse.ratings.map { it.source + " - " + it.value }.toList().take(3)
    }
}

suspend fun fetchOmdb(movie: String, year: String): OMDbResponseModel {
    println("Starting fetching movie on Omdb")
    val omDbResponseModel: String = client.get(OMDB_URL) {
        parameter("apikey", OMDB_API_KEY)
        parameter("t", movie)
        parameter("y", year)
    }.body()
    println("Finished fetching movie on Omdb")
    return gson.fromJson(omDbResponseModel, OMDbResponseModel::class.java)
}

suspend fun fetchTmdb(movie: String, year: String): TmdbResponseModel {
    println("Starting fetching movie on Tmdb")
    val tmdbResponseModel: String = client.get(TMDB_URL) {
        header("Authorization", "Bearer $TMDB_API_KEY")
        parameter("include_adult", "true")
        parameter("language", "en-US")
        parameter("page", "1")
        parameter("year", year)
        parameter("query", movie)
    }.body()
    println("Finished fetching movie on Tmdb")
    return gson.fromJson(tmdbResponseModel, TmdbResponseModel::class.java)
}