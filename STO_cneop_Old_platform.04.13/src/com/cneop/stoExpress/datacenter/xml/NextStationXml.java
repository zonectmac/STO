package com.cneop.stoExpress.datacenter.xml;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import com.cneop.stoExpress.dao.NextStationXmlService;
import com.cneop.stoExpress.model.NextStation;
import com.cneop.util.DateUtil;

import android.content.Context;
import android.util.Xml;


public class NextStationXml implements IDownloadXml{

	public List<NextStation> parseStationStr(String xmlStr) throws Exception {
		XmlPullParser parser = Xml.newPullParser();
		InputStream is = new ByteArrayInputStream(xmlStr.getBytes(Charset
				.forName("utf-8")));

		parser.setInput(is, "utf-8");
		int type = parser.getEventType();
		NextStation nextStation = null;
		List<NextStation> nextStationList = null;
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_DOCUMENT:
				nextStationList = new ArrayList<NextStation>();
				break;
			case XmlPullParser.START_TAG:
				if ("Row".equalsIgnoreCase(parser.getName())) {
					nextStation = new NextStation();
				} else if (nextStation != null) {
					if ("NEXTSITENO".equalsIgnoreCase(parser.getName())) {
						nextStation.setStationId(parser.nextText());
					} else if ("NEXTSITENAME"
							.equalsIgnoreCase(parser.getName())) {
						nextStation.setStationName(parser.nextText());
					} else if ("OPERFLAG".equalsIgnoreCase(parser.getName())) {
						String stateStr = parser.nextText();
						if (!stateStr.equalsIgnoreCase("D")) {
							stateStr = "A";
						}
						nextStation.setState(stateStr);
					} else if ("LASTUPDATE".equalsIgnoreCase(parser.getName())) {
						String lastTimeStr = DateUtil.formatTimeStr(
								parser.nextText(), "yyyy-MM-dd HH:mm:ss");
						nextStation.setLasttime(lastTimeStr);
					}
				}
				break;
			case XmlPullParser.END_TAG:
				if ("Row".equalsIgnoreCase(parser.getName())) {
					if (nextStation != null && nextStationList != null) {
						nextStationList.add(nextStation);
						nextStation = null;
					}
				}
				break;
			}
			type = parser.next();
		}
		is.close();
		return nextStationList;
	}

	@Override
	public int dataProcessing(Context context, String downStr) throws Exception {
	 
		int result = 0;
		setContext(context);
		List<NextStation> nextStationList = parseStationStr(downStr);
		if(nextStationList.size() > 0){
			result = nextStationService.addRecord(nextStationList);
		}
		return result;
	}
	
	NextStationXmlService nextStationService;
	
	private void setContext(Context context){
		if(nextStationService == null){
			nextStationService = new NextStationXmlService(context);
		}
	}
}
