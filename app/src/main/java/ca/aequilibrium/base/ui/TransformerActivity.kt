package ca.aequilibrium.base.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ca.aequilibrium.base.R
import ca.aequilibrium.base.ui.transformer.battle.BattleActivity
import ca.aequilibrium.base.ui.transformer.details.DetailsActivity
import ca.aequilibrium.base.ui.transformer.details.MainFragment
import ca.aequilibrium.base.vo.transformer.Transformer
import ca.aequilibrium.base.vo.transformer.Transformers
import kotlinx.android.synthetic.main.main_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class TransformerActivity : AppCompatActivity() {

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, TransformerActivity::class.java)
            context.startActivity(intent)
        }
    }
    private val viewModel : AppViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            showMain()
        }

        //register app.
        viewModel.allSpark()

        fab.setOnClickListener {
            showDetails()
        }
    }

    private fun showMain() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, MainFragment.newInstance())
            .commitNow()
    }

    fun showDetails(transformer: Transformer? = null) {
        DetailsActivity.startActivity(this, transformer)
    }

    fun startBattle(transformers: Transformers? = null) {
        BattleActivity.startActivity(this, transformers)
    }
}