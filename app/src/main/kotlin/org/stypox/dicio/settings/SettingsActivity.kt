package org.stypox.dicio.settings

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import org.stypox.dicio.settings.externalagent.ExternalAgentSettingsRoute
import org.stypox.dicio.util.BaseActivity

@AndroidEntryPoint
class SettingsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        composeSetContent {
            ExternalAgentSettingsRoute(onNavigateBack = { finish() })
        }
    }
}
