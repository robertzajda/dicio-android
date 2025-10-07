package org.stypox.dicio.skills.fallback.text

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object SkillSettingsExternalAgentSerializer : Serializer<SkillSettingsExternalAgent> {
    override val defaultValue: SkillSettingsExternalAgent =
        SkillSettingsExternalAgent.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): SkillSettingsExternalAgent {
        try {
            return SkillSettingsExternalAgent.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto", exception)
        }
    }

    override suspend fun writeTo(t: SkillSettingsExternalAgent, output: OutputStream) {
        t.writeTo(output)
    }
}
