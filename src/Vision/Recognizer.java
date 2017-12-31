package Vision;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.imageio.ImageIO;

import javafx.util.Pair;

import Knot.VirtualKnot;

public class Recognizer {
	private HashSet<Pair<Integer, Integer>> pixels;
	
	// Constructors
	public Recognizer(BufferedImage img) {
		setImage(img);
	}
	public Recognizer(String path) {
		BufferedImage testImage = null;
		try {
			testImage = ImageIO.read(new File("images/" + path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		setImage(testImage);
	}
	public Recognizer() {
		pixels = new HashSet<Pair<Integer, Integer>>();
	}
	
	// Accessors
	public HashSet<Pair<Integer, Integer>> getPixels() {
		return pixels;
	}
	
	// Mutators
	public void setImage(BufferedImage img) {
		int height = img.getHeight();
		int width = img.getWidth();
		pixels = new HashSet<Pair<Integer, Integer>>();
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (PixelProcessor.isBlack(img.getRGB(x, y))) pixels.add(new Pair<Integer, Integer>(x, y));
			}
		}
	}
	public void setPixels(HashSet<Pair<Integer, Integer>> p) {
		pixels = p;
	}
	
	// Methods
	public VirtualKnot getKnot() {
		return null;
	}
	/*
	 * Finds the number of "protrusions" of the knot at a given point. For example, if the point is an endpoint of a strand 
	 * (like where a strand crosses under another strand), the number of protrusions is 1. If the point is in the middle of a strand,
	 * the number of protrusions is 2. If the point is the center of a virtual crossing, the number of protrusions is 4. 
	 */
	public int getNumProtrusions(Pair<Integer, Integer> p) {
		// This holds the range of radii for which the number of components of the annulus intersected with pixels is calculated
		final int P_WINDOW = 5;
		final int RADIUS_STEP1 = 3;
		final int RADIUS_STEP2 = 1;
		final int INITIAL_RADIUS = 1;
		final double CONTAINED_THRESHOLD = .3;
		
		int radius = INITIAL_RADIUS;
		int numSwitches = 0;
		double amountContained = 1;
		ArrayList<Pair<Integer, Integer>> circle = new ArrayList<Pair<Integer, Integer>>();
		PixelProcessor processor = new PixelProcessor(pixels);
		
		while (amountContained > CONTAINED_THRESHOLD) {
			circle = PixelProcessor.getCircle(p, radius);
			numSwitches = processor.getNumSwitches(circle);
			amountContained = (double)processor.getNumPixelsContained(circle) / (double)circle.size();
			radius += RADIUS_STEP1;
		}
		
		double numProtrusions = (double)numSwitches / (2.0 * (double)P_WINDOW);
		for (int i = 1; i <= P_WINDOW - 1; i++) {
			circle = PixelProcessor.getCircle(p, radius);
			numProtrusions += (double)processor.getNumSwitches(circle) / (2.0 * (double)P_WINDOW);
			radius += RADIUS_STEP2;
		}
		
		return (int)Math.round(numProtrusions);
	}
	/*
	 * Chooses a random sampling of the pixels (samples PERCENT_SAMPLED percent of all pixels in the knot), then calculates
	 * the number of protrusion for each point, then returns a map of the pair (point, numProtrusions(point)).
	 */
	public HashMap<Pair<Integer, Integer>, Integer> getProtrusionMap() {
		HashMap<Pair<Integer, Integer>, Integer> protrusions = new HashMap<Pair<Integer, Integer>, Integer>();
		
		final int PERCENT_SAMPLED = 80;
		double proportionSampled = (double)PERCENT_SAMPLED / 100.0;
		
		int n = pixels.size(); // Remove this after testing
		int count = 0; // Remove this after testing
		
		for (Pair<Integer, Integer> point : pixels) {
			if (Math.random() <= proportionSampled) protrusions.put(point, getNumProtrusions(point));
			System.out.println("Searching for endpoints " + 100.0 * (double)count / (double)n); // Remove this after testing
			count++; //Remove this after testing
		}
		
		return protrusions;
	}
	/*
	 * Given a map of points to numProtrusions(point), smoothes map by setting the value of numProtrusions(p) to the average of
	 * numProtrusions(nearP) for all nearP in a circle centered at P of radius r
	 */
	public HashMap<Pair<Integer, Integer>, Integer> smootheProtrusionMap(HashMap<Pair<Integer, Integer>, Integer> protrusions, double rad) {
		HashMap<Pair<Integer, Integer>, Integer> smoothedProtrusions = new HashMap<Pair<Integer, Integer>, Integer>();
		ArrayList<Pair<Integer, Integer>> disk = new ArrayList<Pair<Integer, Integer>>();
		int sumProtrusions = 0, numNearPoints = 0;
		double avgProtrusions = 0;
		
		for (Pair<Integer, Integer> point : pixels) {
			disk = PixelProcessor.getAnnulus(point, 0, rad);
			sumProtrusions = 0;
			numNearPoints = 0;
			
			for (Pair<Integer, Integer> nearPoint : disk) {
				if (protrusions.containsKey(nearPoint)) {
					numNearPoints++;
					sumProtrusions += protrusions.get(nearPoint);
				}
			}
			
			avgProtrusions = (double)sumProtrusions / (double)numNearPoints;
			smoothedProtrusions.put(point, (int)Math.round(avgProtrusions));
		}
		
		return smoothedProtrusions;
	}
	/*
	 * START HERE TOMORROW
	 */
	public HashMap<Pair<Integer, Integer>, Integer> completelySmootheProtrusionMap(HashMap<Pair<Integer, Integer>, Integer> protrusions) {
		final int INITIAL_SMOOTHING_RADIUS = 3;
		final int SMOOTHING_WINDOW = 3;
		int numCompSeq[] = new int[SMOOTHING_WINDOW];
		
		HashSet<Pair<Integer, Integer>> possibleEndpoints = new HashSet <Pair<Integer, Integer>>();
		for (Pair<Integer, Integer> p : protrusions.keySet()) {
			if (protrusions.get(p) == 1) possibleEndpoints.add(p);
		}
		PixelProcessor proc = new PixelProcessor(possibleEndpoints);
		
		return null;
	}
	/*
	 * Returns an ArrayList of endpoints of the knot. Each endpoint is represented as single point from the end of a strand. 
	 * The method goes through a sampling of the points along the knot, calculates the number of protrusions for each point, 
	 * then collects all of the points with 1 protrusion and chooses one point from each cluster, then returns those endpoints.
	 */
	public ArrayList<Pair<Integer, Integer>> getEndpoints(int numCrossings) {
		// NO MAINTENANCE NECESSARY ON THIS METHOD - IT'S GOING TO BE COMPLETELY CHANGED SOON
		HashMap<Pair<Integer, Integer>, Integer> protrusions = getProtrusionMap();
		ArrayList<Pair<Integer, Integer>> pointsNearEnd = new ArrayList<Pair<Integer, Integer>>();
		
		for (Pair<Integer, Integer> point : protrusions.keySet()) {
			if (protrusions.get(point) == 1) pointsNearEnd.add(point);
		}
		
		ArrayList<Pair<Integer, Integer>> endPoints = new ArrayList<Pair<Integer, Integer>>();
		
		if (numCrossings <= 0 || pointsNearEnd.size() == 0) return null;
		
		double maxMinDistance = 0;
		double minDistance = 0;
		int maxMinIndex = 0;
		endPoints.add(pointsNearEnd.remove(0));
		
		while (endPoints.size() < 2 * numCrossings && pointsNearEnd.size() > 0) {
			maxMinIndex = 1;
			maxMinDistance = PixelProcessor.calcDistance(pointsNearEnd.get(0), PixelProcessor.getClosestPoint(pointsNearEnd.get(0), endPoints));
			
			for (int i = 1; i < pointsNearEnd.size(); i++) {
				minDistance = PixelProcessor.calcDistance(pointsNearEnd.get(i), PixelProcessor.getClosestPoint(pointsNearEnd.get(i), endPoints));
				if (minDistance > maxMinDistance) {
					maxMinDistance = minDistance;
					maxMinIndex = i;
				}
			}
			
			endPoints.add(pointsNearEnd.remove(maxMinIndex));
		}
		
		return endPoints;
	}
	/*
	 * Given an ArrayList of endpoints, this method returns an ArrayList of pairs of endpoints, where the endpoints are paired by distance, so that
	 * an endpoint is paired with the endpoint corresponding to the other under-strand in the same crossing. 
	 */
	public HashSet<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> pairEndpoints(HashSet<Pair<Integer, Integer>> endpoints) {
		HashSet<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> endpointPairs = new HashSet<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>>();
		Pair<Integer, Integer> currentEndpoint = new Pair<Integer, Integer>(0, 0);
		Pair<Integer, Integer> currentPartner = new Pair<Integer, Integer>(0, 0);
		
		int n = endpoints.size(); // Remove this after testing
		int count = 0;	// Remove this after testing
		
		while (endpoints.size() > 0) {
			System.out.println("Pairing endpoints " + 100.0 * (double)count / (double)n); // Remove this after testing
			count++; // Remove this after testing
			currentEndpoint = endpoints.iterator().next();
			currentPartner = PixelProcessor.getClosestPoint(currentEndpoint, endpoints);
			endpoints.remove(currentPartner);
			endpointPairs.add(new Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>(currentEndpoint, currentPartner));
		}
		
		return endpointPairs;
	}
	/*
	 * Returns the individual arcs (not connected components) of the knot. 
	 */
	public HashSet<HashSet<Pair<Integer, Integer>>> getArcs(ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> pairedEndpoints) {
		HashSet<Pair<Integer, Integer>> arcPixels = pixels;
		ArrayList<Pair<Integer, Integer>> rect = new ArrayList<Pair<Integer, Integer>>();
		
		for (Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> pairEndpoints : pairedEndpoints) {
			// Start changing it here
			rect = PixelProcessor.getRectangleBetween(pairEndpoints.getKey(), pairEndpoints.getValue());
			arcPixels.removeAll(rect);
		}
		
		PixelProcessor proc = new PixelProcessor(arcPixels);
		return proc.getComponents();
	}
}