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
		String path = "6_3.png";
		Recognizer rec = new Recognizer(path);
		final int NUM_CROSSINGS = 6;
		
		final int SMOOTHING_RADIUS = 3;
		ArrayList<Integer> numCompSeq = new ArrayList<Integer>();
		PixelProcessor proc = new PixelProcessor();
		
		HashMap<Pair<Integer, Integer>, Integer> protrusions = rec.getProtrusionMap();
		HashSet<Pair<Integer, Integer>> possibleEndpoints = new HashSet<Pair<Integer, Integer>>();
		for (Pair<Integer, Integer> point : protrusions.keySet()) {
			if (protrusions.get(point) == 1) possibleEndpoints.add(point);
		}
		proc.setPixels(possibleEndpoints);
		numCompSeq.add(proc.getComponents().size());
		
		while (numCompSeq.get(numCompSeq.size() - 1) > NUM_CROSSINGS * 2) {
			protrusions = rec.smootheProtrusionMap(protrusions, SMOOTHING_RADIUS);
			possibleEndpoints = new HashSet<Pair<Integer, Integer>>();
			for (Pair<Integer, Integer> point : protrusions.keySet()) {
				if (protrusions.get(point) == 1) possibleEndpoints.add(point);
			}
			proc.setPixels(possibleEndpoints);
			numCompSeq.add(proc.getComponents().size());
		}
		
		System.out.println(numCompSeq);
		
		/*BufferedImage testImage = null;
		try {
			testImage = ImageIO.read(new File("images/" + path));
		} catch (IOException e) {
			e.printStackTrace();
		}HashMap<Pair<Integer, Integer>, Integer> protrusions = rec.getProtrusionMap();
		protrusions = rec.smootheProtrusionMap(protrusions, 5);
		protrusions = rec.smootheProtrusionMap(protrusions, 5);
		protrusions = rec.smootheProtrusionMap(protrusions, 5);
		
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
				if (protrusions.containsKey(new Pair<Integer, Integer>(x, y))) {
					numP = protrusions.get(new Pair<Integer, Integer>(x, y));
					
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
		
		File outputfile = new File("images/test/test_" + path);
		System.out.println(path.substring(path.length() - 3));
        try {
            ImageIO.write(testImage, path.substring(path.length() - 3), outputfile);
        } catch (IOException e1) {
        	System.err.println("Couldn't print file");
        }*/
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
