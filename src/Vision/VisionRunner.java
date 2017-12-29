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
		BufferedImage outImage = PixelProcessor.testProtrusions(path);
		
		Recognizer rec = new Recognizer(path);
		ArrayList<Pair<Integer, Integer>> endpoints = rec.getEndpoints(3);
		
		int[] greenArr = {0, 255, 0};
		int green = PixelProcessor.RGBToInt(greenArr);
		
		for (Pair<Integer, Integer> p : endpoints) {
			outImage.setRGB(p.getKey(), p.getValue(), green);
		}
		
		File outputfile = new File("images/test_" + path);
        try {
            ImageIO.write(outImage, "png", outputfile);
        } catch (IOException e1) {
        	System.err.println("Couldn't print file");
        }
	}
}
