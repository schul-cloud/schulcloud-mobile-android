package org.schulcloud.mobile.views

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.AttrRes
import okhttp3.Request
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.R
import org.schulcloud.mobile.utils.*
import java.io.IOException
import kotlin.properties.Delegates

open class ContentWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr) {

    companion object {
        val TAG: String = ContentWebView::class.java.simpleName

        // language=HTML
        const val CONTENT_TEXT_FORMAT = """
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <style>
        * {
            max-width: 100%%;
            word-wrap: break-word;
        }
        body {
            margin: 0;
            font-family: 'Roboto', sans-serif;
            color: %2${"$"}s;
        }
        body a {
            color: %3${"$"}s;
            text-decoration: none;
        }
        body > :first-child {
            margin-top: 0;
        }
        body > :nth-last-child(2) {
            margin-bottom: 0;
        }
        table {
            table-layout: fixed;
            width: 100%%;
        }
        ul {
            -webkit-padding-start: 25px;
        }
    </style>
</head>
<body>
    %1${"$"}s
    <script>
    for (tag of document.body.getElementsByTagName('*')) {
        tag.style.width = '';
        tag.style.height = '';
    }
</script>
</body>
</html>"""
    }

    var content by Delegates.observable<String?>(null) { _, _, _ ->
        onContentUpdated()
    }
    var contentFallback by Delegates.observable<String?>(null) { _, _, _ ->
        onContentUpdated()
    }

    init {
        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            WebView.setWebContentsDebuggingEnabled(true)

        @Suppress("SetJavaScriptEnabled")
        settings.javaScriptEnabled = true
        webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return openUrl(Uri.parse(url))
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                return openUrl(request.url)
            }

            private fun openUrl(url: Uri): Boolean {
                if (url.toString() == getUrl())
                    return false

                context.openUrl(url)
                return true
            }

            override fun shouldInterceptRequest(view: WebView, url: String): WebResourceResponse? {
                return handleRequest(url)
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? {
                return handleRequest(request.url.toString())
            }

            private fun handleRequest(url: String): WebResourceResponse? {
                return try {
                    val response = HTTP_CLIENT.newCall(Request.Builder().url(url).build()).execute()
                    WebResourceResponse(
                            response.header(HEADER_CONTENT_TYPE)?.substringBefore(';'),
                            response.header(HEADER_CONTENT_ENCODING, ENCODING_UTF_8),
                            response.body()?.byteStream())
                } catch (e: IOException) {
                    null
                } catch (e: IllegalStateException) {
                    null
                } catch (e: IllegalArgumentException) {
                    null
                }
            }
        }
    }

    override fun loadUrl(url: String?) {
        clear()
        super.loadUrl(url)
    }

    override fun loadUrl(url: String?, additionalHttpHeaders: MutableMap<String, String>?) {
        clear()
        super.loadUrl(url, additionalHttpHeaders)
    }

    override fun loadData(data: String?, mimeType: String?, encoding: String?) {
        clear()
        super.loadData(data, mimeType, encoding)
    }

    override fun loadDataWithBaseURL(
        baseUrl: String?,
        data: String?,
        mimeType: String?,
        encoding: String?,
        historyUrl: String?
    ) {
        clear()
        super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl)
    }

    private fun clear() {
        setBackgroundColor(Color.TRANSPARENT)
        // Clear content to fix size if new size is smaller than current one
        super.loadDataWithBaseURL(null, null, MIME_TEXT_HTML, ENCODING_UTF_8, null)
    }


    fun setUrl(url: String?) {
        loadUrl(url)
    }

    private fun onContentUpdated() {
        val content = content.takeUnless { content.isNullOrBlank() } ?: contentFallback ?: ""
        loadDataWithBaseURL(HOST,
                CONTENT_TEXT_FORMAT.format(
                        content,
                        context.getColorFromAttr(R.attr.colorOnBackground).cssColor,
                        context.getColorFromAttr(R.attr.colorOnBackgroundPrimary).cssColor),
                MIME_TEXT_HTML, ENCODING_UTF_8, null)
    }
}
