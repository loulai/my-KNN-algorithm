import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MyKNN  {

	public static void main(String[] args) throws IOException {
		
		// Loading variables
		/*
		File inputFile = new File("./data/testData/1/b1.txt");
		DistanceFunction myCosine = new DistanceCosine();
		int numArticlesToEvaluate = 122;

		// Creating new TFIDF
		long startTime = System.nanoTime();
		System.out.printf(">>>>>>>>>>> TFIDF calculation began for %d articles\n" , numArticlesToEvaluate);
		TFIDF myTFIDF = new TFIDF(numArticlesToEvaluate, inputFile); 
		myTFIDF.addTFIDF();
		myTFIDF.printToCSV("tfidfMatrixWithInput.csv");
		long endTime = System.nanoTime();
		long duration = (endTime - startTime); 
		System.out.println("========= TFIDF complete\n========= Duration (secs): " + (duration/1000000000));
		
		*/
		
		// Generate variables to go in to KNN: input file, TFIDF, distance function and K
		File inputFile = new File("./data/testData/1/b1.txt");
		/*
		TFIDF myTFIDF = new TFIDF(122, inputFile); // Create TFIDF matrix of 122 articles + new input
		myTFIDF.addTFIDF();
		myTFIDF.printToCSV("./tfidfMatrixWithInput.csv");
		*/
		
		// Run algorithm
		//MyKNN myKNN = new MyKNN(inputFile, new DistanceCosine(), 4);
	
		// Validation algorithm
		int bestK = myKNNValidation(new DistanceCosine());
		System.out.println(">>> BEST K " + bestK);
	}

	public MyKNN(File inputFile, DistanceFunction distf, int topK) throws IOException {
		// Import the TFIDF Matrix created in TFIDF.java and vectorize
		CSVToVectors myTFIDF = new CSVToVectors(new File("./tfidfMatrixWithInput.csv"), 123);
		ArrayList<Integer> kChapters = new ArrayList<Integer>(topK);
		
		// Create a store for cosine distance values
		ArrayList<Vector> orderedVectors = new ArrayList<Vector>();
		Vector vecInput = myTFIDF.vectors.get(122); // the last one is the new input
		myTFIDF.vectors.remove(122);
		
		// Manually set correct topic for input vector, used for evaluation later
		String inputChapter = inputFile.getName().replaceAll("[0-9][0-9]?.txt", "");
		if(inputChapter.equals("a")) {
			vecInput.articleChapter = 14;
			vecInput.articleTopic = "Predictive Analytics";
		} else if (inputChapter.equals("b")){
			vecInput.articleChapter = 15;
			vecInput.articleTopic = "Irma & Harvey";
		} else {
			System.out.println("Cannot accurately count precision / recall");
		}
		
		// Calculate cosine distance between the input file (vecA) and all other articles 
		for(Vector currentVector : myTFIDF.vectors) {
			currentVector.setDistanceFromInputVector(distf.calculateDistance(vecInput, currentVector));
			orderedVectors.add(currentVector);	
			//currentVector.printVectorProperties();
		}
		
		// Sort the generated cosine values
		Collections.sort(orderedVectors, new VectorDistanceComparer());
		
		// Take the top K closest articles
		ArrayList<Vector> topKCosines = new ArrayList<Vector>();
		topKCosines = new ArrayList<Vector>(orderedVectors.subList(0,topK));
		
		/* Classify according to most frequent article */
		
		// Store all chapters occuring in K closest vectors
		for(int i = 0; i < topKCosines.size(); i++) { kChapters.add(topKCosines.get(i).articleChapter);}
		
		// Initialize HashMap used to keep count of how many times a chapter occurs 
		HashMap<Integer, Integer> hmap = new HashMap<Integer, Integer>();
		
		// Take only unique chapter names and initialize count to zero
		HashSet<Integer> uniqueChapters = new HashSet<>(kChapters);
		for (Integer value : uniqueChapters) {hmap.put(value, 0);}
	
		// Count how many times a chapter occurs in K closest vectors
		for(int i = 0; i < topK; i++) {
			Integer currentChapter = topKCosines.get(i).articleChapter;
			hmap.put(currentChapter, hmap.get(currentChapter) + 1);
		}
		
		// Take the max chapter occured
		Integer topChapter = 0;
		Integer topCount = -1;
		for (Integer currentChapter : hmap.keySet()){
			Integer currentCount = hmap.get(currentChapter);
			if(currentCount > topCount) {
				topCount = currentCount;
				topChapter = currentChapter;
				//System.out.println("*** Chapter : " + topChapter + " Count : " + topCount);
			} else if (currentCount == topCount){
				// If it's a tie, do nothing, default to existing top chapter
				System.out.println("**** TIE! ****");
			}
		}
		
		// Print K-closest articles
		System.out.printf("========== My KNN ==========\nThe %d closest articles are:\n", topK);
		for(int i = 0; i < topK; i++) {
			Vector currentVector = topKCosines.get(i);
			System.out.printf("%d) %f  %-14s \n", i+1, currentVector.distanceFromInputVector, currentVector.articleName);
		}
		
		// Print chapter the article is classified into
		System.out.printf("----------------------------\n");
		hmap.forEach((chapter,count)-> System.out.println("Chapter " + chapter + " occurs " + count + " times."));
		System.out.printf("\nPredicted: Chapter %d\n", topChapter);
		System.out.printf("Actual   : Chapter %d\n", vecInput.articleChapter);
		System.out.printf("============================\n");
	}
	
	public static int myKNNValidation(DistanceFunction distf) {
		ArrayList<ArrayList<Double>> resultsMatrix = new ArrayList<ArrayList<Double>>();
		int kRange = 10;
		double avgPrecision = 0.0;
		ArrayList<Double> allPrecisions = new ArrayList<Double>();
 		
		// Test for a range of Ks
		for(int r = 0; r < kRange; r++) {
			CSVToVectors myTFIDF = new CSVToVectors(new File("./tfidfMatrixLong.csv"), 122);
			int folds = 10;
			ArrayList<Vector> previousTestVectors = new ArrayList<Vector>();
			ArrayList<Double> foldResults = new ArrayList<Double>();
			
			// Begin 10-fold cross-validation
			for(int z = 0; z < folds; z++) {
				
				// Split dataset into 10/90 test/train
				ArrayList<Vector> testVectors = new ArrayList<Vector>();
				
				for (int i = 0; i < 12; i++) {
					// Generate random number (accounting for size change)
					Integer rand = new Random().nextInt(myTFIDF.vectors.size() - i); 
					
					// Store randomly selected vector
					testVectors.add(myTFIDF.vectors.get(rand));
					
					// Remove randomly selected vector from TFIDF
					myTFIDF.vectors.remove((int)rand);
				}
				
				// Add back the previous test vectors (if not the first iteration)
				if(z != 0) {
					myTFIDF.vectors.addAll(previousTestVectors);
				}
				
				// (1) Calculate cosine distance between the test vector and all other articles
				int numCorrect = 0;
				int numWrong = 0;
				for(int i = 0; i < testVectors.size(); i++) {
					
					// Get current vector
					Vector currentVector = testVectors.get(i);
					ArrayList<Vector> orderedVectors = new ArrayList<Vector>();
					 
					for(Vector otherVector : myTFIDF.vectors) {
						// Calculate distance between input vector and all other vectors
						otherVector.setDistanceFromInputVector(distf.calculateDistance(currentVector, otherVector));
						// Add to 'orderedVectors', which is sorted later
						orderedVectors.add(otherVector);	
					}
					
					// (2) Get top K closest vectors
					int topK = kRange;
					Collections.sort(orderedVectors, new VectorDistanceComparer()); // sort
					ArrayList<Vector> topKCosines = new ArrayList<Vector>(); 
					topKCosines = new ArrayList<Vector>(orderedVectors.subList(0, topK)); 
					
					// (3) Classify
					ArrayList<Integer> kChapters = new ArrayList<Integer>();
					for(int m = 0; m < topKCosines.size(); m++) { kChapters.add(topKCosines.get(m).articleChapter);} // Store all chapters occuring in K closest vectors
					HashMap<Integer, Integer> hmap = new HashMap<Integer, Integer>(); // Initialize HashMap used to keep count of how many times a chapter occurs 
					HashSet<Integer> uniqueChapters = new HashSet<>(kChapters); // Take only unique chapter names and initialize count to zero
					for (Integer value : uniqueChapters) {hmap.put(value, 0);}
					
					//  (3.1) Count how many times a chapter occurs in K closest vectors
					for(int m = 0; m < topK; m++) {
						Integer currentChapter = topKCosines.get(m).articleChapter;
						hmap.put(currentChapter, hmap.get(currentChapter) + 1);
					}

					//  (3.2) Take most frequent chapter occured
					Integer topChapter = 0;
					Integer topCount = -1;
					for (Integer currentChapter : hmap.keySet()){
						Integer currentCount = hmap.get(currentChapter);
						if(currentCount > topCount) {
							topCount = currentCount;
							topChapter = currentChapter;
							//System.out.println("*** Chapter : " + topChapter + " Count : " + topCount);
						} else if (currentCount == topCount){} // If it's a tie, do nothing, default to existing top chapter
					}
					
					/* EVALUATION */
					//System.out.printf("\n[K = %d] --------------\n", topK);
					// Print K-closest articles
					//System.out.println("Vector    : " + currentVector.articleName);
					//System.out.printf("Closest articles:\n", topK);
					for(int m = 0; m < topK; m++) {
						Vector currentVec = topKCosines.get(m);
						//System.out.printf("\t%d) %f  %-14s \n", m+1, currentVec.distanceFromInputVector, currentVec.articleName);
					}
					
					// Print chapter the article is classified into
					//hmap.forEach((chapter,count)-> System.out.println("Ch " + chapter + " x " + count + " times" ));
					//System.out.printf("Predicted : Chapter %d\n", topChapter);
					//System.out.printf("Actual    : Chapter %d", currentVector.articleChapter);
					if(topChapter == currentVector.articleChapter) {
						numCorrect++;
						//System.out.print(" > CORRECT\n");
					} else {
						numWrong++;
						//System.out.println();
					}
				}
				
				// (4) Calculate precision
				double precision = numCorrect/(testVectors.size() + 0.0);
				foldResults.add(precision);
				avgPrecision += precision/folds;
				System.out.printf("\n=============== [fold %d] \n", z+1);
				System.out.printf(" - Correct Predictions    : %d\n", numCorrect);
				System.out.printf(" - Total Predictions made : %d\n", testVectors.size());
				System.out.printf(" - Precision    : %.3f\n===============\n", precision);
				
				// (5) Reset test vectors for next fold
				previousTestVectors.clear();
				previousTestVectors.addAll(testVectors);
			}
			System.out.printf(" - Avg precision: %.3f\n===============\n", avgPrecision);
			resultsMatrix.add(foldResults);
			allPrecisions.add(avgPrecision);
			avgPrecision = 0.0; // Reset
		}
		
		// Print results
		System.out.print("      ");
		System.out.print("  AVG    ");
		for(int l = 0; l < 10; l++) {
			System.out.printf("f%-5d", l+1);
		}
		System.out.println();
		
		for(int m = 0; m < resultsMatrix.size(); m++) {
			System.out.printf("k%-5d", m+1); 
			System.out.printf("|% .3f |", allPrecisions.get(m));
			for(int j = 0; j < resultsMatrix.get(m).size(); j++) {
				System.out.printf("%.3f ", resultsMatrix.get(m).get(j));
			}
			System.out.println();
		}
	
		// (6) Calculate which K gives the highest precision
		double bestKPrecision = Collections.max(allPrecisions);
		int bestK = allPrecisions.indexOf(bestKPrecision) + 1;
		System.out.printf("> Best K   : %d\n> Precision: %.3f\n", bestK, bestKPrecision);
		return bestK;
	}
	
	public int filenameToInt(File file) {
		int c = Integer.valueOf(file.getParentFile().getName().replace("C", "")); //C1
		int a = Integer.valueOf(file.getName().replaceAll("[^0-9]","")); //10
		int position;
		
		int[] aggr = {0, 8, 16, 20, 28, 41, 46, 54, 64, 68, 86, 94 ,104, 111, 116, 122};

		position = aggr[c-1] + a;
		return position;
	}
	
	public static String intToFilename(int num) {
		int[] aggr = {0, 8, 16, 20, 28, 41, 46, 54, 64, 68, 86, 94 ,104, 111, 116, 122};
		int c=0;
		int a=0;
		for(int i = 0; i < aggr.length; i++ ) {
			if(aggr[i] >= num) {
				c = i;
				a = num - aggr[i-1];
				break;
			}
		}
		return String.format("C%d A%d", c,a);
	}
}
