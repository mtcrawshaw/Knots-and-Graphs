import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Knot.VirtualKnot;
import Polynomial.Polynomial;
import Vision.Recognizer;

public class Evaluator {
	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
		String jsonPath = "C:/Users/Michael/Desktop/Plans_for_World_Domination/Research/Knots_and_Graphs/workspace/ArrowPolynomial/src/test/jones.json";
		Object obj = new JSONParser().parse(new FileReader(jsonPath));
		JSONObject jo = (JSONObject)obj;
		
		int numCorrect = 0;
		int numComplete = 0;
		float progress = 0;
		Recognizer rec = new Recognizer();
		VirtualKnot k = new VirtualKnot();
		int numCrossings = 0;
		ArrayList<String> failureReport = new ArrayList<String>();
		ArrayList<String> successReport = new ArrayList<String>();
		ArrayList<String> errorReport = new ArrayList<String>();
		
		HashMap<Integer, Integer> numTrials = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> numSuccesses = new HashMap<Integer, Integer>();
		
		@SuppressWarnings("unchecked")
		Set<String> keys = jo.keySet();
		int numKeys = keys.size();
		int numErrors = 0;
		
		String idString = "";
		
		ArrayList<String> testIDs = new ArrayList<String>();
		testIDs.add("I_12a_0745");
		testIDs.add("I_12a_0743");
		testIDs.add("I_12n_0034");
		testIDs.add("I_12n_0032");
		testIDs.add("I_12a_0764");
		
		for (Object id : keys) {
			try {				
				idString = (String)id;
				
				if (!testIDs.contains(idString)) continue;
				
				String[] idParts = idString.split("_");
				String crossingsString = idParts[1];
				char lastChar = crossingsString.charAt(crossingsString.length() - 1);
				if (lastChar == 'a' || lastChar == 'n') {
					crossingsString = crossingsString.substring(0, crossingsString.length() - 1);
				}
				numCrossings = Integer.parseInt(crossingsString);
				
				// Increment number of trials for numCrossings
				if (numTrials.containsKey(numCrossings)) {
					numTrials.put(numCrossings, numTrials.get(numCrossings) + 1);
				} else {
					numTrials.put(numCrossings, 1);
				}
				
				String filePath = "C:/Users/Michael/Desktop/Plans_for_World_Domination/Research/Knots_and_Graphs/workspace/ArrowPolynomial/images/eval/" + idString + ".png";
				rec = new Recognizer(filePath);
				
				System.out.println(rec.toString());
				System.out.println(k.toString());
				System.out.println(numCrossings);
				
				k = rec.getKnot(numCrossings);
				
				Polynomial calculatedJones = k.getJonesPolynomial();
				
				Polynomial actualJones = new Polynomial((String)jo.get(idString));
				if (actualJones.equals(calculatedJones)) {
					numCorrect++;
					
					if (numSuccesses.containsKey(numCrossings)) {
						numSuccesses.put(numCrossings, numSuccesses.get(numCrossings) + 1);
					} else {
						numSuccesses.put(numCrossings, 1);
					}
					
					successReport.add(idString);
					successReport.add(actualJones.toString());
					successReport.add(calculatedJones.toString());
					successReport.add(k.toString());
					successReport.add("\n");
				} else {
					failureReport.add(idString);
					failureReport.add(actualJones.toString());
					failureReport.add(calculatedJones.toString());
					failureReport.add(k.toString());
					failureReport.add("\n");
				}
			} catch (Exception e) {
				numErrors++;
				errorReport.add(idString);
				errorReport.add(e.toString());
				e.printStackTrace();
				errorReport.add("\n");
				System.out.println("failed: " + idString);
			}
			
			numComplete++;
			progress = (float)numComplete / (float)numKeys;
			System.out.println("Progress: " + progress);
		}
		
		float accuracy = (float)numCorrect / (float)numComplete;
		System.out.println("Accuracy: " + accuracy);
		
		HashMap<Integer, Float> groupAccuracy = new HashMap<Integer, Float>();
		for (Integer n : numTrials.keySet()) {
			if (numSuccesses.containsKey(n)) {
				accuracy = (float)numSuccesses.get(n) / (float)numTrials.get(n);
			} else {
				accuracy = 0;
			}
			
			groupAccuracy.put(n, accuracy);
		}
		
		PrintWriter writer = new PrintWriter("results.txt");
		
		writer.println("======== PERFORMANCE REPORT ========");
		writer.println("Total trials: " + numComplete);
		writer.println("Successes: " + numCorrect);
		writer.println("Failures: " + (numComplete - numCorrect));
		writer.println("Accuracy: " + accuracy);
		writer.println();
		
		for (Integer n : groupAccuracy.keySet()) {
			writer.println("# crossings: " + n);
			writer.println("Successes: " + numSuccesses.get(n));
			writer.println("Trials: " + numTrials.get(n));
			writer.println("Accuracy: " + groupAccuracy.get(n));
		}
		writer.println();
		
		writer.println("======== FAILURES ========");
		for (int i = 0; i < failureReport.size(); i++) {
			writer.println(failureReport.get(i));
		}
		
		writer.println("======== SUCCESSES ========");
		for (int i = 0; i < successReport.size(); i++) {
			writer.println(successReport.get(i));
		}
		
		writer.println("======== ERRORS ========");	
		writer.println("Num errors: " + numErrors);
		
		for (int i = 0; i < errorReport.size(); i++) {
			writer.println(errorReport.get(i));
		}
		
		writer.close();
	}
}
