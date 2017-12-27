import java.util.ArrayList;

import Knot.Arc;
import Knot.ClassicalCrossing;
import Knot.State;
import Knot.VirtualKnot;
import Polynomial.Factor;
import Polynomial.Polynomial;
import Polynomial.Term;

public class Tester {
	public static void main (String[] args){
		/*
		 * 	Methods tested:
		 * 	Arc:
		 *  	- getReverse()
		 *  	- reorderSequentialArcs()
		 *  
		 *  State:
		 *  	- getReduction()
		 *  	- getLoopsAndStates()
		 *  
		 *  Virtual Knot:
		 *  	- getSmoothingsFromInt()
		 *  	- getExpansion()
		 *  	- getArrowPolynomial()
		 *  	- getNormalizedArrowPolynomial()
		 *  
		 *  Polynomial: 
		 *  	- Constructors, accessors, mutators, equals()
		 *  	- add()
		 *  	- multiply()
		 *  	- exponentiate()
		 *  	- Constructor from string
		 */
		
		testReverse();
		testReorder();
		testReduce();
		testGetSmoothingsFromInt();
		testGetExpansion();
		testGetLoopsAndStates();
		testPolynomial();
		testPolynomialFromString();
		testPolynomialAdd();
		testPolynomialMultiply();
		testPolynomialExponentiate();
		testGetArrowPolynomial();
		testGetNormalizedArrowPolynomial();
	}
	
	// Tests
	public static void testReverse() {
		//Oriented
		Arc a = new Arc(true, 1, 2);
		Arc b = a.getReverse();
		Arc c = new Arc(true, 2, 1);
		if (b.equals(c))
			System.out.println("Passed: Reverse order, oriented arc");
		else
			System.out.println("Failed: Reverse order, oriented arc");
				
		// Not Oriented
		a = new Arc(false, 1, 3);
		b = a.getReverse();
		c = new Arc(false, 3, 1);
		if (b.equals(c))
			System.out.println("Passed: Reverse order, not oriented arc");
		else
			System.out.println("Failed: Reverse order failed not oriented arc");
	}
	public static void testReorder() {
		//Oriented, oriented, sequential
		Arc a = new Arc(true, 2, 3);
		Arc b = new Arc(true, 3, 4);
		Arc[] correctReordered = {a, b};
		Arc[] testReordered = Arc.reorderSequentialArcs(a, b);
		if (testReordered[0].equals(correctReordered[0]) && testReordered[1].equals(correctReordered[1]))
			System.out.println("Passed: Reorder, oriented, oriented, sequential");
		else
			System.out.println("Failed: Reorder, oriented, oriented, sequential");
		
		// Oriented, oriented, not sequential
		a = new Arc(true, 2, 3);
		b = new Arc(true, 4, 3);
		correctReordered[0] = null;
		correctReordered[1] = null;
		testReordered = Arc.reorderSequentialArcs(a, b);
		if (testReordered[0] == null && testReordered[1] == null)
			System.out.println("Passed: Reorder, oriented, oriented, not sequential");
		else
			System.out.println("Failed: Reorder, oriented, oriented, not sequential");
		
		// Oriented, not oriented, sequential 1
		a = new Arc(true, 2, 3);
		b = new Arc(false, 3, 4);
		correctReordered[0] = a;
		correctReordered[1] = b;
		testReordered = Arc.reorderSequentialArcs(a, b);
		if (testReordered[0].equals(correctReordered[0]) && testReordered[1].equals(correctReordered[1]))
			System.out.println("Passed: Reorder, oriented, not oriented, sequential 1");
		else
			System.out.println("Failed: Reorder, oriented, not oriented, sequential 1");
		
		// Oriented, not oriented, sequential 2
		a = new Arc(true, 2, 4);
		b = new Arc(false, 6, 4);
		correctReordered[0] = a;
		correctReordered[1] = b.getReverse();
		testReordered = Arc.reorderSequentialArcs(a, b);
		if (testReordered[0].equals(correctReordered[0]) && testReordered[1].equals(correctReordered[1]))
			System.out.println("Passed: Reorder, oriented, not oriented, sequential 2");
		else
			System.out.println("Failed: Reorder, oriented, not oriented, sequential 2");
		
		// Oriented, not oriented, not sequential
		a = new Arc(true, 2, 4);
		b = new Arc(false, 6, 5);
		correctReordered[0] = null;
		correctReordered[1] = null;
		testReordered = Arc.reorderSequentialArcs(a, b);
		if (testReordered[0] == null && testReordered[1] == null)
			System.out.println("Passed: Reorder, oriented, not oriented, not sequential");
		else
			System.out.println("Failed: Reorder, oriented, not oriented, not sequential");
		
		// Not oriented, oriented, sequential 1
		a = new Arc(false, 2, 4);
		b = new Arc(true, 4, 8);
		correctReordered[0] = a;
		correctReordered[1] = b;
		testReordered = Arc.reorderSequentialArcs(a, b);
		if (testReordered[0].equals(correctReordered[0]) && testReordered[1].equals(correctReordered[1]))
			System.out.println("Passed: Reorder, not oriented, oriented, sequential 1");
		else
			System.out.println("Failed: Reorder, not oriented, oriented, sequential 1");
		
		// Not oriented, oriented, sequential 2
		a = new Arc(false, 7, 2);
		b = new Arc(true, 7, 4);
		correctReordered[0] = a.getReverse();
		correctReordered[1] = b;
		testReordered = Arc.reorderSequentialArcs(a, b);
		if (testReordered[0].equals(correctReordered[0]) && testReordered[1].equals(correctReordered[1]))
			System.out.println("Passed: Reorder, not oriented, oriented, sequential 2");
		else
			System.out.println("Failed: Reorder, not oriented, oriented, sequential 2");

		
		// Not oriented, oriented, not sequential
		a = new Arc(false, 2, 8);
		b = new Arc(true, 4, 8);
		correctReordered[0] = null;
		correctReordered[1] = null;
		testReordered = Arc.reorderSequentialArcs(a, b);
		if (testReordered[0] == null && testReordered[1] == null)
			System.out.println("Passed: Reorder, not oriented, oriented, not sequential");
		else
			System.out.println("Failed: Reorder, not oriented, oriented, not sequential");

		
		// Not oriented, not oriented, sequential 1
		a = new Arc(false, 2, 4);
		b = new Arc(false, 4, 8);
		correctReordered[0] = a;
		correctReordered[1] = b;
		testReordered = Arc.reorderSequentialArcs(a, b);
		if (testReordered[0].equals(correctReordered[0]) && testReordered[1].equals(correctReordered[1]))
			System.out.println("Passed: Reorder, not oriented, not oriented, sequential 1");
		else
			System.out.println("Failed: Reorder, not oriented, not oriented, sequential 1");
		
		// Not oriented, not oriented, sequential 2
		a = new Arc(false, 2, 4);
		b = new Arc(false, 8, 4);
		correctReordered[0] = a;
		correctReordered[1] = b.getReverse();
		testReordered = Arc.reorderSequentialArcs(a, b);
		if (testReordered[0].equals(correctReordered[0]) && testReordered[1].equals(correctReordered[1]))
			System.out.println("Passed: Reorder, not oriented, not oriented, sequential 2");
		else
			System.out.println("Failed: Reorder, not oriented, not oriented, sequential 2");
		
		// Not oriented, not oriented, sequential 3
		a = new Arc(false, 4, 2);
		b = new Arc(false, 4, 8);
		correctReordered[0] = a.getReverse();
		correctReordered[1] = b;
		testReordered = Arc.reorderSequentialArcs(a, b);
		if (testReordered[0].equals(correctReordered[0]) && testReordered[1].equals(correctReordered[1]))
			System.out.println("Passed: Reorder, not oriented, not oriented, sequential 3");
		else
			System.out.println("Failed: Reorder, not oriented, not oriented, sequential 3");
		
		// Not oriented, not oriented, sequential 4
		a = new Arc(false, 3, 4);
		b = new Arc(false, 6, 3);
		correctReordered[0] = a.getReverse();
		correctReordered[1] = b.getReverse();
		testReordered = Arc.reorderSequentialArcs(a, b);
		if (testReordered[0].equals(correctReordered[0]) && testReordered[1].equals(correctReordered[1]))
			System.out.println("Passed: Reorder, not oriented, not oriented, sequential 4");
		else
			System.out.println("Failed: Reorder, not oriented, not oriented, sequential 4");
		
		// Not oriented, not oriented, not sequential
		a = new Arc(false, 3, 4);
		b = new Arc(false, 6, 5);
		correctReordered[0] = null;
		correctReordered[1] = null;
		testReordered = Arc.reorderSequentialArcs(a, b);
		if (testReordered[0] == null && testReordered[1] == null)
			System.out.println("Passed: Reorder, not oriented, not oriented, not sequential");
		else
			System.out.println("Failed: Reorder, not oriented, not oriented, sequential 4");
	}
	public static void testReduce() {
		ArrayList<Arc> arcs = new ArrayList<Arc>();
		arcs.add(new Arc(false, 1, 2));
		arcs.add(new Arc(false, 2, 3));
		arcs.add(new Arc(false, 3, 1));
		arcs.add(new Arc(false, 4, 5));
		arcs.add(new Arc(false, 5, 6));
		arcs.add(new Arc(false, 6, 4));
		State originalState = new State(3, arcs);
		State testReduction = originalState.getReduction();
		arcs.clear();
		arcs.add(new Arc(false, 1, 1));
		arcs.add(new Arc(false, 4, 4));
		State correctReduction = new State(3, arcs);
		if (testReduction.equals(correctReduction))
			System.out.println("Passed: State reduction, trefoil AAA");
		else
			System.out.println("Failed: State reduction, trefoil AAA");
		
		// Trefoil 2: AAB
		arcs.clear();
		arcs.add(new Arc(true, 5, 2));
		arcs.add(new Arc(false, 2, 3));
		arcs.add(new Arc(false, 3, 1));
		arcs.add(new Arc(true, 1, 4));
		arcs.add(new Arc(false, 5, 6));
		arcs.add(new Arc(false, 6, 4));
		originalState = new State(1, arcs);
		testReduction = originalState.getReduction();
		arcs.clear();
		arcs.add(new Arc(false, 4, 4));
		correctReduction = new State(1, arcs);
		if (testReduction.equals(correctReduction))
			System.out.println("Passed: State reduction, trefoil AAB");
		else
			System.out.println("Failed: State reduction, trefoil AAB");
		
		// Trefoil 3: ABA
		arcs.clear();
		arcs.add(new Arc(true, 5, 2));
		arcs.add(new Arc(false, 1, 2));
		arcs.add(new Arc(false, 3, 1));
		arcs.add(new Arc(true, 3, 6));
		arcs.add(new Arc(false, 4, 5));
		arcs.add(new Arc(false, 6, 4));
		originalState = new State(1, arcs);
		testReduction = originalState.getReduction();
		arcs.clear();
		arcs.add(new Arc(false, 4, 4));
		correctReduction = new State(1, arcs);
		if (testReduction.equals(correctReduction))
			System.out.println("Passed: State reduction, trefoil ABA");
		else
			System.out.println("Failed: State reduction, trefoil ABA");
		
		// Trefoil 4: ABB
		arcs.clear();
		arcs.add(new Arc(true, 5, 2));
		arcs.add(new Arc(true, 2, 5));
		arcs.add(new Arc(false, 3, 1));
		arcs.add(new Arc(true, 3, 6));
		arcs.add(new Arc(true, 4, 1));
		arcs.add(new Arc(false, 6, 4));
		originalState = new State(-1, arcs);
		testReduction = originalState.getReduction();
		arcs.clear();
		arcs.add(new Arc(false, 4, 4));
		arcs.add(new Arc(false, 5, 5));
		correctReduction = new State(-1, arcs);
		if (testReduction.equals(correctReduction))
			System.out.println("Passed: State reduction, trefoil ABB");
		else
			System.out.println("Failed: State reduction, trefoil ABB");
		
		// Trefoil 5: BAA
		arcs.clear();
		arcs.add(new Arc(true, 1, 4));
		arcs.add(new Arc(false, 4, 5));
		arcs.add(new Arc(false, 1, 2));
		arcs.add(new Arc(true, 6, 3));
		arcs.add(new Arc(false, 3, 2));
		arcs.add(new Arc(false, 6, 5));
		originalState = new State(1, arcs);
		testReduction = originalState.getReduction();
		arcs.clear();
		arcs.add(new Arc(false, 2, 2));
		correctReduction = new State(1, arcs);
		if (testReduction.equals(correctReduction))
			System.out.println("Passed: State reduction, trefoil BAA");
		else
			System.out.println("Failed: State reduction, trefoil BAA");
		
		// Trefoil 6: BAB
		arcs.clear();
		arcs.add(new Arc(true, 1, 4));
		arcs.add(new Arc(true, 4, 1));
		arcs.add(new Arc(true, 2, 5));
		arcs.add(new Arc(true, 6, 3));
		arcs.add(new Arc(false, 3, 2));
		arcs.add(new Arc(false, 6, 5));
		originalState = new State(-1, arcs);
		testReduction = originalState.getReduction();
		arcs.clear();
		arcs.add(new Arc(false, 1, 1));
		arcs.add(new Arc(false, 2, 2));
		correctReduction = new State(-1, arcs);
		if (testReduction.equals(correctReduction))
			System.out.println("Passed: State reduction, trefoil BAB");
		else
			System.out.println("Failed: State reduction, trefoil BAB");
		
		// Trefoil 7: BBA
		arcs.clear();
		arcs.add(new Arc(true, 1, 4));
		arcs.add(new Arc(false, 4, 5));
		arcs.add(new Arc(true, 5, 2));
		arcs.add(new Arc(false, 2, 1));
		arcs.add(new Arc(true, 6, 3));
		arcs.add(new Arc(true, 3, 6));
		originalState = new State(-1, arcs);
		testReduction = originalState.getReduction();
		arcs.clear();
		arcs.add(new Arc(false, 1, 1));
		arcs.add(new Arc(false, 6, 6));
		correctReduction = new State(-1, arcs);
		if (testReduction.equals(correctReduction))
			System.out.println("Passed: State reduction, trefoil BBA");
		else
			System.out.println("Failed: State reduction, trefoil BBA");
		
		// Trefoil 8: BBB
		arcs.clear();
		arcs.add(new Arc(true, 6, 3));
		arcs.add(new Arc(true, 1, 4));
		arcs.add(new Arc(true, 4, 1));
		arcs.add(new Arc(true, 5, 2));
		arcs.add(new Arc(true, 2, 5));
		arcs.add(new Arc(true, 3, 6));
		originalState = new State(-3, arcs);
		testReduction = originalState.getReduction();
		arcs.clear();
		arcs.add(new Arc(false, 1, 1));
		arcs.add(new Arc(false, 6, 6));
		arcs.add(new Arc(false, 5, 5));
		correctReduction = new State(-3, arcs);
		if (testReduction.equals(correctReduction))
			System.out.println("Passed: State reduction, trefoil BBB");
		else
			System.out.println("Failed: State reduction, trefoil BBB");
		
		// Figure 1: AA
		arcs.clear();
		arcs.add(new Arc(true, 1, 3));
		arcs.add(new Arc(true, 2, 4));
		arcs.add(new Arc(true, 2, 4));
		arcs.add(new Arc(true, 3, 1));
		originalState = new State(2, arcs);
		testReduction = originalState.getReduction();
		arcs.clear();
		arcs.add(new Arc(false, 1, 1));
		arcs.add(new Arc(true, 2, 4));
		arcs.add(new Arc(true, 2, 4));
		correctReduction = new State(2, arcs);
		if (testReduction.equals(correctReduction))
			System.out.println("Passed: State reduction, Figure AA");
		else
			System.out.println("Failed: State reduction, Figure AA");
		
		// Figure 2: AB
		arcs.clear();
		arcs.add(new Arc(true, 1, 3));
		arcs.add(new Arc(true, 2, 4));
		arcs.add(new Arc(false, 2, 1));
		arcs.add(new Arc(false, 4, 3));
		originalState = new State(0, arcs);
		testReduction = originalState.getReduction();
		arcs.clear();
		arcs.add(new Arc(true, 2, 4));
		arcs.add(new Arc(true, 2, 4));
		correctReduction = new State(0, arcs);
		if (testReduction.equals(correctReduction))
			System.out.println("Passed: State reduction, Figure AB");
		else
			System.out.println("Failed: State reduction, Figure AB");
		
		// Figure 3: BA
		arcs.clear();
		arcs.add(new Arc(false, 1, 4));
		arcs.add(new Arc(false, 2, 3));
		arcs.add(new Arc(true, 2, 4));
		arcs.add(new Arc(true, 3, 1));
		originalState = new State(0, arcs);
		testReduction = originalState.getReduction();
		arcs.clear();
		arcs.add(new Arc(true, 3, 1));
		arcs.add(new Arc(true, 3, 1));
		correctReduction = new State(0, arcs);
		if (testReduction.equals(correctReduction))
			System.out.println("Passed: State reduction, Figure BA");
		else
			System.out.println("Failed: State reduction, Figure BA");
		
		// Figure 4: BB
		arcs.clear();
		arcs.add(new Arc(false, 1, 4));
		arcs.add(new Arc(false, 2, 3));
		arcs.add(new Arc(false, 2, 1));
		arcs.add(new Arc(false, 3, 4));
		originalState = new State(-2, arcs);
		testReduction = originalState.getReduction();
		arcs.clear();
		arcs.add(new Arc(false, 3, 3));
		correctReduction = new State(-2, arcs);
		if (testReduction.equals(correctReduction))
			System.out.println("Passed: State reduction, Figure BB");
		else
			System.out.println("Failed: State reduction, Figure BB");
	}
	public static void testGetSmoothingsFromInt() {
		VirtualKnot K = new VirtualKnot();
		
		// Get all possible smoothings for trefoil knot
		K.addCrossing(new ClassicalCrossing(false, 1, 4, 6 ,3));
		K.addCrossing(new ClassicalCrossing(true, 3, 6, 2, 5));
		K.addCrossing(new ClassicalCrossing(false, 5, 2, 4, 1));
		
		int numCrossings = K.getCrossings().size();
		int numStates = (int)Math.pow(2, numCrossings);
		
		boolean[][] correctSmoothings = new boolean[numStates][numCrossings];
		correctSmoothings[0][0] = false;
		correctSmoothings[0][1] = false;
		correctSmoothings[0][2] = false;
		correctSmoothings[1][0] = false;
		correctSmoothings[1][1] = false;
		correctSmoothings[1][2] = true;
		correctSmoothings[2][0] = false;
		correctSmoothings[2][1] = true;
		correctSmoothings[2][2] = false;
		correctSmoothings[3][0] = false;
		correctSmoothings[3][1] = true;
		correctSmoothings[3][2] = true;
		correctSmoothings[4][0] = true;
		correctSmoothings[4][1] = false;
		correctSmoothings[4][2] = false;
		correctSmoothings[5][0] = true;
		correctSmoothings[5][1] = false;
		correctSmoothings[5][2] = true;
		correctSmoothings[6][0] = true;
		correctSmoothings[6][1] = true;
		correctSmoothings[6][2] = false;
		correctSmoothings[7][0] = true;
		correctSmoothings[7][1] = true;
		correctSmoothings[7][2] = true;
		
		boolean correct;
		
		for (int i = 0; i < numStates; i++) {
			boolean[] testSmoothings = K.getSmoothingsFromInt(i);
			
			correct = true;
			int j = 0;
			
			if (testSmoothings.length != correctSmoothings[i].length) correct = false;
			
			while (correct && j < testSmoothings.length) {
				if (testSmoothings[j] != correctSmoothings[i][j])
					correct = true;
					
				j++;
			}
			
			if (correct){
				System.out.println("Passed: getSmoothingsFromInt() Trefoil " + i); 
			} else {
				System.out.println("Failed: getSmoothingsFromInt() Trefoil " + i);
			}
		}
	}
	public static void testGetExpansion() {
		
		// Case 1: Trefoil knot
		VirtualKnot K = new VirtualKnot();
		
		K.addCrossing(new ClassicalCrossing(true, 1, 4, 6 ,3));
		K.addCrossing(new ClassicalCrossing(true, 3, 6, 5, 2));
		K.addCrossing(new ClassicalCrossing(true, 2, 5, 4, 1));
		
		int numCrossings = K.getCrossings().size();
		int numStates = (int)Math.pow(2, numCrossings);
		
		boolean[][] smoothings = new boolean[numStates][numCrossings];
		State[] testStates = new State[numStates];
		State[] correctStates = new State[numStates];
		
		ArrayList<Arc> correctArcs = new ArrayList<Arc>();
		correctArcs.add(new Arc(false, 1, 2));
		correctArcs.add(new Arc(false, 2, 3));
		correctArcs.add(new Arc(false, 3, 1));
		correctArcs.add(new Arc(false, 4, 5));
		correctArcs.add(new Arc(false, 5, 6));
		correctArcs.add(new Arc(false, 6, 4));
		correctStates[0] = new State(3, correctArcs);
		
		correctArcs = new ArrayList<Arc>();
		correctArcs.add(new Arc(true, 2, 5));
		correctArcs.add(new Arc(false, 2, 3));
		correctArcs.add(new Arc(false, 3, 1));
		correctArcs.add(new Arc(true, 4, 1));
		correctArcs.add(new Arc(false, 5, 6));
		correctArcs.add(new Arc(false, 6, 4));
		correctStates[1] = new State(1, correctArcs);
		
		correctArcs = new ArrayList<Arc>();
		correctArcs.add(new Arc(true, 5, 2));
		correctArcs.add(new Arc(false, 1, 2));
		correctArcs.add(new Arc(false, 3, 1));
		correctArcs.add(new Arc(true, 3, 6));
		correctArcs.add(new Arc(false, 4, 5));
		correctArcs.add(new Arc(false, 6, 4));
		correctStates[2] = new State(1, correctArcs);
		
		correctArcs = new ArrayList<Arc>();
		correctArcs.add(new Arc(true, 5, 2));
		correctArcs.add(new Arc(true, 2, 5));
		correctArcs.add(new Arc(false, 3, 1));
		correctArcs.add(new Arc(true, 3, 6));
		correctArcs.add(new Arc(true, 4, 1));
		correctArcs.add(new Arc(false, 6, 4));
		correctStates[3] = new State(-1, correctArcs);
		
		correctArcs = new ArrayList<Arc>();
		correctArcs.add(new Arc(true, 1, 4));
		correctArcs.add(new Arc(false, 4, 5));
		correctArcs.add(new Arc(false, 1, 2));
		correctArcs.add(new Arc(true, 6, 3));
		correctArcs.add(new Arc(false, 5, 6));
		correctArcs.add(new Arc(false, 2, 3));
		correctStates[4] = new State(1, correctArcs);
		
		correctArcs = new ArrayList<Arc>();
		correctArcs.add(new Arc(false, 5, 6));
		correctArcs.add(new Arc(false, 2, 3));
		correctArcs.add(new Arc(true, 6, 3));
		correctArcs.add(new Arc(true, 1, 4));
		correctArcs.add(new Arc(true, 4, 1));
		correctArcs.add(new Arc(true, 2, 5));
		correctStates[5] = new State(-1, correctArcs);
		
		correctArcs = new ArrayList<Arc>();
		correctArcs.add(new Arc(false, 1, 2));
		correctArcs.add(new Arc(true, 6, 3));
		correctArcs.add(new Arc(true, 3, 6));
		correctArcs.add(new Arc(true, 5, 2));
		correctArcs.add(new Arc(true, 1, 4));
		correctArcs.add(new Arc(false, 4, 5));
		correctStates[6] = new State(-1, correctArcs);
		
		correctArcs = new ArrayList<Arc>();
		correctArcs.add(new Arc(true, 6, 3));
		correctArcs.add(new Arc(true, 4, 1));
		correctArcs.add(new Arc(true, 2, 5));
		correctArcs.add(new Arc(true, 1, 4));
		correctArcs.add(new Arc(true, 5, 2));
		correctArcs.add(new Arc(true, 3, 6));
		correctStates[7] = new State(-3, correctArcs);
		
		for (int i = 0; i < numStates; i++) {
			smoothings[i] = K.getSmoothingsFromInt(i);
			testStates[i] = K.getState(smoothings[i]);
			
			if (testStates[i].equals(correctStates[i]))
				System.out.println("Passed: getExpansion() Trefoil " + i);
			else
				System.out.println("Failed: getExpansion() Trefoil " + i);
		}
		
		// Case 2: Figure 7
		K = new VirtualKnot();
		
		K.addCrossing(new ClassicalCrossing(false, 2, 3, 1 ,4));
		K.addCrossing(new ClassicalCrossing(false, 3, 4, 2, 1));
		
		numCrossings = K.getCrossings().size();
		numStates = (int)Math.pow(2, numCrossings);
		
		smoothings = new boolean[numStates][numCrossings];
		testStates = new State[numStates];
		correctStates = new State[numStates];
		
		correctArcs = new ArrayList<Arc>();
		correctArcs.add(new Arc(true, 3, 1));
		correctArcs.add(new Arc(true, 4, 2));
		correctArcs.add(new Arc(true, 4, 2));
		correctArcs.add(new Arc(true, 1, 3));
		correctStates[0] = new State(2, correctArcs);
		
		correctArcs = new ArrayList<Arc>();
		correctArcs.add(new Arc(true, 3, 1));
		correctArcs.add(new Arc(true, 4, 2));
		correctArcs.add(new Arc(false, 3, 4));
		correctArcs.add(new Arc(false, 2, 1));
		correctStates[1] = new State(0, correctArcs);
		
		correctArcs = new ArrayList<Arc>();
		correctArcs.add(new Arc(false, 2, 3));
		correctArcs.add(new Arc(false, 1, 4));
		correctArcs.add(new Arc(true, 4, 2));
		correctArcs.add(new Arc(true, 1, 3));
		correctStates[2] = new State(0, correctArcs);
		
		correctArcs = new ArrayList<Arc>();
		correctArcs.add(new Arc(false, 2, 3));
		correctArcs.add(new Arc(false, 1, 4));
		correctArcs.add(new Arc(false, 3, 4));
		correctArcs.add(new Arc(false, 2, 1));
		correctStates[3] = new State(-2, correctArcs);
		
		for (int i = 0; i < numStates; i++) {
			smoothings[i] = K.getSmoothingsFromInt(i);
			testStates[i] = K.getState(smoothings[i]);
			
			if (testStates[i].equals(correctStates[i]))
				System.out.println("Passed: getExpansion() Figure " + i);
			else
				System.out.println("Failed: getExpansion() Figure " + i);
		}
	}
	public static void testGetLoopsAndStates() {
		// Case 1: Trefoil AAA
		State e = new State();
		e.setExponent(3);
		e.addArc(new Arc(false, 1, 1));
		e.addArc(new Arc(false, 4, 4));
		int[] correctPowers = {2, 0, 0, 0};
		int[] testPowers = e.getLoopsAndStates(3);
		if (areSameIntArrays(testPowers, correctPowers)) {
			System.out.println("Passed: getLoopsAndStates() trefoil AAA");
		} else {
			System.out.println("Failed: getLoopsAndStates() trefoil AAA");
		}
		
		// Case 2: Trefoil AAB
		e = new State();
		e.setExponent(1);
		e.addArc(new Arc(false, 4, 4));
		correctPowers[0] = 1;
		testPowers = e.getLoopsAndStates(3);
		if (areSameIntArrays(testPowers, correctPowers)) {
			System.out.println("Passed: getLoopsAndStates() trefoil AAB");
		} else {
			System.out.println("Failed: getLoopsAndStates() trefoil AAB");
		}
		
		// Case 3: Trefoil ABA
		e = new State();
		e.setExponent(1);
		e.addArc(new Arc(false, 4, 4));
		testPowers = e.getLoopsAndStates(3);
		if (areSameIntArrays(testPowers, correctPowers)) {
			System.out.println("Passed: getLoopsAndStates() trefoil ABA");
		} else {
			System.out.println("Failed: getLoopsAndStates() trefoil ABA");
		}
		
		// Case 4: Trefoil ABB
		e = new State();
		e.setExponent(-1);
		e.addArc(new Arc(false, 4, 4));
		e.addArc(new Arc(false, 5, 5));
		correctPowers[0] = 2;
		testPowers = e.getLoopsAndStates(3);
		if (areSameIntArrays(testPowers, correctPowers)) {
			System.out.println("Passed: getLoopsAndStates() trefoil ABB");
		} else {
			System.out.println("Failed: getLoopsAndStates() trefoil ABB");
		}
		
		// Case 5: Trefoil BAA
		e = new State();
		e.setExponent(1);
		e.addArc(new Arc(false, 2, 2));
		correctPowers[0] = 1;
		testPowers = e.getLoopsAndStates(3);
		if (areSameIntArrays(testPowers, correctPowers)) {
			System.out.println("Passed: getLoopsAndStates() trefoil BAA");
		} else {
			System.out.println("Failed: getLoopsAndStates() trefoil BAA");
		}
		
		// Case 6: Trefoil BAB
		e = new State();
		e.setExponent(-1);
		e.addArc(new Arc(false, 1, 1));
		e.addArc(new Arc(false, 2, 2));
		correctPowers[0] = 2;
		testPowers = e.getLoopsAndStates(3);
		if (areSameIntArrays(testPowers, correctPowers)) {
			System.out.println("Passed: getLoopsAndStates() trefoil BAB");
		} else {
			System.out.println("Failed: getLoopsAndStates() trefoil BAB");
		}
		
		// Case 7: Trefoil BBA
		e = new State();
		e.setExponent(-1);
		e.addArc(new Arc(false, 1, 1));
		e.addArc(new Arc(false, 6, 6));
		testPowers = e.getLoopsAndStates(3);
		if (areSameIntArrays(testPowers, correctPowers)) {
			System.out.println("Passed: getLoopsAndStates() trefoil BBA");
		} else {
			System.out.println("Failed: getLoopsAndStates() trefoil BBA");
		}
		
		// Case 8: Trefoil BBB
		e = new State();
		e.setExponent(-3);
		e.addArc(new Arc(false, 1, 1));
		e.addArc(new Arc(false, 5, 5));
		e.addArc(new Arc(false, 6, 6));
		correctPowers[0] = 3;
		testPowers = e.getLoopsAndStates(3);
		if (areSameIntArrays(testPowers, correctPowers)) {
			System.out.println("Passed: getLoopsAndStates() trefoil BBB");
		} else {
			System.out.println("Failed: getLoopsAndStates() trefoil BBB");
		}
		
		// Case 9: Figure AA
		e = new State();
		e.setExponent(2);
		e.addArc(new Arc(false, 1, 1));
		e.addArc(new Arc(true, 2, 4));
		e.addArc(new Arc(true, 2, 4));
		correctPowers[0] = 2;
		correctPowers[1] = 1;
		testPowers = e.getLoopsAndStates(3);
		if (areSameIntArrays(testPowers, correctPowers)) {
			System.out.println("Passed: getLoopsAndStates() figure AA");
		} else {
			System.out.println("Failed: getLoopsAndStates() figure AA");
		}
		
		// Case 10: Figure AB
		e = new State();
		e.setExponent(0);
		e.addArc(new Arc(true, 2, 4));
		e.addArc(new Arc(true, 2, 4));
		correctPowers[0] = 1;
		correctPowers[1] = 1;
		testPowers = e.getLoopsAndStates(3);
		if (areSameIntArrays(testPowers, correctPowers)) {
			System.out.println("Passed: getLoopsAndStates() figure AB");
		} else {
			System.out.println("Failed: getLoopsAndStates() figure AB");
		}
		
		// Case 11: Figure BA
		e = new State();
		e.setExponent(0);
		e.addArc(new Arc(true, 3, 1));
		e.addArc(new Arc(true, 3, 1));
		correctPowers[0] = 1;
		correctPowers[1] = 1;
		testPowers = e.getLoopsAndStates(3);
		if (areSameIntArrays(testPowers, correctPowers)) {
			System.out.println("Passed: getLoopsAndStates() figure BA");
		} else {
			System.out.println("Failed: getLoopsAndStates() figure BA");
		}
		
		// Case 12: Figure BB
		e = new State();
		e.setExponent(-2);
		e.addArc(new Arc(false, 3, 3));
		correctPowers[0] = 1;
		correctPowers[1] = 0;
		testPowers = e.getLoopsAndStates(3);
		if (areSameIntArrays(testPowers, correctPowers)) {
			System.out.println("Passed: getLoopsAndStates() figure BB");
		} else {
			System.out.println("Failed: getLoopsAndStates() figure BB");
		}
		
	}
	public static void testPolynomial() {
		
		// Testing Polynomial, Term, Factor constructors, adding data, combining like terms, equals, etc
		
		Polynomial p = new Polynomial();
		
		ArrayList<Factor> factors = new ArrayList<Factor>();
		factors.add(new Factor("x", 2));
		p.addTerm(new Term(1, factors));
		
		factors = new ArrayList<Factor>();
		factors.add(new Factor("y", 2));
		p.addTerm(new Term(1, factors));
		
		factors = new ArrayList<Factor>();
		factors.add(new Factor("x", 1));
		p.addTerm(new Term(2, factors));
		
		factors = new ArrayList<Factor>();
		factors.add(new Factor("x", 2));
		p.addTerm(new Term(-2, factors));
		
		factors = new ArrayList<Factor>();
		p.addTerm(new Term(1, factors));
		
		Polynomial correctP = new Polynomial();
		
		factors = new ArrayList<Factor>();
		factors.add(new Factor("x", 2));
		correctP.addTerm(new Term(-1, factors));
		
		factors = new ArrayList<Factor>();
		factors.add(new Factor("y", 2));
		correctP.addTerm(new Term(1, factors));
		
		factors = new ArrayList<Factor>();
		factors.add(new Factor("x", 1));
		correctP.addTerm(new Term(2, factors));
		
		factors = new ArrayList<Factor>();
		correctP.addTerm(new Term(1, factors));
		
		if (p.equals(correctP)) {
			System.out.println("Passed: Polynomial, Term, Factor constructors, adding data, combining like terms, combining factors, equals");
		} else {
			System.out.println("Failed: Polynomial, Term, Factor constructors, adding data, combining like terms, combining factors, equals");
		}
	}
	public static void testPolynomialFromString() {
		
		// Case 1: x - 2
		Polynomial testP = new Polynomial("x - 2");
		
		Polynomial correctP = new Polynomial();
		
		ArrayList<Factor> factors = new ArrayList<Factor>();
		factors.add(new Factor("x", 1));
		correctP.addTerm(new Term(1, factors));
		
		factors = new ArrayList<Factor>();
		correctP.addTerm(new Term(-2, factors));
		
		if (testP.equals(correctP)) {
			System.out.println("Passed: polynomialFromString() " + correctP.toString());
		} else {
			System.out.println("Failed: polynomialFromString() " + correctP.toString());
		}
		
		// Case 2: x^(2) + y^(2)z + 2x
		testP = new Polynomial("x^(2) + y^(2)z + 2x");
		
		correctP = new Polynomial();
		
		factors = new ArrayList<Factor>();
		factors.add(new Factor("x", 2));
		correctP.addTerm(new Term(1, factors));
		
		factors = new ArrayList<Factor>();
		factors.add(new Factor("y", 2));
		factors.add(new Factor("z", 1));
		correctP.addTerm(new Term(1, factors));
		
		factors = new ArrayList<Factor>();
		factors.add(new Factor("x", 1));
		correctP.addTerm(new Term(2, factors));
		
		if (testP.equals(correctP)) {
			System.out.println("Passed: polynomialFromString() " + correctP.toString());
		} else {
			System.out.println("Failed: polynomialFromString() " + correctP.toString());
		}
		
		// Case 3: x^(2)zy^(2)
		testP = new Polynomial("x^(2)zy^(2)");
		
		correctP = new Polynomial();
		
		factors = new ArrayList<Factor>();
		factors.add(new Factor("x", 2));
		factors.add(new Factor("z", 1));
		factors.add(new Factor("y", 2));
		correctP.addTerm(new Term(1, factors));
		
		if (testP.equals(correctP)) {
			System.out.println("Passed: polynomialFromString() " + correctP.toString());
		} else {
			System.out.println("Failed: polynomialFromString() " + correctP.toString());
		}
		
		// Case 4: A^(-2) + 2A^(2) + k1 + k1A^(-4) - 2k1^(2)A^(-2) + 2k1^(2)A^(2) + k2A^(-2) + k2A^(2)
		testP = new Polynomial("A^(-2) + 2A^(2) + k1 + k1A^(-4) - 2k1^(2)A^(-2) + 2k1^(2)A^(2) + k2A^(-2) + k2A^(2)");
		
		correctP = new Polynomial();
		
		factors = new ArrayList<Factor>();
		factors.add(new Factor("A", -2));
		correctP.addTerm(new Term(1, factors));
		
		factors = new ArrayList<Factor>();
		factors.add(new Factor("A", 2));
		correctP.addTerm(new Term(2, factors));
				
		factors = new ArrayList<Factor>();
		factors.add(new Factor("k1", 1));
		correctP.addTerm(new Term(1, factors));
		
		factors = new ArrayList<Factor>();
		factors.add(new Factor("A", -4));
		factors.add(new Factor("k1", 1));
		correctP.addTerm(new Term(1, factors));
		
		factors = new ArrayList<Factor>();
		factors.add(new Factor("A", -2));
		factors.add(new Factor("k1", 2));
		correctP.addTerm(new Term(-2, factors));
		
		factors = new ArrayList<Factor>();
		factors.add(new Factor("A", 2));
		factors.add(new Factor("k1", 2));
		correctP.addTerm(new Term(2, factors));
		
		factors = new ArrayList<Factor>();
		factors.add(new Factor("A", -2));
		factors.add(new Factor("k2", 1));
		correctP.addTerm(new Term(1, factors));
		
		factors = new ArrayList<Factor>();
		factors.add(new Factor("A", 2));
		factors.add(new Factor("k2", 1));
		correctP.addTerm(new Term(1, factors));
		
		if (testP.equals(correctP)) {
			System.out.println("Passed: polynomialFromString() " + correctP.toString());
		} else {
			System.out.println("Failed: polynomialFromString() " + correctP.toString());
		}
	}
	public static void testPolynomialAdd() {
		
		// Case 1: Cancel some terms
		
		Polynomial p = new Polynomial("-x^(2) + y^(2) + 2x + 1");		
		Polynomial q = new Polynomial("-y^(2) + 4z - 1");
		
		Polynomial correctSum = new Polynomial("-x^(2) + 4z + 2x");
		Polynomial testSum = p.add(q);
		
		if (testSum.equals(correctSum)) {
			System.out.println("Passed: Polynomial add() cancel some");
		} else {
			System.out.println("Failed: Polynomial add() cancel some");
		}
		
		// Case 2: Cancel all terms
		
		p = new Polynomial("-x^(2) + y^(2) + 2x + 1");		
		q = new Polynomial("x^(2) - y^(2) - 2x - 1");
		
		correctSum = new Polynomial();
		testSum = p.add(q);
		
		if (testSum.equals(correctSum)) {
			System.out.println("Passed: Polynomial add() cancel all");
		} else {
			System.out.println("Failed: Polynomial add() cancel all");
		}
		
		// Case 3: Cancel no terms
		
		p = new Polynomial("-x^(2) + y^(2) + 2x + 1");		
		q = new Polynomial("z^(2) + y^(2) - 2x^(3)");
		
		correctSum = new Polynomial("-x^(2) + y^(2) + 2x + 1 + z^(2) + y^(2) - 2x^(3)");		
		testSum = p.add(q);
		
		if (testSum.equals(correctSum)) {
			System.out.println("Passed: Polynomial add() cancel none");
		} else {
			System.out.println("Failed: Polynomial add() cancel none");
		}
	}
	public static void testPolynomialMultiply() {
		
		// Case 1: 2 * -7
		
		Polynomial p = new Polynomial("2");				
		Polynomial q = new Polynomial("-7");
				
		Polynomial correctProduct = new Polynomial("-14");
		Polynomial testProduct = p.multiply(q);
				
		if (testProduct.equals(correctProduct)) {
			System.out.println("Passed: Polynomial multiply() 1: 2 * -7");
		} else {
			System.out.println("Failed: Polynomial multiply() 1: 2 * -7");
		}
		
		// Case 2: 3 * (x + y)
		
		p = new Polynomial("3");
		q = new Polynomial("x + y");
				
		correctProduct = new Polynomial("3x + 3y");
		testProduct = p.multiply(q);
				
		if (testProduct.equals(correctProduct)) {
			System.out.println("Passed: Polynomial multiply() 2: 3 * (x + y)");
		} else {
			System.out.println("Failed: Polynomial multiply() 2: 3 * (x + y)");
		}
		
		// Case 3: (x + 2) * (y - 3)
		
		p = new Polynomial("x + 2");
		q = new Polynomial("y - 3");
				
		correctProduct = new Polynomial("xy + -3x + 2y - 6");
		testProduct = p.multiply(q);
				
		if (testProduct.equals(correctProduct)) {
			System.out.println("Passed: Polynomial multiply() 3: (x + 2) * (y - 3)");
		} else {
			System.out.println("Failed: Polynomial multiply() 3: (x + 2) * (y - 3)");
		}
		
		// Case 4: (-x^2 + y^2 + 2x + 1) * (-y^2 + 4z - 1)
		
		p = new Polynomial("-x^(2) + y^(2) + 2x + 1");
		q = new Polynomial("-y^(2) + 4z - 1");
		
		correctProduct = new Polynomial("x^(2)y^(2) - y^(4) - 4x^(2)z - 2xy^(2) + 4y^(2)z + x^(2) - 2y^(2) + 8xz - 2x + 4z - 1");
		testProduct = p.multiply(q);
				
		if (testProduct.equals(correctProduct)) {
			System.out.println("Passed: Polynomial multiply() 4: (-x^2 + y^2 + 2x + 1) * (-y^2 + 4z - 1)");
		} else {
			System.out.println("Failed: Polynomial multiply() 4: (-x^2 + y^2 + 2x + 1) * (-y^2 + 4z - 1)");
		}
	}
	public static void testPolynomialExponentiate() {
		
		// Case 1: 2^4
		
		Polynomial p = new Polynomial("2");
				
		Polynomial correctProduct = new Polynomial("16");
		Polynomial testProduct = p.exponentiate(4);
				
		if (testProduct.equals(correctProduct)) {
			System.out.println("Passed: Polynomial exponentiate() 1: 2 * 4");
		} else {
			System.out.println("Failed: Polynomial exponentiate() 1: 2 * 4");
		}
		
		// Case 2: (2x)^5
		
		p = new Polynomial("2x");
		
		correctProduct = new Polynomial("32x^(5)");
		testProduct = p.exponentiate(5);
				
		if (testProduct.equals(correctProduct)) {
			System.out.println("Passed: Polynomial exponentiate() 2: (2x)^5");
		} else {
			System.out.println("Failed: Polynomial exponentiate() 2: (2x)^5");
		}
		
		// Case 3: (x + 2)^5
		
		p = new Polynomial("x + 2");
						
		correctProduct = new Polynomial("x^(5) + 10x^(4) + 40x^(3) + 80x^(2) + 80x + 32");
		testProduct = p.exponentiate(5);
						
		if (testProduct.equals(correctProduct)) {
			System.out.println("Passed: Polynomial exponentiate() 3: (x + 2)^5");
		} else {
			System.out.println("Failed: Polynomial exponentiate() 3: (x + 2)^5");
		}
		
		// Case 4: (xy + z)^4
		
		p = new Polynomial("xy + z");
						
		correctProduct = new Polynomial("x^(4)y^(4) + 4x^(3)y^(3)z + 6x^(2)y^(2)z^(2) + 4xyz^(3) + z^(4)");
		testProduct = p.exponentiate(4);
						
		if (testProduct.equals(correctProduct)) {
			System.out.println("Passed: Polynomial exponentiate() 4: (xy + z)^4");
		} else {
			System.out.println("Failed: Polynomial exponentiate() 4: (xy + z)^4");
		}
	}
	public static void testGetArrowPolynomial() {
		
		// Case 1: Hopf Link
		
		VirtualKnot K = new VirtualKnot();
		K.addCrossing(new ClassicalCrossing(false, 1, 2, 1, 2));
		
		Polynomial testAP = K.getArrowPolynomial();
		Polynomial correctAP = new Polynomial("A^(-1) + Ak1");
		
		if (testAP.equals(correctAP)) {
			System.out.println("Passed: getArrowPolynomial() 1: Hopf Link");
		} else {
			System.out.println("Failed: getArrowPolynomial() 1: Hopf Link");
		}
		
		// Case 2: Miyazawa Knot
		
		K = new VirtualKnot();
		K.addCrossing(new ClassicalCrossing(true, 4, 1, 3, 6));
		K.addCrossing(new ClassicalCrossing(false, 2, 4, 7, 5));
		K.addCrossing(new ClassicalCrossing(true, 8, 6, 1, 5));
		K.addCrossing(new ClassicalCrossing(true, 7, 3, 8, 2));
		
		testAP = K.getArrowPolynomial();
		correctAP = new Polynomial("A^(-2) + 2A^(2) + k1 - k1A^(-4) - 2k1^(2)A^(-2) - 2k1^(2)A^(2) + k2A^(-2) + k2A^(2)");
		
		
				
		if (testAP.equals(correctAP)) {
			System.out.println("Passed: getArrowPolynomial() 2: Miyazawa Knot ?");
		} else {
			System.out.println("Failed: getArrowPolynomial() 2: Miyazawa Knot ?");
		}
		
		// Case 3: Figure 9
		
		K = new VirtualKnot();
		K.addCrossing(new ClassicalCrossing(false, 2, 3, 1, 4));
		K.addCrossing(new ClassicalCrossing(false, 3, 4, 2, 1));
		
		testAP = K.getArrowPolynomial();
		correctAP = new Polynomial("A^(-2) + k1 - k1A^(4)");
				
		if (testAP.equals(correctAP)) {
			System.out.println("Passed: getArrowPolynomial() 3: Figure 9");
		} else {
			System.out.println("Failed: getArrowPolynomial() 3: Figure 9");
		}
		
		// Case 4: Virtualized Trefoil
		
		K = new VirtualKnot();
		K.addCrossing(new ClassicalCrossing(true, 5, 6, 2, 1));
		K.addCrossing(new ClassicalCrossing(true, 3, 4, 6, 5));
		K.addCrossing(new ClassicalCrossing(false, 1, 3, 4, 2));
		
		testAP = K.getArrowPolynomial();
		correctAP = new Polynomial("A^(-5)k1^(2) - A^(3)k1^(2) - A^(-5)");
				
		if (testAP.equals(correctAP)) {
			System.out.println("Passed: getArrowPolynomial() 4: Virtualized Trefoil");
		} else {
			System.out.println("Failed: getArrowPolynomial() 4: Virtualized Trefoil");
		}
		
		// Case 5: Virtual Knot 4.66
		
		K = new VirtualKnot();
		K.addCrossing(new ClassicalCrossing(false, 1, 2, 8, 3));
		K.addCrossing(new ClassicalCrossing(false, 7, 3, 6, 4));
		K.addCrossing(new ClassicalCrossing(true, 2, 6, 1, 5));
		K.addCrossing(new ClassicalCrossing(true, 5, 8, 4, 7));
				
		testAP = K.getArrowPolynomial();
		correctAP = new Polynomial("-A^(6)k1 + A^(-2)k1^(3) + 1 + A^(6)k1^(3) + 2A^(2)k1^(3) - A^(-4)k1^(2) + k2 - A^(2)k1 - A^(2)k1k2 - A^(-2)k1k2 - k1^(2) + A^(-4)");
						
		if (testAP.equals(correctAP)) {
			System.out.println("Passed: getArrowPolynomial() 5: Virtual Knot 4.66 ?");
		} else {
			System.out.println("Failed: getArrowPolynomial() 5: Virtual Knot 4.66 ?");
		}
		
		// Case 6: Virtual Knot 4.95
		
		K = new VirtualKnot();
		K.addCrossing(new ClassicalCrossing(false, 1, 5, 8, 6));
		K.addCrossing(new ClassicalCrossing(false, 8, 3, 7, 4));
		K.addCrossing(new ClassicalCrossing(false, 3, 6, 2, 7));
		K.addCrossing(new ClassicalCrossing(true, 5, 2, 4, 1));
						
		testAP = K.getArrowPolynomial();
		correctAP = new Polynomial("-A^(4)k3 + A^(-2) + k3");
								
		if (testAP.equals(correctAP)) {
			System.out.println("Passed: getArrowPolynomial() 6: Virtual Knot 4.95");
		} else {
			System.out.println("Failed: getArrowPolynomial() 6: Virtual Knot 4.95");
		}
		
		// Case 7: Virtual Trefoil
		
		K = new VirtualKnot();
		K.addCrossing(new ClassicalCrossing(false, 2, 3, 1, 4));
		K.addCrossing(new ClassicalCrossing(false, 3, 4, 2, 1));
						
		testAP = K.getArrowPolynomial();
		correctAP = new Polynomial("-A^(4)k1 + k1 + A^(-2)");
								
		if (testAP.equals(correctAP)) {
			System.out.println("Passed: getArrowPolynomial() 7: Virtual Trefoil");
		} else {
			System.out.println("Failed: getArrowPolynomial() 7: Virtual Trefoil");
		}
		
		// Case 8: 10_164
		
		K = new VirtualKnot();
		K.addCrossing(new ClassicalCrossing(false, 2, 14, 1, 15));
		K.addCrossing(new ClassicalCrossing(false, 3, 9, 2, 10));
		K.addCrossing(new ClassicalCrossing(false, 17, 3, 16, 4));
		K.addCrossing(new ClassicalCrossing(false, 12, 4, 11, 5));
		K.addCrossing(new ClassicalCrossing(false, 6, 20, 5, 1));
		K.addCrossing(new ClassicalCrossing(true, 14, 7, 13, 6));
		K.addCrossing(new ClassicalCrossing(true, 8, 19, 7, 18));
		K.addCrossing(new ClassicalCrossing(true, 18, 9, 17, 8));
		K.addCrossing(new ClassicalCrossing(true, 11, 16, 10, 15));
		K.addCrossing(new ClassicalCrossing(true, 20, 13, 19, 12));
								
		testAP = K.getArrowPolynomial();
		correctAP = new Polynomial("-2A^(-12) + 5A^(-8) - 6A^(-4) + 8 - 8A^(4) + 7A^(8) - 5A^(12) + 3A^(16) - A^(20)");
										
		if (testAP.equals(correctAP)) {
			System.out.println("Passed: getArrowPolynomial() 8: 10_164");
		} else {
			System.out.println("Failed: getArrowPolynomial() 8: 10_164");
		}
	}
	public static void testGetNormalizedArrowPolynomial() {
		
		// Case 1: Virtual Hopf Link
		
		VirtualKnot K = new VirtualKnot();
		K.addCrossing(new ClassicalCrossing(false, 1, 2, 1, 2));
		
		Polynomial testAP = K.getNormalizedArrowPolynomial();
		Polynomial correctAP = new Polynomial("-A^(2) + -A^(4)k1");
		
		if (testAP.equals(correctAP)) {
			System.out.println("Passed: getNormalizedArrowPolynomial() 1: Virtual Hopf Link");
		} else {
			System.out.println("Failed: getNormalizedArrowPolynomial() 1: Virtual Hopf Link");
		}
		
		// Case 2: Miyazawa Knot
		
		K = new VirtualKnot();
		K.addCrossing(new ClassicalCrossing(true, 4, 1, 3, 6));
		K.addCrossing(new ClassicalCrossing(false, 2, 4, 7, 5));
		K.addCrossing(new ClassicalCrossing(true, 8, 6, 1, 5));
		K.addCrossing(new ClassicalCrossing(true, 7, 3, 8, 2));
		
		testAP = K.getNormalizedArrowPolynomial();
		correctAP = new Polynomial("A^(-8) + 2A^(-4) + k1A^(-6) - k1A^(-10) - 2k1^(2)A^(-8) - 2k1^(2)A^(-4) + k2A^(-8) + k2A^(-4)");
				
		if (testAP.equals(correctAP)) {
			System.out.println("Passed: getNormalizedArrowPolynomial() 2: Miyazawa Knot ?");
		} else {
			System.out.println("Failed: getNormalizedArrowPolynomial() 2: Miyazawa Knot ?");
		}
		
		// Case 3: Figure 9
		
		K = new VirtualKnot();
		K.addCrossing(new ClassicalCrossing(false, 2, 3, 1, 4));
		K.addCrossing(new ClassicalCrossing(false, 3, 4, 2, 1));
		
		testAP = K.getNormalizedArrowPolynomial();
		correctAP = new Polynomial("A^(4) + k1A^(6) - k1A^(10)");
				
		if (testAP.equals(correctAP)) {
			System.out.println("Passed: getNormalizedArrowPolynomial() 3: Figure 9");
		} else {
			System.out.println("Failed: getNormalizedArrowPolynomial() 3: Figure 9");
		}
		
		// Case 4: Virtualized Trefoil
		
		K = new VirtualKnot();
		K.addCrossing(new ClassicalCrossing(true, 5, 6, 2, 1));
		K.addCrossing(new ClassicalCrossing(true, 3, 4, 6, 5));
		K.addCrossing(new ClassicalCrossing(false, 1, 3, 4, 2));
		
		testAP = K.getNormalizedArrowPolynomial();
		correctAP = new Polynomial("-A^(-8)k1^(2) + k1^(2) + A^(-8)");
				
		if (testAP.equals(correctAP)) {
			System.out.println("Passed: getNormalizedArrowPolynomial() 4: Virtualized Trefoil");
		} else {
			System.out.println("Failed: getNormalizedArrowPolynomial() 4: Virtualized Trefoil");
		}
		
		// Case 5: Virtual Knot 4.66
		
		K = new VirtualKnot();
		K.addCrossing(new ClassicalCrossing(false, 1, 2, 8, 3));
		K.addCrossing(new ClassicalCrossing(false, 7, 3, 6, 4));
		K.addCrossing(new ClassicalCrossing(true, 2, 6, 1, 5));
		K.addCrossing(new ClassicalCrossing(true, 5, 8, 4, 7));
				
		testAP = K.getNormalizedArrowPolynomial();
		correctAP = new Polynomial("-A^(6)k1 + A^(-2)k1^(3) + 1 + A^(6)k1^(3) + 2A^(2)k1^(3) - A^(-4)k1^(2) + k2 - A^(2)k1 - A^(2)k1k2 - A^(-2)k1k2 - k1^(2) + A^(-4)");
						
		if (testAP.equals(correctAP)) {
			System.out.println("Passed: getNormalizedArrowPolynomial() 5: Virtual Knot 4.66 ?");
		} else {
			System.out.println("Failed: getNormalizedArrowPolynomial() 5: Virtual Knot 4.66 ?");
		}
		
		// Case 6: Virtual Knot 4.95
		
		K = new VirtualKnot();
		K.addCrossing(new ClassicalCrossing(false, 1, 5, 8, 6));
		K.addCrossing(new ClassicalCrossing(false, 8, 3, 7, 4));
		K.addCrossing(new ClassicalCrossing(false, 3, 6, 2, 7));
		K.addCrossing(new ClassicalCrossing(true, 5, 2, 4, 1));
						
		testAP = K.getNormalizedArrowPolynomial();
		correctAP = new Polynomial("-A^(10)k3 + A^(4) + k3A^(6)");
								
		if (testAP.equals(correctAP)) {
			System.out.println("Passed: getNormalizedArrowPolynomial() 6: Virtual Knot 4.95");
		} else {
			System.out.println("Failed: getNormalizedArrowPolynomial() 6: Virtual Knot 4.95");
		}
	}

	// Utility Methods
	public static boolean areSameIntArrays(int[] a, int[] b) {
		if (a.length != b.length) return false;
		
		for (int i = 0; i < a.length; i++) {
			if (a[i] != b[i]) return false;
		}
		
		return true;
	}
	public static void printBooleanArray(boolean[] a) {
		System.out.print("{" + a[0]);
		for (int i = 1; i < a.length; i++)
			System.out.print(", " + a[i]);
		
		System.out.println("}");
	}
	public static void printIntegerArray(int[] a){
		System.out.print("{" + a[0]);
		for (int i = 1; i < a.length; i++)
			System.out.print(", " + a[i]);
		
		System.out.println("}");
	}
}