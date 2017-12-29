package Vision;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;

import javafx.util.Pair;

import Knot.VirtualKnot;

public class Recognizer {
	private HashSet<Pair<Integer, Integer>> pixels;
	
	// Constructors
	public Recognizer(BufferedImage img) {
		setImage(img);
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
	 * Finds the connected components of the 2D pixel array using BFS, with diagonal pixels being considered adjacent.
	 * Each component is recognized as an ArrayList of pairs of integers representing the coordinates of each point.
	 * This function returns an ArrayList of such ArrayLists. 
	 */
	private HashSet<HashSet<Pair<Integer, Integer>>> getComponents(HashSet<Pair<Integer, Integer>> points) {
		HashSet<Pair<Integer, Integer>> remainingPixels = points;
		HashSet<HashSet<Pair<Integer, Integer>>> components = new HashSet<HashSet<Pair<Integer, Integer>>>();
		HashSet<Pair<Integer, Integer>> currentComp = new HashSet<Pair<Integer, Integer>>();
		HashSet<Pair<Integer, Integer>> pixelsToExplore = new HashSet<Pair<Integer, Integer>>();
		
		PixelProcessor processor = new PixelProcessor(points);
		HashSet<Pair<Integer, Integer>> adj = new HashSet<Pair<Integer, Integer>>();
		Pair<Integer, Integer> p = new Pair<Integer, Integer>(0, 0);
		while (remainingPixels.size() > 0) {
			pixelsToExplore.clear();
			p = remainingPixels.iterator().next();
			remainingPixels.remove(p);
			pixelsToExplore.add(p);
			currentComp = new HashSet<Pair<Integer, Integer>>();
			
			while (pixelsToExplore.size() > 0) {
				p = pixelsToExplore.iterator().next();
				pixelsToExplore.remove(p);
				currentComp.add(p);
				
				adj = processor.getAdjacentPixels(p);
				for (Pair<Integer, Integer> a : adj) {
					if (remainingPixels.contains(a) && !pixelsToExplore.contains(a)) { 
						remainingPixels.remove(a);
						pixelsToExplore.add(a);
					}
				}
			}
			
			components.add(currentComp);
		}
		
		return components;
	}
	/*
	 * Finds the number of "protrusions" of the knot at a given point. For example, if the point is an endpoint of a strand 
	 * (like where a strand crosses under another strand), the number of protrusions is 1. If the point is the center of a virtual
	 * crossing, the number of protrusions is 4. 
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
		
		/*
		 * To calculate the number of protrusions from a point, an annulus is "grown" from that point until the number of 
		 * connected components of the annulus intersected with the knot becomes larger than 1. The number of connected components
		 * of the annulus intersected with the knot is then calculated for the next P_WINDOW integer values of the outer radius, 
		 * and those values are averaged to produce the number of protrusions from that point.
		 */
		/*do {
			annulus = PixelProcessor.getAnnulus(p, Math.max(radius / 2, radius - 5), radius);
			intersection = new HashSet<Pair<Integer, Integer>>();
			for (Pair<Integer, Integer> a : annulus) {
				if (pixels.contains(a)) intersection.add(a);
			}
			
			numComponents = getComponents(intersection).size();
			radius += RADIUS_STEP1;
		} while (numComponents == 1);
		
		radius -= RADIUS_STEP1;
		double numProtrusions = (double)numComponents / (double)P_WINDOW;
		
		for (int i = 1; i <= P_WINDOW - 1; i++) {
			annulus = PixelProcessor.getAnnulus(p, Math.max(radius / 2, radius - 5), radius);
			intersection = new HashSet<Pair<Integer, Integer>>();
			for (Pair<Integer, Integer> a : annulus) {
				if (pixels.contains(a)) intersection.add(a);
			}
			
			numComponents = getComponents(intersection).size();
			numProtrusions += (double)numComponents / (double)P_WINDOW;
			radius += RADIUS_STEP2;
		}*/
		
		return (int)Math.round(numProtrusions);
	}
	/*
	 * Returns an ArrayList of endpoints of the knot. Each endpoint is represented as an ArrayList of Pairs. The method
	 * goes through a sampling of the points along the knot, calculates the number of protrusions for each point, then
	 * collects all of the points with 1 protrusion and separates them into connected components, then smooths those
	 * components based on a threshold ENDPOINT_THRESH. The function then returns those endpoints.
	 */
	public ArrayList<ArrayList<Pair<Integer, Integer>>> getEndpoints() {
		final int PERCENT_SAMPLED = 80;
		int step = (int) (pixels.size() * (double)PERCENT_SAMPLED / 100.0);
		
		for (int i = 0; i < pixels.size(); i += step) {
			
		}
		
		return null;
	}
}