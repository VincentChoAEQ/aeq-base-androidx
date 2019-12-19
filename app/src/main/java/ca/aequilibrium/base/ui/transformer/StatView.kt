package ca.aequilibrium.base.ui.transformer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import ca.aequilibrium.base.R
import ca.aequilibrium.base.vo.transformer.Transformer

import kotlinx.android.synthetic.main.view_stat.view.*

/**
 * Created by Chris on 2018-09-29.
 */
class StatView(context: Context?, index: Int, value: Int) : LinearLayout(context) {

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.view_stat, null, false)

        view.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        val name = when (index) {
            Transformer.STRENGTH -> R.string.strength
            Transformer.INTELLIGENCE -> R.string.intelligence
            Transformer.SPEED -> R.string.speed
            Transformer.ENDURANCE -> R.string.endurance
            Transformer.RANK -> R.string.rank
            Transformer.COURAGE -> R.string.courage
            Transformer.FIREPOWER -> R.string.firepower
            Transformer.SKILL -> R.string.skill
            else -> R.string.unknown
        }

        view.name.setText(name)
        view.seek.progress = value
        view.progress.progress = value
        view.value.text = value.toString()

        addView(view)
    }

    fun disable() {
        seek.isEnabled = false
        seek.visibility = View.INVISIBLE
    }

    fun enable() {
        seek.isEnabled = true
        seek.visibility = View.VISIBLE
    }

    fun setValue(v: Int) {
        progress.progress = v
        seek.progress = v
        value.text = v.toString()
    }
}