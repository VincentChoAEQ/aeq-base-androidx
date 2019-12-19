package ca.aequilibrium.base.vo.transformer

import androidx.annotation.VisibleForTesting
import ca.aequilibrium.base.util.MASS_EXTINCTION
import ca.aequilibrium.base.util.NO_WINNER
import ca.aequilibrium.base.util.TEAM_AUTOBOT
import ca.aequilibrium.base.util.TEAM_DECEPTICON
import ca.aequilibrium.base.vo.transformer.Transformer.Companion.COURAGE
import ca.aequilibrium.base.vo.transformer.Transformer.Companion.RANK
import ca.aequilibrium.base.vo.transformer.Transformer.Companion.SKILL
import ca.aequilibrium.base.vo.transformer.Transformer.Companion.STRENGTH

/**
 * Created by Chris on 2018-09-30.
 */
class Battle(transformers: List<Transformer>) {

    val autobots = transformers.filter { it.isAutobot }.sortedByDescending { it[RANK] }
    val decepticons = transformers.filter { it.isDecepticon }.sortedByDescending { it[RANK] }


    val victor: String

    val results = ArrayList<BattleResult>()

    init {

        val battles = Math.min(autobots.size, decepticons.size)

        var isMassExtinction = false

        for (i in 0 until battles) {
            val autobot = autobots[i]
            val decepticon = decepticons[i]

            val winner = if (isMassExtinction) {
                MASS_EXTINCTION
            } else {
                getWinner(autobot, decepticon)
            }

            if (winner == MASS_EXTINCTION && autobot.isLeader() && decepticon.isLeader()) {
                isMassExtinction = true
                results.add(0, BattleResult(autobot, decepticon, winner))
                results.forEach { it.result = MASS_EXTINCTION }
            } else {
                results.add(BattleResult(autobot, decepticon, winner))
            }
        }

        val lhs = results.count { it.result == TEAM_AUTOBOT || it.result == NO_WINNER || it.result == MASS_EXTINCTION }
        val rhs = results.count { it.result == TEAM_DECEPTICON || it.result == NO_WINNER || it.result == MASS_EXTINCTION }

        victor = when {
            isMassExtinction -> MASS_EXTINCTION
            lhs > rhs -> TEAM_AUTOBOT
            lhs < rhs -> TEAM_DECEPTICON
            else -> NO_WINNER
        }
    }


    companion object {
        @VisibleForTesting
        fun getWinner(autobot: Transformer, decepticon: Transformer): String {
            if (autobot.isLeader() || decepticon.isLeader()) {
                if (autobot.isLeader() && decepticon.isLeader())
                    return MASS_EXTINCTION

                if (autobot.isLeader())
                    return TEAM_AUTOBOT

                return TEAM_DECEPTICON
            }


            if (isIntimidating(autobot, decepticon))
                return TEAM_AUTOBOT

            if (isIntimidating(decepticon, autobot))
                return TEAM_DECEPTICON

            if (isOverpowering(autobot, decepticon))
                return TEAM_AUTOBOT

            if (isOverpowering(decepticon, autobot))
                return TEAM_DECEPTICON


            if (equalPower(autobot, decepticon))
                return NO_WINNER

            return if (isStronger(autobot, decepticon))
                TEAM_AUTOBOT
            else
                TEAM_DECEPTICON
        }

        @VisibleForTesting
        fun isOverpowering(lhs: Transformer, rhs: Transformer) = lhs[STRENGTH] >= rhs[STRENGTH] + 3

        @VisibleForTesting
        fun isIntimidating(lhs: Transformer, rhs: Transformer) = lhs[COURAGE] - 4 >= rhs[COURAGE]

        @VisibleForTesting
        fun isTooSkilled(lhs: Transformer, rhs: Transformer) = lhs[SKILL] - 3 >= rhs[SKILL]

        @VisibleForTesting
        fun equalPower(lhs: Transformer, rhs: Transformer) = lhs.total == rhs.total

        @VisibleForTesting
        fun isStronger(lhs: Transformer, rhs: Transformer) = lhs.total > rhs.total
    }
}