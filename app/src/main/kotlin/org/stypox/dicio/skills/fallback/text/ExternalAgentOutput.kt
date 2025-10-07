package org.stypox.dicio.skills.fallback.text

import androidx.compose.runtime.Composable
import org.dicio.skill.context.SkillContext
import org.dicio.skill.skill.InteractionPlan
import org.dicio.skill.skill.SkillOutput
import org.stypox.dicio.io.graphical.Headline

data class ExternalAgentOutput(
    val text: String,
) : SkillOutput {
    override fun getSpeechOutput(ctx: SkillContext): String = text

    override fun getInteractionPlan(ctx: SkillContext): InteractionPlan =
        InteractionPlan.FinishInteraction

    @Composable
    override fun GraphicalOutput(ctx: SkillContext) {
        Headline(text = text)
    }
}
