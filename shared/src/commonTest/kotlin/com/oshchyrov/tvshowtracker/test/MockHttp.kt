package com.oshchyrov.tvshowtracker.test

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun createMockHttpClient(
    handler: suspend (String) -> Pair<Int, String>,
): HttpClient = HttpClient(
    MockEngine { request ->
        val (status, body) = handler(request.url.encodedPath)
        if (status >= 400) {
            respondError(HttpStatusCode.fromValue(status), body)
        } else {
            respond(
                content = body,
                status = HttpStatusCode.fromValue(status),
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
    },
) {
    expectSuccess = true
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true; isLenient = true })
    }
}

const val SAMPLE_SHOW_JSON = """
[
  {
    "id": 1,
    "name": "Under the Dome",
    "summary": "<p>Small town drama.</p>",
    "genres": ["Drama", "Sci-Fi"],
    "rating": { "average": 6.5 },
    "runtime": 43,
    "language": "English",
    "status": "Ended",
    "premiered": "2013-06-24"
  }
]
"""

const val SAMPLE_SEARCH_JSON = """
[
  {
    "score": 0.9,
    "show": {
      "id": 169,
      "name": "Breaking Bad",
      "summary": "<p>Chemistry teacher.</p>",
      "genres": ["Drama"]
    }
  }
]
"""

const val SAMPLE_SHOW_DETAIL_JSON = """
{
  "id": 169,
  "name": "Breaking Bad",
  "summary": "<p>Chemistry teacher.</p>",
  "genres": ["Drama", "Crime"],
  "rating": { "average": 9.5 },
  "runtime": 60,
  "language": "English",
  "status": "Ended",
  "premiered": "2008-01-20",
  "network": { "name": "AMC", "country": { "name": "United States" } }
}
"""

const val SAMPLE_EPISODES_JSON = """
[
  { "id": 1, "name": "Pilot", "season": 1, "number": 1, "airdate": "2008-01-20", "runtime": 58 },
  { "id": 2, "name": "Special", "season": 0, "number": 1 },
  { "id": 3, "name": "Cat's in the Bag", "season": 1, "number": 2, "airdate": "2008-01-27", "runtime": 48 }
]
"""
