package com.cneop.stoExpress.datacenter.xml;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.cneop.stoExpress.model.BarcodTrack;

public class BarcodeTrackXml {
	
	public List<BarcodTrack> paresTrack(String xmlStr) throws Exception{
		
		XmlPullParser parser = Xml.newPullParser();
		
		InputStream is = new ByteArrayInputStream(xmlStr.getBytes(Charset.forName("UTF-8")));
		
		parser.setInput(is, "UTF-8");
		int type = parser.getEventType();
		BarcodTrack track = null;
		List<BarcodTrack> trackList = null;
		
		while(type != XmlPullParser.END_DOCUMENT){
			switch(type){
			case XmlPullParser.START_DOCUMENT:
				trackList = new ArrayList<BarcodTrack>();
				break;
			case XmlPullParser.START_TAG:
				if("detail".equalsIgnoreCase(parser.getName())){
					track = new BarcodTrack();
				}else if(track != null){
					if("time".equalsIgnoreCase(parser.getName())){
						track.setTime(parser.nextText());
					}
					if("scantype".equalsIgnoreCase(parser.getName())){
						track.setScantype(parser.nextText());
					}
					if("memo".equalsIgnoreCase(parser.getName())){
						track.setMemo(parser.nextText());
					}
				}
				break;
			case XmlPullParser.END_TAG:
				if("detail".equalsIgnoreCase(parser.getName())){
					if(track != null && trackList != null){
						trackList.add(track);
						track = null;
					}
				}
				break;
			}
			type = parser.next();
		}
		is.close();
		return trackList;
	}

}
