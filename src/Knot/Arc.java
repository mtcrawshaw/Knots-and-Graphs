package Knot;
public class Arc {
	private boolean isOriented;		// Positive if arc has a nodal arrow/cusp, negative if not
	private int startLabel;			// Identifier of strand at start of arc
	private int endLabel;			// Identifier of strand at end of arc
	// Note: startLabel and endLabel are interchangeable if no nodal arrow is present, i.e. if isOriented is false
	
	// Constructors
	public Arc() {
		isOriented = true;
		startLabel = -1;
		endLabel = -1;
	}
	public Arc(boolean o, int s, int e) {
		isOriented = o;
		startLabel = s;
		endLabel = e;
	}
	public Arc(Arc a){
		isOriented = a.isOriented();
		startLabel = a.getStart();
		endLabel = a.getEnd();
	}
	
	// Accessors
	public boolean isOriented(){
		return isOriented;
	}
	public int getStart(){
		return startLabel;
	}
	public int getEnd(){
		return endLabel;
	}
	
	// Mutators
	public void setOrientation(boolean o){
		isOriented = o;
	}
	public void setStart(int s){
		startLabel = s;
	}
	public void setEnd(int e){
		endLabel = e;
	}
	
	// Arc Calculations
	public Arc getReverse() {
		Arc rev = new Arc(this);
		rev.setStart(this.getEnd());
		rev.setEnd(this.getStart());
		return rev;
	}
	public static Arc[] reorderSequentialArcs(Arc a, Arc b){
		/*
		 * If arc a precedes arc b, then this method returns an array of arcs containing only
		 * two elements. Those two elements are a and b, one or both of which may have been reversed
		 * to make the sequence of labels consistent. If arc a doesn't precede arc b, then this method
		 * returns an array of two null arcs.
		 */
		Arc[] result = {null, null};
		
		if (a.isOriented() && b.isOriented()){
			if (a.getEnd() == b.getStart()){
				result[0] = a;
				result[1] = b;
			}
		} 
		else if (a.isOriented() && !b.isOriented()){
			if (a.getEnd() == b.getStart()){
				result[0] = a;
				result[1] = b;
			} else if (a.getEnd() == b.getEnd()){
				result[0] = a;
				result[1] = b.getReverse();
			}
		}
		else if (!a.isOriented() && b.isOriented()){
			if (a.getEnd() == b.getStart()){
				result[0] = a;
				result[1] = b;
			} else if (a.getStart() == b.getStart()){
				result[0] = a.getReverse();
				result[1] = b;
			}
		}
		else if (!a.isOriented() && !b.isOriented()){
			if (a.getEnd() == b.getStart()){
				result[0] = a;
				result[1] = b;
			} else if (a.getStart() == b.getStart()){
				result[0] = a.getReverse();
				result[1] = b;
			} else if (a.getEnd() == b.getEnd()){
				result[0] = a;
				result[1] = b.getReverse();
			} else if (a.getStart() == b.getEnd()) {
				result[0] = a.getReverse();
				result[1] = b.getReverse();
			}
		}
		
		return result;
	}
	public static boolean areSequential(Arc a, Arc b){
		return reorderSequentialArcs(a, b)[0] != null;
	}
	
	// Utility Methods
	public String toString(){
		String s = "(";
		s += isOriented ? "+, " : "-, ";
		s += startLabel + ", ";
		s += endLabel + ")";
		return s;
	}
	public boolean equals(Arc a){
		if (isOriented){
			return a.isOriented() && startLabel == a.getStart() && this.getEnd() == a.getEnd();
		} else {
			return !a.isOriented() && (startLabel == a.getStart() && this.getEnd() == a.getEnd()) || (startLabel == a.getEnd() && this.getEnd() == a.getStart());
		}
	}
}
