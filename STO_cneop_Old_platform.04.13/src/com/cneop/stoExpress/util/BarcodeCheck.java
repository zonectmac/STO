package com.cneop.stoExpress.util;

import java.util.regex.Pattern;

import com.cneop.stoExpress.common.Enums.EBarcodeType;
import com.cneop.stoExpress.dao.BaseDao;

/*
 * 单号验证
 */
public class BarcodeCheck {

	private BaseDao sJMDSendedRecordsDao = null;

	/*
	 * 单号验证
	 */
	public boolean isValidateBarcode(String code, EBarcodeType type) {
		boolean flag = false;
		Pattern pattern = null;
		switch (type) {
		case barcode:
			// pattern =
			// Pattern.compile("^(((\\d6|[568]8)8)|11\\d|22\\d)\\d{9,10}$");
			// flag = pattern.matcher(code).matches();
			flag = true;
			break;
		case packageNo:
			pattern = Pattern.compile("^900\\d{9}$");
			flag = pattern.matcher(code).matches();
			break;
		case carLotNumber:
			pattern = Pattern.compile("^STO\\d{9}$");
			flag = pattern.matcher(code).matches();
			break;
		case vehicleId:
			pattern = Pattern.compile("^\\d{6}$");
			flag = pattern.matcher(code).matches();
			break;
		case phoneNum:
			pattern = Pattern.compile("^1[34587]\\d{9}$");
			flag = pattern.matcher(code).matches();
			break;
		case tel:
			pattern = Pattern.compile("\\(d+-)?(d{4}-?d{7}|d{3}-?d{8}|^d{7,8})(-d+)?");
			flag = pattern.matcher(code).matches();
			break;
		default:
			break;
		}
		return flag;
	}
}
