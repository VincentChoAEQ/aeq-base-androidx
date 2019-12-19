package ca.aequilibrium.base.ui.transformer.battle

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ca.aequilibrium.base.R
import ca.aequilibrium.base.util.MASS_EXTINCTION
import ca.aequilibrium.base.util.NO_WINNER
import ca.aequilibrium.base.util.TEAM_AUTOBOT
import ca.aequilibrium.base.vo.transformer.BattleResult
import kotlinx.android.synthetic.main.item_battle.view.*
import kotlinx.android.synthetic.main.item_battle_notification.view.*

/**
 * Created by Chris on 2018-10-01.
 */
class BattleAdapter(private val context: Context) : RecyclerView.Adapter<BattleAdapter.ViewHolder>() {

    companion object {
        private const val TYPE_MESSAGE = 0
        private const val TYPE_BATTLE = 1
    }

    private val collection = ArrayList<Any>()

    init {
        collection.add(BattleNotification(context.getString(R.string.battle_start)))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == TYPE_BATTLE) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_battle, parent, false)
            ViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_battle_notification, parent, false)
            ViewHolder(view)
        }
    }


    override fun getItemViewType(position: Int): Int {
        return if( collection[position] is BattleResult) {
            TYPE_BATTLE
        } else {
            TYPE_MESSAGE
        }
    }

    override fun getItemCount() = collection.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (holder.itemViewType == TYPE_BATTLE) {
            val result = collection[position] as BattleResult

            val span = when(result.result) {
                MASS_EXTINCTION -> getMassExtinctionSpan(result)
                NO_WINNER -> getDrawSpan(result)
                else -> getWinnerSpan(result)
            }


            holder.view.name.text = span
        } else {
            holder.view.message.text = (collection[position] as BattleNotification).text
        }


    }

    private fun getMassExtinctionSpan(result: BattleResult): SpannableString {
        val lhs = result.lhs.name
        val rhs = result.rhs.name

        val text = if( result.lhs.isLeader() && result.rhs.isLeader() ) {
            "$lhs and $rhs encountered each other on the battlefield!\n\nThe destruction of their battle has destroyed everyone."
        } else {
            "$lhs and $rhs were destroyed in the blast."
        }

        val span = SpannableString(text)
        setSpan(span, R.color.autobot_light, result.lhs.name)
        setSpan(span, R.color.decepticon_light, result.rhs.name)
        return span
    }

    private fun getDrawSpan(result: BattleResult): SpannableString {
        val lhs = result.lhs.name
        val rhs = result.rhs.name

        val text = "Both $lhs and $rhs were destroyed!"

        val span = SpannableString(text)
        setSpan(span, R.color.autobot_light, lhs)
        setSpan(span, R.color.decepticon_light, rhs)
        return span
    }

    private fun getWinnerSpan(result: BattleResult): SpannableString {
        val lhs = if (result.result == TEAM_AUTOBOT) result.lhs.name else result.rhs.name
        val rhs = if (result.result == TEAM_AUTOBOT) result.rhs.name else result.lhs.name

        val text = "$lhs has destroyed $rhs!"

        val span = SpannableString(text)
        setSpan(span, R.color.autobot_light, result.lhs.name)
        setSpan(span, R.color.decepticon_light, result.rhs.name)
        return span
    }

    private fun setSpan(span: SpannableString, color: Int, text: String) {
        val start = span.indexOf(text)
        span.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, color)), start, start + text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }


    fun addNotification(notification: BattleNotification) {
        collection.add(0, notification)
        notifyItemRangeInserted(0, 1)
    }

    fun addBattle(result: BattleResult) {
        collection.add(0, result)
        notifyItemRangeInserted(0, 1)
    }


    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    class BattleNotification(val text: String)
}