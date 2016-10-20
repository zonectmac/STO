package com.cneop.stoExpress.datacenter.xml;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import com.cneop.stoExpress.model.Order;

import android.annotation.SuppressLint;
import android.util.Xml;

public class OrderStatusXml {

	@SuppressLint("NewApi")
	public List<Order> parseOrderStatusStr(String xmlStr) throws Exception {
		XmlPullParser parser = Xml.newPullParser();
		InputStream is = new ByteArrayInputStream(xmlStr.getBytes(Charset.forName("utf-8")));

		parser.setInput(is, "utf-8");
		int type = parser.getEventType();
		Order order = null;
		List<Order> orderList = null;
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_TAG:
				if ("code".equalsIgnoreCase(parser.getName())) {
					if (parser.nextText().equals("00")) {
						orderList = new ArrayList<Order>();
					}
				} else if (orderList != null) {
					if ("row".equalsIgnoreCase(parser.getName())) {
						order = new Order();
					} else if (order != null) {
						if ("logisticid".equalsIgnoreCase(parser.getName())) {
							order.setLogisticid(parser.nextText());
						} else if ("remind".equalsIgnoreCase(parser.getName())) {
							String remind = parser.nextText();
							if (remind.equals("订单催促")) {
								order.setIsUrge(1);
							} else if (remind.equals("订单取消")) {
								order.setIsUrge(2);
							}
						}
					}
				}
				break;
			case XmlPullParser.END_TAG:
				if ("row".equalsIgnoreCase(parser.getName())) {
					if (order != null && orderList != null) {
						orderList.add(order);
						order = null;
					}
				}
				break;
			}
			type = parser.next();
		}
		is.close();
		return orderList;
	}
}
