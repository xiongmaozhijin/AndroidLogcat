# AndroidLogcat
在手机端实现logcat显示，解决不方便通过数据线等原因无法查看log的问题。


功能点：
1. 手机端实时查看logcat信息
2. 自定义保存指定tag的信息，不用增加改变原有的任何代码

Github地址：
https://github.com/xiongmaozhijin/AndroidLogcat


首先引用依赖：
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

	dependencies {
	        implementation 'com.github.xiongmaozhijin:AndroidLogcat:2.0.0.1'
	}
```

首先在适当位置进行初始化：
```
    ILogcatManager.getsInstance().init(getApplicationContext());
```

如果想要在应用中打开全局悬浮框，实时查看，调用下面一句代码即可：
```
     ILogcatManager.getsInstance().getLogcatWindow().show();
```

当然首先要申请悬浮框权限：
```
       startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
```



如果想要把log文件保存在手机中，调用下面一句代码，即可：
```
    ILogcatManager.getsInstance().enableLocalStorage();
```
默认保持的文件位置在 `context.getExternalCacheDir()).getAbsolutePath()`.


文件保存更多设置：
```
        LogcatParam localStorageParam = ILogcatManager.getsInstance().getLocalStorageParam();
        localStorageParam.setLogTags("MonitorTag1", "MonitorTag2"); // 要过滤的tag，传null则不过滤
        localStorageParam.logLevel = "D"; // logcat loglevel
        localStorageParam.logStorageDir = getApplicationContext().getExternalCacheDir().getAbsolutePath(); // log保存的文件夹
        ILogcatManager.getsInstance().enableLocalStorage(); // 开启记录
```

UI显示更多设置：
```
        ILogcatManager.getsInstance().setLogcatTag("UiMonitorTag1", "UiMonitorTag2"); // ui界面过滤的logtag
        ILogcatManager.getsInstance().setKeyword("keyword"); // 要过滤的keyword
        ILogcatManager.getsInstance().setLogLevel("V"); // logcat loglevel
        ILogcatManager.getsInstance().setWindowDialog(new SimpleWindowDialog(getApplicationContext())); // 实现自己的显示悬浮框
        ILogcatManager.getsInstance().getLogcatWindow().show(); // 显示
```



效果图：
![device-2021-08-15-092819.png](https://upload-images.jianshu.io/upload_images/14299996-20fdeae181f50508.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![Screenshot_2021-07-25-22-38-06-008_com.xiongmaozhijin.androidlogcat.jpg](https://upload-images.jianshu.io/upload_images/14299996-f9b7d70a7d9f3af8.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)    ![Screenshot_2021-07-25-22-36-36-185_com.xiongmaozhijin.androidlogcat.jpg](https://upload-images.jianshu.io/upload_images/14299996-51b4ae15d7bbd9c7.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)![Screenshot_2021-07-25-22-36-44-609_com.xiongmaozhijin.androidlogcat.jpg](https://upload-images.jianshu.io/upload_images/14299996-fd708042dc4c14c2.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![Screenshot_2021-07-25-22-36-58-756_com.xiongmaozhijin.androidlogcat.jpg](https://upload-images.jianshu.io/upload_images/14299996-786f823fd2eac765.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)    ![Screenshot_2021-07-25-22-38-01-051_com.xiongmaozhijin.androidlogcat.jpg](https://upload-images.jianshu.io/upload_images/14299996-4d156ca9b193d88f.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
