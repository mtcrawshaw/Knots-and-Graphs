package Knot;
import java.util.ArrayList;

public class State {
	private int exponent; // This is # of A smoothings - # of B smoothings
	private ArrayList<Arc> arcs;
	
	// Constructors
	public State() {
		exponent = 0;
		arcs = new ArrayList<Arc>();
	}
	public State(int e, ArrayList<Arc> a){
		exponent = e;
		arcs = a;
	}
	public State(State e){
		exponent = e.getExponent();
		ArrayList<Arc> newArcs = e.getArcs();
		arcs = new ArrayList<Arc>();
		
		for (int i = 0; i < newArcs.size(); i++)
			arcs.add(new Arc(newArcs.get(i)));
	}
	
	// Accessors
	public int getExponent(){
		return exponent;
	}
	public ArrayList<Arc> getArcs(){
		return arcs;
	}
	
	// Mutators
	public void setExponent(int e){
		exponent = e;
	}
	public void incrementExponent(){
		exponent++;
	}
	public void decrementExponent(){
		exponent--;
	}
	public void setArcs(ArrayList<Arc> a){
		arcs = new ArrayList<Arc>();
		
		for (int i = 0; i < a.size(); i++)
			arcs.add(new Arc(a.get(i)));
	}
	public void setArc(int index, Arc a){
		arcs.set(index, new Arc(a));
	}
	public void addArc(Arc a){
		arcs.add(new Arc(a));
	}
	public void removeArc(Arc a){
		arcs.remove(a);
	}
	public void removeArc(int index){
		arcs.remove(index);
	}
	
	// State Calculations
	public State getReduction(){
		State reduction = this;
		ArrayList<Arc> rArcs = reduction.getArcs();
		boolean reducible = true;
		int n = rArcs.size();
		
		while (reducible){
			reducible = false;
			//Loop over all arcs to check for reducibility
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					
					//Make sure we are checking valid arcs
					if (i == j || rArcs.get(i) == null || rArcs.get(j) == null) continue;
					
					//Only try to reduce if arcs are sequential
					if (Arc.areSequential(rArcs.get(i), rArcs.get(j))) {
						//Reorder the arcs so they can be properly reduced
						Arc[] reorientedArcs = Arc.reorderSequentialArcs(rArcs.get(i), rArcs.get(j));
						rArcs.set(i, reorientedArcs[0]);
						rArcs.set(j, reorientedArcs[1]);
						
						// Case 1 and 3
						if (!rArcs.get(i).isOriented() && !rArcs.get(j).isOriented()) {
							Arc newI = new Arc(rArcs.get(i));
							newI.setEnd(rArcs.get(j).getEnd());
							
							rArcs.set(i, newI);
							rArcs.set(j, null);
						}
						// Case 2
						else if (rArcs.get(i).isOriented() && rArcs.get(j).isOriented()) {
							Arc newI = new Arc(rArcs.get(i));
							newI.setEnd(rArcs.get(j).getEnd());
							newI.setOrientation(false);
							
							rArcs.set(i, newI);
							rArcs.set(j, null);
						}
						// Case 3
						else if (rArcs.get(i).isOriented() && !rArcs.get(j).isOriented()) {
							Arc newI = new Arc(rArcs.get(i));
							newI.setEnd(rArcs.get(j).getEnd());
							
							rArcs.set(i, newI);
							rArcs.set(j, null);
						}
						//Case 4
						else {
							Arc newJ = new Arc(rArcs.get(j));
							newJ.setStart(rArcs.get(i).getStart());
							rArcs.set(i, null);
							rArcs.set(j, newJ);
						}
						reducible = true;
					}
				}
			}
		}
		
		for (int i = 0; i < rArcs.size(); i++){
			if (rArcs.get(i) == null){
				rArcs.remove(i);
				i--;
			}
		}
		
		reduction.setArcs(rArcs);
		return reduction;
	}
	public int[] getLoopsAndStates(int numCrossings) {
		int[] result = new int[numCrossings + 1];
		int start, end, arrowCount;
			
		for (int i = 0; i < arcs.size(); i++) {
			if (arcs.get(i).getStart() == arcs.get(i).getEnd()) {
				result[0] = result[0] + 1;
				arcs.remove(i);
				i--;
			}
		}
		
		for (int i = 0; i < arcs.size(); i++) {			
			if (arcs.get(i) == null) continue;
			
			start = arcs.get(i).getStart();
			end = arcs.get(i).getEnd();
			arrowCount = 1;
			
			while (start != end) {
				for (int j = i + 1; j < arcs.size(); j++) {
					if (arcs.get(j) == null) continue;
					
					if (start == arcs.get(j).getEnd()) {
						start = arcs.get(j).getStart();
						arrowCount++;
						arcs.set(j, null);
					} else if (start == arcs.get(j).getStart()) {
						start = arcs.get(j).getEnd();
						arrowCount++;
						arcs.set(j, null);
					} else if (end == arcs.get(j).getStart()) {
						end = arcs.get(j).getEnd();
						arrowCount++;
						arcs.set(j, null);
					} else if (end == arcs.get(j).getEnd()) {
						end = arcs.get(j).getStart();
						arrowCount++;
						arcs.set(j, null);
					}
				}
			}
			
			result[0] = result[0] + 1;
			
			if (2 <= arrowCount && arrowCount <= 2 * numCrossings)
				result[arrowCount / 2] = result[arrowCount / 2] + 1;
		}
			
		return result;
	}
	
	// Utility Methods
	public String toString(){
		String s = "{";
		s += exponent + ", ";
		s += arcs.toString();
		s += "}";
		return s;
	}
	public boolean equals(State s){
		return this.hasSameArcs(s) && exponent == s.getExponent();
	}
	public boolean hasSameArcs(State s){
		ArrayList<Arc> hisArcs = s.getArcs();
		
		if (hisArcs.size() != arcs.size()) return false;
		
		for (Arc a : hisArcs)
			if (!hasArc(a)) return false;
		
		return true;
	}
	public boolean hasArc(Arc a) {
		boolean hasArc = false;
		
		for (Arc b : arcs)
			if (b.equals(a)) hasArc = true;
		
		return hasArc;
	}
}
