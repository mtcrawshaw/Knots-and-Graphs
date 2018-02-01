package Vision;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.imageio.ImageIO;

import Knot.VirtualKnot;
import javafx.util.Pair;

/*
 * Runner class to test the functionality of the Recognizer. Each of the different test functions below test a different
 * part of the recognition process, though each step does rely on the previous one, so a successful run of testKnot 
 * pretty much indicates that all other tests should run successfully. 
 */
public class VisionRunner {
	static HashMap<String, Integer> paths;
	static HashMap<String, Integer> colors;
	//Note: colorsByIndex every element of colors except for black and white
	static ArrayList<Integer> colorsByIndex;
	static boolean verbose;
	
	public static void main(String[] args) {
		fillPaths();
		fillColors();
		verbose = false;
		
		//Run test for each image whose path is in paths
		for (String path : paths.keySet()) {
			System.out.println("Starting " + path);
			testKnot(path);
	        System.out.println("Finished " + path + "\n");
		}
	}
	
	public static void fillPaths() {
		paths = new HashMap<String, Integer>();
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
		
		// These slower ones can be easily optimized once we take a systematic sampling of the pixel array instead of the whole thing
		// For now I'll leave them out
		paths.put("newTrefoil2.png", 3); // SLOW
		paths.put("perkoPair.gif", 10);  // SLOW
		paths.put("redKnot.png", 5);	 // SLOW
		paths.put("trefoil.png", 3);	 // SLOW
	}
	public static void fillColors() {
		colors = new HashMap<String, Integer>();
		
		int[][] colorArrs = {{0, 0, 0}, {255, 255, 255}, {255, 0, 0}, {255, 165, 0}, {255, 255, 0}, {0, 255, 0}, {0, 0, 255}, {0, 28, 200}, {238, 130, 238}};
		colors.put("black", PixelProcessor.RGBToInt(colorArrs[0]));
		colors.put("white", PixelProcessor.RGBToInt(colorArrs[1]));
		colors.put("red", PixelProcessor.RGBToInt(colorArrs[2]));
		colors.put("orange", PixelProcessor.RGBToInt(colorArrs[3]));
		colors.put("yellow", PixelProcessor.RGBToInt(colorArrs[4]));
		colors.put("green", PixelProcessor.RGBToInt(colorArrs[5]));
		colors.put("blue", PixelProcessor.RGBToInt(colorArrs[6]));
		colors.put("indigo", PixelProcessor.RGBToInt(colorArrs[7]));
		colors.put("violet", PixelProcessor.RGBToInt(colorArrs[8]));
		
		colorsByIndex = new ArrayList<Integer>();
		for (int i = 2; i < colorArrs.length; i++) {
			colorsByIndex.add(PixelProcessor.RGBToInt(colorArrs[i]));
		}
	}
	
	/*
	 * All these test cases essentially just get a recognizer to run part of the recognition process on a given image, and then
	 * generate a new image using the information returned by the recognizer, and write that new image to some specified test folder.
	 */
	public static void testProtrusions(String filename) {
		Recognizer rec = new Recognizer("images/" + filename);
		rec.setVerbose(verbose);
		
		BufferedImage testImage = null;
		try {
			testImage = ImageIO.read(new File("images/" + filename));
		} catch (IOException e) {
			System.err.println("Error while reading file!");
			e.printStackTrace();
		}
		
		HashMap<Pair<Integer, Integer>, Integer> protrusionMap = rec.getProtrusionMap();
		int numProtrusions;
		
		for (Pair<Integer, Integer> point : protrusionMap.keySet()) {
			numProtrusions = protrusionMap.get(point);
			
			if (numProtrusions == 1)
				testImage.setRGB(point.getKey(), point.getValue(), colors.get("orange"));
		}
		
		File outputfile = new File("images/test/1_protrusions/testProtrusions_" + filename);
        try {
            ImageIO.write(testImage, filename.substring(filename.length() - 3), outputfile);
        } catch (IOException e1) {
        	System.err.println("Couldn't print file");
        }
	}
	public static void testEndpointClusters(String filename) {
		Recognizer rec = new Recognizer("images/" + filename);
		rec.setVerbose(verbose);
		
		BufferedImage testImage = null;
		try {
			testImage = ImageIO.read(new File("images/" + filename));
		} catch (IOException e) {
			System.err.println("Error while reading file!");
			e.printStackTrace();
		}
		
		HashMap<Pair<Integer, Integer>, Integer> protrusionMap = rec.getProtrusionMap();
		ArrayList<HashSet<Pair<Integer, Integer>>> endpointClusters = rec.getEndpointClusters(paths.get(filename), protrusionMap);
		
		for (HashSet<Pair<Integer, Integer>> cluster : endpointClusters) {
			for (Pair<Integer, Integer> point : cluster) {
				testImage.setRGB(point.getKey(), point.getValue(), colors.get("orange"));
			}
		}
		
		File outputfile = new File("images/test/2_clusters/testClusters_" + filename);
        try {
            ImageIO.write(testImage, filename.substring(filename.length() - 3), outputfile);
        } catch (IOException e1) {
        	System.err.println("Couldn't print file");
        }
	}
	public static void testEndpoints(String filename) {
		Recognizer rec = new Recognizer("images/" + filename);
		rec.setVerbose(verbose);
		
		BufferedImage testImage = null;
		try {
			testImage = ImageIO.read(new File("images/" + filename));
		} catch (IOException e) {
			System.err.println("Error while reading file!");
			e.printStackTrace();
		}
		
		HashMap<Pair<Integer, Integer>, Integer> protrusionMap = rec.getProtrusionMap();
		ArrayList<HashSet<Pair<Integer, Integer>>> endpointClusters = rec.getEndpointClusters(paths.get(filename), protrusionMap);
		HashSet<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> endpoints = rec.getEndpoints(endpointClusters);
		
		ArrayList<Pair<Integer, Integer>> adj = new ArrayList<Pair<Integer, Integer>>();
		Pair<Integer, Integer> endpoint1, endpoint2;
		int color, pairNum = 0;
		
		for (Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> endpointPair : endpoints) {
			endpoint1 = endpointPair.getKey();
			endpoint2 = endpointPair.getValue();
			color = colorsByIndex.get(pairNum % colorsByIndex.size());
			pairNum++;
			
			adj = PixelProcessor.getAnnulus(endpoint1, 0, 3);
			for (Pair<Integer, Integer> point : adj) {
				testImage.setRGB(point.getKey(), point.getValue(), color);
			}
			
			adj = PixelProcessor.getAnnulus(endpoint2, 0, 3);
			for (Pair<Integer, Integer> point : adj) {
				testImage.setRGB(point.getKey(), point.getValue(), color);
			}
		}
		
		File outputfile = new File("images/test/3_endpoints/testEndpoints_" + filename);
        try {
            ImageIO.write(testImage, filename.substring(filename.length() - 3), outputfile);
        } catch (IOException e1) {
        	System.err.println("Couldn't print file");
        }
	}
	public static void testArcs(String filename) {
		Recognizer rec = new Recognizer("images/" + filename);
		rec.setVerbose(verbose);
		
		BufferedImage testImage = null;
		try {
			testImage = ImageIO.read(new File("images/" + filename));
		} catch (IOException e) {
			System.err.println("Error while reading file!");
			e.printStackTrace();
		}
		
		HashMap<Pair<Integer, Integer>, Integer> protrusionMap = rec.getProtrusionMap();
		ArrayList<HashSet<Pair<Integer, Integer>>> endpointClusters = rec.getEndpointClusters(paths.get(filename), protrusionMap);
		HashSet<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> endpoints = rec.getEndpoints(endpointClusters);
		HashMap<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>, ArrayList<Pair<Integer, Integer>>> overstrandLines = rec.getOverstrandLineMap(endpoints);
		HashMap<Pair<Integer, Integer>, Integer> arcsMap = rec.getArcsMap(overstrandLines);
		
		int width = testImage.getWidth();
		int height = testImage.getHeight();
		Pair<Integer, Integer> point = new Pair<Integer, Integer>(0, 0);
		
		for (int x = 0; x < width; x++){
			for (int y = 0; y < height; y++) {
				point = new Pair<Integer, Integer>(x, y);
				
				if (arcsMap.containsKey(point)) {
					testImage.setRGB(x, y, colorsByIndex.get(arcsMap.get(point) % colorsByIndex.size()));
				} 
			}
		}
		
		File outputfile = new File("images/test/4_arcs/testArcs_" + filename);
        try {
            ImageIO.write(testImage, filename.substring(filename.length() - 3), outputfile);
        } catch (IOException e1) {
        	System.err.println("Couldn't print file");
        }
	}
	public static void testCrossings(String filename) {
		Recognizer rec = new Recognizer("images/" + filename);
		rec.setVerbose(verbose);
		
		BufferedImage testImage = null;
		try {
			testImage = ImageIO.read(new File("images/" + filename));
		} catch (IOException e) {
			System.err.println("Error while reading file!");
			e.printStackTrace();
		}
		
		HashMap<Pair<Integer, Integer>, Integer> protrusionMap = rec.getProtrusionMap();
		ArrayList<HashSet<Pair<Integer, Integer>>> endpointClusters = rec.getEndpointClusters(paths.get(filename), protrusionMap);
		HashSet<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> endpoints = rec.getEndpoints(endpointClusters);
		HashMap<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>, ArrayList<Pair<Integer, Integer>>> overstrandLines = rec.getOverstrandLineMap(endpoints);
		HashMap<Pair<Integer, Integer>, Integer> arcsMap = rec.getArcsMap(overstrandLines);
		ArrayList<ArrayList<Pair<Integer, Integer>>> crossings = rec.getCrossings(overstrandLines, arcsMap);
		crossings = rec.getCrossingsWithDistinctReps(crossings, arcsMap);
		
		ArrayList<Pair<Integer, Integer>> adj = new ArrayList<Pair<Integer, Integer>>();
		
		for (ArrayList<Pair<Integer, Integer>> crossing : crossings) {
			for (int i = 0; i < crossing.size(); i++) {
				adj = PixelProcessor.getAnnulus(crossing.get(i), 0, 3);
				
				for (Pair<Integer, Integer> point : adj) {
					testImage.setRGB(point.getKey(), point.getValue(), (i == 0) ? colors.get("yellow") : colors.get("orange"));
				}
			}
		}
		
		File outputfile = new File("images/test/5_crossings/testCrossings_" + filename);
        try {
            ImageIO.write(testImage, filename.substring(filename.length() - 3), outputfile);
        } catch (IOException e1) {
        	System.err.println("Couldn't print file");
        }
	}
	public static void testOrientation(String filename) {
		Recognizer rec = new Recognizer("images/" + filename);
		rec.setVerbose(verbose);
		
		BufferedImage testImage = null;
		try {
			testImage = ImageIO.read(new File("images/" + filename));
		} catch (IOException e) {
			System.err.println("Error while reading file!");
			e.printStackTrace();
		}
		
		HashMap<Pair<Integer, Integer>, Integer> protrusionMap = rec.getProtrusionMap();
		ArrayList<HashSet<Pair<Integer, Integer>>> endpointClusters = rec.getEndpointClusters(paths.get(filename), protrusionMap);
		HashSet<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> endpoints = rec.getEndpoints(endpointClusters);
		HashMap<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>, ArrayList<Pair<Integer, Integer>>> overstrandLines = rec.getOverstrandLineMap(endpoints);
		HashMap<Pair<Integer, Integer>, Integer> arcsMap = rec.getArcsMap(overstrandLines);
		ArrayList<ArrayList<Pair<Integer, Integer>>> crossings = rec.getCrossings(overstrandLines, arcsMap);
		
		for (ArrayList<Pair<Integer, Integer>> crossing : crossings) {
			for (Pair<Integer, Integer> rep : crossing) {
				if (rep.getValue() == 0 && rep.getKey() == 0) return;
			}
		}
				
		crossings = rec.getCrossingsWithDistinctReps(crossings, arcsMap);
		HashMap<Pair<Integer, Integer>, Boolean> orientation = rec.orientArcs(crossings, arcsMap);
		
		ArrayList<Pair<Integer, Integer>> adj = new ArrayList<Pair<Integer, Integer>>();
		int color;
		
		for (Pair<Integer, Integer> center : orientation.keySet()) {
			adj = PixelProcessor.getAnnulus(center, 0, 3);
			color = orientation.get(center) ? colors.get("orange") : colors.get("green");
			
			for (Pair<Integer, Integer> point : adj) {
				testImage.setRGB(point.getKey(), point.getValue(), color);
			}
		}
		
		File outputfile = new File("images/test/6_orientation/testOrientation_" + filename);
        try {
            ImageIO.write(testImage, filename.substring(filename.length() - 3), outputfile);
        } catch (IOException e1) {
        	System.err.println("Couldn't print file");
        }
	}
	public static void testOrderedCrossings(String filename) {
		Recognizer rec = new Recognizer("images/" + filename);
		rec.setVerbose(verbose);
		
		BufferedImage testImage = null;
		try {
			testImage = ImageIO.read(new File("images/" + filename));
		} catch (IOException e) {
			System.err.println("Error while reading file!");
			e.printStackTrace();
		}
		
		HashMap<Pair<Integer, Integer>, Integer> protrusionMap = rec.getProtrusionMap();
		ArrayList<HashSet<Pair<Integer, Integer>>> endpointClusters = rec.getEndpointClusters(paths.get(filename), protrusionMap);
		HashSet<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> endpoints = rec.getEndpoints(endpointClusters);
		HashMap<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>, ArrayList<Pair<Integer, Integer>>> overstrandLines = rec.getOverstrandLineMap(endpoints);
		HashMap<Pair<Integer, Integer>, Integer> arcsMap = rec.getArcsMap(overstrandLines);
		ArrayList<ArrayList<Pair<Integer, Integer>>> crossings = rec.getCrossings(overstrandLines, arcsMap);
		
		for (ArrayList<Pair<Integer, Integer>> crossing : crossings) {
			for (Pair<Integer, Integer> rep : crossing) {
				if (rep.getValue() == 0 && rep.getKey() == 0) return;
			}
		}
				
		crossings = rec.getCrossingsWithDistinctReps(crossings, arcsMap);
		HashMap<Pair<Integer, Integer>, Boolean> orientation = rec.orientArcs(crossings, arcsMap);
		crossings = rec.orderArcsInCrossings(crossings, orientation);
		
		ArrayList<Pair<Integer, Integer>> adj = new ArrayList<Pair<Integer, Integer>>();
		
		for (ArrayList<Pair<Integer, Integer>> crossing : crossings) {
			for (int i = 0; i < crossing.size(); i++) {
				adj = PixelProcessor.getAnnulus(crossing.get(i), 0, i + 1);
				
				for (Pair<Integer, Integer> point : adj) {
					testImage.setRGB(point.getKey(), point.getValue(), (i == 0) ? colors.get("yellow") : colors.get("orange"));
				}
			}
		}
		
		File outputfile = new File("images/test/7_orderedCrossings/testOrderedCrossings_" + filename);
        try {
            ImageIO.write(testImage, filename.substring(filename.length() - 3), outputfile);
        } catch (IOException e1) {
        	System.err.println("Couldn't print file");
        }
	}
	public static void testKnot(String filename) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter("images/test/8_knot/testKnot_" + filename.substring(0, filename.length() - 4) + ".txt", "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		Recognizer rec = new Recognizer("images/" + filename);
		VirtualKnot knot = rec.getKnot(paths.get(filename));
		
		writer.println(knot.toString());
		writer.println(knot.getNormalizedArrowPolynomial());
		writer.close();
	}
}
