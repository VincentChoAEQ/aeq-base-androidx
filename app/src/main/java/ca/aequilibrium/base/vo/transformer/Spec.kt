package ca.aequilibrium.base.vo.transformer

import androidx.room.Ignore
import java.io.Serializable
import java.util.*

data class Specs(var strength: Int, var intelligence: Int, var speed: Int, var endurance: Int,
                 var rank: Int, var courage: Int, var firepower: Int, var skill: Int) : Serializable {

    constructor(average: Int) : this(average, average, average, average, average, average, average, average)

    constructor() : this(Transformer.SKILLS_DEFAULT)
    @Ignore
    internal val array = Array(Transformer.SKILLS_SIZE) { Transformer.SKILLS_DEFAULT }

    internal val total: Int
        get() = array[Transformer.STRENGTH] + array[Transformer.INTELLIGENCE] + array[Transformer.SPEED] + array[Transformer.ENDURANCE] + array[Transformer.FIREPOWER]

    init {
        array[Transformer.STRENGTH] = strength
        array[Transformer.INTELLIGENCE] = intelligence
        array[Transformer.SPEED] = speed
        array[Transformer.ENDURANCE] = endurance
        array[Transformer.RANK] = rank
        array[Transformer.COURAGE] = courage
        array[Transformer.FIREPOWER] = firepower
        array[Transformer.SKILL] = skill
    }

    fun setAverage(average: Int) {
        for (i in 0 until Transformer.SKILLS_SIZE) {
            array[i] = average
        }
    }

    fun clone(specs: Specs) {
        for (i in 0 until Transformer.SKILLS_SIZE) {
            array[i] = specs[i]
        }
        copySpecToAttribute()
    }

    fun randomize() {
        val random = Random()

        for (i in 0 until Transformer.SKILLS_SIZE) {
            array[i] = Transformer.SKILLS_MIN + random.nextInt(Transformer.SKILLS_MAX - Transformer.SKILLS_MIN)
        }

        copySpecToAttribute()
    }

    fun copySpecToAttribute(){
        strength = array[Transformer.STRENGTH]
        intelligence = array[Transformer.INTELLIGENCE]
        speed = array[Transformer.SPEED]
        endurance = array[Transformer.ENDURANCE]
        rank = array[Transformer.RANK]
        courage = array[Transformer.COURAGE]
        firepower = array[Transformer.FIREPOWER]
        skill = array[Transformer.SKILL]
    }


    operator fun get(position: Int) = array[position]

}