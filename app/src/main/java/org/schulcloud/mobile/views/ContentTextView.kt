package org.schulcloud.mobile.views

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes
import org.jsoup.Jsoup
import org.schulcloud.mobile.R

open class ContentTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.contentTextViewStyle
) : CompatTextView(context, attrs, defStyleAttr) {

    companion object {
        val TAG: String = ContentTextView::class.java.simpleName
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        val raw = Jsoup.parse(text?.toString() ?: "")
                .wholeText()
                .trim()
                .replace("\r\n\r\n", "\r\n")
        super.setText(raw, type)
    }
}
