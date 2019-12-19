package ca.aequilibrium.base.networking

import ca.aequilibrium.base.vo.transformer.Specs
import ca.aequilibrium.base.vo.transformer.Transformer
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

/**
 * Created by Chris on 2018-10-01.
 */
class TransformerDeserializer : JsonDeserializer<Transformer> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Transformer {
        val obj = json.asJsonObject

        val id = obj.get("id")?.asString ?: ""
        val name = obj.get("name").asString
        val team = obj.get("team").asString
        val icon = obj.get("icon")?.asString

        val specs = Specs(
                obj.get("strength").asInt,
                obj.get("intelligence").asInt,
                obj.get("speed").asInt,
                obj.get("endurance").asInt,
                obj.get("rank").asInt,
                obj.get("courage").asInt,
                obj.get("firepower").asInt,
                obj.get("skill").asInt
        )

        return Transformer(id, name, specs, team, icon)
    }
}