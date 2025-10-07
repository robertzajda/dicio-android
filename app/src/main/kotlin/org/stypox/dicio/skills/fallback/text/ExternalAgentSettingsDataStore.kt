package org.stypox.dicio.skills.fallback.text

import android.content.Context
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStore
import androidx.datastore.migrations.SharedPreferencesMigration

private const val SETTINGS_FILE_NAME = "skill_settings_external_agent.pb"
internal const val KEY_EXTERNAL_AGENT_URL = "external_agent_url"
internal const val KEY_EXTERNAL_AGENT_API_KEY = "external_agent_api_key"

internal val Context.externalAgentDataStore by dataStore(
    fileName = SETTINGS_FILE_NAME,
    serializer = SkillSettingsExternalAgentSerializer,
    corruptionHandler = ReplaceFileCorruptionHandler {
        SkillSettingsExternalAgentSerializer.defaultValue
    },
    produceMigrations = { context ->
        listOf(
            SharedPreferencesMigration(
                context,
                context.packageName + "_preferences",
            ) { prefs, current ->
                current.toBuilder()
                    .setUrl(prefs.getString(KEY_EXTERNAL_AGENT_URL, "")?.trim().orEmpty())
                    .setApiKey(prefs.getString(KEY_EXTERNAL_AGENT_API_KEY, "")?.trim().orEmpty())
                    .build()
            }
        )
    },
)
