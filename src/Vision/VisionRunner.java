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
		paths.put("8_5.png", 8);	//Getting stuck here. Investigate
		paths.put("newTrefoil2.png", 3); //Also getting stuck here.
		//paths.put("perkoPair.gif", 10);
		//paths.put("redKnot.png", 5);
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
			
			Pair<HashSet<HashSet<Pair<Integer, Integer>>>, ArrayList<Pair<Integer, Integer>>> arcsAndCrossings = rec.getArcsAndCrossings(rec.getEndpoints(paths.get(path)));
			HashSet<HashSet<Pair<Integer, Integer>>> arcs = arcsAndCrossings.getKey();
			ArrayList<Pair<Integer, Integer>> crossings = arcsAndCrossings.getValue();
			HashSet<Pair<Integer, Integer>> arcsRange = new HashSet<Pair<Integer, Integer>>();
			
			for (HashSet<Pair<Integer, Integer>> arc : arcs) {
				arcsRange.addAll(arc);
			}
			
			int[] redArr = {255, 0, 0};
			int[] blueArr = {0, 0, 255};
			int[] greenArr = {0, 255, 0};
			int red = PixelProcessor.RGBToInt(redArr);
			int blue = PixelProcessor.RGBToInt(blueArr);
			int green = PixelProcessor.RGBToInt(greenArr);
			int color = green;
			int height = testImage.getHeight();
			int width = testImage.getWidth();
			HashSet<Pair<Integer, Integer>> adj = new HashSet<Pair<Integer, Integer>>();
			Pair<Integer, Integer> crossing = new Pair<Integer, Integer>(0, 0);
			for (int i = 0; i < crossings.size(); i++) {
				crossing = crossings.get(i);
				color = (i % 5 == 0) ? red : green;
				
				adj = PixelProcessor.getAdjacentPoints(crossing);
				testImage.setRGB(crossing.getKey(), crossing.getValue(), color);
				for (Pair<Integer, Integer> crossingNeighbor : adj) {
					testImage.setRGB(crossingNeighbor.getKey(), crossingNeighbor.getValue(), color);
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
