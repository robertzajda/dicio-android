package org.stypox.dicio.settings.externalagent

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.stypox.dicio.R
import org.stypox.dicio.settings.ui.SettingsCategoryTitle
import org.stypox.dicio.settings.ui.StringSetting
import org.stypox.dicio.skills.fallback.text.SkillSettingsExternalAgent
import org.stypox.dicio.skills.fallback.text.externalAgentDataStore

@Composable
fun ExternalAgentSettingsRoute(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val dataStore = context.externalAgentDataStore
    val data by dataStore.data.collectAsState(SkillSettingsExternalAgent.getDefaultInstance())
    val scope = rememberCoroutineScope()

    ExternalAgentSettingsScreen(
        settings = data,
        onUrlChange = { url ->
            scope.launch {
                dataStore.updateData { current ->
                    current.toBuilder()
                        .setUrl(url)
                        .build()
                }
            }
        },
        onApiKeyChange = { apiKey ->
            scope.launch {
                dataStore.updateData { current ->
                    current.toBuilder()
                        .setApiKey(apiKey)
                        .build()
                }
            }
        },
        onNavigateBack = onNavigateBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExternalAgentSettingsScreen(
    settings: SkillSettingsExternalAgent,
    onUrlChange: (String) -> Unit,
    onApiKeyChange: (String) -> Unit,
    onNavigateBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.pref_external_agent_category)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        ExternalAgentSettingsContent(
            modifier = Modifier.padding(paddingValues),
            settings = settings,
            onUrlChange = onUrlChange,
            onApiKeyChange = onApiKeyChange,
        )
    }
}

@Composable
private fun ExternalAgentSettingsContent(
    modifier: Modifier = Modifier,
    settings: SkillSettingsExternalAgent,
    onUrlChange: (String) -> Unit,
    onApiKeyChange: (String) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        item { SettingsCategoryTitle(stringResource(R.string.pref_external_agent_category), topPadding = 4.dp) }
        item {
            StringSetting(
                title = stringResource(R.string.pref_external_agent_url_title),
                description = stringResource(R.string.pref_external_agent_url_summary),
                descriptionWhenEmpty = stringResource(R.string.pref_external_agent_url_summary),
            ).Render(
                value = settings.url,
                onValueChange = { onUrlChange(it.trim()) },
            )
        }
        item {
            StringSetting(
                title = stringResource(R.string.pref_external_agent_api_key_title),
                description = stringResource(R.string.pref_external_agent_api_key_summary),
                descriptionWhenEmpty = stringResource(R.string.pref_external_agent_api_key_summary),
            ).Render(
                value = settings.apiKey,
                onValueChange = { onApiKeyChange(it.trim()) },
            )
        }
    }
}
