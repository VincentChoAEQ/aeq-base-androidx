package ca.aequilibrium.base.vo.transformer

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ca.aequilibrium.base.util.TEAM_AUTOBOT
import ca.aequilibrium.base.util.TEAM_DECEPTICON
import java.io.Serializable
import java.util.*

/**
 * Created by Chris on 2018-09-29.
 */
@Entity(tableName = "transformers")
data class Transformer(
    @PrimaryKey val id: String = "",
    var name: String,
    var team: String,
    val icon: String? = null,

    @Embedded val specs: Specs = Specs()

) : Serializable {

    constructor(name: String, average: Int, team: String) : this("", name, team) {
        specs.setAverage(average)
    }

    constructor(id: String, name: String, specs: Specs, team: String, icon: String?): this(id, name, team, icon) {
        this.specs.clone(specs)
    }

    constructor() : this(name = "", team = TEAM_AUTOBOT)

    companion object {

        const val STRENGTH = 0
        const val INTELLIGENCE = 1
        const val SPEED = 2
        const val ENDURANCE = 3
        const val RANK = 4
        const val COURAGE = 5
        const val FIREPOWER = 6
        const val SKILL = 7

        const val SKILLS_SIZE = 8
        const val SKILLS_MAX = 10
        const val SKILLS_MIN = 1
        const val SKILLS_DEFAULT = SKILLS_MIN
    }

    fun isLeader(): Boolean {
        return when (name.toLowerCase()) {
            "optimus prime",
            "predaking" -> true
            else -> false
        }
    }

    val isAutobot: Boolean
        get() = team == "A"

    val isDecepticon: Boolean
        get() = !isAutobot

    val stats: Array<Int>
        get() = specs.array

    val total: Int
        get() = specs.total

    fun randomize() {
        val random = Random()

        val names = listOf("Optimus Prime", "PREDAKING", "Rat Trap", "Solaire", "Ash",
                "Cyrpto", "Cena", "Vlad the Impaler", "HULK", "Batman", "Megatron", "Megatron v2.0", "UNKNOWN")

        name = names[random.nextInt(names.size)]

        team = if (random.nextBoolean()) TEAM_AUTOBOT else TEAM_DECEPTICON

        specs.randomize()
    }

    operator fun get(position: Int) = specs[position]
}