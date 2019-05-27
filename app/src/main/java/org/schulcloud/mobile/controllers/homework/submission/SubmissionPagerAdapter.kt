package org.schulcloud.mobile.controllers.homework.submission

import android.content.Context
import androidx.annotation.IntDef
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import org.schulcloud.mobile.R


class SubmissionPagerAdapter(private val context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {
    companion object {
        private const val TAB_INVALID = 0
        private const val TAB_OVERVIEW = 1
        private const val TAB_FEEDBACK = 2

        @Retention(AnnotationRetention.SOURCE)
        @MustBeDocumented
        @IntDef(TAB_INVALID, TAB_OVERVIEW, TAB_FEEDBACK)
        annotation class Tab
    }

    override fun getItem(position: Int): Fragment {
        return when (getTabType(position)) {
            TAB_OVERVIEW -> OverviewFragment()
            TAB_FEEDBACK -> FeedbackFragment()

            // Shouldn't occur but we have to return something
            else -> OverviewFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        val titleId = when (getTabType(position)) {
            TAB_OVERVIEW -> R.string.homework_submission_overview
            TAB_FEEDBACK -> R.string.homework_submission_feedback

            TAB_INVALID -> return null
            else -> return null
        }
        return context.getString(titleId)
    }

    override fun getCount() = 2

    @Tab
    private fun getTabType(position: Int): Int {
        return when (position) {
            0 -> TAB_OVERVIEW
            1 -> TAB_FEEDBACK
            else -> TAB_INVALID
        }
    }
}
