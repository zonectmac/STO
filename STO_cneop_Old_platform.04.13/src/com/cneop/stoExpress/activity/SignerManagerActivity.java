package com.cneop.stoExpress.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.activity.scan.SignatureActivity;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.dao.SignerService;
import com.cneop.stoExpress.model.Signer;
import com.cneop.util.PromptUtils;
import com.cneop.util.StrUtil;
import com.cneop.util.activity.CommonTitleActivity;

public class SignerManagerActivity extends CommonTitleActivity {

	Button btnOk;
	Button BtnBack;
	Button btnDelete;
	ListView lvList;
	EditText etsigner;
	TextView tvsigner;
	SimpleAdapter simpleAdapter;
	List<Map<String, Object>> listSource;
	int iListSelectedIndex = -1;

	SignerService baseDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.activity_signer_manager);
		setTitle("常用签收名");
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initializeComponent() {

		super.initializeComponent();

		lvList = (ListView) findViewById(R.id.lv_signerma_lvSignerList);
		lvList.setOnItemClickListener(listener);

		tvsigner = (TextView) findViewById(R.id.tv_carlotnumber_tvCarLotNumber);
		tvsigner.setText("常用名");
		etsigner = bindEditText(R.id.et_carlotnumber_etCarLotNumber, null);
		etsigner.setFilters(new InputFilter[] { new InputFilter.LengthFilter(14) });
		btnOk = bindButton(R.id.bottom_3_btnUpload);
		btnOk.setText("添加");
		BtnBack = bindButton(R.id.bottom_3_btnBack);
		btnDelete = bindButton(R.id.bottom_3_btnDel);
	}

	private OnItemClickListener listener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

			iListSelectedIndex = arg2;
		}
	};

	@Override
	protected void initializeValues() {

		super.initializeValues();
		baseDao = new SignerService(getApplicationContext());
		setTitleHead();
		initView();
	}

	private void initView() {

		simpleAdapter = new SimpleAdapter(this, getSignType(), R.layout.lv_item_column_2, new String[] { "id", "signerName" }, new int[] { R.id.tv_list_item_two_tvcolumn1, R.id.tv_list_item_two_tvcolumn2 });
		lvList.setAdapter(simpleAdapter);
	}

	private List<Map<String, Object>> getSignType() {
		Signer signerModel = new Signer();
		baseDao = new SignerService(getApplicationContext());
		List<Object> listObj = baseDao.getListObj(null, null, signerModel);
		listSource = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < listObj.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", i + 1);
			map.put("signerName", ((Signer) listObj.get(i)).getSignerName());
			listSource.add(map);
		}
		return listSource;
	}

	@SuppressLint("NewApi")
	private void insertSigner() {

		String signerName = etsigner.getText().toString().trim();
		signerName = StrUtil.FilterSpecial(signerName);
		if (signerName.isEmpty() || signerName.equals("")) {
			PromptUtils.getInstance().showToast("签收人异常", SignerManagerActivity.this);
			return;
		}
		if (SignatureActivity.count(etsigner.getText().toString().trim()) > 14) {
			PromptUtils.getInstance().showToast("签收人长度受限", SignerManagerActivity.this);
			return;
		}
		if (baseDao.isExist(signerName) > 0) {
			PromptUtils.getInstance().showToast("签收人异常", SignerManagerActivity.this);
			return;
		}
		Signer signerModel = new Signer();
		baseDao = new SignerService(getApplicationContext());
		List<Signer> objList = new ArrayList<Signer>();

		signerModel.setUserNo(GlobalParas.getGlobalParas().getUserName());
		signerModel.setSignerName(signerName);
		objList.add(signerModel);
		baseDao.insertSigner(objList);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", listSource.size() + 1);
		map.put("signerName", signerName);
		listSource.add(map);

		etsigner.setText("");
		simpleAdapter.notifyDataSetChanged();
		lvList.invalidate();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		setResult(RESULT_OK);
		finish();
	}

	private void setTitleHead() {

		TextView head1 = (TextView) findViewById(R.id.tv_list_head_two_tvhead1);
		head1.setText("编号");

		TextView head2 = (TextView) findViewById(R.id.tv_list_head_two_tvhead2);
		head2.setText("签收人");
	}

	@Override
	protected void uiOnClick(View v) {

		super.uiOnClick(v);
		switch (v.getId()) {
		case R.id.bottom_3_btnUpload:
			insertSigner();
			break;
		case R.id.bottom_3_btnBack:
			finish();
			break;
		case R.id.bottom_3_btnDel:
			deleteRow(SignerManagerActivity.this);
			break;
		}
	}

	// @Override
	// protected void doLeftButton() {
	// super.doLeftButton();
	// insertSigner();
	// }
	//
	// @Override
	// protected boolean doKeyCode_Back() {
	// setResult(1);
	// return super.doKeyCode_Back();
	// }

	@SuppressWarnings("unchecked")
	public void deleteRow(Context content) {
		try {
			String signer = "";
			if (iListSelectedIndex >= 0) {
				Map<String, Object> map = (HashMap<String, Object>) lvList.getItemAtPosition(iListSelectedIndex);
				signer = (String) map.get("signerName").toString().trim();
			}
			final Context context = content;
			final EditText et = new EditText(context);
			et.setText(signer);
			new AlertDialog.Builder(context).setTitle("提示").setIcon(android.R.drawable.ic_dialog_alert).setView(et).setPositiveButton("删除", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

					String signer = et.getText().toString().trim();
					if (signer.equals("")) {
						PromptUtils.getInstance().showToast("签收人不能为空！", context);
					} else {
						if (baseDao.deleteSigner(signer) > 0) {
							// 数据库删除成功
							// 该单号是否在列表中
							listSource.remove(iListSelectedIndex);
							simpleAdapter.notifyDataSetChanged();
							lvList.invalidate();
						}
					}
				}
			}).setNegativeButton("取消", null).show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}