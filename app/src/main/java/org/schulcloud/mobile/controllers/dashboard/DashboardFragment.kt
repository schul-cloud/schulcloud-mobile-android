package org.schulcloud.mobile.controllers.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.main.FragmentType
import org.schulcloud.mobile.controllers.main.MainFragment
import org.schulcloud.mobile.controllers.main.MainFragmentConfig
import org.schulcloud.mobile.utils.asLiveData


class DashboardFragment : MainFragment<DashboardFragment, ViewModel>() {
    companion object {
        val TAG: String = DashboardFragment::class.java.simpleName
    }

    private var widgets: Array<Widget> = emptyArray()


    override var url: String? = "/dashboard"
    override fun provideSelfConfig() = MainFragmentConfig(
            fragmentType = FragmentType.PRIMARY,
            title = getString(R.string.dashboard_title)
    ).asLiveData()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (childFragmentManager.fragments.isEmpty()) {
            widgets = provideWidgets()
            childFragmentManager.beginTransaction().apply {
                for (widget in widgets)
                    add(R.id.contentList, widget, widget.javaClass.simpleName)
            }.commit()
        } else
            widgets = childFragmentManager.fragments
                    .map { it as Widget }.toTypedArray()
    }


    override suspend fun refresh() {
        for (widget in widgets)
            widget.refresh()
    }

    private fun provideWidgets(): Array<Widget> {
        return arrayOf(
                org.schulcloud.mobile.controllers.dashboard.EventsWidget(),
                org.schulcloud.mobile.controllers.dashboard.HomeworkWidget(),
                NewsWidget()
        )
    }
}
