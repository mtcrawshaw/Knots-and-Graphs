package Polynomial;
import java.util.ArrayList;

import Misc.TextFormat;

public class Term {
	private int constant;
	private ArrayList<Factor> factors;
	
	// Constructors
	public Term() {
		constant = 1;
		factors = new ArrayList<Factor>();
		combineFactors();
	}
	public Term(int c, ArrayList<Factor> f) {
		constant = c;
		setFactors(f);
		combineFactors();
		sortFactors();
	}
	public Term(Term t) {
		constant = t.getConstant();
		setFactors(t.getFactors());
	}
	public Term(String s) {
		constant = constantFromTermString(s);
		factors = new ArrayList<Factor>();

		if (TextFormat.getFirstLetterIndex(s) == -1) s = "";
		else if (constant == -1) s = s.substring(1);
		else if (constant != 1) s = s.substring(Integer.toString(constant).length());
		
		while (!s.equals("")) {
			int startNextFactor = TextFormat.getFirstLetterIndex(s.substring(1)) + 1;
			
			if (startNextFactor != 0) {
				factors.add(new Factor(s.substring(0, startNextFactor)));
				s = s.substring(startNextFactor);
			}
			else {
				factors.add(new Factor(s));
				s = "";
			}
			sortNewestFactor();
		}
		
		combineFactors();
	}
	
	// Accessors
	public int getConstant() { return constant; }
	public ArrayList<Factor> getFactors() { return factors; }
	
	// Mutators
	public void setConstant (int c) { constant = c; }
	public void setFactors (ArrayList<Factor> f) { 
		factors = new ArrayList<Factor>();
		
		for (Factor currentFactor : f) { factors.add(new Factor(currentFactor)); }
		
		combineFactors();
		sortFactors();
	}
	public void addFactor(Factor f) { 
		factors.add(new Factor(f));
		combineFactors();
		sortNewestFactor();
	}
	public void removeFactor(int index) { factors.remove(index); }
	public void removeFactor(Factor f) { factors.remove(f); }
	
	// Term Calculations
	public void combineFactors() {
		for (int i = 0; i < factors.size(); i++) {
			for (int j = i + 1; j < factors.size(); j++) {
				if (factors.get(i) == null || factors.get(j) == null) continue;
				
				if (factors.get(i).getBase().equals(factors.get(j).getBase())) {
					Factor newI = new Factor(factors.get(i));
					newI.setPower(newI.getPower() + factors.get(j).getPower());
					
					if (newI.getPower() != 0) {
						factors.remove(i);
						factors.add(i, newI);
						factors.remove(j);
						factors.add(j, null);
					} else {
						factors.remove(i);
						factors.add(i, null);
						factors.remove(j);
						factors.add(j, null);
					}
				}
			}
		}
		
		for (int i = 0; i < factors.size(); i++) {
			if (factors.get(i) == null || factors.get(i).getPower() == 0) {
				factors.remove(i);
				i--;
			}
		}
	}
	public Term multiply (Term t) {
		Term product = new Term(constant * t.getConstant(), factors);
		
		ArrayList<Factor> newFactors = product.getFactors();
		newFactors.addAll(t.getFactors());
		product.setFactors(newFactors);
		
		product.setConstant(constant * t.getConstant());
		product.sortFactors();
		
		return product;
	}
	public static int constantFromTermString(String s) {
		int c = 0;
		
		int firstLetterIndex = TextFormat.getFirstLetterIndex(s);
		
		if (firstLetterIndex == -1) c = Integer.parseInt(s);
		else if (firstLetterIndex == 0) c = 1;
		else if (firstLetterIndex == 1 && s.charAt(0) == '-') c = -1;
		else c = Integer.parseInt(s.substring(0, firstLetterIndex));
		
		return c;
	}

	// Utility Methods
	public String toString() {
		String toString = "";
		
		if (factors.size() == 0) return Integer.toString(constant);
		if (constant != 1 && constant != -1) toString += constant;
		if (constant == -1) toString = "-" + toString;
		
		for (int i = 0; i < factors.size(); i++) toString += factors.get(i).toString();
		
		return toString;
	}
	public static boolean likeTerms(Term a, Term b) {
		a.combineFactors();
		b.combineFactors();
		
		ArrayList<Factor> aFactors = a.getFactors();
		ArrayList<Factor> bFactors = b.getFactors();
		
		if (aFactors.size() != bFactors.size()) return false;
		
		boolean likeTerms = true;
		
		for (int i = 0; i < aFactors.size(); i++)
			if (!b.hasFactor(aFactors.get(i))) likeTerms = false;
		
		return likeTerms;
	}
	public boolean hasFactor(Factor f) {
		boolean hasFactor = false;
		
		for (Factor a : factors)
			if (a.equals(f)) hasFactor = true;
		
		return hasFactor;
	}
	public boolean equals(Term t) {
		return likeTerms(this, t) && constant == t.getConstant();
	}
	public void sortFactors() {
		// Uses Quick Sort to sort factors. The one line method just calling another method isn't as elegant, but it hides the need to pass
		// 0 and factors.size() - 1 in as parameters, and that is enough of a reason in my opinion to separate them. Just to sweep those 
		// parameters under the rug. 
		quicksort(0, factors.size() - 1);
	}
	public void quicksort(int low, int high) {
		if (low < high) {
			int pi = partition(low, high);
			quicksort(low, pi - 1);
			quicksort(pi + 1, high);
		}
	}
	public int partition(int low, int high) {
		
		Factor pivot = factors.get(high);
		int i = low - 1;
		
		for (int j = low; j < high; j++ ){
			if (factors.get(j).compareTo(pivot) < 0) {
				i++;
				Factor temp = factors.get(i);
				factors.set(i, factors.get(j));
				factors.set(j, temp);
			}
		}
		Factor temp = factors.get(i + 1);
		factors.set(i + 1, factors.get(high));
		factors.set(high, temp);
		
		return i + 1;
	}
	public void sortNewestFactor() {
		for (int i = 0; i < factors.size() - 1; i++) {
			Factor newFactor = factors.get(factors.size() - 1);
			if (newFactor.compareTo(factors.get(i)) < 0) {
				factors.add(i, newFactor);
				factors.remove(factors.size() - 1);
			}
		}
	}
}
