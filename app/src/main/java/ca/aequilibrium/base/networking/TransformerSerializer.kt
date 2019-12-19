package ca.aequilibrium.base.networking

import ca.aequilibrium.base.vo.transformer.Transformer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

/**
 * Created by Chris on 2018-10-01.
 */
class TransformerSerializer : JsonSerializer<Transformer> {

    override fun serialize(src: Transformer, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val obj = JsonObject()

        obj.addProperty("id", src.id)
        obj.addProperty("name", src.name)

        obj.addProperty("strength", src[Transformer.STRENGTH])
        obj.addProperty("intelligence", src[Transformer.INTELLIGENCE])
        obj.addProperty("speed", src[Transformer.SPEED])
        obj.addProperty("endurance", src[Transformer.ENDURANCE])
        obj.addProperty("rank", src[Transformer.RANK])
        obj.addProperty("courage", src[Transformer.COURAGE])
        obj.addProperty("firepower", src[Transformer.FIREPOWER])
        obj.addProperty("skill", src[Transformer.SKILL])

        obj.addProperty("team", src.team)
        obj.addProperty("icon", src.icon)

        return obj
    }
}