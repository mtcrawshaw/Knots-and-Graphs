package Polynomial;
import java.util.ArrayList;

public class Polynomial {
	private ArrayList<Term> terms;
	
	// Used to hold all variables in each term in polynomial in alphabetical order, for sorting purposes
	private ArrayList<String> vars;
	
	// Constructors
	public Polynomial() {
		terms = new ArrayList<Term>();
		vars = new ArrayList<String>();
	}
	public Polynomial(ArrayList<Term> t) {
		setTerms(t);
		calculateVars();
		sortTerms();
	}
	public Polynomial(Polynomial p) {
		setTerms(p.getTerms());
		calculateVars();
	}
	public Polynomial(String s) {
		terms = new ArrayList<Term>();
		
		if (s.indexOf(" ") == -1 && !s.equals("")) { 
			terms.add(new Term(s)); 
			s = "";
		} else if (!s.equals("")){
			String firstTermString = s.substring(0, s.indexOf(" "));
			terms.add(new Term(firstTermString));
			s = s.substring(s.indexOf(" ") + 1);
		}
		
		String termString = "";
		
		while (s.indexOf(" ", 2) != -1) {
			termString = s.substring(2, s.indexOf(" ", 2));
			if (s.charAt(0) == '-'){ termString = "-" + termString; }
			
			terms.add(new Term(termString));
			s = s.substring(s.indexOf(" ", 2) + 1);
		}
		
		if (s.indexOf(" ") != -1) {
			termString = s.substring(2);
			if (s.charAt(0) == '-') { termString = "-" + termString; }
			
			terms.add(new Term(termString));
			s = "";
		}
		
		combineLikeTerms();
		calculateVars();
		sortTerms();
	}
	
	// Accessors
	public ArrayList<Term> getTerms() { return terms; }
	
	// Mutators
	public void setTerms(ArrayList<Term> t) { 
		terms = new ArrayList<Term>();
		
		for (Term currentTerm : t) { terms.add(new Term(currentTerm)); }
		combineLikeTerms();
		calculateVars();
		sortTerms();
	}
	public void addTerm(Term t) { 
		terms.add(t);
		combineLikeTerms();
		calculateVars();
		sortNewestTerm();
	}
	public void removeTerm(Term t) { 
		terms.remove(t);
		calculateVars();
	}
	public void removeTerm(int index) {
		terms.remove(index);
		calculateVars();
	}
	
	// Polynomial Calculations
	public Polynomial add(Polynomial p) {
		Polynomial sum = new Polynomial(this);
		ArrayList<Term> pTerms = p.getTerms();
		
		for (Term t : pTerms) { sum.addTerm(t); }
		
		return sum;
	}
	public Polynomial multiply(Polynomial p) {
		Polynomial product = new Polynomial();
		ArrayList<Term> pTerms = p.getTerms();
		
		for (int i = 0; i < terms.size(); i++) {
			for (int j = 0; j < pTerms.size(); j++) {
				product.addTerm(terms.get(i).multiply(pTerms.get(j)));
			}
		}
		
		product.combineLikeTerms();
		
		return product;
	}
	public Polynomial exponentiate(int power) {
		assert power >= 0;
		
		Polynomial product = new Polynomial();
		
		if (power == 0) {
			product.addTerm(new Term(1, new ArrayList<Factor>()));
		}
		else {
			int currentPower = 1;
			product = new Polynomial(this);
			
			while (currentPower < power) {
				product = product.multiply(this);
				currentPower++;
			}
		}
		
		return product;
	}	
	public void removeZeros() {
		for (int i = 0; i < terms.size(); i++) {
			if (terms.get(i).getConstant() == 0) {
				terms.remove(i);
				i--;
			}
		}
		
		calculateVars();
	}
	public void combineLikeTerms() {
		for (int i = 0; i < terms.size(); i++) {
			for (int j = i + 1; j < terms.size(); j++) {
				if (terms.get(i) == null || terms.get(j) == null) continue;
				
				if(Term.likeTerms(terms.get(i), terms.get(j))) {
					Term newI = terms.get(i);
					newI.setConstant(newI.getConstant() + terms.get(j).getConstant());
					
					if (newI.getConstant() != 0) {
						terms.remove(i);
						terms.add(i, newI);
						terms.remove(j);
						terms.add(j, null);
					} else {
						terms.remove(i);
						terms.add(i, null);
						terms.remove(j);
						terms.add(j, null);
					}
				}
			}
		}
		
		for (int i = 0; i < terms.size(); i++) {
			if (terms.get(i) == null || terms.get(i).getConstant() == 0) {
				terms.remove(i);
				i--;
			}
		}
	}
	
	// Utility Methods
	public String toString() {
		String s = "";
		
		if (terms.size() == 0) return "0";
		
		s += terms.get(0).toString();
		String currentString = "";
		
		for (int i = 1; i < terms.size(); i++) {
			currentString = terms.get(i).toString();
			
			if (currentString.charAt(0) == '-')
				s += " - " + currentString.substring(1);
			else 
				s += " + " + currentString;
		}
		
		return s;
	}
	public boolean hasTerm(Term t) {
		for (Term currentTerm : terms){
			if (currentTerm.equals(t)) return true;
		}
		
		return false;
	}
	public boolean equals(Polynomial p) {
		if (terms.size() != p.getTerms().size()) return false;
		
		for (Term t : terms) {
			if (!p.hasTerm(t)) return false;
		}
		
		return true;
	}
	public void sortTerms() {
		quicksortTerms(0, terms.size() - 1);
	}
	public void quicksortTerms(int low, int high) {
		if (low < high) {
			int pi = partitionTerms(low, high);
			quicksortTerms(low, pi - 1);
			quicksortTerms(pi + 1, high);
		}
	}
	public void quicksortVars(int low, int high) {
		if (low < high) {
			int pi = partitionVars(low, high);
			quicksortVars(low, pi - 1);
			quicksortVars(pi + 1, high);
		}
	}
	public int partitionTerms(int low, int high) {
		Term pivot = terms.get(high);
		int i = low - 1;
		
		for (int j = low; j < high; j++ ){
			if (compareTerms(terms.get(j), pivot) < 0) {
				i++;
				Term temp = terms.get(i);
				terms.set(i, terms.get(j));
				terms.set(j, temp);
			}
		}
		Term temp = terms.get(i + 1);
		terms.set(i + 1, terms.get(high));
		terms.set(high, temp);
		
		return i + 1;
	}
	public int partitionVars(int low, int high) {
		String pivot = vars.get(high);
		int i = low - 1;
		
		for (int j = low; j < high; j++ ){
			if (vars.get(j).compareTo(pivot) < 0) {
				i++;
				String temp = vars.get(i);
				vars.set(i, vars.get(j));
				vars.set(j, temp);
			}
		}
		String temp = vars.get(i + 1);
		vars.set(i + 1, vars.get(high));
		vars.set(high, temp);
		
		return i + 1;
	}
	public int compareTerms(Term a, Term b) {
		ArrayList<Factor> aFactors = a.getFactors();
		ArrayList<Factor> bFactors = b.getFactors();
		ArrayList<String> aBases = new ArrayList<String>();
		ArrayList<String> bBases = new ArrayList<String>();
		
		for (int i = 0; i < aFactors.size(); i++) { aBases.add(aFactors.get(i).getBase()); }
		for (int i = 0; i < bFactors.size(); i++) { bBases.add(bFactors.get(i).getBase()); }
		
		for (int i = 0; i < vars.size(); i++) {
			int iIndexA = aBases.indexOf(vars.get(i));
			int iIndexB = bBases.indexOf(vars.get(i));
			int aPower = 0;
			int bPower = 0;
			
			if (iIndexA != -1) aPower = aFactors.get(aBases.indexOf(vars.get(i))).getPower();
			if (iIndexB != -1) bPower = bFactors.get(bBases.indexOf(vars.get(i))).getPower();
			
			if (aPower != bPower) return bPower - aPower;
		}
		
		return 0;
	}
	public void calculateVars() {
		vars = new ArrayList<String>();
		for (Term t : terms) {
			ArrayList<Factor> currentF = t.getFactors();
			for (Factor f : currentF) {
				if (!vars.contains(f.getBase())) vars.add(f.getBase());
			}
		}
		
		quicksortVars(0, vars.size() - 1);
	}
	public static boolean containsFactor(ArrayList<Factor> F, Factor f) {
		for (Factor temp : F) {
			if (temp.equals(f)) return true;
		}
		
		return false;
	}
	public void sortNewestTerm() {
		for (int i = 0; i < terms.size() - 1; i++) {
			Term newTerm = terms.get(terms.size() - 1);
			if (compareTerms(newTerm, terms.get(i)) < 0) {
				terms.add(i, newTerm);
				terms.remove(terms.size() - 1);
			}
		}
	}
}
