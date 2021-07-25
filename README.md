# AndroidLogcat
在手机端简单实现logcat显示，解决不方便通过数据线等原因无法查看log的问题。

首先引用依赖：
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

	dependencies {
	        implementation 'com.github.xiongmaozhijin:AndroidLogcat:Tag'
	}
```

然后调用即可调用即可：
```
AndroidLogcatManager.getsInstance().init(getApplicationContext());
AndroidLogcatManager.getsInstance().showMonitor(this);
```

效果图：

![Screenshot_2021-07-25-22-38-06-008_com.xiongmaozhijin.androidlogcat.jpg](https://upload-images.jianshu.io/upload_images/14299996-f9b7d70a7d9f3af8.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)    ![Screenshot_2021-07-25-22-36-36-185_com.xiongmaozhijin.androidlogcat.jpg](https://upload-images.jianshu.io/upload_images/14299996-51b4ae15d7bbd9c7.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)![Screenshot_2021-07-25-22-36-44-609_com.xiongmaozhijin.androidlogcat.jpg](https://upload-images.jianshu.io/upload_images/14299996-fd708042dc4c14c2.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![Screenshot_2021-07-25-22-36-58-756_com.xiongmaozhijin.androidlogcat.jpg](https://upload-images.jianshu.io/upload_images/14299996-786f823fd2eac765.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)    ![Screenshot_2021-07-25-22-38-01-051_com.xiongmaozhijin.androidlogcat.jpg](https://upload-images.jianshu.io/upload_images/14299996-4d156ca9b193d88f.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
