package ca.aequilibrium.base.vo.transformer

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Chris on 2018-09-29.
 */
data class Transformers(
        @SerializedName("transformers") val transformers: List<Transformer>
): Serializable