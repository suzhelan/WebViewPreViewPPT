package top.sacz.webviewpreviewppt

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import top.sacz.webviewpreviewppt.ui.theme.WebViewPreViewPPTTheme
import top.sacz.webviewpreviewppt.ui.view.PPTWebPreView
import top.sacz.webviewpreviewppt.ui.view.rememberWebViewState
import top.sacz.webviewpreviewppt.util.FileUtil

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WebViewPreViewPPTTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainView(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun MainView(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val webViewState = rememberWebViewState()

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            val file = FileUtil.getFileFromUri(context, uri)
            webViewState.loadFile(file)
        }
    }
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        //选择ppt文件
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            onClick = {
                filePickerLauncher.launch(
                    arrayOf(
                        "application/vnd.ms-powerpoint",
                        "application/vnd.openxmlformats-officedocument.presentationml.presentation"
                    )
                )
            }
        ) {
            Text(text = "选择PPT文件")
        }
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            //上一页 下一页的button
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    webViewState.goPreviousPage()
                }
            ) {
                Text(text = "上一页")
            }
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    webViewState.goNextPage()
                }
            ) {
                Text(text = "下一页")
            }
        }

        Text(
            modifier = Modifier.padding(16.dp),
            text = "当前页：${webViewState.curPageIndex + 1}/${webViewState.totalPageCount}"
        )

        PPTWebPreView(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            webViewState = webViewState
        )
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WebViewPreViewPPTTheme {
        MainView()
    }
}