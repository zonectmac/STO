package com.cneop.stoExpress.datacenter.download;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import com.cneop.stoExpress.dao.RouteService;
import com.cneop.util.DateUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Xml;

class Route implements IDownload {

	RouteService routeService;

	@Override
	public int dataProcessing(Context context, String downStr) {

		int result = 0;
		setContext(context);
		if (!strUtil.isNullOrEmpty(downStr)) {
			List<com.cneop.stoExpress.model.Route> routeList = null;
			try {
				routeList = parseRouteStr(downStr);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (routeList != null && routeList.size() > 0) {
				result = routeService.addRecord(routeList);
			}
		}
		return result;
	}

	private void setContext(Context context) {

		if (routeService == null) {
			routeService = new RouteService(context);
		}
	}

	@SuppressLint("NewApi") private List<com.cneop.stoExpress.model.Route> parseRouteStr(String xmlStr) throws Exception {
		XmlPullParser parser = Xml.newPullParser();
		InputStream is = new ByteArrayInputStream(xmlStr.getBytes(Charset.forName("utf-8")));

		parser.setInput(is, "utf-8");
		int type = parser.getEventType();
		com.cneop.stoExpress.model.Route route = null;
		List<com.cneop.stoExpress.model.Route> routeList = null;
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_DOCUMENT:
				routeList = new ArrayList<com.cneop.stoExpress.model.Route>();
				break;
			case XmlPullParser.START_TAG:
				if ("Row".equalsIgnoreCase(parser.getName())) {
					route = new com.cneop.stoExpress.model.Route();
				} else if (route != null) {
					if ("ROUTINGNO".equalsIgnoreCase(parser.getName())) {
						route.setRouteId(parser.nextText());
					} else if ("SITENAME".equalsIgnoreCase(parser.getName())) {
						route.setRouteDesc(parser.nextText());
					} else if ("NEXTAREA".equalsIgnoreCase(parser.getName())) {
						route.setNextStationName(parser.nextText());
					} else if ("DESTAREA".equalsIgnoreCase(parser.getName())) {
						route.setSecondStationName(parser.nextText());
					} else if ("OPERFLAG".equalsIgnoreCase(parser.getName())) {
						String stateStr = parser.nextText();
						if (!stateStr.equalsIgnoreCase("D")) {
							stateStr = "A";
						}
						route.setState(stateStr);
					} else if ("LASTUPDATE".equalsIgnoreCase(parser.getName())) {
						String lastTimeStr = DateUtil.formatTimeStr(parser.nextText(), "yyyy-MM-dd HH:mm:ss");
						route.setLasttime(lastTimeStr);
					}
				}
				break;
			case XmlPullParser.END_TAG:
				if ("Row".equalsIgnoreCase(parser.getName())) {
					if (route != null && routeList != null) {
						routeList.add(route);
						route = null;
					}
				}
				break;
			}
			type = parser.next();
		}
		is.close();
		return routeList;
	}

}
