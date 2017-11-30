import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
		MyKNN myKNN = new MyKNN(inputFile, new DistanceCosine(), 3);
	}

	
	public MyKNN(File inputFile, DistanceFunction distf, int topK) throws IOException {;
		CSVToVectors myTFIDF = new CSVToVectors(new File("./tfidfMatrixWithInput.csv"));
		
		// A store for cosine distance values
		//ArrayList<Double> cosineValues = new ArrayList<Double>();
		ArrayList<Vector> orderedVectors = new ArrayList<Vector>();
		Vector vecA = myTFIDF.vectors.get(122); // the last one is the new input
		myTFIDF.vectors.remove(122);
		
		// Calculate cosine distance between the input file (vecA) and all other articles 
		for(Vector currentVector : myTFIDF.vectors) {
			currentVector.setDistanceFromInputVector(distf.calculateDistance(vecA, currentVector));
			orderedVectors.add(currentVector);	
			//currentVector.printVectorProperties();
		}
		
		// Sort the generated cosine values
		Collections.sort(orderedVectors, new VectorDistanceComparer());
		
		ArrayList<Vector> topKCosines = new ArrayList<Vector>();
		topKCosines = new ArrayList<Vector>(orderedVectors.subList(0,topK+1));
		
		for(int i = 0; i < topK; i++) {
			Vector currentVector = topKCosines.get(i);
			System.out.printf("%-14s %f\n", currentVector.articleName, currentVector.distanceFromInputVector);
		}
		
		/*8
		ArrayList<Double> sortedCosine = (ArrayList<Double>) orderedVectors
		Collections.sort(orderedVectors, Collections.reverseOrder());
		*/
		//ArrayList<Double> topKCosines = new ArrayList<Double>();
		//topKCosines = new ArrayList<Double>(sortedCosine.subList(0,topK+1));
		//System.out.println(topKCosines);
		//System.out.println(topKCosines.size());
		/*
		ArrayList<String>topKArticles = new ArrayList<String>();
		
		int[] topKIndex = new int[topK];
		for(int i = 0; i < topK; i++) {
			topKIndex[i] = cosineValues.indexOf(topKCosines.get(i));
			topKArticles.add(intToFilename(topKIndex[i] + 1));
			System.out.printf("Index: %d %s\n", topKIndex[i], topKArticles.get(i));
		}
		*/
		//System.out.println(topKCosines);
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
