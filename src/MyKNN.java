import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
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
		MyKNN myKNN = new MyKNN(inputFile, new DistanceCosine(), 4);
	}

	
	public MyKNN(File inputFile, DistanceFunction distf, int topK) throws IOException {;
		CSVToVectors myTFIDF = new CSVToVectors(new File("./tfidfMatrixWithInput.csv"));
		ArrayList<Integer> kChapters = new ArrayList<Integer>(topK);
		
		// A store for cosine distance values
		ArrayList<Vector> orderedVectors = new ArrayList<Vector>();
		Vector vecInput = myTFIDF.vectors.get(122); // the last one is the new input
		myTFIDF.vectors.remove(122);
		
		// Manually set correct topic for input vector, used for evaluation later
		
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
		
		// Print closest chapter
		System.out.printf("----------------------------\n");
		hmap.forEach((chapter,count)-> System.out.println("Chapter " + chapter + " occurs " + count + " times."));
		System.out.println("Belongs to chapter: " + topChapter);
		System.out.printf("============================\n");
		
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
