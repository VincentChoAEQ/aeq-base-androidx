package ca.aequilibrium.base.ui.transformer.battle

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.aequilibrium.base.R
import ca.aequilibrium.base.ui.transformer.details.DetailsActivity
import ca.aequilibrium.base.util.MASS_EXTINCTION
import ca.aequilibrium.base.util.NO_WINNER
import ca.aequilibrium.base.util.TEAM_AUTOBOT
import ca.aequilibrium.base.util.TEAM_DECEPTICON
import ca.aequilibrium.base.vo.transformer.Battle
import ca.aequilibrium.base.vo.transformer.Transformers
import kotlinx.android.synthetic.main.battle_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


/**
 * Created by Chris on 2018-10-01.
 */
class BattleActivity : AppCompatActivity() {

    companion object {
        private const val BATTLE_DELAY = 1250L
        private const val BATTLE_DELAY_MASS_EXTINCTION = 250L
        private const val EXTRA_TRANSFORMERS = "EXTRA_TRANSFORMERS"

        fun startActivity(context: Context, transformers: Transformers?) {
            val intent = Intent(context, BattleActivity::class.java)
            intent.putExtra(BattleActivity.EXTRA_TRANSFORMERS, transformers)
            context.startActivity(intent)
        }
    }

    private val viewModel by viewModel<BattleViewModel>()
    private lateinit var adapter: BattleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.battle_activity)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_close_white_24dp)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val transformers = intent.getSerializableExtra(BattleActivity.EXTRA_TRANSFORMERS) as? Transformers

        transformers?.let{
            viewModel.start(it)
        }

        val battle = viewModel.battle

        adapter = BattleAdapter(this)

        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter

        if (battle.results.size == 0) {
            adapter.addNotification(BattleAdapter.BattleNotification(getString(R.string.battle_empty)))
            announceWinner(battle)
        } else {
            setupBattleMessageTimer(battle)
        }
    }

    private fun setupBattleMessageTimer(battle: Battle) {
        var index = 0

        var lhs = 0
        var rhs = 0

        val delay = if (battle.victor == MASS_EXTINCTION) BATTLE_DELAY_MASS_EXTINCTION else BATTLE_DELAY

        val timer = Timer()
        val task = object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    val result = battle.results[index]

                    adapter.addBattle(result)

                    when (result.result) {
                        TEAM_AUTOBOT -> lhs++
                        TEAM_DECEPTICON -> rhs++
                        NO_WINNER -> {
                            lhs++
                            rhs++
                        }
                    }

                    autobots_score.text = lhs.toString()
                    decepticons_score.text = rhs.toString()

                    index++

                    if (index >= battle.results.size) {
                        adapter.addNotification(BattleAdapter.BattleNotification(getString(R.string.battle_end)))

                        cancel()
                        announceWinner(battle)
                    }

                    list.layoutManager?.smoothScrollToPosition(list, RecyclerView.State(), 0)
                }
            }
        }

        timer.scheduleAtFixedRate(task, delay, delay)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun announceWinner(battle: Battle) {

        val text = when (battle.victor) {
            MASS_EXTINCTION -> getString(R.string.battle_mass_extinction)
            TEAM_AUTOBOT -> getString(R.string.battle_victory_autobots)
            TEAM_DECEPTICON -> getString(R.string.battle_victory_decepticons)
            else -> getString(R.string.battle_draw)
        }

        adapter.addNotification(BattleAdapter.BattleNotification(text))
    }
}