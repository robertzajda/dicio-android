package org.stypox.dicio.skills.fallback.text

import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.dicio.skill.context.SkillContext
import org.dicio.skill.skill.SkillInfo
import org.dicio.skill.skill.SkillOutput
import org.json.JSONException
import org.json.JSONObject
import org.stypox.dicio.R
import org.stypox.dicio.util.RecognizeEverythingSkill
import org.stypox.dicio.util.getString
import java.io.IOException

class ExternalAgentSkill(correspondingSkillInfo: SkillInfo) :
    RecognizeEverythingSkill(correspondingSkillInfo) {

    override suspend fun generateOutput(ctx: SkillContext, inputData: String): SkillOutput {
        val preferences = PreferenceManager.getDefaultSharedPreferences(ctx.android)
        val url = preferences.getString(KEY_EXTERNAL_AGENT_URL, "")?.trim().orEmpty()
        if (url.isBlank()) {
            return ExternalAgentOutput(ctx.getString(R.string.external_agent_missing_url))
        }
        val apiKey =
            preferences.getString(KEY_EXTERNAL_AGENT_API_KEY, "")?.trim().orEmpty()

        return withContext(Dispatchers.IO) {
            try {
                val requestBody = JSONObject().apply {
                    put("text", inputData)
                }.toString().toRequestBody(JSON_MEDIA_TYPE)

                val requestBuilder = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .header("Content-Type", JSON_CONTENT_TYPE)

                if (apiKey.isNotEmpty()) {
                    requestBuilder.header("Authorization", "Bearer $apiKey")
                }

                httpClient.newCall(requestBuilder.build()).execute().use { response ->
                    val body = response.body?.string().orEmpty()

                    if (!response.isSuccessful) {
                        return@withContext ExternalAgentOutput(
                            ctx.getString(
                                R.string.external_agent_http_error,
                                response.code
                            )
                        )
                    }

                    if (body.isBlank()) {
                        return@withContext ExternalAgentOutput(
                            ctx.getString(R.string.external_agent_empty_response)
                        )
                    }

                    val content = parseMessageContent(body)
                        ?: return@withContext ExternalAgentOutput(
                            ctx.getString(R.string.external_agent_invalid_response)
                        )

                    ExternalAgentOutput(content.trim())
                }
            } catch (exception: IOException) {
                ExternalAgentOutput(
                    ctx.getString(
                        R.string.external_agent_request_failed,
                        exception.localizedMessage ?: exception.javaClass.simpleName
                    )
                )
            } catch (exception: JSONException) {
                ExternalAgentOutput(
                    ctx.getString(
                        R.string.external_agent_invalid_response_with_reason,
                        exception.localizedMessage ?: exception.javaClass.simpleName
                    )
                )
            } catch (exception: IllegalArgumentException) {
                ExternalAgentOutput(
                    ctx.getString(
                        R.string.external_agent_request_failed,
                        exception.localizedMessage ?: exception.javaClass.simpleName
                    )
                )
            }
        }
    }

    private fun parseMessageContent(body: String): String? {
        return try {
            val json = JSONObject(body)
            json.optJSONArray("choices")
                ?.optJSONObject(0)
                ?.optJSONObject("message")
                ?.optString("content")
                ?.takeIf { it.isNotBlank() }
        } catch (_: JSONException) {
            null
        }
    }

    companion object {
        private const val KEY_EXTERNAL_AGENT_URL = "external_agent_url"
        private const val KEY_EXTERNAL_AGENT_API_KEY = "external_agent_api_key"
        private const val JSON_CONTENT_TYPE = "application/json"
        private val JSON_MEDIA_TYPE = "$JSON_CONTENT_TYPE; charset=utf-8".toMediaType()
        private val httpClient = OkHttpClient()
    }
}
