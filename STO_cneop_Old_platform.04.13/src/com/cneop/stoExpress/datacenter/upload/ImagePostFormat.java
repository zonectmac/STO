package com.cneop.stoExpress.datacenter.upload;

import java.util.List;

public class ImagePostFormat {
	private String source;
	private List<ImageData> requests;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public List<ImageData> getRequests() {
		return requests;
	}

	public void setRequests(List<ImageData> requests) {
		this.requests = requests;
	}

}
