package com.cneop.stoExpress.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.activity.scan.ScanBaseActivity;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.dao.PhotoService;
import com.cneop.stoExpress.model.PhotoModel;
import com.cneop.stoExpress.common.Enums.*;
import com.cneop.stoExpress.util.BrocastUtil;
import com.cneop.stoExpress.util.ControlUtil;
import com.cneop.util.ImageUtil;
import com.cneop.util.PromptUtils;
import com.cneop.util.activity.CommonTitleActivity;
import com.cneop.util.controls.ListViewEx;
import com.cneop.util.file.FileUtil;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class PicQueryActivity extends CommonTitleActivity {

	Button btnView;
	Button btnDelete;
	Button btnBack;
	ListViewEx lvx;
	TextView tvHead1;
	TextView tvHead2;
	String uploadStatus = "";
	private String ITEM_UPLOADSTATUS = "ITEM_UPLOADSTATUS";
	private String ITEM_BARCODE = "ITEM_BARCODE";
	EPicType picType;
	String barcode;
	String startTime;
	String endTime;
	// String uploadStatus;
	PhotoService photoService;
	ProgressDialog pd;
	List<Map<String, Object>> sourceList;
	BrocastUtil brocastUtil;
	Display display;
	String strSql = "";
	ControlUtil controlUtil = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_pic_query);
		setTitle("图片查看");
		controlUtil = new ControlUtil();
		super.onCreate(savedInstanceState);

	}

	@Override
	protected void initializeComponent() {

		super.initializeComponent();
		btnView = bindButton(R.id.bottom_3_btnUpload);
		btnDelete = bindButton(R.id.bottom_3_btnDel);
		btnBack = bindButton(R.id.bottom_3_btnBack);
		btnView.setText("查看");
		tvHead1 = (TextView) this.findViewById(R.id.tv_list_head_two_tvhead1);
		tvHead2 = (TextView) this.findViewById(R.id.tv_list_head_two_tvhead2);
		controlUtil.setControlLayoutWidth(tvHead1, 130, PicQueryActivity.this);

		lvx = (ListViewEx) this.findViewById(R.id.lv_pic_query_lv);
		lvx.inital(R.layout.list_item_two_a, new String[] { ITEM_UPLOADSTATUS, ITEM_BARCODE }, new int[] { R.id.tv_list_item_two_a_tvhead1, R.id.tv_list_item_two_a_tvhead2 });
		display = getWindowManager().getDefaultDisplay();
	}

	@Override
	protected void initializeValues() {

		super.initializeValues();
		Intent intent = getIntent();
		String businessType = intent.getStringExtra(DataQueryActivity.BUSINESSTYPE_NAME);
		barcode = intent.getStringExtra(DataQueryActivity.BAROCDE);
		startTime = intent.getStringExtra(DataQueryActivity.STARTTIME);
		endTime = intent.getStringExtra(DataQueryActivity.ENDTIME);
		uploadStatus = intent.getStringExtra(DataQueryActivity.SYNCSTATUS);
		strSql = intent.getStringExtra("sql");
		tvHead1.setText("状态");
		tvHead2.setText("名称(" + businessType + ")");
		picType = EPicType.problem;
		if (businessType.contains("签收")) {
			picType = EPicType.sign;
		}
		brocastUtil = new BrocastUtil(PicQueryActivity.this);
		photoService = new PhotoService(GlobalParas.getGlobalParas().getSignUnUploadPath(), GlobalParas.getGlobalParas().getSignUploadPath(), GlobalParas.getGlobalParas().getProblemUnUploadPath(), GlobalParas.getGlobalParas().getProblemUploadPath(), GlobalParas.getGlobalParas().getImageSuffix());

		QueryPicThread queryPicThread = new QueryPicThread();
		queryPicThread.isDaemon();
		queryPicThread.start();
		pd = ProgressDialog.show(PicQueryActivity.this, "提示", "正在查询，请稍候...");
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);
			// 加载界面
			lvx.add(sourceList);
			if (pd != null) {
				pd.dismiss();
			}
		}

	};

	private class QueryPicThread extends Thread {

		@Override
		public void run() {

			super.run();
			List<PhotoModel> photoModelList = photoService.queryDetail(barcode, startTime, endTime, uploadStatus, picType);// strSql
																															// 拍照签收的不能根据用户来查询
			sourceList = new ArrayList<Map<String, Object>>();
			if (photoModelList != null && photoModelList.size() > 0) {
				for (PhotoModel photoModel : photoModelList) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put(ITEM_BARCODE, photoModel.getBarcode());
					map.put(ITEM_UPLOADSTATUS, photoModel.getUploadStatus());
					sourceList.add(map);
				}
			}
			handler.sendEmptyMessage(0);
		}
	}

	@Override
	protected void uiOnClick(View v) {

		super.uiOnClick(v);
		switch (v.getId()) {
		case R.id.bottom_3_btnUpload:
			viewPic();
			break;
		case R.id.bottom_3_btnDel:
			if (GlobalParas.getGlobalParas().getUserRole() == null ||GlobalParas.getGlobalParas().getUserRole() == EUserRole.other) {
				deleteListData();
			} else {
				delete();
			}
			break;
		case R.id.bottom_3_btnBack:
			setResult(100);
			back();
			break;
		}
	}

	private void deleteData() {

	}

	private void delete() {

		final Map<String, Object> map = lvx.GetSelValue();
		if (map == null) {
			PromptUtils.getInstance().showToastHasFeel("请选择要删除的一项！", PicQueryActivity.this, EVoiceType.fail, 0);
			return;
		}
		String t_uplaodStatus = map.get(ITEM_UPLOADSTATUS).toString().trim();
		if (t_uplaodStatus.equals("已上传")) {
			PromptUtils.getInstance().showToastHasFeel("已上传的图片不能删除！", PicQueryActivity.this, EVoiceType.fail, 0);
			return;
		}
		final String barcode = map.get(ITEM_BARCODE).toString().trim();
		String t_path = "";
		if (picType == EPicType.sign) {
			t_path = GlobalParas.getGlobalParas().getSignUnUploadPath() + barcode + ".jpg";
		} else {
			t_path = GlobalParas.getGlobalParas().getProblemUnUploadPath() + barcode + ".jpg";
		}
		final String path = t_path;
		PromptUtils.getInstance().showAlertDialog(PicQueryActivity.this, "确定要删除吗？", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				if (FileUtil.delFile(path)) {
					lvx.delete(barcode, ITEM_BARCODE);

					brocastUtil.sendUnUploadCountChange(-1, EUploadType.pic);

				} else {
					PromptUtils.getInstance().showToastHasFeel("删除失败！", PicQueryActivity.this, EVoiceType.fail, 0);
				}
			}
		}, null);
	}

	int UnuploadPicCount = 0;

	private void deleteListData() {

		PromptUtils.getInstance().showAlertDialog(PicQueryActivity.this, "是否删除以上图片", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				for (int i = 0; i < sourceList.size(); i++) {
					String barcode = sourceList.get(i).get(ITEM_BARCODE).toString().trim();
					String t_path = "";
					boolean isUploaded = true;

					if (sourceList.get(i).get(ITEM_UPLOADSTATUS).toString().equals("未上传")) {
						UnuploadPicCount++;
						isUploaded = false;
					}

					if (picType == EPicType.sign) {
						t_path = GlobalParas.getGlobalParas().getSignUploadPath() + barcode + ".jpg";
						if (!isUploaded) {
							t_path = GlobalParas.getGlobalParas().getSignUnUploadPath() + barcode + ".jpg";
						}
					} else {
						t_path = GlobalParas.getGlobalParas().getProblemUploadPath() + barcode + ".jpg";
						if (!isUploaded) {
							t_path = GlobalParas.getGlobalParas().getProblemUnUploadPath() + barcode + ".jpg";
						}
					}
					if (FileUtil.delFile(t_path)) {
						lvx.delete(barcode, ITEM_BARCODE);

						brocastUtil.sendUnUploadCountChange(-UnuploadPicCount, EUploadType.pic);
						UnuploadPicCount = 0;
					} else {
						PromptUtils.getInstance().showToastHasFeel("删除失败！", PicQueryActivity.this, EVoiceType.fail, 0);
					}
				}
			}
		}, null);
	}

	private void viewPic() {

		Map<String, Object> map = lvx.GetSelValue();
		if (map == null) {
			PromptUtils.getInstance().showToastHasFeel("请选择要查看的项！", PicQueryActivity.this, EVoiceType.fail, 0);
			return;
		}
		String barcode = map.get(ITEM_BARCODE).toString().trim();
		String t_uploadStatus = map.get(ITEM_UPLOADSTATUS).toString().trim();
		String pathStr = getPathStr(t_uploadStatus, barcode, picType);
		initPopWindow(pathStr);
	}

	private void initPopWindow(String pathStr) {
		File fileImg = new File(pathStr);
		if (!fileImg.exists()) {
			PromptUtils.getInstance().showToastHasFeel("该图片不存在！", PicQueryActivity.this, EVoiceType.fail, 0);
			return;
		}

		Intent intent = new Intent(PicQueryActivity.this, ShowImageActivity.class);
		intent.putExtra(ScanBaseActivity.ImagePath, pathStr);
		startActivity(intent);
	}

	/**
	 * 路径
	 * 
	 * @param t_uploadStatus
	 * @param barcode
	 * @param picType
	 * @return
	 */
	private String getPathStr(String t_uploadStatus, String barcode, EPicType picType) {
		String pathStr = "";
		if (picType == EPicType.sign) {
			if (t_uploadStatus.equals("未上传")) {
				pathStr = GlobalParas.getGlobalParas().getSignUnUploadPath() + barcode + GlobalParas.getGlobalParas().getImageSuffix();
			} else {
				pathStr = GlobalParas.getGlobalParas().getSignUploadPath() + barcode + GlobalParas.getGlobalParas().getImageSuffix();
			}
		} else if (picType == EPicType.problem) {
			if (t_uploadStatus.equals("未上传")) {
				pathStr = GlobalParas.getGlobalParas().getProblemUnUploadPath() + barcode + GlobalParas.getGlobalParas().getImageSuffix();
			} else {
				pathStr = GlobalParas.getGlobalParas().getProblemUploadPath() + barcode + GlobalParas.getGlobalParas().getImageSuffix();
			}
		}
		return pathStr;
	}

	// @Override
	// protected void doLeftButton() {
	//
	// super.doLeftButton();
	// viewPic();
	// }

}
