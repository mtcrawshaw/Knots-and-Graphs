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
		HashMap<String, Integer> paths = new HashMap<String, Integer>();
		paths.put("4_1.png", 4);
		paths.put("5_1.png", 5);
		paths.put("5_2.png", 5);
		paths.put("6_1.png", 6);
		paths.put("6_2.png", 6);
		paths.put("6_3.png", 6);
		paths.put("7_1.png", 7);
		paths.put("7_2.png", 7);
		paths.put("8_1.png", 8);
		paths.put("8_2.png", 8);
		paths.put("8_5.png", 8);
		paths.put("newTrefoil2.png", 3);
		paths.put("perkoPair.gif", 10);
		paths.put("redKnot.png", 5);
		paths.put("trefoil.png", 3);
		
		for (String path : paths.keySet()) {
			System.out.println("Starting " + path);
			
			Recognizer rec = new Recognizer(path);
			
			BufferedImage testImage = null;
			try {
				testImage = ImageIO.read(new File("images/" + path));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			HashSet<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> endpoints = rec.getEndpoints(paths.get(path));
			HashMap<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>, ArrayList<Pair<Integer, Integer>>> overstrandLines = rec.getOverstrandLineMap(endpoints);
			HashMap<Pair<Integer, Integer>, Integer> arcsMap = rec.getArcsMap(overstrandLines);
			ArrayList<ArrayList<Pair<Integer, Integer>>> crossings = rec.getCrossings(overstrandLines, arcsMap);
			
			int[][] colorArrs = {{255, 0, 0}, {0, 255, 0}, {0, 0, 255}, {255, 255, 0}, {255, 0, 255}, {0, 255, 255}, {255, 255, 255}};
			int[] colors = new int[7];
			
			for (int i = 0; i < colors.length; i++) {
				colors[i] = PixelProcessor.RGBToInt(colorArrs[i]);
			}
			
			int height = testImage.getHeight();
			int width = testImage.getWidth();
			int x, y;
			ArrayList<Pair<Integer, Integer>> adj = new ArrayList<Pair<Integer, Integer>>();
			
			for (ArrayList<Pair<Integer, Integer>> crossing : crossings) {
				for (int i = 0; i < crossing.size(); i++) {
					adj = PixelProcessor.getAnnulus(crossing.get(i), 0, 3);
					
					for (Pair<Integer, Integer> a : adj) {
						x = a.getKey();
						y = a.getValue();
						if (0 <= x && x < width && 0 <= y && y < height) testImage.setRGB(a.getKey(), a.getValue(), (i == 0) ? colors[0] : colors[5]);
					}
				}
			}
			
			File outputfile = new File("images/test/crossings/testCrossings_" + path);
	        try {
	            ImageIO.write(testImage, path.substring(path.length() - 3), outputfile);
	        } catch (IOException e1) {
	        	System.err.println("Couldn't print file");
	        }
	        
	        System.out.println("Finished " + path + "\n");
		}
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
