package ca.aequilibrium.base.ui.transformer.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import ca.aequilibrium.base.R
import ca.aequilibrium.base.ui.TransformerActivity
import ca.aequilibrium.base.vo.transformer.Transformer
import kotlinx.android.synthetic.main.list_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ListFragment : Fragment(), ListAdapter.OnTransformerClickListener {

    private val adapter = ListAdapter(this)
    private val viewModel : ListViewModel by viewModel()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val team = arguments?.getString(EXTRA_TEAM)

        team?.let{
            observeTransformer(it)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val team = arguments?.getString(EXTRA_TEAM)

        team?.let{
            observeTransformer(it)
        }
    }

    fun observeTransformer(team: String?) {
        viewModel.transformers.observe(this, Observer {

            val team = it.data?.filter { it.team == team }?.sortedByDescending { it.total }

            team?.let {
                adapter.setTransformers(it)
            }

            list.adapter = adapter
            list.layoutManager = LinearLayoutManager(context)
        })
    }

    override fun onClick(transformer: Transformer) {
        (activity as? TransformerActivity)?.showDetails(transformer)
    }

    companion object {
        private const val EXTRA_TEAM = "EXTRA_TEAM"

        fun newInstance(team: String): ListFragment {
            val fragment = ListFragment()

            val bundle = Bundle()
            bundle.putString(EXTRA_TEAM, team)
            fragment.arguments = bundle

            return fragment
        }
    }

}
