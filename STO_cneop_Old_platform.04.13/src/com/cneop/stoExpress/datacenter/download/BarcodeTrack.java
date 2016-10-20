package com.cneop.stoExpress.datacenter.download;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import android.util.Xml;
import com.cneop.stoExpress.model.BarcodeTrackingModel;
import com.cneop.util.StrUtil;

public class BarcodeTrack {

	StrUtil strUtil;

	public BarcodeTrack() {
		strUtil = new StrUtil();
	}

	/**
	 * ½âÎö
	 * 
	 * @param xmlStr
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public List<BarcodeTrackingModel> parseXmlStr(String xmlStr)
			throws Exception {
		if (strUtil.isNullOrEmpty(xmlStr)) {
			return null;
		}
		XmlPullParser parser = Xml.newPullParser();
		InputStream is = new ByteArrayInputStream(xmlStr.getBytes(Charset
				.forName("utf-8")));

		parser.setInput(is, "utf-8");
		int type = parser.getEventType();
		BarcodeTrackingModel model = null;
		List<BarcodeTrackingModel> barcodeTrackingList = null;
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_DOCUMENT:
				barcodeTrackingList = new ArrayList<BarcodeTrackingModel>();
				break;
			case XmlPullParser.START_TAG:
				if ("detail".equalsIgnoreCase(parser.getName())) {
					model = new BarcodeTrackingModel();
				} else if (model != null) {
					if ("time".equalsIgnoreCase(parser.getName())) {
						model.setTime(parser.nextText());
					} else if ("scantype".equalsIgnoreCase(parser.getName())) {
						model.setScantype(parser.nextText());
					} else if ("memo".equalsIgnoreCase(parser.getName())) {
						model.setMemo(parser.nextText());
					}
				}
				break;
			case XmlPullParser.END_TAG:
				if ("detail".equalsIgnoreCase(parser.getName())) {
					if (model != null && barcodeTrackingList != null) {
						barcodeTrackingList.add(model);
						model = null;
					}
				}
				break;
			}
			type = parser.next();
		}
		is.close();
		return barcodeTrackingList;
	}

}
