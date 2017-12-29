package Vision;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.imageio.ImageIO;

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
	public static BufferedImage testProtrusions(String path) {
		BufferedImage testImage = null;
		try {
			testImage = ImageIO.read(new File("images/" + path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Recognizer rec = new Recognizer(testImage);
		HashSet<Pair<Integer, Integer>> pixels = rec.getPixels();
		int p = 0;
		int n = pixels.size();
		HashMap<Pair<Integer, Integer>, Integer> pixelsWithProtrusions = new HashMap<Pair<Integer, Integer>, Integer>();
		
		for (Pair<Integer, Integer> point : pixels) {
			int numP = rec.getNumProtrusions(point);
			System.out.println((double)p / (double)n);
			p++;
			pixelsWithProtrusions.put(point, numP);
		}
		
		int height = testImage.getHeight();
		int width = testImage.getWidth();
		int numP = 0;
		int[] blackArr = {0, 0, 0};
		int[] redArr = {255, 0, 0};
		int[] blueArr = {0, 0, 255};
		int[] greenArr = {0, 255, 0};
		int[] whiteArr = {255, 255, 255};
		int black = PixelProcessor.RGBToInt(blackArr);
		int red = PixelProcessor.RGBToInt(redArr);
		int blue = PixelProcessor.RGBToInt(blueArr);
		int green = PixelProcessor.RGBToInt(greenArr);
		int white = PixelProcessor.RGBToInt(whiteArr);
		BufferedImage outImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (pixelsWithProtrusions.containsKey(new Pair<Integer, Integer>(x, y))) {
					numP = pixelsWithProtrusions.get(new Pair<Integer, Integer>(x, y));
					
					switch (numP) {
					case 1:
						outImage.setRGB(x, y, red);
						break;
					case 2:
						outImage.setRGB(x, y, black);
						break;
					case 3:
						outImage.setRGB(x, y, blue);
						break;
					default:
						outImage.setRGB(x, y, green);
					}
				} else {
					outImage.setRGB(x, y, white);
				}
			}
		}
		
		return outImage;
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
	public static HashSet<Pair<Integer, Integer>> getAnnulus(Pair<Integer, Integer> center, double innerRadius, double outerRadius) {
		HashSet<Pair<Integer, Integer>> annulus = new HashSet<Pair<Integer, Integer>>();
		
		int centerX = center.getKey();
		int centerY = center.getValue();
		int startX = (int) (centerX - outerRadius - 1);
		int endX = (int) (centerX + outerRadius + 1);
		int startY = (int) (centerY - outerRadius - 1);
		int endY = (int) (centerY + outerRadius + 1);
		double distance = 0;
		Pair<Integer, Integer> temp = new Pair<Integer, Integer>(0, 0);
		
		for (int x = startX; x <= endX; x++) {
			for (int y = startY; y <= endY; y++) {
				temp = new Pair<Integer, Integer>(x, y);
				distance = calcDistance(center, temp);
				if (distance >= innerRadius && distance <= outerRadius) {
					annulus.add(temp);
				}
			}
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
	public static double calcDistance(Pair<Integer, Integer> p1, Pair<Integer, Integer> p2) {
		return Math.sqrt(Math.pow(p1.getValue() - p2.getValue(), 2) + Math.pow(p1.getKey() - p2.getKey(), 2));
	}
	public static double getMinDistance(Pair<Integer, Integer> p, ArrayList<Pair<Integer, Integer>> listPoints) {
		if (listPoints.size() == 0) return 0;
		
		double minDistance = calcDistance(p, listPoints.get(0));
		double distance = 0;
		
		for (int i = 1; i < listPoints.size(); i++) {
			distance = calcDistance(p, listPoints.get(i));
			if (distance < minDistance) minDistance = distance;
		}
		
		return minDistance;
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
		return rgb[0] + rgb[1] + rgb[2] <= 382;
	}
}
