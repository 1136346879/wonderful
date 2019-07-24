package com.xgr.wonderful;

import java.io.File;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;
import cn.bmob.v3.BmobUser;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.xgr.wonderful.entity.QiangYu;
import com.xgr.wonderful.entity.User;
import com.xgr.wonderful.utils.ActivityManagerUtils;

public class MyApplication extends MultiDexApplication {

	public static String TAG;
	public static String APPID = "de4c1e74c8949c9b0b91ba7017b7671e";

	private static MyApplication myApplication = null;
	
	private QiangYu currentQiangYu = null;
	
	public static MyApplication getInstance(){
		return myApplication;
	}
	public User getCurrentUser() {
		User user = BmobUser.getCurrentUser( User.class);
		if(user!=null){
			return user;
		}
		return null;
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		TAG = this.getClass().getSimpleName();
		//由于Application类本身已经单例，所以直接按以下处理即可。
		myApplication = this;
		initImageLoader();
		/**
		 * TODO 2.1、SDK初始化方式一
		 */

		Bmob.initialize(this, APPID);

		/**
		 * TODO 2.2、SDK初始化方式二
		 * 设置BmobConfig，允许设置请求超时时间、文件分片上传时每片的大小、文件的过期时间
		 */
		BmobConfig config = new BmobConfig.Builder(this)
				//设置APPID
				.setApplicationId(APPID)
				//请求超时时间（单位为秒）：默认15s
				.setConnectTimeout(30)
				//文件分片上传时每片的大小（单位字节），默认512*1024
				.setUploadBlockSize(1024 * 1024)
				//文件的过期时间(单位为秒)：默认1800s
				.setFileExpiration(5500)
				.build();
		Bmob.initialize(config);
	}

	
	
	public QiangYu getCurrentQiangYu() {
		return currentQiangYu;
	}

	public void setCurrentQiangYu(QiangYu currentQiangYu) {
		this.currentQiangYu = currentQiangYu;
	}

	public void addActivity(Activity ac){
		ActivityManagerUtils.getInstance().addActivity(ac);
	}
	
	public void exit(){
		ActivityManagerUtils.getInstance().removeAllActivity();
	}
	
	public Activity getTopActivity(){
		return ActivityManagerUtils.getInstance().getTopActivity();
	}
	
	/**
	 * 初始化imageLoader
	 */
	public void initImageLoader(){
		File cacheDir = StorageUtils.getCacheDirectory(getApplicationContext());
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
										.memoryCache(new LruMemoryCache(5*1024*1024))
										.memoryCacheSize(10*1024*1024)
										.discCache(new UnlimitedDiscCache(cacheDir))
										.discCacheFileNameGenerator(new HashCodeFileNameGenerator())
										.build();
		ImageLoader.getInstance().init(config);
	}
	
	public DisplayImageOptions getOptions(int drawableId){
		return new DisplayImageOptions.Builder()
		.showImageOnLoading(drawableId)
		.showImageForEmptyUri(drawableId)
		.showImageOnFail(drawableId)
		.resetViewBeforeLoading(true)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.imageScaleType(ImageScaleType.EXACTLY)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}
}
