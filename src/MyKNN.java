import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class MyKNN {

	public static void main(String[] args) throws IOException {
		File testArticle = new File("./data/C1/article02.txt");
		DistanceFunction myCosine = new DistanceCosine();
		MyKNN kSim = new MyKNN(testArticle, 3, myCosine);
	}

	
	public MyKNN(File file, int topK, DistanceFunction distf) throws IOException {
		//String vectorKey = String.valueOf(filenameToInt(file)-1);
		int n = 3; // Number of articles to evaluate
		TFIDF myTFIDF = new TFIDF(n); // Create TFIDF matrix of N articles
		myTFIDF.addTFIDF();
		myTFIDF.printTFIDF();
		
		/*
		
		ArrayList<Double> vecA = myTFIDF.columnsMap.get(vectorKey);
		
		//System.out.println(vecA);
		ArrayList<Double> cosineValues = new ArrayList<Double>();
		
		for(String key : myTFIDF.columnsMap.keySet()) {
			ArrayList<Double> vecX = myTFIDF.columnsMap.get(key);
			cosineValues.add(distf.calculateDistance(vecA, vecX));
		}
		
		ArrayList<Double> sortedCosine = (ArrayList<Double>) cosineValues.clone();
		Collections.sort(sortedCosine, Collections.reverseOrder());
		
		System.out.println("Original cosine vals " + cosineValues);
		System.out.println("Sorted cosine vals " + sortedCosine);
		ArrayList<Double> topKCosines = new ArrayList<Double>();
		int[] topKIndex = new int[topK];
		
		topKCosines = new ArrayList<Double>(sortedCosine.subList(1,topK+1));
		ArrayList<String>topKArticles = new ArrayList<String>();
		
		for(int i = 0; i < topK; i++) {
			topKIndex[i] = cosineValues.indexOf(topKCosines.get(i));
			topKArticles.add(intToFilename(topKIndex[i] + 1));
			System.out.printf("Index: %d %s\n", topKIndex[i], topKArticles.get(i));
		}
		
		System.out.println(topKCosines); */
		/*for(int i = 0; i < topK; i++) {
			System.out.println(intToFilename(topKIndex[i]));
		}*/
		
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
