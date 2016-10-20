package com.cneop.stoExpress.datacenter.download;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import com.cneop.stoExpress.dao.AbnormalService;
import com.cneop.util.DateUtil;
import android.content.Context;
import android.util.Xml;

class Abnormal implements IDownload {

	AbnormalService abnormalService;

	@Override
	public int dataProcessing(Context context, String downStr) {
	 
		int result = 0;
		setContext(context);
		if (!strUtil.isNullOrEmpty(downStr)) {
			List<com.cneop.stoExpress.model.Abnormal> abnormalList = null;
			try {
				abnormalList = parseAbnormalStr(downStr);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (abnormalList != null && abnormalList.size() > 0) {
				result = abnormalService.addRecord(abnormalList);
			}
		}
		return result;
	}

	private void setContext(Context context) {
	 
		if (abnormalService == null) {
			abnormalService = new AbnormalService(context);
		}
	}

	private List<com.cneop.stoExpress.model.Abnormal> parseAbnormalStr(String xmlStr)
			throws Exception {
		XmlPullParser parser = Xml.newPullParser();
		InputStream is = new ByteArrayInputStream(xmlStr.getBytes(Charset
				.forName("utf-8")));

		parser.setInput(is, "utf-8");
		int type = parser.getEventType();
		com.cneop.stoExpress.model.Abnormal abnormal = null;
		List<com.cneop.stoExpress.model.Abnormal> abnormalList = null;
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_DOCUMENT:
				abnormalList = new ArrayList<com.cneop.stoExpress.model.Abnormal>();
				break;
			case XmlPullParser.START_TAG:
				if ("Row".equalsIgnoreCase(parser.getName())) {
					abnormal = new com.cneop.stoExpress.model.Abnormal();
				} else if (abnormal != null) {
					if ("PROBLEMNO".equalsIgnoreCase(parser.getName())) {
						abnormal.setCode(parser.nextText());
					} else if ("PROBLEMTYPE".equalsIgnoreCase(parser.getName())) {
						abnormal.setReasonDesc(parser.nextText());
					} else if ("TYPECODE".equalsIgnoreCase(parser.getName())) {
						abnormal.setTypeId(parser.nextText());
					} else if ("TYPE".equalsIgnoreCase(parser.getName())) {
						abnormal.setTypeName(parser.nextText());
					} else if ("ATTRIBUTE".equalsIgnoreCase(parser.getName())) {
						abnormal.setAttribute(parser.nextText());
					} else if ("OPERFLAG".equalsIgnoreCase(parser.getName())) {
						String stateStr = parser.nextText();
						if (!stateStr.equalsIgnoreCase("D")) {
							stateStr = "A";
						}
						abnormal.setState(stateStr);
					} else if ("LASTUPDATE".equalsIgnoreCase(parser.getName())) {
						String lastTimeStr = DateUtil.formatTimeStr(
								parser.nextText(), "yyyy-MM-dd HH:mm:ss");
						abnormal.setLasttime(lastTimeStr);
					}
				}
				break;
			case XmlPullParser.END_TAG:
				if ("Row".equalsIgnoreCase(parser.getName())) {
					if (abnormal != null && abnormalList != null) {
						abnormalList.add(abnormal);
						abnormal = null;
					}
				}
				break;
			}
			type = parser.next();
		}
		is.close();
		return abnormalList;
	}

}
