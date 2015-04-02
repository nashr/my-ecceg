package util;

public class NumberFormatter {
	
	/**
	 * convert binary to byte with 2's complement representation
	 * @param s: String of 8 bits length
	 * @return
	 */
	public static byte parseBinaryToByte(String s) {
		byte b = 0;
		for (int i = 1; i < s.length(); i++) {
			b *= 2;
			if (s.charAt(i) == '1') {
				b += 1;
			}
		}
		if (s.charAt(0) == '1') {
			b -= Math.pow(2, s.length()-1);
		}
		return b;
	}
	
	/**
	 * convert binary to long with 2's complement representation
	 * @param s: String of 64 bits length
	 * @return
	 */
	public static long parseBinaryToLong(String s) {
		long l = 0;
		for (int i = 1; i < s.length(); i++) {
			l *= 2;
			if (s.charAt(i) == '1') {
				l += 1;
			}
		}
		if (s.charAt(0) == '1') {
			l -= Math.pow(2, s.length()-1);
		}
		return l;
	}
	
	/**
	 * convert hex to long with 2's complement representation
	 * @param s: String of 64 bits length
	 * @return
	 */
	public static long parseHexToLong(String s) {
		while (s.length() < 16) {
			s = "0" + s;
		}
		
		String t = "";
		for (int i = 0; i < s.length(); i++) {
			switch (s.charAt(i)) {
			case '0':
				t += "0000"; break;
			case '1':
				t += "0001"; break;
			case '2':
				t += "0010"; break;
			case '3':
				t += "0011"; break;
			case '4':
				t += "0100"; break;
			case '5':
				t += "0101"; break;
			case '6':
				t += "0110"; break;
			case '7':
				t += "0111"; break;
			case '8':
				t += "1000"; break;
			case '9':
				t += "1001"; break;
			case 'A':
			case 'a':
				t += "1010"; break;
			case 'B':
			case 'b':
				t += "1011"; break;
			case 'C':
			case 'c':
				t += "1100"; break;
			case 'D':
			case 'd':
				t += "1101"; break;
			case 'E':
			case 'e':
				t += "1110"; break;
			case 'F':
			case 'f':
				t += "1111"; break;
			}
		}
		
		return parseBinaryToLong(t);
	}
}
