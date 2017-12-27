import Knot.VirtualKnot;

public class ArrowFromFile {
	public static void main(String[] args) {
		VirtualKnot K = new VirtualKnot(args[0]);
		
		System.out.println("Arrow Polynomial: " + K.getArrowPolynomial());
		System.out.println("Writhe Normalization Factor: " + K.getNormalizationFactor());
		System.out.println("Normalized Arrow Polynomial: " + K.getNormalizedArrowPolynomial());
	}
}