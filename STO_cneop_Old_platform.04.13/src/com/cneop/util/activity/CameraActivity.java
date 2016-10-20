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

	private SurfaceView mySurfaceView;// surfaceView����
	private SurfaceHolder holder;// surfaceHolder����
	private Camera myCamera;// �������
	private String filePath = ""; // Environment.getExternalStorageDirectory() +
	// "/wjh.jpg";// "/sdcard/wjh.jpg";// ��Ƭ����·��
	boolean isClicked = false;// �Ƿ�����ʶ
	private int compressRate = 10;// //30 ��ѹ���ʣ���ʾѹ��70%; �����ѹ����100��
	// ����jpegͼƬ�ص����ݶ���
	private PictureCallback jpeg = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

			try {// ���ͼƬ
				Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);

				Bitmap bmNew = ImageUtil.zoomImg(bm, 1000, 750);// �ñ����Ǹ������ǵ�����ͷ�ȱ����ŵó��Ĵ�С
				bm.recycle();

				File file = new File(filePath);
				if (file.exists()) {
					file.delete();
				}

				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
				// bmNew.compress(Bitmap.CompressFormat.JPEG, compressRate,
				// bos);// ��ͼƬѹ��������
				// bos.close();

				// ��ͼƬ��תΪ���ķ���
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
					bos.flush();// ���
					bos.close();// �ر�
				}

				if (!newbitmap.isRecycled())
					newbitmap.recycle();
				if (!bm.isRecycled())
					bm.recycle();// ����bitmap�ռ�

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				// �����Զ��ر�
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

		if (keyCode == DeviceUtil.getLeftKeyCode() && event.getRepeatCount() == 0) {// ���
			this.takeCamera();
			return false;
		}
		return true;
	}

	private Bitmap compressImageFromFile(String srcPath) {

		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;// ֻ����,��������
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
		newOpts.inSampleSize = be;// ���ò�����

		newOpts.inPreferredConfig = Config.ARGB_8888;// ��ģʽ��Ĭ�ϵ�,�ɲ���
		newOpts.inPurgeable = true;// ͬʱ���òŻ���Ч
		newOpts.inInputShareable = true;// ����ϵͳ�ڴ治��ʱ��ͼƬ�Զ�������

		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		// return compressBmpFromBmp(bitmap);//ԭ���ķ������������������ͼ���ж���ѹ��
		// ��ʵ����Ч��,��Ҿ��ܳ���

		return bitmap;
	}

	/**
	 * ��ȡͼƬ���ԣ���ת�ĽǶ�
	 * 
	 * @param path
	 *            ͼƬ����·��
	 * @return degree��ת�ĽǶ�
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
	 * ��תͼƬ
	 * 
	 * @param angle
	 * 
	 * @param bitmap
	 * 
	 * @return Bitmap
	 */
	public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {

		// ��תͼƬ ����
		Matrix matrix = new Matrix();
		;
		matrix.postRotate(angle);
		System.out.println("angle2=" + angle);
		// �����µ�ͼƬ
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

		// �������㷽��( landscape �Ǻ���portrait ������)
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// �ޱ���

		setContentView(R.layout.activity_camera);
		// ��ÿؼ�
		mySurfaceView = (SurfaceView) findViewById(R.id.sv_camera_preview);
		mySurfaceView.setOnKeyListener(onKeyListener);
		// ��þ��
		holder = mySurfaceView.getHolder();
		// ��ӻص�
		holder.addCallback(this);
		// ��������
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		// ���ü���
		mySurfaceView.setOnClickListener(this);

		filePath = this.getIntent().getStringExtra("imagePath");
		if (filePath == null || filePath.length() == 0) {
			filePath = Environment.getExternalStorageDirectory() + "/wjh.jpg";
		}
		btn_camera_take = (Button) findViewById(R.id.btn_camera_take);
		// btn_camera_take.setOnKeyListener(onKeyListener);// ���ϴ˾��Ŀǰ��֮ǰ�����û��Ӧ
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

		// ���ò�������ʼԤ��
		setCameraParemeters();

		// myCamera.startPreview();
		startView();

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		// �������
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

		// �ر�Ԥ�����ͷ���Դ]
		if (myCamera == null)
			return;
		myCamera.stopPreview();
		myCamera.release();
		myCamera = null;

	}

	@Override
	public void onClick(View v) {
		if (!isClicked) {
			myCamera.autoFocus(this);// �Զ��Խ�
			isClicked = true;
		} else {
			startView();

		}

	}

	private boolean isPreviewing = false;// �Ƿ�����Ԥ��

	private void startView() {

		if (!isPreviewing) {
			myCamera.startPreview();// ����Ԥ��
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
		if (!btn_camera_take.isEnabled()) {// ��ֹ��ε������ɵ�ҳ�濨��
			return;
		}
		if (!isClicked) {
			myCamera.autoFocus(this);// �Զ��Խ�
			isClicked = true;
		} else {
			// myCamera.startPreview();// ����Ԥ��
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
			// ���ò���,������

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
		myCamera.setDisplayOrientation(90);// ����Ԥ���ĽǶ�

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
