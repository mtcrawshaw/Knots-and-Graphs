package Vision;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.imageio.ImageIO;

import javafx.util.Pair;
import Knot.ClassicalCrossing;
import Knot.VirtualKnot;

/*
 * Object that performs the recognition of a knot, given an image.
 */
public class Recognizer {
	private HashSet<Pair<Integer, Integer>> pixels;
	private boolean verbose = false;
	private double strandWidth = 0;
	
	// Constructors
	public Recognizer(BufferedImage img) {
		setImage(img);
	}
	public Recognizer(String path) {
		setImage(path);
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
	public void setImage(String path) {
		BufferedImage testImage = null;
		try {
			testImage = ImageIO.read(new File(path));
		} catch (IOException e) {
			System.err.println("Error while reading file!");
			e.printStackTrace();
		}
		
		setImage(testImage);
	}
	public void setImage(BufferedImage img) {
		int height = img.getHeight();
		int width = img.getWidth();
		pixels = new HashSet<Pair<Integer, Integer>>();
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (PixelProcessor.isBlack(img.getRGB(x, y))) pixels.add(new Pair<Integer, Integer>(x, y));
			}
		}
		
		measureWidth();
	}
	public void setPixels(HashSet<Pair<Integer, Integer>> p) {
		pixels = p;
	}
	public void setVerbose(boolean v) {
		verbose = v;
	}
	
	// Methods
	// Note: Most of these methods should be made private later, but they can be kept as package/public for now for testing purposes
	
	/*
	 * Finds the knot represented by the image given.
	 */
	public VirtualKnot getKnot(int numCrossings) {
		HashMap<Pair<Integer, Integer>, Integer> protrusionMap = getProtrusionMap();
		ArrayList<HashSet<Pair<Integer, Integer>>> endpointClusters = getEndpointClusters(numCrossings, protrusionMap);
		HashSet<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> endpoints = getEndpoints(endpointClusters);
		HashMap<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>, ArrayList<Pair<Integer, Integer>>> overstrandLines = getOverstrandLineMap(endpoints);
		HashMap<Pair<Integer, Integer>, Integer> arcsMap = getArcsMap(overstrandLines);
		ArrayList<ArrayList<Pair<Integer, Integer>>> crossings = getCrossings(overstrandLines, arcsMap);
		
		// This is error detection in the recognition process. If any of the crossing representatives are (0, 0), the recognition messed up and we return a new knot instance
		for (ArrayList<Pair<Integer, Integer>> crossing : crossings) {
			for (Pair<Integer, Integer> rep : crossing) {
				if (rep.getValue() == 0 && rep.getKey() == 0) return new VirtualKnot();
			}
		}
		
		crossings = getCrossingsWithDistinctReps(crossings, arcsMap);
		HashMap<Pair<Integer, Integer>, Boolean> orientation = orientArcs(crossings, arcsMap);
		crossings = orderArcsInCrossings(crossings, orientation);
		
		VirtualKnot knot = new VirtualKnot();
		ClassicalCrossing c = new ClassicalCrossing();
		int[] arcLabels = new int[4];
		boolean writhe = true;
		
		for (ArrayList<Pair<Integer, Integer>> crossing : crossings) {
			for (int i = 0; i < 4; i++) {
				arcLabels[i] = arcsMap.get(crossing.get(i + 1));
			}
			writhe = getWritheFromCrossing(crossing, orientation);
			
			c.setLabels(arcLabels);
			c.setCrossingType(writhe);
			knot.addCrossing(c);
		}
		
		return knot;
	}
	/*
	 * Estimates the width of the strand making the knot.
	 */
	public void measureWidth() {
		double width = 0;
		
		// Find leftmost point, rightmost point, highest point, and lowest point
		ArrayList<Pair<Integer, Integer>> extremes = new ArrayList<Pair<Integer, Integer>>();
		extremes.add(new Pair<Integer, Integer>(Integer.MAX_VALUE, 0));		// Leftmost point
		extremes.add(new Pair<Integer, Integer>(0, 0));						// Rightmost point
		extremes.add(new Pair<Integer, Integer>(0, Integer.MAX_VALUE));		// Highest point (closest to origin)
		extremes.add(new Pair<Integer, Integer>(0, 0));						// Lowest point (furthest from origin)
		
		int x, y;
		
		for (Pair<Integer, Integer> point : pixels) {
			x = point.getKey();
			y = point.getValue();
			
			if (x < extremes.get(0).getKey()) extremes.set(0, new Pair<Integer, Integer>(x, y));
			if (x > extremes.get(1).getKey()) extremes.set(1, new Pair<Integer, Integer>(x, y));
			if (y < extremes.get(2).getValue()) extremes.set(2, new Pair<Integer, Integer>(x, y));
			if (y > extremes.get(3).getValue()) extremes.set(3, new Pair<Integer, Integer>(x, y));
		}
		
		
		// Measure width at each of the extreme points, return the average
		Pair<Integer, Integer> point = new Pair<Integer, Integer>(0, 0);
		ArrayList<Pair<Integer, Integer>> inc = new ArrayList<Pair<Integer, Integer>>();
		int currentWidth = 0;
		inc.add(new Pair<Integer, Integer>(1, 0));
		inc.add(new Pair<Integer, Integer>(-1, 0));
		inc.add(new Pair<Integer, Integer>(0, 1));
		inc.add(new Pair<Integer, Integer>(0, -1));
		
		for (int i = 0; i < extremes.size(); i++) {
			point = new Pair<Integer, Integer>(extremes.get(i).getKey(), extremes.get(i).getValue());
			currentWidth = 0;
			
			while (pixels.contains(point)) {
				point = new Pair<Integer, Integer>(point.getKey() + inc.get(i).getKey(), point.getValue() + inc.get(i).getValue());
				currentWidth++;
			}
			
			width += (double)currentWidth / (double)extremes.size();
		}
		
		strandWidth = width;
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
		
		double radius = INITIAL_RADIUS;
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
	 * Returns the clusters made by the points with 1 protrusion, given the number of crossings and the protrusion map.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<HashSet<Pair<Integer, Integer>>> getEndpointClusters(int numCrossings, HashMap<Pair<Integer, Integer>, Integer> protrusions) {
		// This method is super rudimentary right now. We will change this soon.
		
		// Smoothe protrusion map until number of connected components of subset of points with 1 protrusion is 2 * numCrossings.
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
		
		return sortedClusters;
	}
	/*
	 * Returns an ArrayList of endpoints of the knot. Each endpoint is represented as single point from the end of a strand. 
	 * The method goes through a sampling of the points along the knot, calculates the number of protrusions for each point, 
	 * then collects all of the points with 1 protrusion and chooses one point from each cluster, then returns those endpoints.
	 */
	public HashSet<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> getEndpoints(ArrayList<HashSet<Pair<Integer, Integer>>> endpointClusters) {
		HashSet<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> endPoints = new HashSet<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>>();
		
		// Get one point as a "representative" for each connected component, then pair representatives by distance
		HashSet<Pair<Integer, Integer>> endpointReps = new HashSet<Pair<Integer, Integer>>();
		for (HashSet<Pair<Integer, Integer>> cluster : endpointClusters) {
			endpointReps.add(PixelProcessor.getMeanRepresentative(cluster));
		}
		HashSet<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> pairedEndpoints = pairEndpoints(endpointReps);
		
		// Use pairing of endpoints to choose representative from each cluster that is closest to the mean of the partner cluster
		HashSet<Pair<Integer, Integer>> keyCluster = new HashSet<Pair<Integer, Integer>>();
		HashSet<Pair<Integer, Integer>> valueCluster = new HashSet<Pair<Integer, Integer>>();
		Pair<Integer, Integer> keyRep = new Pair<Integer, Integer>(0, 0);
		Pair<Integer, Integer> valueRep = new Pair<Integer, Integer>(0, 0);
		for (Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> pair : pairedEndpoints) {
			keyCluster = PixelProcessor.findParentCluster(endpointClusters, pair.getKey());
			valueCluster = PixelProcessor.findParentCluster(endpointClusters, pair.getValue());
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
	 * Returns the lines across the overstrand between each pair of endpoints, i.e. each crossing. Specifically, for each pair of endpoints,
	 * this function "draws" a line between the two endpoints, finds the section of that line that intersects the overstrand, and adds that line
	 * to the list. Returns the list of these lines. 
	 */
	@SuppressWarnings("unchecked")
	public HashMap<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>, ArrayList<Pair<Integer, Integer>>> getOverstrandLineMap(HashSet<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> endpoints) {
		HashMap<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>, ArrayList<Pair<Integer, Integer>>> overstrandLines = new HashMap<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>, ArrayList<Pair<Integer, Integer>>>();
		
		ArrayList<Pair<Integer, Integer>> line = new ArrayList<Pair<Integer, Integer>>();
		ArrayList<Pair<Integer, Integer>> strandLine = new ArrayList<Pair<Integer, Integer>>();
		int numPoints, x1, y1, x2, y2;
		double t;
		Pair<Integer, Integer> p1, p2, currentPoint;
		
		PixelProcessor proc = new PixelProcessor(pixels);
		ArrayList<Pair<Integer, Integer>> gaps = new ArrayList<Pair<Integer, Integer>>();
		int startPos1, startPos2, endPos1, endPos2;
		int startStrandPos, endStrandPos;
		
		// Iterate over the pairs of endpoints
		for (Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> pairEndpoints : endpoints) {
			p1 = pairEndpoints.getKey();
			p2 = pairEndpoints.getValue();
			x1 = p1.getKey();
			y1 = p1.getValue();
			x2 = p2.getKey();
			y2 = p2.getValue();
			numPoints = Math.abs(x2 - x1) + Math.abs(y2 - y1);
			line.clear();
			strandLine.clear();
			
			// Generate the line between the two paired endpoints
			for (int i = 0; i <= numPoints; i++) {
				t = (double)i / (double)numPoints;
				currentPoint = new Pair<Integer, Integer>((int)Math.round((1 - t) * x1 + t * x2), (int)Math.round((1 - t) * y1 + t * y2));
				line.add(currentPoint);
			}
			
			// Look for the two "gaps" in the line - the two largest continguous regions of the line that aren't contained in pixels.
			// The region between these two gaps should be the line across the overstrand. 
			gaps = proc.posOfTwoLargestGaps(line);
			startPos1 = gaps.get(0).getKey();
			endPos1 = gaps.get(0).getValue();
			startPos2 = gaps.get(1).getKey();
			endPos2 = gaps.get(1).getValue();
			startStrandPos = Math.min(endPos1, endPos2);
			endStrandPos = Math.max(startPos1, startPos2);
			
			if (startPos2 == -1) {
				startStrandPos = 0;
				endStrandPos = 0;
			}
			
			for (int i = startStrandPos - 3; i <= endStrandPos + 3; i++) {
				if (i >= 0 && i < line.size()) strandLine.add(line.get(i));
			}
			overstrandLines.put(pairEndpoints, (ArrayList<Pair<Integer, Integer>>)strandLine.clone());
		}
		
		return overstrandLines;
	}
	/*
	 * Returns a HashMap of pixel -> arc index. This is a more useful format for the arcs than a HashSet of HashSets that just the collection of arcs,
	 * because we want to use this information to be able to quickly look up the arc index of a single point.
	 */
	@SuppressWarnings("unchecked")
	public HashMap<Pair<Integer, Integer>, Integer> getArcsMap(HashMap<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>, ArrayList<Pair<Integer, Integer>>> overstrandLineMap) {
		HashSet<Pair<Integer, Integer>> arcPixels = (HashSet<Pair<Integer, Integer>>) pixels.clone();
		PixelProcessor proc = new PixelProcessor(pixels);
		
		// For each line, remove all of the points adjacent to this line from the pixels set to get a set of pixels who connected components are the arcs
		for (ArrayList<Pair<Integer, Integer>> overstrandLine : overstrandLineMap.values()) {
			for (Pair<Integer, Integer> linePoint : overstrandLine) {
				arcPixels.removeAll(proc.getAdjacentPixels(linePoint));
			}
		}
		
		HashSet<HashSet<Pair<Integer, Integer>>> arcs = (new PixelProcessor(arcPixels)).getComponents();
		HashMap<Pair<Integer, Integer>, Integer> arcsMap = new HashMap<Pair<Integer, Integer>, Integer>();
		int arcIndex = 0;
		
		for (HashSet<Pair<Integer, Integer>> arc : arcs) {
			for (Pair<Integer, Integer> arcPoint : arc) {
				arcsMap.put(arcPoint, arcIndex);
			}
			
			arcIndex++;
		}
		
		return arcsMap;
	}
	/*
	 * Returns an ArrayList of points that represents a sequence of 5-tuples of crossings. Each 5-tuple represents a crossing. The first element
	 * of the 5-tuple is a point near the center of the crossing, then the other 4 elements are 4 points from each arc associated with that crossing.
	 * It is important to note that these points aren't ordered as they should be for the planar diagram, since such an ordering can't be done
	 * until the arcs have been oriented, but they are in order of traversal around the circle, so that 1 and 3 are paired, and 2 and 4 are paired. 
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<ArrayList<Pair<Integer, Integer>>> getCrossings(HashMap<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>, ArrayList<Pair<Integer, Integer>>> overstrandLineMap, HashMap<Pair<Integer, Integer>, Integer> arcsMap) {
		ArrayList<ArrayList<Pair<Integer, Integer>>> crossings = new ArrayList<ArrayList<Pair<Integer, Integer>>>();
		
		ArrayList<Pair<Integer, Integer>> crossing = new ArrayList<Pair<Integer, Integer>>();
		ArrayList<Pair<Integer, Integer>> overstrandLine = new ArrayList<Pair<Integer, Integer>>();
		Pair<Integer, Integer> center, endpoint1, endpoint2, currentPoint;
		ArrayList<Pair<Integer, Integer>> circle = new ArrayList<Pair<Integer, Integer>>();
		HashSet<Integer> arcsAlreadyFound = new HashSet<Integer>();
		double radius;
		int numPoints;
		
		// Iterate over pairs of endpoints to get 5-tuple corresponding to each crossing
		for (Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> pairEndpoints: overstrandLineMap.keySet()) {
			overstrandLine = overstrandLineMap.get(pairEndpoints);
			center = overstrandLine.get(overstrandLine.size() / 2);
			endpoint1 = pairEndpoints.getKey();
			endpoint2 = pairEndpoints.getValue();
			
			radius = 2 + Math.max(PixelProcessor.calcDistance(center, endpoint1), PixelProcessor.calcDistance(center, endpoint2));
			circle = PixelProcessor.getCircle(center, radius);
			numPoints = circle.size();
			arcsAlreadyFound.clear();
			
			crossing.clear();
			crossing.add(center);
			
			// Traverse circle centered at center of crossing, look for intersections with surrounding arcs
			for (int i = 0; i < numPoints; i++) {
				currentPoint = circle.get(i);
				
				if (arcsMap.containsKey(currentPoint) && !arcsAlreadyFound.contains(arcsMap.get(currentPoint))) {
					crossing.add(currentPoint);
					arcsAlreadyFound.add(arcsMap.get(currentPoint));
					
					if (crossing.size() == 5) break;
				}
			}
			
			while (crossing.size() < 5) {
				crossing.add(new Pair<Integer, Integer>(0, 0));
			}
			crossings.add((ArrayList<Pair<Integer, Integer>>)crossing.clone());
		}
		
		return crossings;
	}	
	/*
	 * Given the ArrayList of crossings, returns a modified list so that all representatives of each crossing are unique, so that no collision problems
	 * occur later when we orient the arcs by mapping each representative to a boolean. 
	 */
	public ArrayList<ArrayList<Pair<Integer, Integer>>> getCrossingsWithDistinctReps(ArrayList<ArrayList<Pair<Integer, Integer>>> crossings, HashMap<Pair<Integer, Integer>, Integer> arcsMap) {
		HashSet<Pair<Integer, Integer>> reps = new HashSet<Pair<Integer, Integer>>();
		
		HashSet<Pair<Integer, Integer>> arcPixels = new HashSet<Pair<Integer, Integer>>();
		for (Pair<Integer, Integer> point : arcsMap.keySet()) {
			arcPixels.add(point);
		}
		
		for (ArrayList<Pair<Integer, Integer>> crossing : crossings) {
			for (int i = 1; i < crossing.size(); i++) {
				while (reps.contains(crossing.get(i))) {
					arcPixels.remove(crossing.get(i));
					crossing.set(i, PixelProcessor.getClosestPoint(crossing.get(i), arcPixels));
					arcPixels.add(crossing.get(i));
				}
				
				reps.add(crossing.get(i));
			}
		}
		
		return crossings;
	}
	/*
	 * Returns a HashMap of point -> boolean to represent the orientation of the knot. Each point in the key set of the map is one of the 4 representative
	 * points for some crossing. A value of true for a representative means the orientation is going out from the crossing on the corresponding arc, false
	 * means in.
	 */
	@SuppressWarnings("unchecked")
	public HashMap<Pair<Integer, Integer>, Boolean> orientArcs(ArrayList<ArrayList<Pair<Integer, Integer>>> crossings, HashMap<Pair<Integer, Integer>, Integer> arcsMap) {
		HashMap<Pair<Integer, Integer>, Boolean> orientation = new HashMap<Pair<Integer, Integer>, Boolean>();
		
		// Pair arc representatives by the rule that two representatives across a crossing are paired
		HashMap<Pair<Integer, Integer>, Pair<Integer, Integer>> crossingPartners = new HashMap<Pair<Integer, Integer>, Pair<Integer, Integer>>();
		for (ArrayList<Pair<Integer, Integer>> crossing : crossings) {
			crossingPartners.put(crossing.get(1), crossing.get(3));
			crossingPartners.put(crossing.get(3), crossing.get(1));
			crossingPartners.put(crossing.get(2), crossing.get(4));
			crossingPartners.put(crossing.get(4), crossing.get(2));
		}
		
		// Pair arc representatives by the rule that two representatives in te same arc are paired
		HashMap<Pair<Integer, Integer>, Pair<Integer, Integer>> arcPartners = new HashMap<Pair<Integer, Integer>, Pair<Integer, Integer>>();
		
		int numArcs = 2 * crossings.size();
		ArrayList<HashSet<Pair<Integer, Integer>>> repsInArc = new ArrayList<HashSet<Pair<Integer, Integer>>>();
		for (int i = 0; i < numArcs; i++) {
			repsInArc.add(new HashSet<Pair<Integer, Integer>>());
		}
		
		Iterator<Pair<Integer, Integer>> arcIterator = crossingPartners.keySet().iterator();
		Pair<Integer, Integer> rep, repPartner;
		HashSet<Pair<Integer, Integer>> arcReps;
		int arc;
		
		while (arcIterator.hasNext()) {
			rep = arcIterator.next();
			arc = arcsMap.get(rep);
			arcReps = repsInArc.get(arc);
			
			if (arcReps.size() == 1) {
				repPartner = arcReps.iterator().next();
				arcPartners.put(new Pair<Integer, Integer>(rep.getKey(), rep.getValue()), new Pair<Integer, Integer>(repPartner.getKey(), repPartner.getValue()));
				arcPartners.put(new Pair<Integer, Integer>(repPartner.getKey(), repPartner.getValue()), new Pair<Integer, Integer>(rep.getKey(), rep.getValue()));
			}
			
			arcReps.add(new Pair<Integer, Integer>(rep.getKey(), rep.getValue()));
			repsInArc.set(arc, (HashSet<Pair<Integer, Integer>>)arcReps.clone());
		}
		
		rep = crossingPartners.keySet().iterator().next();
		orientation.put(new Pair<Integer, Integer>(rep.getKey(), rep.getValue()), true);
		rep = crossingPartners.get(rep);
		orientation.put(new Pair<Integer, Integer>(rep.getKey(), rep.getValue()), false);
		rep = arcPartners.get(rep);
		
		// Assign orientation to each representative, traversing from representative to its crossingPartner, to that representative's arc partner, and again
		while (!orientation.keySet().contains(rep)) {
			orientation.put(new Pair<Integer, Integer>(rep.getKey(), rep.getValue()), true);
			rep = crossingPartners.get(rep);
			orientation.put(new Pair<Integer, Integer>(rep.getKey(), rep.getValue()), false);
			rep = arcPartners.get(rep);
		}
		
		return orientation;
	}
	/*
	 * Orders the reps of each crossing so that the first rep is the out rep of the overstrand, and the others follow in counterclockwise order. Takes
	 * as input the crossings and the orientation of each rep.
	 */
	public ArrayList<ArrayList<Pair<Integer, Integer>>> orderArcsInCrossings(ArrayList<ArrayList<Pair<Integer, Integer>>> crossings, HashMap<Pair<Integer, Integer>, Boolean> orientation) {
		ArrayList<ArrayList<Pair<Integer, Integer>>> orderedCrossings = new ArrayList<ArrayList<Pair<Integer, Integer>>>();
		ArrayList<Pair<Integer, Integer>> orderedCrossing = new ArrayList<Pair<Integer, Integer>>();
		int firstRep = -1;
		int partnerIndex = 0;
		int index = 0;
		
		// Creating a map from point to component number, so we can quickly tell which points are in the same component
		HashSet<HashSet<Pair<Integer, Integer>>> components = (new PixelProcessor(pixels)).getComponents();
		HashMap<Pair<Integer, Integer>, Integer> componentMap = new HashMap<Pair<Integer, Integer>, Integer>();
		int componentNum = 0;
		
		for (HashSet<Pair<Integer, Integer>> component : components) {
			for (Pair<Integer, Integer> point : component) {
				componentMap.put(point, componentNum);
			}
			
			componentNum++;
		}
		
		// Ordering each crossing by finding which representative is oriented out and part of the over strand
		for (ArrayList<Pair<Integer, Integer>> crossing : crossings) {
			orderedCrossing = new ArrayList<Pair<Integer, Integer>>();
			orderedCrossing.add(crossing.get(0));
			firstRep = -1;
			
			// Find which rep should be first in ordered crossing
			for (int i = 1; i < crossing.size(); i++) {
				partnerIndex = i + ((i <= 2) ? 2 : -2);
				
				// This condition just says if the rep is oriented out and is connected to its partner, i.e. part of the overstrand of the crossing
				if (orientation.get(crossing.get(i)) && componentMap.get(crossing.get(i)) == componentMap.get(crossing.get(partnerIndex))) {
					firstRep = i;
					break;
				}
			}
			
			// If such a rep is found, reorder reps in crossing
			if (firstRep != -1) {
				orderedCrossing.add(crossing.get(firstRep));
				index = firstRep + 1;
				if (index == 5) index = 1;
				
				while (index != firstRep) {					
					orderedCrossing.add(crossing.get(index));
					
					index++;
					if (index == 5) index = 1;
				}
			}
			
			orderedCrossings.add(orderedCrossing);
		}
		
		return orderedCrossings;
	}
	/*
	 * Given an ArrayList of 5 points representing a crossing (assuming that the 4 arc representatives have been ordered) and
	 * the orientation of the arc representatives, returns the writhe of the crossing.
	 */
	public boolean getWritheFromCrossing(ArrayList<Pair<Integer, Integer>> crossing, HashMap<Pair<Integer, Integer>, Boolean> orientation) {
		return orientation.get(crossing.get(2));
	}
}