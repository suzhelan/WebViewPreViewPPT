package top.sacz.webviewpreviewppt.ui.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.tencent.tbs.reader.ITbsReader
import com.tencent.tbs.reader.ITbsReaderCallback
import com.tencent.tbs.reader.TbsFileInterfaceImpl
import java.io.File


@Composable
fun PPTWebPreView(
    modifier: Modifier,
    webViewState: WebViewState
) {
    AndroidView(
        modifier = modifier,
        factory = {
            val tbsView = FrameLayout(it).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
            webViewState.init(tbsView)
            tbsView
        }
    )
}

@Composable
fun rememberWebViewState(): WebViewState = remember {
    WebViewState()
}

class WebViewState() {
    enum class State {
        LOADING,
        OPEN,
        SUCCESS,
        CLOSE
    }


    private val TAG = "WebViewController"
    private lateinit var tbsView: FrameLayout

    /**
     * 需要被外部订阅更新状态的使用这个写法
     */
    var curPageIndex by mutableIntStateOf(0)
        private set
    var totalPageCount by mutableIntStateOf(0)
        private set

    var state by mutableStateOf(State.CLOSE)

    @SuppressLint("SetJavaScriptEnabled")
    fun init(webView: FrameLayout) {
        this.tbsView = webView
    }

    fun loadFile(file: File) {
        //判断状态如果没有释放则释放
        if (state != State.CLOSE) {
            TbsFileInterfaceImpl.getInstance().closeFileReader()
            state = State.CLOSE
            curPageIndex = 0
            totalPageCount = 0
        }
        //创建回调
        val callback = ITbsReaderCallback { actionType, args, result ->
            Log.i(TAG, "actionType=$actionType，args=$args，result=$result")
            if (ITbsReader.OPEN_FILEREADER_STATUS_UI_CALLBACK == actionType) {
                args as Bundle
                val id = args.getInt("typeId")
                if (ITbsReader.TBS_READER_TYPE_STATUS_UI_SHUTDOWN == id) {
                    //关闭事件
                }
                if (ITbsReader.TBS_READER_TYPE_STATUS_UI_OPENED == id) {
                    //打开事件
                    state = State.SUCCESS
                }

            }
            //页数回调
            if (ITbsReader.READER_PAGE_TOAST == actionType) {
                args as Bundle
                // cur_page获取到的是当前真实的页码，需要-1才能得到页面对应的数组下标
                curPageIndex = args.getInt("cur_page") - 1
                totalPageCount = args.getInt("page_count")
                Log.d(TAG, "totalPageCount: $totalPageCount,$curPageIndex")
            }
        }
        //获取文件后缀名
        val fileName = file.name
        val fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1)
        val param = Bundle().apply {
            //必选参数
            putString("filePath", file.absolutePath)
            putString("fileExt", fileSuffix)
            putString("tempPath", tbsView.context.cacheDir.absolutePath)
            //layout模式必选
            putInt("set_content_view_height", tbsView.height)
            //如果是ppt可以设置翻页
            if (fileSuffix == "ppt" || fileSuffix == "pptx") {
                putBoolean("file_reader_is_ppt_page_mode_default", true)
            }
        }
        TbsFileInterfaceImpl.getInstance().openFileReader(tbsView.context, param, callback, tbsView)
    }


    // 下一页
    fun goNextPage() {
        Log.d(TAG, "goNextPage: $totalPageCount,$curPageIndex")
        if (curPageIndex < totalPageCount - 1) {
            val param = Bundle()
            param.putInt("progress", curPageIndex + 1)
            TbsFileInterfaceImpl.getInstance().gotoPosition(param)
        }
    }

    // 上一页
    fun goPreviousPage() {
        if (curPageIndex > 0) {
            val param = Bundle()
            param.putInt("progress", curPageIndex - 1)
            TbsFileInterfaceImpl.getInstance().gotoPosition(param)
        }
    }


}
