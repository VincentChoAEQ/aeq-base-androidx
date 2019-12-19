package ca.aequilibrium.base.ui.transformer.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ca.aequilibrium.base.R
import ca.aequilibrium.base.vo.transformer.Specs
import ca.aequilibrium.base.vo.transformer.Transformer
import kotlinx.android.synthetic.main.item_stats.view.*

/**
 * Created by Chris on 2018-10-01.
 */
class StatsAdapter(private val specs: Specs) : RecyclerView.Adapter<StatsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stats, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = Transformer.SKILLS_SIZE

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val text = when (position) {
            Transformer.STRENGTH -> R.string.strength_short
            Transformer.INTELLIGENCE -> R.string.intelligence_short
            Transformer.SPEED -> R.string.speed_short
            Transformer.ENDURANCE -> R.string.endurance_short
            Transformer.RANK -> R.string.rank_short
            Transformer.COURAGE -> R.string.courage_short
            Transformer.FIREPOWER -> R.string.firepower_short
            Transformer.SKILL -> R.string.skill_short
            else -> R.string.unknown_short
        }

        holder.view.apply {
            name.text = context.getString(text)
            value.text = specs[position].toString()
        }

    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

}