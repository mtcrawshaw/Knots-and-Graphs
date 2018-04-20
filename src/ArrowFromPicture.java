import Knot.VirtualKnot;
import Vision.Recognizer;

public class ArrowFromPicture {
	public static void main(String[] args) {
		Recognizer rec = new Recognizer(args[0]);
		VirtualKnot K = rec.getKnot(Integer.parseInt(args[1]));

		System.out.println("Knot: " + K.toString());
		System.out.println("Arrow Polynomial: " + K.getArrowPolynomial());
		System.out.println("Writhe Normalization Factor: " + K.getNormalizationFactor());
		System.out.println("Normalized Arrow Polynomial: " + K.getNormalizedArrowPolynomial());
	}
}
