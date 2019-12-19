package ca.aequilibrium.base.ui.transformer.details

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.TransitionManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import ca.aequilibrium.base.R
import ca.aequilibrium.base.networking.TransformerService
import ca.aequilibrium.base.ui.TransformerActivity
import ca.aequilibrium.base.ui.transformer.StatView
import ca.aequilibrium.base.util.TEAM_AUTOBOT
import ca.aequilibrium.base.util.TEAM_DECEPTICON
import ca.aequilibrium.base.vo.transformer.Transformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.details_activity.*
import kotlinx.android.synthetic.main.view_stat.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailsActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener {
    companion object {

        private const val BACKGROUND_TRANSITION_DURATION = 150
        private const val EXTRA_TRANSFORMER = "EXTRA_TRANSFORMER"

        fun startActivity(context: Context, transformer: Transformer?) {
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra(EXTRA_TRANSFORMER, transformer)
            context.startActivity(intent)
        }
    }

    private val viewModel by viewModel<DetailsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.details_activity)

        val transformer = intent.getSerializableExtra(EXTRA_TRANSFORMER) as? Transformer

        if(transformer == null){
            viewModel.transformer = Transformer()
        } else {
            viewModel.transformer = transformer
        }

        viewModel.start()

        viewModel.state.observe(this, Observer {
            setEditMode(it)
        })

        viewModel.spinner.observe(this) { value ->
            value.let { show ->
                progressBar.visibility = if (show) View.VISIBLE else View.GONE
                close.visibility = if (show) View.GONE else View.VISIBLE
            }
        }

        viewModel.snackbar.observe(this) { text ->
            text?.let {
                Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
                viewModel.onSnackbarShown()
            }
        }

        icon_decepticon.setOnClickListener {
            selectTeam(TEAM_DECEPTICON)
        }

        icon_autobot.setOnClickListener {
            selectTeam(TEAM_AUTOBOT)
        }

        name.setText(viewModel.transformer.name)
        animateTeamIcon(viewModel.transformer.team)
        animateBackgroundColor(viewModel.transformer.team)

        name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0 != null)
                    viewModel.transformer.name = p0.toString()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        })

        viewModel.transformer.stats.forEachIndexed { index, stat ->
            val view = StatView(this, index, stat)

            view.seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, value: Int, p2: Boolean) {
                    val value = Math.max(Transformer.SKILLS_MIN, value)
                    viewModel.transformer.stats[index] = value
                    view.setValue(value)
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {}

                override fun onStopTrackingTouch(p0: SeekBar?) {}
            })

            stats.addView(view)
        }

        close.setOnClickListener {
            finish()
        }

        options.setOnClickListener {
            val popupMenu = PopupMenu(this, it)
            popupMenu.inflate(R.menu.details_edit)
            popupMenu.setOnMenuItemClickListener(this)
            popupMenu.show()
        }

        save.setOnClickListener {
            saveTransformer()
        }

        fab.setOnClickListener {
            randomize()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.details_edit, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun selectTeam(team: String) {
        animateTeamIcon(team)
        animateBackgroundColor(team)

        viewModel.transformer.team = team
    }

    private fun animateBackgroundColor(team: String) {
        val color = if (team == TEAM_AUTOBOT) R.color.autobot_light else R.color.decepticon_light

        val colorFrom = (root.background as ColorDrawable).color
        val colorTo = ContextCompat.getColor(this, color)
        val animation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
        animation.duration = BACKGROUND_TRANSITION_DURATION.toLong()
        animation.addUpdateListener { animator -> root.setBackgroundColor(animator.animatedValue as Int) }
        animation.start()
    }

    private fun animateTeamIcon(team: String) {


        val constraint = ConstraintSet()
        constraint.clone(icons_container)

        val selected = if (team == TEAM_AUTOBOT) R.id.icon_autobot else R.id.icon_decepticon
        val unselected = if (team == TEAM_AUTOBOT) R.id.icon_decepticon else R.id.icon_autobot

        val startSide = if (team == TEAM_AUTOBOT) ConstraintSet.START else ConstraintSet.END
        val endSide = if (team == TEAM_AUTOBOT) ConstraintSet.END else ConstraintSet.START

        val size = resources.getDimension(R.dimen.icon_size).toInt()
        val smallSize = resources.getDimension(R.dimen.icon_size_small).toInt()
        val margin = resources.getDimension(R.dimen.icon_margin).toInt()

        constraint.clear(selected)
        constraint.constrainHeight(selected, size)
        constraint.constrainWidth(selected, size)
        constraint.centerHorizontally(selected, R.id.icons_container)
        constraint.centerVertically(selected, R.id.icons_container)

        constraint.clear(unselected)
        constraint.constrainHeight(unselected, smallSize)
        constraint.constrainWidth(unselected, smallSize)
        constraint.centerVertically(unselected, R.id.icons_container)
        constraint.connect(unselected, startSide, selected, endSide, margin)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition(icons_container)
            constraint.applyTo(icons_container)
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {

        return when (item?.itemId) {
            R.id.action_edit -> {
                viewModel.state.value = DetailsViewModel.STATE_EDIT
                true
            }
            R.id.action_delete -> {
                deleteTransformer()
                true
            }
            else -> false
        }
    }

    private fun deleteTransformer() {
        if (viewModel.transformer.id.isNullOrEmpty())
            return

        viewModel.deleteTransformer(viewModel.transformer)
        finish()
    }

    private fun saveTransformer() {
        val transformer = viewModel.transformer

        if (transformer.name.isBlank()) {
            Toast.makeText(this, "Transformer must have a valid name", Toast.LENGTH_SHORT).show()
            return
        }

        if (transformer.id.isNullOrEmpty()) {
            createTransformer(transformer)
        } else {
            updateTransformer(transformer)
        }
    }

    private fun createTransformer(transformer: Transformer) {
        viewModel.createTransformer(transformer)
        viewModel.state.postValue(DetailsViewModel.STATE_VIEW)
    }

    private fun updateTransformer(transformer: Transformer) {
        viewModel.updateTransformer(transformer)
        viewModel.state.postValue(DetailsViewModel.STATE_VIEW)
    }

    private fun setEditMode(state: Int) {

        val isEditing = state == DetailsViewModel.STATE_EDIT || state == DetailsViewModel.STATE_CREATE

        for (i in 0 until stats.childCount) {
            val view = stats.getChildAt(i) as StatView
            if (isEditing) {
                view.enable()
            } else {
                view.disable()
            }
        }

        if (isEditing) {
            fab.show()
        } else {
            fab.hide()
        }

        options.visibility = if (state == DetailsViewModel.STATE_VIEW) View.VISIBLE else View.GONE

        save.visibility = if (isEditing) View.VISIBLE else View.INVISIBLE

        if (isEditing) {
            icon_autobot.visibility = View.VISIBLE
            icon_decepticon.visibility = View.VISIBLE
        } else {
            val team = viewModel.transformer.team
            if (team == TEAM_AUTOBOT) {
                icon_decepticon.visibility = View.GONE
            } else {
                icon_autobot.visibility = View.GONE
            }
        }

        name.isEnabled = isEditing
    }

    fun randomize() {
        viewModel.transformer.let { transformer ->
            transformer.randomize()

            name.setText(transformer.name)
            selectTeam(transformer.team)

            transformer.stats.forEachIndexed { index, value ->
                (stats.getChildAt(index) as StatView).setValue(value)
            }
        }
    }
}
