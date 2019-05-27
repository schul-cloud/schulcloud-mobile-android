package org.schulcloud.mobile.controllers.homework.detailed

import android.content.Context
import androidx.annotation.IntDef
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import org.schulcloud.mobile.R
import org.schulcloud.mobile.models.homework.Homework
import kotlin.properties.Delegates


class HomeworkPagerAdapter(private val context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {
    companion object {
        private const val TAB_INVALID = 0
        private const val TAB_DETAILS = 1
        private const val TAB_SUBMISSIONS = 2

        @Retention(AnnotationRetention.SOURCE)
        @MustBeDocumented
        @IntDef(TAB_INVALID, TAB_DETAILS, TAB_SUBMISSIONS)
        annotation class Tab
    }

    var homework by Delegates.observable<Homework?>(null) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Fragment {
        return when (getTabType(position)) {
            TAB_DETAILS -> OverviewFragment()
            TAB_SUBMISSIONS -> SubmissionsFragment()

            // Shouldn't occur but we have to return something
            else -> OverviewFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        val titleId = when (getTabType(position)) {
            TAB_DETAILS -> R.string.homework_overview
            TAB_SUBMISSIONS -> R.string.homework_submissions

            TAB_INVALID -> return null
            else -> return null
        }
        return context.getString(titleId)
    }

    override fun getCount(): Int {
        return if (homework?.canSeeSubmissions() == true) 2
        else 1
    }

    @Tab
    private fun getTabType(position: Int): Int {
        return when {
            position == 0 -> TAB_DETAILS
            position == 1 && homework?.canSeeSubmissions() == true -> TAB_SUBMISSIONS
            else -> TAB_INVALID
        }
    }
}
