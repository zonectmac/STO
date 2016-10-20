package com.cneop.stoExpress.activity.scan;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.cneop.Date.DATE;
import com.cneop.stoExpress.activity.ShowImageActivity;
import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.common.Enums.EBarcodeType;
import com.cneop.stoExpress.common.Enums.EScanType;
import com.cneop.stoExpress.common.Enums.EUploadType;
import com.cneop.stoExpress.common.Enums.EVoiceType;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.datacenter.AddScanDataThread;
import com.cneop.stoExpress.model.ScanDataModel;
import com.cneop.stoExpress.util.ScreenUtil;
import com.cneop.util.ImageUtil;
import com.cneop.util.PromptUtils;
import com.cneop.util.StrUtil;
import com.cneop.util.activity.CameraActivity;
import com.cneop.util.controls.ListViewEx;
import com.cneop.util.file.FileUtil;
import com.cneop.util.scan.ScanManager;
import com.math.math;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class SignaturePhotoActivity extends ScanBaseActivity {

	private EditText etSignType, etSigner;
	private Button btnPhoto;
	private Checkable cksign;
	private String strPreScanedBar = "";// 上一次扫描的单号
	private ImageView ivPicture;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.activity_signaturephoto_scan);
		setTitle("签收扫描");
		super.scannerPower = true;
		super.onCreate(savedInstanceState);
		ScanManager.getInstance().getScanner().setScanResultHandler(handleScanData);
	}

	/**
	 * 扫描数据
	 */
	@SuppressWarnings("static-access")
	@Override
	protected void setScanData(String barcode) {

		PromptUtils.getInstance().closeAlertDialog();
		if (etBarcode != null) {
			etBarcode.setText(barcode);
			boolean flag = new math().CODE1(etBarcode.getText().toString().trim());
			if (flag) {
				addRecord();
			} else {
				PromptUtils.getInstance().showToastHasFeel("非法条码", this, EVoiceType.fail, 0);
				etBarcode.setText("");
				etBarcode.requestFocus();
			}

		}
	}

	@Override
	protected void initializeComponent() {

		super.initializeComponent();
		etSignType = bindEditText(R.id.et_sign_type_lly1, null, null);

		etSignType.setText("拍照签收");

		etSigner = bindEditText(R.id.et_sign_type_lly2, null, null);
		btnPhoto = bindButton(R.id.btn_photograph_btnPhoto);

		cksign = (CheckBox) findViewById(R.id.ck_signphoto);

		etBarcode = bindEditText(R.id.et_barcode_etBarcode, null, null);
		btnAdd = bindButton(R.id.btn_barcode_btnAdd);
		ivPicture = (ImageView) this.findViewById(R.id.iv_photograph_ivPic);
		setImageEmpty();
		ivPicture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!strUtil.isNullOrEmpty(newPath)) {
					Intent intent = new Intent(SignaturePhotoActivity.this, ShowImageActivity.class);
					intent.putExtra(ScanBaseActivity.ImagePath, newPath);
					startActivity(intent);
				}
			}
		});
	}

	@Override
	protected void onDestroy() {

		if (bb != null) {
			bb.recycle();
		}
		super.onDestroy();
	}

	@Override
	protected void onOtherResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK && requestCode == CameraActivity.g_i_cameraRequest) {
			newPath = GlobalParas.getGlobalParas().getSignUnUploadPath() + "/" + strPreScanedBar + ".jpg";
			System.out.println("==========newpath   " + newPath);
			File fileTemp = new File(newPath);
			if (fileTemp.exists()) {
				// 照片缩略图
				Bitmap bm = BitmapFactory.decodeFile(newPath);
				if (bm != null) {
					if (bb != null) {
						bb.recycle();
					}
					// releaseImage();
					bb = ThumbnailUtils.extractThumbnail(bm, 320, 240);
					// 想图像显示在ImageView视图上
					ivPicture.setImageBitmap(bb);
				}
				if (!bm.isRecycled()) {
					bm.recycle();
				}
			}

		}
	}

	private void setImageEmpty() {

		ivPicture.setImageResource(R.drawable.no_pic);
		newPath = "";
		strPreScanedBar = "";
	}

	@Override
	protected void refreshImgCtrl(String barcode) {

		if (strPreScanedBar.equals(barcode)) {
			setImageEmpty();
		}

	};

	String newPath = "";

	@Override
	protected void initListView() {

		lvx = (ListViewEx) findViewById(R.id.lv_signphoto_lvBarcodeList);
		lvx.inital(R.layout.list_item_three, new String[] { barcodeKey, sigerKey, signTypeKey }, new int[] {
				R.id.tv_list_item_three_tvhead1, R.id.tv_list_item_three_tvhead2, R.id.tv_list_item_three_tvhead3 });
	}

	@Override
	protected void setHeadTitle() {

		TextView tvHead1 = (TextView) this.findViewById(R.id.tv_list_head_three_tvhead1);
		TextView tvHead2 = (TextView) this.findViewById(R.id.tv_list_head_three_tvhead2);
		TextView tvHead3 = (TextView) this.findViewById(R.id.tv_list_head_three_tvhead3);
		tvHead1.setText("单号");
		tvHead2.setText("签收人 ");
		tvHead3.setText("签收类型");
		tvHead1.setLayoutParams(
				new LayoutParams(ScreenUtil.dip2px(SignaturePhotoActivity.this, 130), LayoutParams.WRAP_CONTENT));
	}

	@Override
	protected void initScanCode() {

		scanType = EScanType.QS;
		uploadType = EUploadType.scanData;
	}

	@Override
	protected void uiOnClick(View v) {

		super.uiOnClick(v);
		switch (v.getId()) {
		case R.id.btn_photograph_btnPhoto:
			openCamera();
			break;
		case R.id.btn_barcode_btnAdd:
			if (!DATE.guard_time(getApplicationContext())) {
				return;
			}
			if (etBarcode != null) {
				if (etSigner.getText().toString().trim().length() <= 0) {
					Toast.makeText(getApplicationContext(), "签收人不能为空", 1).show();
					etSigner.requestFocus();
				} else {
					etBarcode.setText(etBarcode.getText().toString().trim());
					boolean flag = new math().CODE1(etBarcode.getText().toString().trim());
					if (flag) {
						addRecord();
					} else {
						Toast.makeText(getApplicationContext(), "非法条码", Toast.LENGTH_SHORT).show();
						etBarcode.setText("");
						etBarcode.requestFocus();
					}
				}

			}
			break;

		}
	}

	private void openCamera() {

		if (StrUtil.isNullOrEmpty(strPreScanedBar)) {
			PromptUtils.getInstance().showToast("请先扫描单号再拍照", this);
			return;
		}
		// ScanManager.getInstance().setisDoingOtherOpt(true);
		String newPath = GlobalParas.getGlobalParas().getProblemUnUploadPath() + strPreScanedBar + ".jpg";
		Intent intent = new Intent(this, CameraActivity.class);
		intent.putExtra("imagePath", newPath);
		startActivityForResult(intent, CameraActivity.g_i_cameraRequest);
	}

	Bitmap mBitmap;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CameraActivity.g_i_cameraRequest) {
			if (resultCode == CameraActivity.RESULT_OK) {
				Map<String, Object> map = lvx.GetValue(0);
				if (map != null) {
					// 保存图片
					newPath = GlobalParas.getGlobalParas().getProblemUnUploadPath() + strPreScanedBar + ".jpg";
					String barcode = map.get(barcodeKey).toString();
					String destPath = GlobalParas.getGlobalParas().getProblemUnUploadPath() + barcode
							+ GlobalParas.getGlobalParas().getImageSuffix();
					if (FileUtil.isExist(destPath, false) == 0) {
						// 更新图片数量
						brocastUtil.sendUnUploadCountChange(1, EUploadType.pic);
					}
					FileUtil.copyFile(GlobalParas.getGlobalParas().getPhotoPath(), destPath);

					// 显示缩略图
					System.out.println("==================path \t" + GlobalParas.getGlobalParas().getPhotoPath());
					mBitmap = ImageUtil.decodeSampledBitmapFromPath(destPath, 115, 110);
					ivPicture.setImageBitmap(BitmapFactory.decodeFile(destPath));
				}
			}
		}
	}

	private Bitmap bb;

	@Override
	protected void addRecord() {

		controlUtil.setEditVeiwFocus(etBarcode);
		String barcode = etBarcode.getText().toString().trim();
		int existsBarcode = lvx.isExists(barcode, barcodeKey);//获取listview列表单号位置
		if (existsBarcode>=0) {//判断单号是否在列表已经存在
			PromptUtils.getInstance().showToastHasFeel("单号：" + barcode + "已扫描00！", this, EVoiceType.fail, 0);
			return;
		}
		String signer = etSigner.getText().toString().trim();
		String signtype = etSignType.getText().toString().trim();
		if (StrUtil.isNullOrEmpty(signer)) {
			signer = signtype;// 如果签收人为空，则签收人＝签收类型的值(即拍照签收)
		}
		signer = StrUtil.FilterSpecial(signer);
		if (!validateInput()) {
			return;
		}

		ScanDataModel scanDataModel = new ScanDataModel();
		scanDataModel.setScanCode(scanType.value());
		scanDataModel.setBarcode(barcode);
		scanDataModel.setScanUser(GlobalParas.getGlobalParas().getUserNo());
		scanDataModel.setSigner(signer);
		scanDataModel.setSignTypeCode(signtype);
		scanDataModel.setSiteProperties(String.valueOf(siteProperties.value()));
		// 拍照签收要先过滤是否重复的原因是:在线程中增加成功后,会弹出拍照的提示框,
		// 原来的程序是:如果已重复也会弹出,但只是ListView上的数据被删除而已
		if (AddScanDataThread.getIntance(SignaturePhotoActivity.this).isExists(scanDataModel)) {

			PromptUtils.getInstance().showToastHasFeel("单号：" + barcode + "已扫描！", this, EVoiceType.fail, 0);
			return;
		}

		// 添加到插入队列中
		AddScanDataThread.getIntance(SignaturePhotoActivity.this).add(scanDataModel);

		// 更新界面
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(barcodeKey, barcode);
		map.put(sigerKey, signer);
		map.put(signTypeKey, signtype);
		lvx.add(map);

		scanCount++;
		updateView();
		if (!cksign.isChecked()) {
			etSigner.setText("");
		}

		strPreScanedBar = barcode;
		openCamera();// 扫描

	}

	@Override
	protected boolean validateInput() {

		if (!validateBarcode(etBarcode, EBarcodeType.barcode, barcodeValidateErrorTip)) {
			return false;
		}
		if (!validateSignerInput()) {
			return false;
		}
		return true;
	}

	private boolean validateSignerInput() {

		boolean flag = true;
		String signer = etSigner.getText().toString().trim();
		if (!strUtil.isNullOrEmpty(signer)) {
			if (signer.length() > 14) {
				PromptUtils.getInstance().showToast("签收人的长度不能超过14位", SignaturePhotoActivity.this);
				flag = false;
			}
		}
		return flag;
	}

}
