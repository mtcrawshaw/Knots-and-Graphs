package Vision;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import javafx.util.Pair;

public class VisionRunner {
	public static void main(String[] args) {
		String path = "trefoil.svg";
		BufferedImage testImage = null;
		try {
			testImage = ImageIO.read(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Recognizer rec = new Recognizer(testImage);
		ArrayList<Pair<Integer, Integer>> pixels = rec.getPixels();
		Pair<Integer, Integer> point = new Pair<Integer, Integer>(0, 0);
		int[] freq = new int[100];
		int index = 0;
		
		final int SAMPLE_SIZE = 500;
		
		for (int i = 0; i < SAMPLE_SIZE; i++) {
			index = (int)(i * pixels.size() / SAMPLE_SIZE);
			freq[rec.getNumProtrusions(pixels.get(index))]++;
			System.out.println((double)i / (double)SAMPLE_SIZE);
		}
		
		for (int i = 0; i < 100; i++) {
			System.out.println(i + ": " + freq[i]);
		}
		
		//22, 263 had 3 (outside)
		//321, 202 had 3 (middle)
		
	}
}
