package Polynomial;

import Misc.TextFormat;

public class Factor {
	// Bases are represented as strings to allow for any kind of variable, but the string can't contain a space, and the first character can't be a number!
	private String base;
	private int power;
	
	// Constructors
	public Factor() {
		base = "1";
		power = 0;
	}
	public Factor(String b, int p) {
		assert b.length() != 0;
		assert b.indexOf(" ") == -1;
		assert !TextFormat.isDigit(b.charAt(0));
		
		base = b;
		power = p;
	}
	public Factor(Factor f) {
		base = f.getBase();
		power = f.getPower();
	}
	public Factor(String s) {
		int carotIndex = s.indexOf("^");
		
		if (carotIndex != -1) {
			base = s.substring(0, s.indexOf("^"));
			power = Integer.parseInt(s.substring(s.indexOf("(") + 1, s.indexOf(")")));
		} else {
			base = s;
			power = 1;
		}
	}
	
	// Accessors
	public String getBase() {
		return base;
	}
	public int getPower() {
		return power;
	}
	
	// Mutators
	public void setBase(String b) {
		assert b.length() != 0;
		assert b.indexOf(" ") == -1;
		assert !TextFormat.isDigit(b.charAt(0));
		
		base = b;
	}
	public void setPower(int p) {
		power = p;
	}
	
	// Utility Methods
	public String toString() {
		String toString = base;
		if (power == 1) return toString;
		//toString = toString + TextFormat.superscript(power);
		toString = toString + "^(" + power + ")";
		
		return toString;
	}
	public boolean equals(Factor f) {
		return base.equals(f.getBase()) && power == f.getPower();
	}
	public static boolean isValidBase(String b) {
		boolean isValid = true;
		
		if (b.length() == 0) isValid = false;
		if (!TextFormat.isLetter(b.charAt(0))) isValid = false;
		if (b.length() > 1 && !TextFormat.isNumber(b.substring(1))) isValid = false;
		
		return isValid;
	}
	public int compareTo(Factor f) {
		char c1 = base.toLowerCase().charAt(0);
		char c2 = f.getBase().toLowerCase().charAt(0);
		
		if (c1 != c2) return (int)c1 - (int)c2;
		
		int sub1 = (base.length() > 1) ? Integer.parseInt(base.substring(1)) : 0;
		int sub2 = (f.getBase().length() > 1) ? Integer.parseInt(f.getBase().substring(1)) : 0;
		
		return sub1 - sub2;
	}
}
