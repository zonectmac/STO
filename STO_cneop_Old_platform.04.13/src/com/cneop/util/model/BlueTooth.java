package com.cneop.util.model;

public class BlueTooth {

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private String mac;
	private String name;

	@Override
	public boolean equals(Object o) {

		boolean bool = true;
		if (this == o) {
			return true;
		}

		if ((o == null) || (!(o instanceof BlueTooth))) {
			bool = false;
		} else {
			BlueTooth localBlueToothVO = (BlueTooth) o;
			if ((this.mac != null) || (localBlueToothVO.getMac() != null)) {
				if ((this.mac == null) || (localBlueToothVO.getMac() == null)) {
					bool = false;
				} else {
					bool = this.mac.equals(localBlueToothVO.getMac());
				}
			}
		}
		return bool;

	}
}
