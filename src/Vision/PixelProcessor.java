package Vision;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import javafx.util.Pair;

public class PixelProcessor {
	private HashSet<Pair<Integer, Integer>> pixels;
	
	// Constructors
	public PixelProcessor() {
		pixels = new HashSet<Pair<Integer, Integer>>();
	}
	public PixelProcessor(BufferedImage img) {
		setImage(img);
	}
	public PixelProcessor(HashSet<Pair<Integer, Integer>> p) {
		pixels = p;
	}
	public PixelProcessor(ArrayList<Pair<Integer, Integer>> p) {
		pixels = new HashSet<Pair<Integer, Integer>>();
		for (Pair<Integer, Integer> point : p) {
			pixels.add(point);
		}
	}
	
	// Mutators
	public void setImage(BufferedImage img) {
		int height = img.getHeight();
		int width = img.getWidth();
		pixels = new HashSet<Pair<Integer, Integer>>();
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (isBlack(img.getRGB(x, y))) pixels.add(new Pair<Integer, Integer>(x, y));
			}
		}
	}
	public void setPixels(HashSet<Pair<Integer, Integer>> p) {
		pixels = p;
	}
	
	// Accessors
	public HashSet<Pair<Integer, Integer>> getPixels() {
		return pixels;
	}
	
	// Methods
	public HashSet<HashSet<Pair<Integer, Integer>>> getComponents() {
		HashSet<Pair<Integer, Integer>> remainingPixels = pixels;
		HashSet<HashSet<Pair<Integer, Integer>>> components = new HashSet<HashSet<Pair<Integer, Integer>>>();
		HashSet<Pair<Integer, Integer>> currentComp = new HashSet<Pair<Integer, Integer>>();
		HashSet<Pair<Integer, Integer>> pixelsToExplore = new HashSet<Pair<Integer, Integer>>();
		
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
				
				adj = getAdjacentPixels(p);
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
	public HashSet<Pair<Integer, Integer>> getAdjacentPixels(Pair<Integer, Integer> p) {
		HashSet<Pair<Integer, Integer>> adj = new HashSet<Pair<Integer, Integer>>();
		int x = p.getKey();
		int y = p.getValue();
		
		if (pixels.contains(new Pair<Integer, Integer>(x - 1, y - 1))) adj.add(new Pair<Integer, Integer>(x - 1, y - 1));
		if (pixels.contains(new Pair<Integer, Integer>(x - 1, y))) adj.add(new Pair<Integer, Integer>(x - 1, y));
		if (pixels.contains(new Pair<Integer, Integer>(x - 1, y + 1))) adj.add(new Pair<Integer, Integer>(x - 1, y + 1));
		if (pixels.contains(new Pair<Integer, Integer>(x, y - 1))) adj.add(new Pair<Integer, Integer>(x, y - 1));
		if (pixels.contains(new Pair<Integer, Integer>(x, y + 1))) adj.add(new Pair<Integer, Integer>(x, y + 1));
		if (pixels.contains(new Pair<Integer, Integer>(x + 1, y - 1))) adj.add(new Pair<Integer, Integer>(x + 1, y - 1));
		if (pixels.contains(new Pair<Integer, Integer>(x + 1, y))) adj.add(new Pair<Integer, Integer>(x + 1, y));
		if (pixels.contains(new Pair<Integer, Integer>(x + 1, y + 1))) adj.add(new Pair<Integer, Integer>(x + 1, y + 1));
		
		return adj;
	}
	public static HashSet<Pair<Integer, Integer>> getAdjacentPoints(Pair<Integer, Integer> p) {
		HashSet<Pair<Integer, Integer>> adj = new HashSet<Pair<Integer, Integer>>();
		int x = p.getKey();
		int y = p.getValue();
		
		adj.add(new Pair<Integer, Integer>(x - 1, y - 1));
		adj.add(new Pair<Integer, Integer>(x - 1, y));
		adj.add(new Pair<Integer, Integer>(x - 1, y + 1));
		adj.add(new Pair<Integer, Integer>(x, y - 1));
		adj.add(new Pair<Integer, Integer>(x, y + 1));
		adj.add(new Pair<Integer, Integer>(x + 1, y - 1));
		adj.add(new Pair<Integer, Integer>(x + 1, y));
		adj.add(new Pair<Integer, Integer>(x + 1, y + 1));
		
		return adj;
	}
	public static ArrayList<Pair<Integer, Integer>> getAnnulus(Pair<Integer, Integer> center, double innerRadius, double outerRadius) {
		ArrayList<Pair<Integer, Integer>> annulus = new ArrayList<Pair<Integer, Integer>>();
		
		for (int i = (int)Math.round(innerRadius); i <= Math.round(outerRadius); i++) {
			annulus.addAll(getCircle(center, i));
		}
		
		return annulus;
	}
	public static ArrayList<Pair<Integer, Integer>> getCircle(Pair<Integer, Integer> center, double radius) {
		ArrayList<Pair<Integer, Integer>> circle = new ArrayList<Pair <Integer, Integer>>();
		int x = center.getKey();
		int y = center.getValue();
		int newX = x;
		int newY = y;
		
		int numPoints = (int)Math.round(8 * radius);
		
		for (int i = 0; i < numPoints; i++) {
			newX = (int)Math.round(x + radius * Math.cos(2 * 3.1415926535 / numPoints * i));
			newY = (int)Math.round(y - radius * Math.sin(2 * 3.1415926535 / numPoints * i)); // This is a minus here since the y-axis is flipped between cartesian coordinates and image coordinates
			
			if (circle.size() == 0 || newX != circle.get(circle.size() - 1).getKey() || newX != circle.get(circle.size() - 1).getValue())
				circle.add(new Pair<Integer, Integer>(newX, newY));
		}
		
		return circle;
	}
	public int getNumSwitches(ArrayList<Pair<Integer, Integer>> curve) {
		int numSwitches = 0;
		boolean insidePixels = pixels.contains(curve.get(0));
		
		for (int i = 1; i < curve.size(); i++) {
			if (insidePixels ^ pixels.contains(curve.get(i))) {
				insidePixels = !insidePixels;
				numSwitches++;
			}
		}
		
		return numSwitches;
	}
	public int getNumPixelsContained(ArrayList<Pair<Integer, Integer>> curve) {
		int numContained = 0;
		
		for (int i = 0; i < curve.size(); i++) {
			if (pixels.contains(curve.get(i))) numContained++;
		}
		
		return numContained;
	}
	public static ArrayList<Pair<Integer, Integer>> getRectangleBetween(Pair<Integer, Integer> p1, Pair<Integer, Integer> p2) {
		ArrayList<Pair<Integer, Integer>> rectangle = new ArrayList<Pair<Integer, Integer>>();
		
		int x1, y1, x2, y2;
		int x, y;
		Pair<Integer, Integer> currentPoint = new Pair<Integer, Integer>(0, 0);
		double t;
		
		x1 = p1.getKey();
		y1 = p1.getValue();
		x2 = p2.getKey();
		y2 = p2.getValue();
		int numTraversalPoints = Math.abs(y2 - y1) + Math.abs(x2 - x1);
			
		for (int n = 0; n <= numTraversalPoints; n++) {
			t = (double)n / (double)numTraversalPoints;
			x = (int)Math.round((1 - t) * x1 + t * x2);
			y = (int)Math.round((1 - t) * y1 + t * y2);
			currentPoint = new Pair<Integer, Integer>(x, y);
			rectangle.add(currentPoint);
			rectangle.addAll(getAdjacentPoints(currentPoint));
		}
				
		return rectangle;
	}
	public static double calcDistance(Pair<Integer, Integer> p1, Pair<Integer, Integer> p2) {
		return Math.sqrt(Math.pow(p1.getValue() - p2.getValue(), 2) + Math.pow(p1.getKey() - p2.getKey(), 2));
	}
	public static Pair<Integer, Integer> getClosestPoint(Pair<Integer, Integer> p, Collection<Pair<Integer, Integer>> listPoints) {
		if (listPoints.size() == 0) return null;
		
		double minDistance = Integer.MAX_VALUE;
		Pair<Integer, Integer> closestPoint = new Pair<Integer, Integer>(0, 0);
		double distance = 0;
		
		for (Pair<Integer, Integer> listP : listPoints) {
			distance = calcDistance(p, listP);
			if (distance < minDistance) {
				minDistance = distance;
				closestPoint = listP;
			}
		}
		
		return closestPoint;
	}
	public static int RGBToInt(int[] rgb) {
		int n = rgb[0];
		n = (n << 8) + rgb[1];
		n = (n << 8) + rgb[2];
		return n;
	}
	public static int[] intToRGB(int n) {
		int[] rgb = new int[3];
		rgb[0] = (n >> 16) & 0xFF;
		rgb[1] = (n >> 8) & 0xFF;
		rgb[2] = n & 0xFF;
		return rgb;
	}
	public static boolean isBlack(int n) {
		return isBlack(intToRGB(n));
	}
	public static boolean isBlack(int[] rgb) {
		return rgb[0] + rgb[1] + rgb[2] <= 381;
	}
}
