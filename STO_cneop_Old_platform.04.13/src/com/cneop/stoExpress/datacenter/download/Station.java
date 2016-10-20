package com.cneop.stoExpress.datacenter.download;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import android.content.Context;
import android.util.Xml;
import com.cneop.stoExpress.dao.StationService;
import com.cneop.util.DateUtil;

class Station implements IDownload {

	StationService stationService;

	private void setContext(Context context) {
	 
		if (stationService == null) {
			stationService = new StationService(context);
		}
	}

	@Override
	public int dataProcessing(Context context, String downStr) {
	 
		int result = 0;
		setContext(context);
		if (!strUtil.isNullOrEmpty(downStr)) {
			List<com.cneop.stoExpress.model.Station> stationList = null;
			try {
				stationList = parseStationStr(downStr);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (stationList!=null&& stationList.size() > 0) {
				result = stationService.addRecord(stationList);
			}
		}
		return result;
	}

	private List<com.cneop.stoExpress.model.Station> parseStationStr(String stationStr)
			throws Exception {
		XmlPullParser parser = Xml.newPullParser();
		InputStream is = new ByteArrayInputStream(stationStr.getBytes(Charset
				.forName("utf-8")));
		parser.setInput(is, "utf-8");
		int type = parser.getEventType();
		com.cneop.stoExpress.model.Station station = null;
		List<com.cneop.stoExpress.model.Station> stationList = null;
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_DOCUMENT:
				stationList = new ArrayList<com.cneop.stoExpress.model.Station>();
				break;
			case XmlPullParser.START_TAG:
				if ("Row".equalsIgnoreCase(parser.getName())) {
					station = new com.cneop.stoExpress.model.Station();
				} else if (station != null) {
					if ("SITENO".equalsIgnoreCase(parser.getName())) {
						station.setStationId(parser.nextText());
					} else if ("SITENAME".equalsIgnoreCase(parser.getName())) {
						station.setStationName(parser.nextText());
					} else if ("OPERFLAG".equalsIgnoreCase(parser.getName())) {
						String stateStr = parser.nextText();
						if (!stateStr.equalsIgnoreCase("D")) {
							stateStr = "A";
						}
						station.setState(stateStr);
					} else if ("SITETYPE".equalsIgnoreCase(parser.getName())) {
						String attribute = station.getAttribute();
						attribute = parser.nextText() + attribute;
						station.setAttribute(attribute);
					} else if ("HKSIGN".equalsIgnoreCase(parser.getName())) {
						String attribute = station.getAttribute();
						if (parser.nextText().equals("1")) {
							attribute += "º½¿Õ";
						}
						station.setAttribute(attribute);
					} else if ("LASTUPDATE".equalsIgnoreCase(parser.getName())) {
						String lastTimeStr = DateUtil.formatTimeStr(
								parser.nextText(), "yyyy-MM-dd HH:mm:ss");
						station.setLasttime(lastTimeStr);
					}
				}
				break;
			case XmlPullParser.END_TAG:
				if ("Row".equalsIgnoreCase(parser.getName())) {
					if (station != null && stationList != null) {
						stationList.add(station);
						station = null;
					}
				}
				break;
			}
			type = parser.next();
		}
		is.close();
		return stationList;
	}

}
