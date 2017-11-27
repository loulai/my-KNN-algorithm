import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class MyKNN {

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
		ArrayList<Double> cosineValues = new ArrayList<Double>();
		Vector vecA = myTFIDF.vectors.get(122); // the last one is the new input
		
		// Calculate cosine distance between the input file (vecA) and all other articles
		for(Vector currentVector : myTFIDF.vectors) {
			Vector vecX = currentVector;
			cosineValues.add(distf.calculateDistance(vecA, vecX));
		}
		
		// Sort the generated cosine values
		ArrayList<Double> sortedCosine = (ArrayList<Double>) cosineValues.clone();
		Collections.sort(sortedCosine, Collections.reverseOrder());
		
		ArrayList<Double> topKCosines = new ArrayList<Double>();
		int[] topKIndex = new int[topK];
		
		topKCosines = new ArrayList<Double>(sortedCosine.subList(1,topK+1));
		ArrayList<String>topKArticles = new ArrayList<String>();
		
		for(int i = 0; i < topK; i++) {
			topKIndex[i] = cosineValues.indexOf(topKCosines.get(i));
			topKArticles.add(intToFilename(topKIndex[i] + 1));
			System.out.printf("Index: %d %s\n", topKIndex[i], topKArticles.get(i));
		}
		
		System.out.println(topKCosines);
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
