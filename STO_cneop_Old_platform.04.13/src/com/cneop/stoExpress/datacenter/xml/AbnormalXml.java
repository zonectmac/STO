package com.cneop.stoExpress.datacenter.xml;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import com.cneop.stoExpress.dao.AbnormalXmlService;
import com.cneop.stoExpress.model.Abnormal;
import com.cneop.util.DateUtil;

import android.content.Context;
import android.util.Xml;

public class AbnormalXml implements IDownloadXml {

	AbnormalXmlService abnormalService;

	public List<Abnormal> parseAbnormalStr(String xmlStr) throws Exception {
		XmlPullParser parser = Xml.newPullParser();
		InputStream is = new ByteArrayInputStream(xmlStr.getBytes(Charset.forName("utf-8")));

		parser.setInput(is, "utf-8");
		int type = parser.getEventType();
		Abnormal abnormal = null;
		List<Abnormal> abnormalList = null;
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_DOCUMENT:
				abnormalList = new ArrayList<Abnormal>();
				break;
			case XmlPullParser.START_TAG:
				if ("Row".equalsIgnoreCase(parser.getName())) {
					abnormal = new Abnormal();
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
						String lastTimeStr = DateUtil.formatTimeStr(parser.nextText(), "yyyy-MM-dd HH:mm:ss");
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

	@Override
	public int dataProcessing(Context context, String downStr) throws Exception {

		int result = 0;
		setContext(context);
		List<Abnormal> abnormalList = parseAbnormalStr(downStr);
		if (abnormalList.size() > 0) {
			result = abnormalService.addRecord(abnormalList);
		}
		return result;
	}

	private void setContext(Context context) {

		if (abnormalService == null) {
			abnormalService = new AbnormalXmlService(context);
		}
	}
}
