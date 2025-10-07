package org.stypox.dicio.skills.fallback.text

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import org.dicio.skill.context.SkillContext
import org.dicio.skill.skill.Skill
import org.dicio.skill.skill.SkillInfo
import org.stypox.dicio.R

object ExternalAgentInfo : SkillInfo("external_agent") {
    override fun name(context: Context) =
        context.getString(R.string.skill_external_agent_name)

    override fun sentenceExample(context: Context) = ""

    @Composable
    override fun icon() = rememberVectorPainter(Icons.Filled.SmartToy)

    override fun isAvailable(ctx: SkillContext): Boolean = true

    override fun build(ctx: SkillContext): Skill<*> {
        return ExternalAgentSkill(this)
    }
}
