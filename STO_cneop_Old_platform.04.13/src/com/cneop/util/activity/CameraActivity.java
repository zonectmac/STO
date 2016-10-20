package com.cneop.util.activity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.Button;
import com.cneop.stoExpress.cneops.R;
import com.cneop.util.ImageUtil;
import com.cneop.util.device.DeviceUtil;

public class CameraActivity extends Activity implements Callback, OnClickListener, AutoFocusCallback {
	public static int g_i_cameraRequest = 1010;
	private Button btn_camera_take;
	private Button btn_camera_cancel;

	private SurfaceView mySurfaceView;// surfaceView声明
	private SurfaceHolder holder;// surfaceHolder声明
	private Camera myCamera;// 相机声明
	private String filePath = ""; // Environment.getExternalStorageDirectory() +
	// "/wjh.jpg";// "/sdcard/wjh.jpg";// 照片保存路径
	boolean isClicked = false;// 是否点击标识
	private int compressRate = 10;// //30 是压缩率，表示压缩70%; 如果不压缩是100，
	// 创建jpeg图片回调数据对象
	private PictureCallback jpeg = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

			try {// 获得图片
				Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);

				Bitmap bmNew = ImageUtil.zoomImg(bm, 1000, 750);// 该比例是根据我们的摄像头等比缩放得出的大小
				bm.recycle();

				File file = new File(filePath);
				if (file.exists()) {
					file.delete();
				}

				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
				// bmNew.compress(Bitmap.CompressFormat.JPEG, compressRate,
				// bos);// 将图片压缩到流中
				// bos.close();

				// 把图片旋转为正的方向
				Bitmap newbitmap = rotaingImageView(90, bmNew);
				newbitmap.compress(Bitmap.CompressFormat.JPEG, compressRate, bos);
				if (newbitmap != null) {
					newbitmap.recycle();
				}
				if (bmNew != null) {
					bmNew.recycle();
				}

				bmNew = compressImageFromFile(filePath);
				// iv.setImageBitmap(newbitmap);
				if (bos != null) {
					bos.flush();// 输出
					bos.close();// 关闭
				}

				if (!newbitmap.isRecycled())
					newbitmap.recycle();
				if (!bm.isRecycled())
					bm.recycle();// 回收bitmap空间

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				// 拍完自动关闭
				doSave();
			}

		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (!OnLeftKeyDown(keyCode, event)) {
			return false;
		}
		return super.onKeyDown(keyCode, event);

	}

	private boolean OnLeftKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == DeviceUtil.getLeftKeyCode() && event.getRepeatCount() == 0) {// 左键
			this.takeCamera();
			return false;
		}
		return true;
	}

	private Bitmap compressImageFromFile(String srcPath) {

		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;// 只读边,不读内容
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = 800f;//
		float ww = 480f;//
		int be = 1;
		if (w > h && w > ww) {
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置采样率

		newOpts.inPreferredConfig = Config.ARGB_8888;// 该模式是默认的,可不设
		newOpts.inPurgeable = true;// 同时设置才会有效
		newOpts.inInputShareable = true;// 。当系统内存不够时候图片自动被回收

		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		// return compressBmpFromBmp(bitmap);//原来的方法调用了这个方法企图进行二次压缩
		// 其实是无效的,大家尽管尝试

		return bitmap;
	}

	/**
	 * 读取图片属性：旋转的角度
	 * 
	 * @param path
	 *            图片绝对路径
	 * @return degree旋转的角度
	 */
	public static int readPictureDegree(String path) {

		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/*
	 * 旋转图片
	 * 
	 * @param angle
	 * 
	 * @param bitmap
	 * 
	 * @return Bitmap
	 */
	public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {

		// 旋转图片 动作
		Matrix matrix = new Matrix();
		;
		matrix.postRotate(angle);
		System.out.println("angle2=" + angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

		return resizedBitmap;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Intent intent2 = new Intent("ACTION_BAR_SCANCFG");
		intent2.putExtra("EXTRA_SCAN_POWER", 0);
		sendBroadcast(intent2);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		// 设置拍摄方向( landscape 是横向，portrait 是纵向)
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 无标题

		setContentView(R.layout.activity_camera);
		// 获得控件
		mySurfaceView = (SurfaceView) findViewById(R.id.sv_camera_preview);
		mySurfaceView.setOnKeyListener(onKeyListener);
		// 获得句柄
		holder = mySurfaceView.getHolder();
		// 添加回调
		holder.addCallback(this);
		// 设置类型
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		// 设置监听
		mySurfaceView.setOnClickListener(this);

		filePath = this.getIntent().getStringExtra("imagePath");
		if (filePath == null || filePath.length() == 0) {
			filePath = Environment.getExternalStorageDirectory() + "/wjh.jpg";
		}
		btn_camera_take = (Button) findViewById(R.id.btn_camera_take);
		// btn_camera_take.setOnKeyListener(onKeyListener);// 加上此句的目前是之前按左键没反应
		btn_camera_cancel = (Button) findViewById(R.id.btn_camera_cancel);

	}

	private OnKeyListener onKeyListener = new OnKeyListener() {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {

			if (!OnLeftKeyDown(keyCode, event)) {
				return false;
			}
			return false;
		}

	};

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

		// 设置参数并开始预览
		setCameraParemeters();

		// myCamera.startPreview();
		startView();

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		// 开启相机
		if (myCamera == null) {
			myCamera = Camera.open();
			try {
				myCamera.setPreviewDisplay(holder);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

		// 关闭预览并释放资源]
		if (myCamera == null)
			return;
		myCamera.stopPreview();
		myCamera.release();
		myCamera = null;

	}

	@Override
	public void onClick(View v) {
		if (!isClicked) {
			myCamera.autoFocus(this);// 自动对焦
			isClicked = true;
		} else {
			startView();

		}

	}

	private boolean isPreviewing = false;// 是否正在预览

	private void startView() {

		if (!isPreviewing) {
			myCamera.startPreview();// 开启预览
			isPreviewing = true;
			isClicked = false;
		}

	}

	public void uiOnClick(View v) {

		switch (v.getId()) {
		case R.id.btn_camera_cancel: {
			File file = new File(filePath);
			if (file.exists()) {
				file.delete();
			}
			this.finish();
		}
			break;
		case R.id.btn_camera_take: {
			takeCamera();

		}
			break;

		}

	}

	private void takeCamera() {
		if (!btn_camera_take.isEnabled()) {// 防止多次点击，造成的页面卡死
			return;
		}
		if (!isClicked) {
			myCamera.autoFocus(this);// 自动对焦
			isClicked = true;
		} else {
			// myCamera.startPreview();// 开启预览
			startView();
			isClicked = false;
		}
	}

	private void doSave() {

		setResult(RESULT_OK);
		this.finish();
	}

	@Override
	public void onAutoFocus(boolean success, Camera camera) {

		if (success) {
			// 设置参数,并拍照

			// myCamera.setDisplayOrientation(270);
			// myCamera.setDisplayOrientation ( 90 );
			// btn_camera_take.setEnabled(false);
			setBtnEnable(false);
			myCamera.takePicture(null, null, jpeg);
		}

	}

	private void setBtnEnable(boolean enabled) {

		btn_camera_cancel.setEnabled(enabled);
		btn_camera_take.setEnabled(enabled);
	}

	private void setCameraParemeters() {

		Camera.Parameters params = myCamera.getParameters();
		params.setPictureFormat(PixelFormat.JPEG);
		// /params.setPreviewSize(640, 480);
		// params.set("orientation", "portrait");
		// params.set("orientation", "landscape");
		// params.set("rotation", 90);

		myCamera.setParameters(params);
		myCamera.setDisplayOrientation(90);// 设置预览的角度

		// Camera.Parameters params = myCamera.getParameters();
		// params.setPictureFormat(PixelFormat.JPEG);
		// params.setPreviewSize(640, 480);
		// params.set("orientation", "portrait");
		// params.set("orientation", "landscape");
		// params.set("orientation", "landscape");
		// params.set("rotation", 90);

		// myCamera.setParameters(params);
		// setCameraDisplayOrientation(MainActivity.this,0,myCamera);
		// myCamera.setDisplayOrientation(90);
	}

}
