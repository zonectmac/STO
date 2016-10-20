package com.cneop.stoExpress.activity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.activity.scan.ScanBaseActivity;
import com.cneop.util.PromptUtils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.ImageView;

public class ShowImageActivity extends Activity {

	ImageView ivPicture;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_showimage);

		ivPicture = (ImageView) findViewById(R.id.large_image);

		String path = this.getIntent().getStringExtra(ScanBaseActivity.ImagePath);
		ShowLargeImage(path);

	}

	private void ShowLargeImage(String iamgepath) {

		File fileTemp = new File(iamgepath);
		if (fileTemp.exists()) {
			Display display = getWindowManager().getDefaultDisplay();
			Bitmap bm = BitmapFactory.decodeFile(iamgepath);
			if (bm == null) {

				PromptUtils.getInstance().showToast("图片损坏，查看失败", ShowImageActivity.this);
				return;
			}
			int width = bm.getWidth();
			int height = bm.getHeight();
			int w = display.getWidth();
			int h = display.getHeight();
			float scaleWidth = ((float) w) / width;
			float scaleHeight = ((float) h) / height;
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			if (bm != null) {
				// releaseImage();
				Bitmap newBitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
				// 想图像显示在ImageView视图上
				ivPicture.setImageBitmap(newBitmap);
			}
			if (!bm.isRecycled()) {
				bm.recycle();
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			finish();
			break;
		}
		return super.onTouchEvent(event);
	}
}
