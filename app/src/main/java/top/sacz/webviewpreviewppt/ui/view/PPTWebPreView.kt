package top.sacz.webviewpreviewppt.ui.view

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView


@Composable
fun PPTWebPreView(
    modifier: Modifier,
    webViewController: WebViewController
) {
    AndroidView(
        modifier = modifier,
        factory = {
            val webView = WebView(it).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
            webViewController.init(webView)
            webView
        }
    )
}

fun rememberWebViewController() = WebViewController()

class WebViewController() {
    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    fun init(webView: WebView) {
        this.webView = webView
        //支持js
        webView.settings.javaScriptEnabled = true
        // 解决图片不显示
        webView.settings.blockNetworkImage = false
        webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        //自适应屏幕
        webView.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        webView.settings.loadWithOverviewMode = true
        //设置可以支持缩放
        webView.settings.setSupportZoom(false)
        //扩大比例的缩放
        webView.settings.useWideViewPort = false
        //设置是否出现缩放工具
        webView.settings.builtInZoomControls = false
        //解决白屏问题，原因不明
        webView.settings.domStorageEnabled = true
        webView.loadUrl("file:///android_asset/web-preview-ppt.html")
    }

    fun loadDocxFromBase64(base64: String) {
        webView.evaluateJavascript(
            "window.DocxViewer.loadFromBase64('$base64')",
            null
        )
    }

    // 上一页
    fun goPreviousPage() {
        webView.evaluateJavascript("window.DocxViewer.prevPage()", null)
    }

    // 下一页
    fun goNextPage() {
        webView.evaluateJavascript("window.DocxViewer.nextPage()", null)
    }

    // 获取当前页码
    fun getCurrentPage(callback: (Int) -> Unit) {
        webView.evaluateJavascript("window.DocxViewer.getCurrentPage()") { result ->
            val page = result.removeSurrounding("\"").toIntOrNull() ?: 0
            callback(page)
        }
    }

}
