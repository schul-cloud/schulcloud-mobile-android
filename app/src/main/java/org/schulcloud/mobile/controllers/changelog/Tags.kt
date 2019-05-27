package org.schulcloud.mobile.controllers.changelog

import android.content.Context
import androidx.core.content.ContextCompat
import com.michaelflisar.changelog.tags.IChangelogTag
import org.schulcloud.mobile.R
import org.schulcloud.mobile.utils.cssColorHex

private fun String.fixHtmlTags(): String {
    return replace('[', '<')
            .replace(']', '>')
}

class IntroTag : IChangelogTag {
    override fun getXMLTagName() = "intro"

    override fun formatChangelogRow(context: Context?, changeText: String?): String {
        return changeText!!
    }
}

class FeatureTag : IChangelogTag {
    override fun getXMLTagName() = "feature"

    override fun formatChangelogRow(context: Context?, changeText: String?): String {
        return context!!.getString(R.string.changelog_tag_feature_format,
                ContextCompat.getColor(context, R.color.material_green_500).cssColorHex,
                changeText)
                .fixHtmlTags()
    }
}

class FixTag : IChangelogTag {
    override fun getXMLTagName() = "fix"

    override fun formatChangelogRow(context: Context?, changeText: String?): String {
        return context!!.getString(R.string.changelog_tag_fix_format,
                ContextCompat.getColor(context, R.color.theme_error).cssColorHex,
                changeText)
                .fixHtmlTags()
    }
}

class NoteTag : IChangelogTag {
    override fun getXMLTagName() = "note"

    override fun formatChangelogRow(context: Context?, changeText: String?): String {
        return "<i>${context!!.getString(R.string.changelog_tag_note_format, changeText)}</i>"
    }
}
