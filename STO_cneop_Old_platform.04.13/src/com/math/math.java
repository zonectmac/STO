package com.math;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class math {
	private static boolean CODE;

	public static void main(String[] args) {
		// =======================����=ҵ��Ա
		/**
		 * �ռ� ���ţ�CODE0001��CODE0002 ����� ���ţ�CODE0001��CODE0002
		 * �ռ����ţ�CODE0001��CODE0002
		 */
		System.out.println(new math().CODE1("6681234567004"));
		/**
		 * ǩ�� ���ţ�CODE0001��
		 */
		// System.out.println(new math().CODE2("368123456789"));

		// =======================����=ɨ��Ա
	}

	/**
	 * 12λ���֣���У�����
	 * 
	 * @param code
	 * @return
	 */
	public boolean CODE1(String code) {
		CODE = false;
		boolean flag = code.matches("[0-9]+");
		if (flag && code.length() == 12) {
			Pattern pattern1 = Pattern.compile("[0-9]*");
			boolean tagg = pattern1.matcher(code.substring(3, code.length())).matches();// 705137111
			boolean tagg1 = pattern1.matcher(code.substring(2, code.length())).matches();// 8705137111
			boolean tagg2 = pattern1.matcher(code.substring(1, code.length())).matches();// 68705137111
			if (code.substring(0, 3).equals("268") && tagg) {
				CODE = true;
			} else if (code.substring(0, 3).equals("368") && tagg) {
				CODE = true;
			} else if (code.substring(0, 3).equals("468") && tagg) {
				CODE = true;
			} else if (code.substring(0, 3).equals("568") && tagg) {
				CODE = true;
			} else if (code.substring(0, 3).equals("668") && tagg) {
				CODE = true;
			} else if (code.substring(0, 3).equals("768") && tagg) {
				CODE = true;
			} else if (code.substring(0, 3).equals("868") && tagg) {
				CODE = true;
			} else if (code.substring(0, 3).equals("968") && tagg) {
				CODE = true;
			} else if (code.substring(0, 3).equals("588") && tagg) {
				CODE = true;
			} else if (code.substring(0, 3).equals("688") && tagg) {
				CODE = true;
			} else if (code.substring(0, 3).equals("888") && tagg) {
				CODE = true;
			} else if (code.substring(0, 3).equals("900") && tagg) {
				CODE = true;
			} else if (code.substring(0, 2).equals("11") && tagg1) {
				CODE = true;
			} else if (code.substring(0, 2).equals("22") && tagg1) {
				CODE = true;
			} else if (code.substring(0, 1).equals("4") && tagg2) {
				if (code.substring(0, 2).equals("44") && tagg1) {
					CODE = false;
				} else {
					CODE = true;
				}
			}
		} else if (code.length() == 13 && flag) {
			boolean flags = yanz(code);
			if (flags) {
				CODE = true;
			} else {
				CODE = false;
			}
		}
		return CODE;
	}

	// ------------------------------------------------------------------------------------------

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static boolean yanz(String code) {
		boolean YANZHENG = false;
		String math = "795324689";// �̶��㷨��ʽ
		ArrayList arrayList3 = new ArrayList();
		arrayList3.clear();
		ArrayList arrayList = new ArrayList();
		arrayList.clear();
		ArrayList arrayList1 = new ArrayList();
		arrayList1.clear();
		// ---------------------------------------------------------------------
		boolean flag = code.matches("[0-9]+");
		if (flag && code.length() == 13) {
			if (code.substring(0, 2).equals("33")) {
				String start = code.substring(3, code.length() - 1);// ȥ��ǰ׺2λ����,ȥ�����һλ
				for (int i = 0; i < start.length(); i++) {
					arrayList.add(start.substring(i, i + 1));
				}
				for (int i = 0; i < math.length(); i++) {
					arrayList1.add(math.substring(i, i + 1));
				}
				for (int i = 0; i < arrayList.size(); i++) {
					int math1 = Integer.parseInt(arrayList.get(i).toString());
					int math2 = Integer.parseInt(arrayList1.get(i).toString());
					arrayList3.add(math1 * math2 + "");
				}
				int RESULT = 0;
				for (int i = 0; i < arrayList3.size() - 1; i++) {
					int resule = Integer.parseInt(arrayList3.get(i).toString());
					RESULT += resule;
				}
				RESULT += Integer.parseInt(arrayList3.get(arrayList3.size() - 1).toString());
				int vc = (RESULT % 11);
				if (vc == 0) {
					if (5 == Integer.parseInt(code.substring(code.length() - 1))) {
						System.out.println("��ȷ");
						return true;
					}
				} else if (vc == 1) {
					if (0 == Integer.parseInt(code.substring(code.length() - 1))) {
						System.out.println("��ȷ");
						return true;
					}
				}
				int vb = 11 - vc;// �õ�У����
				if (vb == Integer.parseInt(code.substring(code.length() - 1))) {
					YANZHENG = true;
					System.out.println("��ȷ");
				} else {
					System.out.println("����");
				}
			} else if (code.substring(0, 2).equals("44")) {
				String start = code.substring(3, code.length() - 1);// ȥ��ǰ׺2λ����,ȥ�����һλ
				for (int i = 0; i < start.length(); i++) {
					arrayList.add(start.substring(i, i + 1));
				}
				for (int i = 0; i < math.length(); i++) {
					arrayList1.add(math.substring(i, i + 1));
				}
				for (int i = 0; i < arrayList.size(); i++) {
					int math1 = Integer.parseInt(arrayList.get(i).toString());
					int math2 = Integer.parseInt(arrayList1.get(i).toString());
					arrayList3.add(math1 * math2 + "");
				}
				int RESULT = 0;
				for (int i = 0; i < arrayList3.size() - 1; i++) {
					int resule = Integer.parseInt(arrayList3.get(i).toString());
					RESULT += resule;
				}
				RESULT += Integer.parseInt(arrayList3.get(arrayList3.size() - 1).toString());
				int vc = (RESULT % 11);
				if (vc == 0) {
					if (5 == Integer.parseInt(code.substring(code.length() - 1))) {
						System.out.println("��ȷ");
						return true;
					}
				} else if (vc == 1) {
					if (0 == Integer.parseInt(code.substring(code.length() - 1))) {
						System.out.println("��ȷ");
						return true;
					}
				}
				int vb = 11 - vc;// �õ�У����
				if (vb == Integer.parseInt(code.substring(code.length() - 1))) {
					YANZHENG = true;
					System.out.println("��ȷ");
				} else {
					System.out.println("����");
				}

			} else if (code.substring(0, 2).equals("55")) {

				String start = code.substring(3, code.length() - 1);// ȥ��ǰ׺2λ����,ȥ�����һλ
				for (int i = 0; i < start.length(); i++) {
					arrayList.add(start.substring(i, i + 1));
				}
				for (int i = 0; i < math.length(); i++) {
					arrayList1.add(math.substring(i, i + 1));
				}
				for (int i = 0; i < arrayList.size(); i++) {
					int math1 = Integer.parseInt(arrayList.get(i).toString());
					int math2 = Integer.parseInt(arrayList1.get(i).toString());
					arrayList3.add(math1 * math2 + "");
				}
				int RESULT = 0;
				for (int i = 0; i < arrayList3.size() - 1; i++) {
					int resule = Integer.parseInt(arrayList3.get(i).toString());
					RESULT += resule;
				}
				RESULT += Integer.parseInt(arrayList3.get(arrayList3.size() - 1).toString());
				int vc = (RESULT % 11);
				if (vc == 0) {
					if (5 == Integer.parseInt(code.substring(code.length() - 1))) {
						System.out.println("��ȷ");
						return true;
					}
				} else if (vc == 1) {
					if (0 == Integer.parseInt(code.substring(code.length() - 1))) {
						System.out.println("��ȷ");
						return true;
					}
				}
				int vb = 11 - vc;// �õ�У����
				if (vb == Integer.parseInt(code.substring(code.length() - 1))) {
					YANZHENG = true;
					System.out.println("��ȷ");
				} else {
					System.out.println("����");
				}

			} else if (code.substring(0, 2).equals("66")) {
				if (code.substring(0, 3).equals("668")) {
					return false;
				}
				String start = code.substring(3, code.length() - 1);// ȥ��ǰ׺2λ����,ȥ�����һλ
				for (int i = 0; i < start.length(); i++) {
					arrayList.add(start.substring(i, i + 1));
				}
				for (int i = 0; i < math.length(); i++) {
					arrayList1.add(math.substring(i, i + 1));
				}
				for (int i = 0; i < arrayList.size(); i++) {
					int math1 = Integer.parseInt(arrayList.get(i).toString());
					int math2 = Integer.parseInt(arrayList1.get(i).toString());
					arrayList3.add(math1 * math2 + "");
				}
				int RESULT = 0;
				for (int i = 0; i < arrayList3.size() - 1; i++) {
					int resule = Integer.parseInt(arrayList3.get(i).toString());
					RESULT += resule;
				}
				RESULT += Integer.parseInt(arrayList3.get(arrayList3.size() - 1).toString());
				int vc = (RESULT % 11);
				if (vc == 0) {
					if (5 == Integer.parseInt(code.substring(code.length() - 1))) {
						System.out.println("��ȷ");
						return true;
					}
				} else if (vc == 1) {
					if (0 == Integer.parseInt(code.substring(code.length() - 1))) {
						System.out.println("��ȷ");
						return true;
					}
				}
				int vb = 11 - vc;// �õ�У����
				if (vb == Integer.parseInt(code.substring(code.length() - 1))) {
					YANZHENG = true;
					System.out.println("��ȷ");
				} else {
					System.out.println("����");
				}

			} else if (code.substring(0, 2).equals("77")) {

				String start = code.substring(3, code.length() - 1);// ȥ��ǰ׺2λ����,ȥ�����һλ
				for (int i = 0; i < start.length(); i++) {
					arrayList.add(start.substring(i, i + 1));
				}
				for (int i = 0; i < math.length(); i++) {
					arrayList1.add(math.substring(i, i + 1));
				}
				for (int i = 0; i < arrayList.size(); i++) {
					int math1 = Integer.parseInt(arrayList.get(i).toString());
					int math2 = Integer.parseInt(arrayList1.get(i).toString());
					arrayList3.add(math1 * math2 + "");
				}
				int RESULT = 0;
				for (int i = 0; i < arrayList3.size() - 1; i++) {
					int resule = Integer.parseInt(arrayList3.get(i).toString());
					RESULT += resule;
				}
				RESULT += Integer.parseInt(arrayList3.get(arrayList3.size() - 1).toString());
				int vc = (RESULT % 11);
				if (vc == 0) {
					if (5 == Integer.parseInt(code.substring(code.length() - 1))) {
						System.out.println("��ȷ");
						return true;
					}
				} else if (vc == 1) {
					if (0 == Integer.parseInt(code.substring(code.length() - 1))) {
						System.out.println("��ȷ");
						return true;
					}
				}
				int vb = 11 - vc;// �õ�У����
				if (vb == Integer.parseInt(code.substring(code.length() - 1))) {
					YANZHENG = true;
					System.out.println("��ȷ");
				} else {
					System.out.println("����");
				}

			} else if (code.substring(0, 2).equals("88")) {
				if (code.substring(0, 3).equals("888")) {
					return false;
				}
				String start = code.substring(3, code.length() - 1);// ȥ��ǰ׺2λ����,ȥ�����һλ
				for (int i = 0; i < start.length(); i++) {
					arrayList.add(start.substring(i, i + 1));
				}
				for (int i = 0; i < math.length(); i++) {
					arrayList1.add(math.substring(i, i + 1));
				}
				for (int i = 0; i < arrayList.size(); i++) {
					int math1 = Integer.parseInt(arrayList.get(i).toString());
					int math2 = Integer.parseInt(arrayList1.get(i).toString());
					arrayList3.add(math1 * math2 + "");
				}
				int RESULT = 0;
				for (int i = 0; i < arrayList3.size() - 1; i++) {
					int resule = Integer.parseInt(arrayList3.get(i).toString());
					RESULT += resule;
				}
				RESULT += Integer.parseInt(arrayList3.get(arrayList3.size() - 1).toString());
				int vc = (RESULT % 11);
				if (vc == 0) {
					if (5 == Integer.parseInt(code.substring(code.length() - 1))) {
						System.out.println("��ȷ");
						return true;
					}
				} else if (vc == 1) {
					if (0 == Integer.parseInt(code.substring(code.length() - 1))) {
						System.out.println("��ȷ");
						return true;
					}
				}
				int vb = 11 - vc;// �õ�У����
				if (vb == Integer.parseInt(code.substring(code.length() - 1))) {
					YANZHENG = true;
					System.out.println("��ȷ");
				} else {
					System.out.println("����");
				}

			} else if (code.substring(0, 2).equals("99")) {

				String start = code.substring(3, code.length() - 1);// ȥ��ǰ׺2λ����,ȥ�����һλ
				for (int i = 0; i < start.length(); i++) {
					arrayList.add(start.substring(i, i + 1));
				}
				for (int i = 0; i < math.length(); i++) {
					arrayList1.add(math.substring(i, i + 1));
				}
				for (int i = 0; i < arrayList.size(); i++) {
					int math1 = Integer.parseInt(arrayList.get(i).toString());
					int math2 = Integer.parseInt(arrayList1.get(i).toString());
					arrayList3.add(math1 * math2 + "");
				}
				int RESULT = 0;
				for (int i = 0; i < arrayList3.size() - 1; i++) {
					int resule = Integer.parseInt(arrayList3.get(i).toString());
					RESULT += resule;
				}
				RESULT += Integer.parseInt(arrayList3.get(arrayList3.size() - 1).toString());
				int vc = (RESULT % 11);
				if (vc == 0) {
					if (5 == Integer.parseInt(code.substring(code.length() - 1))) {
						System.out.println("��ȷ");
						return true;
					}
				} else if (vc == 1) {
					if (0 == Integer.parseInt(code.substring(code.length() - 1))) {
						System.out.println("��ȷ");
						return true;
					}
				}
				int vb = 11 - vc;// �õ�У����
				if (vb == Integer.parseInt(code.substring(code.length() - 1))) {
					YANZHENG = true;
					System.out.println("��ȷ");
				} else {
					System.out.println("����");
				}

			} else if (code.substring(0, 3).equals("668")) {
				YANZHENG = false;
			} else if (code.substring(0, 3).equals("888")) {
				YANZHENG = false;
			} else {
				YANZHENG = false;
			}
		}
		return YANZHENG;

	}

	// ------------------------------------------------------------------------------------------
	public boolean CODE2(String code) {
		CODE = false;
		boolean flag = code.matches("[0-9]+");
		if (flag && code.length() == 12) {
			Pattern pattern1 = Pattern.compile("[0-9]*");
			boolean tagg = pattern1.matcher(code.substring(3, code.length())).matches();// 705137111
			boolean tagg1 = pattern1.matcher(code.substring(2, code.length())).matches();// 8705137111
			boolean tagg2 = pattern1.matcher(code.substring(1, code.length())).matches();// 68705137111

			if (code.substring(0, 3).equals("268") && tagg) {
				CODE = true;
			} else if (code.substring(0, 2).equals("44") && tagg1) {
				CODE = false;
			} else if (code.substring(0, 3).equals("368") && tagg) {
				CODE = true;
			} else if (code.substring(0, 3).equals("468") && tagg) {
				CODE = true;
			} else if (code.substring(0, 3).equals("568") && tagg) {
				CODE = true;
			} else if (code.substring(0, 3).equals("668") && tagg) {
				CODE = true;
			} else if (code.substring(0, 3).equals("768") && tagg) {
				CODE = true;
			} else if (code.substring(0, 3).equals("868") && tagg) {
				CODE = true;
			} else if (code.substring(0, 3).equals("968") && tagg) {
				CODE = true;
			} else if (code.substring(0, 3).equals("588") && tagg) {
				CODE = true;
			} else if (code.substring(0, 3).equals("688") && tagg) {
				CODE = true;
			} else if (code.substring(0, 3).equals("888") && tagg) {
				CODE = true;
			} else if (code.substring(0, 2).equals("11") && tagg1) {
				CODE = true;
			} else if (code.substring(0, 2).equals("22") && tagg1) {
				CODE = true;
			} else if (code.substring(0, 1).equals("4") && tagg2) {
				if (code.substring(0, 2).equals("44") && tagg1) {
					CODE = false;
				} else {
					CODE = true;
				}

			}
		} else if (flag && code.length() == 13) {
			boolean as = yanz(code);
			if (as) {
				CODE = true;
			} else {
				CODE = false;
			}
		}
		return CODE;
	}

	// -----------------------------------------
	// ��֤����
	boolean daihao;

	/**
	 * ��֤����
	 * 
	 * @param str
	 * @return
	 */
	public boolean daihao(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		if (str.length() == 12 && str.substring(0, 3).equals("900")
				&& pattern.matcher(str.substring(3, str.length())).matches()) {
			daihao = true;
		}

		return daihao;
	}

	// ��֤��ǩ��
	boolean cqh;

	/**
	 * ��֤��ǩ��
	 * 
	 * @param str
	 * @return
	 */
	public boolean cqh(String str) {
		cqh = false;
		Pattern pattern = Pattern.compile("[0-9]*");
		if (str.length() <= 0) {
			return false;
		}
		if (str.length() == 12 && str.substring(0, 3).equals("STO")
				&& pattern.matcher(str.substring(3, str.length())).matches()
				|| str.length() == 12 && str.substring(0, 3).equals("sto")
						&& pattern.matcher(str.substring(3, str.length())).matches()) {
			cqh = true;
		}
		return cqh;
	}

	boolean luyouhao;

	/**
	 * ��֤·�ɺ�
	 * 
	 * @param code
	 * @return
	 */
	public boolean luyouhao(String code) {
		luyouhao = false;
		Pattern pattern = Pattern.compile("[0-9]*");
		if (code.length() <= 0) {
			return false;
		}
		if (pattern.matcher(code).matches() && code.length() > 6 && code.length() <= 10) {
			return true;
		}

		return luyouhao;

	}

}
