package Misc;

import java.util.ArrayList;

public class TextFormat {
	public static String superscript(int n) {
		String sup = "";
		
		if (n < 0) { sup = "-"; }
		ArrayList<Integer> digits = getDigits(n);
		for (int d : digits) { sup = sup + digitSuperscript(d); }
		
		return sup;
	}
	public static String subscript(int n) {
		String sub = "";
		
		if (n < 0) { sub = ""; }
		ArrayList<Integer> digits = getDigits(n);
		for (int d : digits) { sub = sub + digitSubscript(d); }
		
		return sub;
	}
	public static String digitSubscript(int n) {
		assert n >= 0 && n <= 9;
			
		return Integer.toString(n);
	}
	public static String digitSuperscript(int n) {
		assert n >= 0 && n <= 9;
		
		String superscript = "";
		
		if (n == 1)
			superscript = Character.toString((char)185);
		else if (n == 2)
			superscript = Character.toString((char)178);
		else if (n == 3)
			superscript = Character.toString((char)179);
		else
			superscript = Integer.toString(n);
		
		return superscript;
	}
	public static boolean isNumber(String s) {
		if (s.equals("")) return false;
		
		boolean isNumber = true;
		
		for (int i = 0; i < s.length(); i++) {
			if (isDigit(s.charAt(i))) {
				isNumber = true;
				break;
			}
		}
		
		return isNumber;
	}
	public static boolean isDigit(char c) {
		if ((int)c >= 48 && (int)c <= 57)
			return true;
		else
			return false;
	}
	public static boolean isLetter(char c){
		if ((int)c >= 65 && (int)c <= 90 || (int)c >= 97 && (int)c <= 122)
			return true;
		else
			return false;
	}
	public static int getFirstLetterIndex(String s) {
		int firstLetterIndex = 0;
		while (firstLetterIndex < s.length() && !TextFormat.isLetter(s.charAt(firstLetterIndex))) firstLetterIndex++;
		if (firstLetterIndex == s.length()) firstLetterIndex = -1;
		
		return firstLetterIndex;
	}
	public static ArrayList<Integer> getDigits(int n) {		
		ArrayList<Integer> digits = new ArrayList<Integer>();
		
		if (n == 0) {
			digits.add(0);
			return digits;
		}
		
		if (n < 0) n *= -1;
		
		while (n > 0) {
			digits.add(0, n % 10);
			n = (n - (n % 10)) / 10;
		}
		
		return digits;
	}
}
