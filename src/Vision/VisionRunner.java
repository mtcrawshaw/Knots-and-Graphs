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
		String path = "newTrefoil2.png";
		BufferedImage testImage = null;
		try {
			testImage = ImageIO.read(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Recognizer rec = new Recognizer(testImage);
		HashSet<Pair<Integer, Integer>> pixels = rec.getPixels();
		int freq[] = new int[50];
		
		int p = 0;
		int n = pixels.size();
		HashMap<Pair<Integer, Integer>, Integer> ends = new HashMap<Pair<Integer, Integer>, Integer>();
		
		for (Pair<Integer, Integer> point : pixels) {
			int numP = rec.getNumProtrusions(point);
			freq[numP]++;
			System.out.println((double)p / (double)n);
			p++;
			
			if (numP != 2) {
				ends.put(point, numP);
			}
		}
		
		for (int i = 0; i < freq.length; i++) {
			System.out.println(i + ": " + freq[i]);
		}
		
		/*for (Pair<Integer, Integer> point : ends.keySet()) {
			System.out.println(point + " " + ends.get(point));
		}*/
	}
}
