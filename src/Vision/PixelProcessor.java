package Vision;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javafx.util.Pair;

public class PixelProcessor {
	private ArrayList<Pair<Integer, Integer>> pixels;
	
	// Constructors
	public PixelProcessor() {
		pixels = new ArrayList<Pair<Integer, Integer>>();
	}
	public PixelProcessor(BufferedImage img) {
		setImage(img);
	}
	public PixelProcessor(ArrayList<Pair<Integer, Integer>> p) {
		pixels = p;
	}
	
	// Mutators
	public void setImage(BufferedImage img) {
		int height = img.getHeight();
		int width = img.getWidth();
		pixels = new ArrayList<Pair<Integer, Integer>>();
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (isBlack(img.getRGB(x, y))) pixels.add(new Pair<Integer, Integer>(x, y));
			}
		}
	}
	public void setPixels(ArrayList<Pair<Integer, Integer>> p) {
		pixels = p;
	}
	
	// Accessors
	public ArrayList<Pair<Integer, Integer>> getPixels() {
		return pixels;
	}
	
	// Methods
	public ArrayList<Pair<Integer, Integer>> getAdjacentPixels(Pair<Integer, Integer> p) {
		ArrayList<Pair<Integer, Integer>> adj = new ArrayList<Pair<Integer, Integer>>();
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
	public static ArrayList<Pair<Integer, Integer>> getAnnulus(Pair<Integer, Integer> center, double innerRadius, double outerRadius) {
		ArrayList<Pair<Integer, Integer>> annulus = new ArrayList<Pair<Integer, Integer>>();
		
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
	public static double calcDistance(Pair<Integer, Integer> p1, Pair<Integer, Integer> p2) {
		return Math.sqrt(Math.pow(p1.getValue() - p2.getValue(), 2) + Math.pow(p1.getKey() - p2.getKey(), 2));
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
