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
	private boolean verbose = false;
	
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
	public boolean getVerbose() {
		return verbose;
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
	public void setVerbose(boolean v) {
		verbose = v;
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
		final double CONTAINED_THRESHOLD = .25;
		
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
		
		final int PERCENT_SAMPLED = 100;
		double proportionSampled = (double)PERCENT_SAMPLED / 100.0;
		
		int completed = 0;
		int total = pixels.size();
		
		for (Pair<Integer, Integer> point : pixels) {
			if (Math.random() <= proportionSampled) protrusions.put(point, getNumProtrusions(point));
			
			completed++;
			if (verbose)
				System.out.println("Searching for possible endpoints: " + (double)(int)(10000.0 * (double)completed / (double)total) / 100.0 + "%");
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
	 * Returns a HashSet of HashSets of points representing the connected components of the subset of points with 1 protrusion, after the 
	 * protrusion map is smoothed until the number of connected components doesn't change. 
	 */
	@SuppressWarnings("unchecked")
	public HashSet<HashSet<Pair<Integer, Integer>>> getPossibleEndpointClusters(HashMap<Pair<Integer, Integer>, Integer> protrusions) {
		HashSet<HashSet<Pair<Integer, Integer>>> endpointClusters = new HashSet<HashSet<Pair<Integer, Integer>>>();
		
		final double INITIAL_SMOOTHING_RADIUS = 1;
		final int SMOOTHING_WINDOW = 2;
		ArrayList<Integer> numCompSeq = new ArrayList<Integer>();
		double smoothingRadius = INITIAL_SMOOTHING_RADIUS;
		
		// Smoothe protrusion map SMOOTHING_WINDOW times to fill array
		HashSet<Pair<Integer, Integer>> possibleEndpoints = new HashSet <Pair<Integer, Integer>>();
		for (Pair<Integer, Integer> p : protrusions.keySet()) {
			if (protrusions.get(p) == 1) possibleEndpoints.add(p);
		}
		PixelProcessor proc = new PixelProcessor(possibleEndpoints);
		endpointClusters = proc.getComponents();
		numCompSeq.add(endpointClusters.size());
		
		HashMap<Pair<Integer, Integer>, Integer> smoothedProtrusions = (HashMap<Pair<Integer, Integer>, Integer>)protrusions.clone();
		
		for (int i = 1; i < SMOOTHING_WINDOW; i++) {
			smoothedProtrusions = smootheProtrusionMap(smoothedProtrusions, smoothingRadius);
			
			possibleEndpoints.clear();
			for (Pair<Integer, Integer> p : smoothedProtrusions.keySet()) {
				if (smoothedProtrusions.get(p) == 1) possibleEndpoints.add(p);
			}
			proc = new PixelProcessor(possibleEndpoints);
			endpointClusters = proc.getComponents();
			numCompSeq.add(endpointClusters.size());
		}
		
		HashSet<Integer> numCompSet = new HashSet<Integer>();
		for (int i = numCompSeq.size() - SMOOTHING_WINDOW; i < numCompSeq.size(); i++) {
			numCompSet.add(numCompSeq.get(i));
		}
		// Smoothe protrusion map until stabilization (number of clusters doesn't change for SMOOTHING_WINDOW smoothings)
		while (numCompSet.size() != 1) {
			smoothedProtrusions = smootheProtrusionMap(smoothedProtrusions, smoothingRadius);
			
			possibleEndpoints.clear();
			for (Pair<Integer, Integer> p : smoothedProtrusions.keySet()) {
				if (smoothedProtrusions.get(p) == 1) possibleEndpoints.add(p);
			}
			proc = new PixelProcessor(possibleEndpoints);
			endpointClusters = proc.getComponents();
			numCompSeq.add(endpointClusters.size());
			//numCompSeq.remove(0);
			
			numCompSet.clear();
			for (int i = numCompSeq.size() - SMOOTHING_WINDOW; i < numCompSeq.size(); i++) {
				numCompSet.add(numCompSeq.get(i));
			}
		}
		
		return endpointClusters;
	}
	/*
	 * Returns an ArrayList of endpoints of the knot. Each endpoint is represented as single point from the end of a strand. 
	 * The method goes through a sampling of the points along the knot, calculates the number of protrusions for each point, 
	 * then collects all of the points with 1 protrusion and chooses one point from each cluster, then returns those endpoints.
	 */
	@SuppressWarnings("unchecked")
	public HashSet<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> getEndpoints(int numCrossings) {
		// This method is super rudimentary right now. We will change this soon.
		HashSet<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> endPoints = new HashSet<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>>();
		
		// Get and smoothe protrusion map until number of connected components of subset of points with 1 protrusion is 2 * numCrossings.
		HashMap<Pair<Integer, Integer>, Integer> protrusions = getProtrusionMap();
		HashSet<Pair<Integer, Integer>> possibleEndpoints = new HashSet<Pair<Integer, Integer>>();
		for (Pair<Integer, Integer> point : protrusions.keySet()) {
			if (protrusions.get(point) == 1) possibleEndpoints.add(point);
		}
		HashSet<HashSet<Pair<Integer, Integer>>> endpointClusters = (new PixelProcessor(possibleEndpoints)).getComponents();
		HashSet<HashSet<Pair<Integer, Integer>>> prevClusters = (HashSet<HashSet<Pair<Integer, Integer>>>) endpointClusters.clone();
		int numComp = endpointClusters.size(), prevNumComp = numComp, stableLength = 0;
		double radius = 1;
		final int STABILIZATION_THRESHOLD = 10;
		
		if (verbose) System.out.println("Finding endpoint clusters");
		
		while (numComp > 2 * numCrossings) {
			protrusions = smootheProtrusionMap(protrusions, radius);
			possibleEndpoints.clear();
			for (Pair<Integer, Integer> point : protrusions.keySet()) {
				if (protrusions.get(point) == 1) possibleEndpoints.add(point);
			}
			
			// We keep the clusters from the last smoothing cycle in case we smoothe too much and a component disappears, in which case we take the largest components from the previous clusters
			prevClusters = (HashSet<HashSet<Pair<Integer, Integer>>>) endpointClusters.clone();
			endpointClusters = (new PixelProcessor(possibleEndpoints)).getComponents();
			prevNumComp = numComp;
			numComp = endpointClusters.size();
			
			if (numComp == prevNumComp) stableLength++;
			else stableLength = 1;
			
			if (stableLength >= STABILIZATION_THRESHOLD) {
				radius += .5;
				stableLength = 0;
			}
		}
		ArrayList<HashSet<Pair<Integer, Integer>>> sortedClusters = new ArrayList<HashSet<Pair<Integer, Integer>>>();
		
		if (numComp < 2 * numCrossings) {
			endpointClusters = (HashSet<HashSet<Pair<Integer, Integer>>>)prevClusters.clone();
			for (HashSet<Pair<Integer, Integer>> cluster : endpointClusters) sortedClusters.add(cluster);
			
			sortedClusters = PixelProcessor.quickSortBySize(sortedClusters);
			while (sortedClusters.size() > 2 * numCrossings) sortedClusters.remove(sortedClusters.size() - 1);
		} else {
			for (HashSet<Pair<Integer, Integer>> cluster : endpointClusters) sortedClusters.add(cluster);
		}
		
		// Get one point as a "representative" for each connected component, then pair representatives by distance
		HashSet<Pair<Integer, Integer>> endpointReps = new HashSet<Pair<Integer, Integer>>();
		for (HashSet<Pair<Integer, Integer>> cluster : sortedClusters) {
			endpointReps.add(PixelProcessor.getMeanRepresentative(cluster));
		}
		HashSet<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> pairedEndpoints = pairEndpoints(endpointReps);
		
		// Use pairing of endpoints to choose representative from each cluster that is closest to the mean of the partner cluster
		HashSet<Pair<Integer, Integer>> keyCluster = new HashSet<Pair<Integer, Integer>>();
		HashSet<Pair<Integer, Integer>> valueCluster = new HashSet<Pair<Integer, Integer>>();
		Pair<Integer, Integer> keyRep = new Pair<Integer, Integer>(0, 0);
		Pair<Integer, Integer> valueRep = new Pair<Integer, Integer>(0, 0);
		for (Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> pair : pairedEndpoints) {
			keyCluster = PixelProcessor.findParentCluster(sortedClusters, pair.getKey());
			valueCluster = PixelProcessor.findParentCluster(sortedClusters, pair.getValue());
			valueRep = PixelProcessor.getClosestPoint(pair.getKey(), valueCluster);
			keyRep = PixelProcessor.getClosestPoint(pair.getValue(), keyCluster);
			endPoints.add(new Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>(keyRep, valueRep));
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
		
		int n = endpoints.size() / 2;
		int count = 0;
		
		while (endpoints.size() > 0) {
			count++;
			if (verbose) 
				System.out.println("Pairing endpoints " + 100.0 * (double)count / (double)n + "%");
			
			currentEndpoint = endpoints.iterator().next();
			endpoints.remove(currentEndpoint);
			currentPartner = PixelProcessor.getClosestPoint(currentEndpoint, endpoints);
			endpoints.remove(currentPartner);
			endpointPairs.add(new Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>(currentEndpoint, currentPartner));
		}
		
		return endpointPairs;
	}
	/*
	 * Returns the individual arcs (not connected components) and crossings of the knot, where each crossing is identified with 4 individual points, each from 1 arc involved in that crossing.
	 * So the ArrayList is a sequence of groups of 5 points, where each group of 5 points is (crossing, pointFromArc1, pointFromArc2, pointFromArc3, pointFromArc4).
	 */
	public Pair<HashSet<HashSet<Pair<Integer, Integer>>>, ArrayList<Pair<Integer, Integer>>> getArcsAndCrossings(HashSet<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> endpoints) {
		HashSet<Pair<Integer, Integer>> arcPixels = pixels;
		ArrayList<Pair<Integer, Integer>> crossings = new ArrayList<Pair<Integer, Integer>>();
		Pair<Integer, Integer> crossing = new Pair<Integer, Integer>(0, 0);
		
		ArrayList<Pair<Integer, Integer>> line = new ArrayList<Pair<Integer, Integer>>();
		int numPoints;
		Pair<Integer, Integer> p1, p2, currentPoint;
		int x1, y1, x2, y2;
		double t;
		
		PixelProcessor proc = new PixelProcessor(pixels);
		ArrayList<Pair<Integer, Integer>> gaps = new ArrayList<Pair<Integer, Integer>>();
		
		ArrayList<Pair<Integer, Integer>> circle = new ArrayList<Pair<Integer, Integer>>();
		boolean lookingForArc = true;
		int circleSize = 0, numArcPoints = 0;
		double radius = 0;
		
		// Iterate over each pair of endpoints, i.e. each crossing
		for (Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> pairEndpoints : endpoints) {
			p1 = pairEndpoints.getKey();
			p2 = pairEndpoints.getValue();
			x1 = p1.getKey();
			y1 = p1.getValue();
			x2 = p2.getKey();
			y2 = p2.getValue();
			numPoints = Math.abs(x2 - x1) + Math.abs(y2 - y1);
			line.clear();
			
			// Generate the line between the two paired endpoints
			for (int i = 0; i <= numPoints; i++) {
				t = (double)i / (double)numPoints;
				currentPoint = new Pair<Integer, Integer>((int)Math.round((1 - t) * x1 + t * x2), (int)Math.round((1 - t) * y1 + t * y2));
				line.add(currentPoint);
			}
			
			// Look for the two "gaps" in the line - the two largest continguous regions of the line that aren't contained in pixels. Remove what lies between the two gaps from pixels to get arcs
			gaps = proc.posOfTwoLargestGaps(line);
			int startPos1 = gaps.get(0).getKey(), endPos1 = gaps.get(0).getValue();
			int startPos2 = gaps.get(1).getKey(), endPos2 = gaps.get(1).getValue();
			int startStrandPos = Math.min(endPos1, endPos2);
			int endStrandPos = Math.max(startPos1, startPos2);
			if (startPos2 == -1) {
				startStrandPos = 0;
				endStrandPos = 0;
			}
			for (int i = startStrandPos; i < endStrandPos; i++) {
				arcPixels.removeAll(proc.getAdjacentPixels(line.get(i)));
			}
			crossing = line.get((startStrandPos + endStrandPos) / 2);
			crossings.add(crossing);
			
			// Add first two points associated with this crossing
			crossings.add(p1);
			crossings.add(p2);
			
			// Find two other points associated with this crossing
			radius = (PixelProcessor.calcDistance(crossing, p1) + PixelProcessor.calcDistance(crossing, p2)) / 2.0;
			circle = PixelProcessor.getCircle(crossing, radius);
			lookingForArc = true;
			circleSize = circle.size();
			numArcPoints = 0;
			
			for (int i = 0; i < circleSize; i++) {
				// Put condition here about adding points to crossings depending on if points are in arcPixels and not connected to either previous endPoint
				/*if (lookingForArc && arcPixels.contains(circle.get(i))) {
					lookingForArc = false;
					crossings.add(circle.get(i));
					numArcPoints++;
					
					if (numArcPoints == 2) break;
				} else if (!lookingForArc && !arcPixels.contains(circle.get(i))) {
					lookingForArc = true;
				}*/
			}
		}
		
		proc = new PixelProcessor(arcPixels);
		return new Pair<HashSet<HashSet<Pair<Integer, Integer>>>, ArrayList<Pair<Integer, Integer>>>(proc.getComponents(), crossings);
	}
	
}