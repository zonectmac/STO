package com.cneop.update;

public class Enums {
	public enum EUpdateState {
		NewVersion(1), Discovery(2), Downing(3), Install(4), Fail(5), NetworkDisable(6), DownProgress(7), HasUnUploadCount(8), other(9);

		private int value = 0;

		private EUpdateState(int value) {
			this.value = value;
		}

		public int value() {
			return this.value;
		}
	}
}
