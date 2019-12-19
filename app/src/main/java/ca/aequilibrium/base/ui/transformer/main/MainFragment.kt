package ca.aequilibrium.base.ui.transformer.details

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import ca.aequilibrium.base.R
import ca.aequilibrium.base.ui.TransformerActivity
import ca.aequilibrium.base.ui.transformer.list.ListFragment
import ca.aequilibrium.base.ui.transformer.main.MainViewModel
import ca.aequilibrium.base.util.TEAM_AUTOBOT
import ca.aequilibrium.base.util.TEAM_DECEPTICON
import ca.aequilibrium.base.vo.transformer.Transformer
import ca.aequilibrium.base.vo.transformer.Transformers
import kotlinx.android.synthetic.main.main_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainFragment : Fragment() {

    companion object {

        private const val BACKGROUND_TRANSITION_DURATION = 150

        private const val ALPHA_ANIMATION_DURATION = 250L
        private const val START_ALPHA = 1.0f
        private const val END_ALPHA = 0.0f

        fun newInstance() = MainFragment()
    }

    private val viewModel : MainViewModel by viewModel()
    private var transformers: List<Transformer>? = null
    private var currentTeam = TEAM_AUTOBOT

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        animateHeader(currentTeam)

        activity?.let {
            val adapter = ViewPagerAdapter(childFragmentManager)
            adapter.addFragment(ListFragment.newInstance(TEAM_AUTOBOT), getString(R.string.autobots))
            adapter.addFragment(ListFragment.newInstance(TEAM_DECEPTICON), getString(R.string.decepticons))

            pager.adapter = adapter

            tabs.setupWithViewPager(pager)
        }

        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                val team = if (position == 0) TEAM_AUTOBOT else TEAM_DECEPTICON
                animateHeader(team)
            }
        })

        viewModel.transformers.observe(this, Observer {
            transformers = it.data
        })

        battle.setOnClickListener {
            transformers?.let {
                (activity as? TransformerActivity)?.startBattle(Transformers(it))
            }
        }
    }

    private fun animateHeader(team: String) {
        animateIcon(team)
        animateBackgroundColor(team)
        currentTeam = team
    }

    private fun animateIcon(team: String) {
        val animDuration = if (currentTeam != team) ALPHA_ANIMATION_DURATION else 0

        val drawable = if (team == TEAM_AUTOBOT) R.drawable.ic_autobot else R.drawable.ic_decepticon

        val animation = AlphaAnimation(START_ALPHA, END_ALPHA).apply {
            duration = animDuration
            repeatCount = 1
            repeatMode = Animation.REVERSE

            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {}
                override fun onAnimationRepeat(animation: Animation) {
                    icon.setImageResource(drawable)
                }
            })
        }

        icon.startAnimation(animation)
    }

    private fun animateBackgroundColor(team: String) {
        val duration = if (currentTeam != team) BACKGROUND_TRANSITION_DURATION else 0

        val color = if (team == TEAM_AUTOBOT) R.color.autobot_light else R.color.decepticon_light

        val colorFrom = (collapsing_toolbar.background as ColorDrawable).color
        val colorTo = ContextCompat.getColor(context ?: return, color)
        val animation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
        animation.duration = duration.toLong()
        animation.addUpdateListener { animator -> collapsing_toolbar.setBackgroundColor(animator.animatedValue as Int) }
        animation.start()
    }

    internal class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {

        private val fragments = ArrayList<Fragment>()
        private val titles = ArrayList<String>()

        override fun getItem(position: Int) = fragments[position]
        override fun getPageTitle(position: Int) = titles[position]
        override fun getCount() = fragments.size

        fun addFragment(fragment: Fragment, title: String) {
            fragments.add(fragment)
            titles.add(title)
        }
    }
}