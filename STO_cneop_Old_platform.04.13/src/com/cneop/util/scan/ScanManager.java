package com.cneop.util.scan;

public class ScanManager {

	private ScanManager() {
		
	}

	private static ScanManager instance;
	private static IScan scanner;

	public static synchronized ScanManager getInstance() {
		if (instance == null) {
			instance = new ScanManager();
			if (android.os.Build.MODEL.equals("CN-V6")) {
				scanner = V6ScanManager.getInstance();
			} else {
				scanner = NewScanManager.getInstance();
			}

		}

		return instance;
	}

	public IScan getScanner() {
		return getInstance().scanner;
	}

}
