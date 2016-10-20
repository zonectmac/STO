package com.cneop.stoExpress.activity.common;

import java.util.Calendar;

import com.cneop.stoExpress.cneops.R;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class DatePicker extends LinearLayout {

	private Button btnChooseDate;
	private EditText etDate;
	private View mView;
	private Context mContext;
	private boolean mShowDetailDate; // 是否显示详细日期
	private int uType = 1; // 1 开始时间 2结束时间

	public DatePicker(Context context) {
		// TODO Auto-generated constructor stub
		this(context, null);
	}

	public void setuType(int utype) {
		this.uType = utype;
	}

	public DatePicker(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		this.mContext = context;
		this.mView = LayoutInflater.from(context).inflate(R.layout.date_picker, null);
		// 自定义属性
		TypedArray localTypedArray = context.obtainStyledAttributes(attributeSet, R.styleable.StoDataEdittext);
		this.mShowDetailDate = localTypedArray.getBoolean(R.styleable.StoDataEdittext_show, true);
		this.etDate = (EditText) this.mView.findViewById(R.id.datepicker_etDate);
		this.btnChooseDate = (Button) this.mView.findViewById(R.id.datepicker_btnChooseDate);
		this.btnChooseDate.setOnClickListener(ocl);
		addView(mView);
		localTypedArray.recycle();
	}

	public String getCurrentData() {
		return this.etDate.getText().toString().trim();
	}

	public String getCurrentEndData() {
		return this.etDate.getText().toString().trim() + " 23:59:59";
	}

	public String getCurrentStartData() {
		return this.etDate.getText().toString().trim() + " 00:00:00";
	}

	/*
	 * 设置时间
	 */
	public void setTime(String paramString) {
		switch (this.uType) {
		case 1: // 起始时间
			if (this.mShowDetailDate) {
				paramString += " 00:00:00";
			}
			break;
		case 2:
			if (this.mShowDetailDate) {
				paramString += " 23:59:59";
			}
			break;
		default:
			break;
		}
		this.etDate.setText(paramString);
	}

	/*
	 * 设置时间
	 */
	public void setTime(String paramString, int paramInt) {
		this.uType = paramInt;
		setTime(paramString);
	}

	private String changeTimeStyle(int paramInt) {
		if (paramInt > 9)
			return String.valueOf(paramInt);
		return "0" + paramInt;
	}

	private OnClickListener ocl = new OnClickListener() {

		@Override
		public void onClick(View v) {

			Calendar localCalendar = Calendar.getInstance();
			new DatePickerDialog(DatePicker.this.mContext, new DatePickerDialog.OnDateSetListener() {

				@Override
				public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {

					setTime(year + "-" + changeTimeStyle(monthOfYear + 1) + "-" + changeTimeStyle(dayOfMonth));
				}
			}, localCalendar.get(Calendar.YEAR), localCalendar.get(Calendar.MONTH), localCalendar.get(Calendar.DAY_OF_MONTH)).show();
		}
	};

}
