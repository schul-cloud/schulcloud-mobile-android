package org.schulcloud.mobile.controllers.learnstore


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_learnstore.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.main.FragmentType
import org.schulcloud.mobile.controllers.main.MainFragment
import org.schulcloud.mobile.controllers.main.MainFragmentConfig
import org.schulcloud.mobile.models.material.Material
import org.schulcloud.mobile.models.material.MaterialRepository
import org.schulcloud.mobile.utils.asLiveData
import org.schulcloud.mobile.viewmodels.MaterialListViewModel
import org.schulcloud.mobile.views.ItemOffsetDecoration

class LearnstoreFragment : MainFragment<MaterialListViewModel>() {

    companion object {
        val TAG: String = LearnstoreFragment::class.java.simpleName
    }

    private val currentMaterialAdapter: MaterialListAdapter by lazy {
        MaterialListAdapter()
    }

    private val popularMaterialAdapter: MaterialListAdapter by lazy {
        MaterialListAdapter()
    }

    override var url: String? = "/content"
    override fun provideConfig(): LiveData<MainFragmentConfig> = MainFragmentConfig(
            fragmentType = FragmentType.PRIMARY,
            title = getString(R.string.learnstore_title)
    ).asLiveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MaterialListViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_learnstore, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView(recyclerViewPopular, popularMaterialAdapter, viewModel.popularMaterials, popularEmpty)
        setUpRecyclerView(recyclerViewCurrent, currentMaterialAdapter, viewModel.currentMaterials, currentEmpty)
    }

    override suspend fun refresh() {
        MaterialRepository.syncMaterials()
    }

    private fun setUpRecyclerView(recyclerView: RecyclerView,
                                  adapter: MaterialListAdapter,
                                  materials: LiveData<List<Material>>,
                                  empty: View) {
        adapter.emptyIndicator = empty
        materials.observe(this, Observer {
            adapter.update(it ?: emptyList())
        })
        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            this.adapter = adapter
            addItemDecoration(ItemOffsetDecoration(context, R.dimen.grid_spacing))
        }
    }
}
