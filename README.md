## Android 展示PPT Demo

PPT可以算是展示最费劲的文档格式了,如果是pdf还好说,安卓自带的都可以转换为一堆图片并分页展示  
现在整理了几个展示ppt的方案  

要求  
 - 1.能正常获取到页数,并在外部实现上一页和下一页的按钮
 - 2.清晰度不能损失,尽可能清晰
 - 3.流畅和优秀的性能

appDemo 展示的现有方案是使用腾讯浏览服务,预览文档实现翻页功能

根据资料收集 还有几个方案可以选择  
 - 1.使用腾讯浏览服务,其中包含本地文档预览功能,并且支持ppt上下翻页,解析性能优秀(缺点:付费的,大概499 = 15万次,可以免费领取试用包)
 - 2.后端解析服务,让后端使用ppt解析为图片插件,解析好后将解析好的图片链接传给前端,前端也能进行分页展示
 - 3.使用微软的在线预览插件
src参数为文档链接,可以是任意格式,两个样式各有差异
 ```text
https://view.officeapps.live.com/op/view.aspx?src=xxx.xlsx
https://view.officeapps.live.com/op/embed.aspx?src=xxx.xlsx
```
我的推荐使用:(可以上下翻页ppt),优点:又免费又省事
```kotlin
                    val webView = WebView(it).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                    webView.webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView,
                            request: WebResourceRequest
                        ): Boolean {
                            return true
                        }
                    }
                    val webSettings = webView.settings
                    webSettings.domStorageEnabled = true
                    webSettings.databaseEnabled = true
                    webView.settings.useWideViewPort = true
                    webView.settings.loadWithOverviewMode = true
                    //支持js
                    webView.settings.javaScriptEnabled = true

                    // 解决图片不显示
                    webView.settings.blockNetworkImage = false
                    // 自适应屏幕
                    webView.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
                    // 设置可以支持缩放
                    webView.settings.setSupportZoom(true)
                    webView.settings.loadWithOverviewMode = true //自适应屏幕
                    // 加载
                    webView.loadUrl("https://view.officeapps.live.com/op/view.aspx?src=${pptUrl}")
```
