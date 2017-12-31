package Vision;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.imageio.ImageIO;

import javafx.util.Pair;

public class VisionRunner {
	public static void main(String[] args) {
		String path = "trefoil.svg";
		Recognizer rec = new Recognizer(path);
		
		BufferedImage testImage = null;
		try {
			testImage = ImageIO.read(new File("images/" + path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//testImage = testProtrusions(rec, testImage);
		
		ArrayList<Pair<Integer, Integer>> endpoints = rec.getEndpoints(3);
		testImage = testEndpoints(endpoints, testImage);
		
		ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> pairedEndpoints = rec.pairEndpoints(endpoints); 
		testImage = testPairingEndpoints(pairedEndpoints, testImage);
		
		System.out.println(rec.getArcs(pairedEndpoints).size());
		
		File outputfile = new File("images/test_" + path);
        try {
            ImageIO.write(testImage, "png", outputfile);
        } catch (IOException e1) {
        	System.err.println("Couldn't print file");
        }
	}
	
	public static BufferedImage testProtrusions(Recognizer rec, BufferedImage testImage) {
		HashSet<Pair<Integer, Integer>> pixels = rec.getPixels();
		int p = 0;
		int n = pixels.size();
		HashMap<Pair<Integer, Integer>, Integer> pixelsWithProtrusions = new HashMap<Pair<Integer, Integer>, Integer>();
		
		for (Pair<Integer, Integer> point : pixels) {
			int numP = rec.getNumProtrusions(point);
			System.out.println("Searching for endpoints " + 100.0 * (double)p / (double)n);
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
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (pixelsWithProtrusions.containsKey(new Pair<Integer, Integer>(x, y))) {
					numP = pixelsWithProtrusions.get(new Pair<Integer, Integer>(x, y));
					
					switch (numP) {
					case 1:
						testImage.setRGB(x, y, red);
						break;
					case 2:
						testImage.setRGB(x, y, black);
						break;
					case 3:
						testImage.setRGB(x, y, blue);
						break;
					default:
						testImage.setRGB(x, y, green);
					}
				} else {
					testImage.setRGB(x, y, white);
				}
			}
		}
		
		return testImage;
	}
	public static BufferedImage testEndpoints(ArrayList<Pair<Integer, Integer>> endpoints, BufferedImage testImage) {
		int[] colorArr = {0, 0, 0};
		int color = PixelProcessor.RGBToInt(colorArr);
		
		for (Pair<Integer, Integer> p : endpoints) {
			testImage.setRGB(p.getKey(), p.getValue(), color);
		}
		
		return testImage;
	}
	public static BufferedImage testPairingEndpoints(ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> pairedEndpoints, BufferedImage testImage) {
		ArrayList<Pair<Integer, Integer>> rect = new ArrayList<Pair<Integer, Integer>>();
		
		int[] colorArr = {0, 0, 0};
		int color = PixelProcessor.RGBToInt(colorArr);
		
		for (Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> pairEndpoints : pairedEndpoints) {
			rect = PixelProcessor.getRectangleBetween(pairEndpoints.getKey(), pairEndpoints.getValue());
			
			for (Pair<Integer, Integer> point : rect) {
				if (0 <= point.getKey() && point.getKey() < testImage.getWidth() && 0 <= point.getValue() && point.getValue() < testImage.getHeight()) 
					testImage.setRGB(point.getKey(), point.getValue(), color);
			}
		}
		
		return testImage;
	}
}
