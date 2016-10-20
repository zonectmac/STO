package com.cneop.util.activity;

import com.cneop.util.scan.ScanManager;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class BaseActivity extends Activity {
	@SuppressWarnings("unused")
	private int iListViewSelectedIndex = -1;// ListView被选中行的索引
	@SuppressWarnings("unused")
	private boolean isGroupKeyDowned = false;
	protected boolean scannerPower;
	protected boolean isMenuKeyWork = false;// menu键是否启用(不启用时,在软件中当右键用)
	public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000; // 需要自己定义标志

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		initializeComponent();
		super.onCreate(savedInstanceState);
		initializeValues();
		setScannerPower();
	}

	/**
	 * 初始化控件
	 */
	protected void initializeComponent() {

		this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);// 关键代码
	}

	/**
	 * 1.绑定数据到控制上 2.给一些成员变量赋值(e.g:修改时根据id来绑定页面)
	 * 
	 */
	protected void initializeValues() {

	}

	protected void setScannerPower() {

		ScanManager.getInstance().getScanner().setPower(scannerPower);
	}

	public OnClickListener ctrlOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {

			uiOnClick(v);
		}
	};

	protected Button bindButton(int resourceId) {

		Button btn = (Button) findViewById(resourceId);
		if (btn != null) {
			btn.setOnClickListener(ctrlOnClick);
		}
		return btn;
	}

	protected EditText bindEditText(int resourceId, OnFocusChangeListener onfocus) {

		EditText et = (EditText) this.findViewById(resourceId);
		et.setOnFocusChangeListener(et_focus_changeEvent);

		return et;
	}

	protected EditText bindEditText(int resourceId, OnFocusChangeListener onfocus, TextWatcher txtChangeListener) {

		EditText et = (EditText) findViewById(resourceId);
		et.setOnFocusChangeListener(et_focus_changeEvent);
		if (txtChangeListener != null) {
			et.addTextChangedListener(txtChangeListener);
		}
		return et;
	}

	private OnFocusChangeListener et_focus_changeEvent = new OnFocusChangeListener() {
		// 注册一个回调函数被调用时的焦点这一观点发生了改变。
		@Override
		public void onFocusChange(View view, boolean hasFocus) {

			editFocusChange(view, hasFocus);
			if (hasFocus) {
				EditText et = (EditText) view;
				et.setSelection(et.getText().length());// 将焦点置到文本的最后
			}
		}

	};

	protected void editFocusChange(View view, boolean hasFocus) {
	}

	protected ListView bindListView(int resourceId) {
		ListView lv = (ListView) this.findViewById(resourceId);
		lv.setOnItemClickListener(lstItemClick);
		return lv;
	}

	protected OnItemClickListener lstItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			iListViewSelectedIndex = position;
			if (((ListView) parent).getTag() != null) {
				((View) ((ListView) parent).getTag()).setBackgroundColor(Color.WHITE);
			}
			((ListView) parent).setTag(view);
			view.setBackgroundColor(Color.BLUE);
			view.clearFocus();
			view.requestFocusFromTouch();

		}
	};

	// 子类可以继承的方法
	protected void uiOnClick(View v) {

	}

	protected void back() {

		finish();
	}

	protected void groupButtonEvent0() {

	}

	protected void groupButtonEvent1() {

	}

	protected void groupButtonEvent2() {

	}

	protected void groupButtonEvent3() {

	}

	protected void groupButtonEvent4() {

	}

	protected void groupButtonEvent5() {

	}

	protected void groupButtonEvent6() {

	}

	protected void groupButtonEvent7() {

	}

	protected void buttonEvent1() {

	}

	protected void buttonEvent2() {

	}

	protected void buttonEvent3() {

	}

	protected void buttonEvent4() {

	}

	protected void buttonEvent5() {

	}

	protected void buttonEvent6() {

	}

	protected void buttonEvent7() {

	}

	protected void buttonEvent8() {

	}

	protected void buttonEvent9() {

	}

	/**
	 * 左键快捷键
	 */
	protected void doLeftButton() {

	}

	/*
	 * 退格键 ，子类实现
	 */
	protected boolean doKeyCode_Back() {

		return true;
	}

	/**
	 * F1键
	 */
	protected void doF1KeyEvent() {

	}

	/**
	 * F2按键
	 */
	protected void doF2KeyEent() {

	}

	/**
	 * 扫描键，子类实现
	 */
	protected void doStartScan() {

	}

	/**
	 * 关闭扫描
	 */
	protected void doStopScan() {

	}

}
