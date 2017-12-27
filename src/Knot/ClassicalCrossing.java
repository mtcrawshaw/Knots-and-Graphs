package Knot;
public class ClassicalCrossing {
	private boolean crossingType;	// Holds whether crossing is positive or negative
	/*
	 *  labels holds the identifiers of the 4 strands which create the crossing.
	 *  The first element in the array is the end of the over-strand, then labels
	 *  should be ordered as they appear in counter clockwise rotation around the crossing.
	 */
	private int[] labels; 
	
	// Constructors
	public ClassicalCrossing() {
		crossingType = true;
		labels = new int[4];
		labels[0] = -1;
		labels[1] = -1;
		labels[2] = -1;
		labels[3] = -1;
	}
	public ClassicalCrossing(boolean t, int l0, int l1, int l2, int l3) {
		crossingType = t;
		labels = new int[4];
		labels[0] = l0;
		labels[1] = l1;
		labels[2] = l2;
		labels[3] = l3;
	}
	public ClassicalCrossing(ClassicalCrossing c) {
		crossingType = c.getCrossingType();
		int[] cLabels = c.getLabels();
		labels = new int[4];
		
		for (int i = 0; i < 4; i++){
			labels[i] = cLabels[i];
		}
	}
	public ClassicalCrossing(boolean t, int[] l) {
		assert l.length == 4;
		
		crossingType = t;
		labels = l;
	}
	
	// Accessors
	public boolean getCrossingType() {
		return crossingType;
	}
	public int[] getLabels() {
		return labels;
	}
	public int getLabel(int i) {
		return labels[i];
	}
	
	// Mutators
	public void setCrossingType(boolean t){
		crossingType = t;
	}
	public void setLabels(int[] l){
		assert l.length == 4;
		labels[0] = l[0];
		labels[1] = l[1];
		labels[2] = l[2];
		labels[3] = l[3];
	}
	public void setLabel(int index, int l){
		labels[index] = l;
	}
	
	// Utility Methods
	public String toString(){
		String s = "(";
		s += crossingType ? "+, " : "-, ";
		s += labels[0] + ", ";
		s += labels[1] + ", ";
		s += labels[2] + ", ";
		s += labels[3] + ")";
		return s;
	}
}
