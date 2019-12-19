package ca.aequilibrium.base.ui.transformer.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.aequilibrium.base.R
import ca.aequilibrium.base.vo.transformer.Transformer
import kotlinx.android.synthetic.main.item_transformer.view.*

/**
 * Created by Chris on 2018-09-29.
 */
class ListAdapter(private val onClickListener: OnTransformerClickListener) : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    private val transformers = ArrayList<Transformer>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transformer, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = transformers.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transformer = transformers[position]

        holder.view.apply {
            name.text = transformer.name
            overall_rating.text = transformer.total.toString()

//            val color = if (transformer.isAutobot) R.color.autobot_light else R.color.decepticon_dark

//            card.setCardBackgroundColor(ContextCompat.getColor(context, color))

            val adapter = StatsAdapter(transformer.specs)
            stats_container.adapter = adapter
            stats_container.layoutManager = GridLayoutManager(context, 4, RecyclerView.VERTICAL, false)


            setOnClickListener {
                onClickListener.onClick(transformer)
            }
        }
    }

    fun setTransformers(team: List<Transformer>) {
        transformers.clear()
        transformers.addAll(team)

        notifyDataSetChanged()
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)


    interface OnTransformerClickListener {
        fun onClick(transformer: Transformer)
    }

}