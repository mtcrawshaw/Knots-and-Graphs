package Knot;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import Misc.TextFormat;
import Polynomial.Factor;
import Polynomial.Polynomial;
import Polynomial.Term;

public class VirtualKnot {
	private ArrayList<ClassicalCrossing> crossings;
	
	//Constructors
	public VirtualKnot(){
		crossings = new ArrayList<ClassicalCrossing>();
	}
	public VirtualKnot(ArrayList<ClassicalCrossing> c){
		crossings = c;
	}
	public VirtualKnot(VirtualKnot K){
		crossings = new ArrayList<ClassicalCrossing>();
		ArrayList<ClassicalCrossing> KCrossings = K.getCrossings();
			
		for (int i = 0; i < KCrossings.size(); i++)
			crossings.add(new ClassicalCrossing(KCrossings.get(i)));
	}
	public VirtualKnot(String filename) {
		crossings = new ArrayList<ClassicalCrossing>();
		Scanner reader = new Scanner("");
		try {
			reader = new Scanner(new File("./" + filename));
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
			e.printStackTrace();
			System.exit(0);
		}
		
		boolean type = true;
		int[] strands = new int[4];
		String currentLine;
		String[] entries;
		
		while (reader.hasNext()) {
			currentLine = reader.nextLine();
			entries = currentLine.split(" ");
			type = entries[0].equals("+");
			strands[0] = Integer.parseInt(entries[1]);
			strands[1] = Integer.parseInt(entries[2]);
			strands[2] = Integer.parseInt(entries[3]);
			strands[3] = Integer.parseInt(entries[4]);
			crossings.add(new ClassicalCrossing(type, strands));
		}
		
		reader.close();
	}
	
	//Accessors
	public ArrayList<ClassicalCrossing> getCrossings(){
		return crossings;
	}
	public ClassicalCrossing getCrossing(int index){
		return crossings.get(index);
	}
	
	//Mutators
	public void setCrossings(ArrayList<ClassicalCrossing> c){
		crossings = new ArrayList<ClassicalCrossing>();
		
		for (int i = 0; i < c.size(); i++)
			crossings.add(new ClassicalCrossing(c.get(i)));
	}
	public void setCrossing(int index, ClassicalCrossing c){
		crossings.set(index, new ClassicalCrossing(c));
	}
	public void addCrossing(ClassicalCrossing c){
		crossings.add(new ClassicalCrossing(c));
	}
	public void deleteCrossing(ClassicalCrossing c){
		crossings.remove(c);
	}
	public void deleteCrossing(int index){
		crossings.remove(index);
	}
	
	// Knot Calculations
	public int getWrithe() {
		int writhe = 0;
		
		for (ClassicalCrossing c : crossings)
			writhe += c.getCrossingType() ? 1 : -1;
		
		return writhe;
	}
	public boolean[] getSmoothingsFromInt(int n) {		// Move to other class?
		int numSmoothings = crossings.size();
		boolean[] smoothings = new boolean[numSmoothings];
		
		for (int i = 0; i < numSmoothings; i++) {
			smoothings[i] = Math.floor(n / Math.pow(2, numSmoothings - i - 1)) % 2 == 1;
		}
		
		return smoothings;
	}
	public State getState(boolean[] smoothings) {
		/*
		 * Important: smoothings[i] = false means that the ith crossing should be smoothed using an A smoothing!
		 */
		
		State e = new State();
		int exponent = 0;
		boolean oriented;
		
		for (int i = 0; i < smoothings.length; i++) {
			exponent += smoothings[i] ? -1 : 1;
			boolean cType = crossings.get(i).getCrossingType();
			oriented = cType ^ !smoothings[i];
			
			if (!smoothings[i]) {
				e.addArc(new Arc(oriented, crossings.get(i).getLabel(3), crossings.get(i).getLabel(0)));
				e.addArc(new Arc(oriented, crossings.get(i).getLabel(1), crossings.get(i).getLabel(2)));
			} else {
				e.addArc(new Arc(oriented, crossings.get(i).getLabel(0), crossings.get(i).getLabel(1)));
				e.addArc(new Arc(oriented, crossings.get(i).getLabel(2), crossings.get(i).getLabel(3)));
			}
		}
		
		e.setExponent(exponent);
		
		return e;
	}
	public Polynomial getSummand(State state, int[] powers) {
		// Generating term d^(|s| - 1) = (-A^2 - A^(-2))^(|s| - 1)
		Polynomial summand = new Polynomial("-A^(2) - A^(-2)");
		summand = summand.exponentiate(powers[0] - 1);
		
		// Generating term A^(alpha - beta)
		Polynomial temp = new Polynomial();
		
		ArrayList<Factor> factors = new ArrayList<Factor>();
		factors.add(new Factor("A", state.getExponent()));
		temp.addTerm(new Term(1, factors));
		
		summand = summand.multiply(temp);
		
		// Generating powers of k's
		temp = new Polynomial();
		
		factors = new ArrayList<Factor>();
		
		for (int i = 1; i < powers.length; i++) {
			String indexSubscript = "";
			ArrayList<Integer> digits = TextFormat.getDigits(i);
			
			for (int j = 0; j < digits.size(); j++) 
				indexSubscript += TextFormat.subscript(digits.get(j));
			
			if (powers[i] != 0)
				factors.add(new Factor("k" + indexSubscript, powers[i]));
		}
		temp.addTerm(new Term(1, factors));
		
		summand = summand.multiply(temp);
		
		return summand;
	}
	public Polynomial getArrowPolynomial(){
		Polynomial AP = new Polynomial();
		int numStates = (int)Math.pow(2, crossings.size());
		boolean[] smoothings;
		State state;
		int[] powers;
		
		for (int i = 0; i < numStates; i++) {
			smoothings = getSmoothingsFromInt(i);
			state = getState(smoothings);
			state = state.getReduction();
			powers = state.getLoopsAndStates(crossings.size());
			
			AP = AP.add(getSummand(state, powers));
		}
		
		return AP;
	}
	public Polynomial getNormalizationFactor() {
		Polynomial normFactor = new Polynomial();
		int writhe = getWrithe();
		
		int coefficient = (writhe % 2 == 0) ? 1 : -1;
		
		ArrayList<Factor> factors = new ArrayList<Factor>();
		factors.add(new Factor("A", -3 * writhe));
		normFactor.addTerm(new Term(coefficient, factors));
		
		return normFactor;
	}
	public Polynomial getNormalizedArrowPolynomial() {
		return (getNormalizationFactor()).multiply(getArrowPolynomial());
	}

	// Utility Methods
	public String toString() {
		return crossings.toString();
	}
}
