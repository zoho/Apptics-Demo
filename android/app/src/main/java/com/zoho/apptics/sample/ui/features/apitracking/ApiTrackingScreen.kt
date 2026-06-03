package com.zoho.apptics.sample.ui.features.apitracking

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Tune
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.zoho.apptics.analytics.AppticsApiTracker
import com.zoho.apptics.sample.network.RetrofitClient
import com.zoho.apptics.sample.network.multidomain.RetrofitClientMD
import com.zoho.apptics.sample.ui.components.CodeBlock
import com.zoho.apptics.sample.ui.components.FeatureScaffold
import com.zoho.apptics.sample.ui.components.LiveStatePanel
import com.zoho.apptics.sample.ui.components.RunButton
import com.zoho.apptics.sample.ui.components.SecondaryButton
import com.zoho.apptics.sample.ui.components.SectionCard
import com.zoho.apptics.sample.ui.components.StatusBadge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun ApiTrackingScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()

    var inFlight by remember { mutableStateOf(false) }
    var lastAuto by remember { mutableStateOf("—") }
    var lastNormalized by remember { mutableStateOf("—") }
    var lastManual by remember { mutableStateOf("—") }
    var lastLegacy by remember { mutableStateOf("—") }
    var activeConfig by remember { mutableStateOf("Default — track all, auto-normalize") }

    // Runs a Retrofit suspend call and renders the outcome the way the interceptor sees
    // it: a status code on any response, or code 0 on a network failure.
    fun fire(label: String, call: suspend () -> Response<ResponseBody>) {
        if (inFlight) return
        inFlight = true
        scope.launch {
            val start = System.currentTimeMillis()
            val result = try {
                val resp = call()
                "$label → ${resp.code()} ${resp.message().ifBlank { "" }} (${System.currentTimeMillis() - start} ms)"
            } catch (t: Throwable) {
                // The interceptor records code 0 with the exception message, then re-throws
                // — which is what we catch here. Existing error handling is unaffected.
                "$label → network error, tracked as code 0: ${t.message ?: t.javaClass.simpleName}"
            }
            lastAuto = result
            inFlight = false
        }
    }

    FeatureScaffold(
        title = "API Tracking",
        description = "Measure the success rate and response time of your network calls. Add one " +
            "interceptor and every OkHttp request is tracked automatically — no console " +
            "registration, no annotations. Fire calls below and watch the live state update.",
        onBack = onBack
    ) {
        // 1 — Automatic tracking via the interceptor.
        SectionCard(
            title = "Automatic tracking",
            subtitle = "RetrofitClient installs AppticsApiTrackingInterceptor, so these calls are " +
                "tracked with zero extra code. Try a 200, a 404, and a network error (code 0)."
        ) {
            RunButton(
                label = "GET /movielist.json  (200)",
                icon = Icons.Filled.PlayArrow,
                enabled = !inFlight
            ) {
                fire("GET /movielist.json") {
                    RetrofitClient.getApiService(RetrofitClient.BASE_URL_ONE).getMoviesList()
                }
            }
            RunButton(
                label = "GET /current.json  (404)",
                icon = Icons.Filled.PlayArrow,
                enabled = !inFlight
            ) {
                fire("GET /current.json") {
                    RetrofitClient.getApiService(RetrofitClient.BASE_URL_ONE).checkFor404()
                }
            }
            SecondaryButton(
                label = "GET randomuser /api  (200)",
                icon = Icons.Filled.CheckCircle,
                enabled = !inFlight
            ) {
                fire("GET /api") {
                    RetrofitClient.getApiService(RetrofitClient.BASE_URL_TWO).getProduct()
                }
            }
            SecondaryButton(
                label = "Trigger network error  (code 0)",
                icon = Icons.Filled.CloudOff,
                enabled = !inFlight
            ) {
                fire("GET (unreachable host)") {
                    RetrofitClient.getApiService("https://host.invalid/").getMoviesList()
                }
            }

            CodeBlock(
                title = "How it's wired",
                code = """
                    val client = OkHttpClient.Builder()
                        .addInterceptor(AppticsApiTrackingInterceptor())
                        .build()
                    // That's all — every request through `client` is now tracked.
                """.trimIndent()
            )
        }

        // 2 — Endpoint normalization.
        SectionCard(
            title = "Endpoint normalization",
            subtitle = "Dynamic path segments are grouped so /posts/1 and /posts/2 report as a " +
                "single /posts/* endpoint. Fire a request with a numeric id to see it."
        ) {
            RunButton(
                label = "GET /posts/42  →  /posts/*",
                icon = Icons.Filled.Route,
                enabled = !inFlight
            ) {
                if (inFlight) return@RunButton
                inFlight = true
                scope.launch {
                    lastNormalized = try {
                        val resp = RetrofitClient.getApiService(RetrofitClient.BASE_URL_THREE).getPost(42)
                        "GET /posts/42 → ${resp.code()}; recorded as /posts/*"
                    } catch (t: Throwable) {
                        "GET /posts/42 → error: ${t.message ?: t.javaClass.simpleName}"
                    }
                    inFlight = false
                }
            }
            SecondaryButton(
                label = "GET /posts/42/comments  →  /posts/*/comments",
                icon = Icons.Filled.Route,
                enabled = !inFlight
            ) {
                if (inFlight) return@SecondaryButton
                inFlight = true
                scope.launch {
                    lastNormalized = try {
                        val resp = RetrofitClient.getApiService(RetrofitClient.BASE_URL_THREE).getPostComments(42)
                        "GET /posts/42/comments → ${resp.code()}; recorded as /posts/*/comments"
                    } catch (t: Throwable) {
                        "GET /posts/42/comments → error: ${t.message ?: t.javaClass.simpleName}"
                    }
                    inFlight = false
                }
            }

            CodeBlock(
                title = "Raw URL → recorded path",
                code = """
                    GET /users/42/orders          →  /users/*/orders
                    GET /users/<uuid>/profile     →  /users/*/profile
                    DELETE /sessions/<jwt>        →  /sessions/*
                    GET /products/search          →  /products/search   (static, unchanged)

                    // Numeric IDs, UUIDs and JWTs are auto-detected. Query strings are stripped.
                """.trimIndent()
            )
            CodeBlock(
                title = "Customize normalization",
                code = """
                    AppticsApiTracker.configure {
                        // Explicit pattern wins over auto-detection:
                        addPattern("/v1/accounts/{accountId}/users/{userId}")
                        // Keep meaningful segments that look dynamic:
                        preserveSegments("v1", "v2")
                    }
                """.trimIndent()
            )
        }

        // 3 — Filtering / configure DSL (applied at runtime).
        SectionCard(
            title = "Filtering (configure DSL)",
            subtitle = "configure {} controls which requests are tracked. Each call replaces the " +
                "previous config entirely. Apply one, then re-fire calls above to see the effect."
        ) {
            SecondaryButton(label = "Track all (reset)", icon = Icons.Filled.Tune) {
                AppticsApiTracker.configure { }
                activeConfig = "Default — track all, auto-normalize"
            }
            SecondaryButton(label = "Allow only randomuser.me", icon = Icons.Filled.FilterAlt) {
                AppticsApiTracker.configure { allowOnlyDomains("randomuser.me") }
                activeConfig = "allowOnlyDomains(\"randomuser.me\") — others skipped"
            }
            SecondaryButton(label = "Ignore /movielist.json", icon = Icons.Filled.FilterAlt) {
                AppticsApiTracker.configure { ignoreEndpoint("/movielist.json") }
                activeConfig = "ignoreEndpoint(\"/movielist.json\")"
            }
            SecondaryButton(label = "Group regional domains", icon = Icons.Filled.FilterAlt) {
                AppticsApiTracker.configure { groupDomains("api.myapp.*") }
                activeConfig = "groupDomains(\"api.myapp.*\")"
            }

            CodeBlock(
                title = "All options combine in one block",
                code = """
                    AppticsApiTracker.configure {
                        allowOnlyDomains("api.yourapp.com", "api.yourapp.in")
                        groupDomains("api.yourapp.*")
                        ignoreEndpoint("/health", "/ping", "/internal/**")
                        addPattern("/v2/catalog/{categoryId}/items/{itemId}")
                        preserveSegments("v1", "v2")
                    }
                """.trimIndent()
            )
        }

        // 4 — Manual tracking for non-OkHttp clients.
        SectionCard(
            title = "Manual tracking (non-OkHttp)",
            subtitle = "Using a different networking library? Wrap the call in startTrackApi / " +
                "endTrackApi. The same normalization and filtering rules apply."
        ) {
            RunButton(
                label = "Run manual tracked GET",
                icon = Icons.Filled.PlayArrow,
                enabled = !inFlight
            ) {
                if (inFlight) return@RunButton
                inFlight = true
                scope.launch {
                    lastManual = withContext(Dispatchers.IO) {
                        val url = "${RetrofitClient.BASE_URL_THREE}posts/1"
                        // startTrackApi returns a per-call id; -1 means the URL was filtered
                        // out, and endTrackApi is a safe no-op on -1, so no guard is needed.
                        val trackId = AppticsApiTracker.startTrackApi(url, "GET")
                        try {
                            val conn = (URL(url).openConnection() as HttpURLConnection)
                            conn.requestMethod = "GET"
                            val code = conn.responseCode
                            val message = conn.responseMessage ?: ""
                            conn.disconnect()
                            AppticsApiTracker.endTrackApi(trackId, code, message)
                            "trackId=$trackId → $code $message"
                        } catch (t: Throwable) {
                            AppticsApiTracker.endTrackApi(trackId, 0, t.message ?: "")
                            "trackId=$trackId → error (code 0): ${t.message ?: t.javaClass.simpleName}"
                        }
                    }
                    inFlight = false
                }
            }

            CodeBlock(
                title = "Manual flow",
                code = """
                    val trackId = AppticsApiTracker.startTrackApi(url, "GET")
                    // ... make your network call ...
                    AppticsApiTracker.endTrackApi(trackId, responseCode, responseMessage)
                """.trimIndent()
            )
        }

        // 5 — Backward compatibility (deprecated).
        SectionCard(
            title = "Backward compatibility (deprecated)",
            subtitle = "The old @TrackApiWith annotation and apiId-based startTrackApi still work, " +
                "but are no longer required. This call uses the legacy multi-domain interceptor."
        ) {
            SecondaryButton(
                label = "Run legacy (apiId) tracked call",
                icon = Icons.Filled.PlayArrow,
                enabled = !inFlight
            ) {
                if (inFlight) return@SecondaryButton
                inFlight = true
                scope.launch {
                    lastLegacy = try {
                        val resp = RetrofitClientMD
                            .getApiService(RetrofitClientMD.BASE_URL_ONE)
                            .getMoviesList()
                        "legacy GET /movielist.json → ${resp.code()}"
                    } catch (t: Throwable) {
                        "legacy call error: ${t.message ?: t.javaClass.simpleName}"
                    }
                    inFlight = false
                }
            }

            CodeBlock(
                title = "Migration: before → after",
                code = """
                    // Before (deprecated)
                    @TrackApiWith(apiId = 123456L)
                    @GET("users/{id}")
                    suspend fun getUser(@Path("id") id: String): User

                    // After (recommended) — just remove the annotation
                    @GET("users/{id}")
                    suspend fun getUser(@Path("id") id: String): User
                """.trimIndent()
            )
        }

        StatusBadge(on = !inFlight, label = if (inFlight) "Request in flight…" else "Idle")

        LiveStatePanel(
            rows = listOf(
                "Active config" to activeConfig,
                "Last automatic call" to lastAuto,
                "Last normalized call" to lastNormalized,
                "Last manual call" to lastManual,
                "Last legacy call" to lastLegacy
            )
        )
    }
}
