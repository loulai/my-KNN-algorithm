import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class takes the TFIDF CSV file as input and generates a 2D ArrayList as output.
 * The output is then fed into Dataset.java.
 * @see Dataset.java
 */
public class CSVToVectors {
	
	public static void main(String[] args) {
		
		try {
			File inputFile = new File("./tfidfMatrixLong.csv");
			CSVToVectors testReader = new CSVToVectors();
			ArrayList<Vector> testVectors = testReader.generateVectors(inputFile);
		} catch (IOException e) { e.printStackTrace();}
		
	}
	
	int numArticles; // Number of articles equals vectors 
	int dimensions;  // Dimensions equals the number of terms (e.g. 1499)
	//ArrayList<Vector> vectors;
	ArrayList<Vector> vectors;

	public CSVToVectors(){
	}
	
	/**
	 * 	Generates vectors as 2D ArrayLists
	 */
	public CSVToVectors(File file){
		try { generateVectors(file); } catch (IOException e) {}
	}
	
	
	public ArrayList<Vector> generateVectors(File file) throws IOException {
		
		// **temp** 14, in testing should be all 122 articles //3 , 8
		int numArticles = 123;

		// Input type is a CSV
		File csvFile = file;
		
		// Initializing buffered reader
		BufferedReader br = new BufferedReader(new FileReader(csvFile));
		
		// Each vector is an arrayList<Double>
		// The return value is an arrayList of vectors, hence ArrayList<Vector>
		vectors = new ArrayList<Vector>(numArticles);
		
		// Each line is one row being read by bufferedReader
		String line; 
		
		// Read the header row, which has the article 'names'
		String[] headerLine = br.readLine().split(",");
		//System.out.println(headerLine.split(",")[1]);
		
		// Initial rowCounter begins at 1 (not 0) to omit the header row we just read
		int rowCounter = 1;
		
		// Loops through each term (i.e. each word, which is also each dimension)
		while ((line = br.readLine()) != null) {
			String[] oneLine = line.split(",");
			
			int numColumns = oneLine.length; //15 (14 articles, 1 term column) ** temp **
			
			if(rowCounter == 1) {
				//initializing *14* vectors
				for(int i = 0; i < numArticles; i++) {   // 2020 dimensions for 20 articles
					Vector newVector = new Vector(9228); // ****** temp ***** change 1499 to the total number of terms == dimensions of each vector
					newVector.setArticleProperties(i+1); // needs plus 1 because naming begins from 1, not 0
					vectors.add(newVector);
				}
			}
			
			// Loop through each article, assign value
			// Begins at 1 to omit term column
			for (int c = 1; c < numArticles + 1; c++) { 
				Double value = Double.parseDouble(oneLine[c]);
				Vector currentVector = vectors.get(c-1);
				currentVector.setValue(rowCounter-1, value);
				//System.out.println("currentVector " + currentVector.articleName);
				//System.out.println(currentVector.values);
			}
			
			// At the end, this number is the total number of rows in the tfidf matrix
			rowCounter++; 
			
			//Vector currentVector = vectors.get(2);
			//System.out.println(currentVector.articleName + ": " + currentVector.values );
	    }
		
		// Account for counting the header
		dimensions = rowCounter - 1;
		
		// For debugging: prints out articles as vectors
		/*
		for(int i = 0; i < numArticles; i++){ // End: should iterate 122 times for 122 articles
			// Get each vector
			Vector vector = vectors.get(i); //not gonna be get i
			System.out.println("--------------------- Vector or Article " + (i+1) + " ---------------------");
			System.out.println("Article Name: " + vector.getArticleName());
			System.out.println("Dimensions  : " + vector.getSize());
			
			// print the first 5 TFIDF values. To print all, change to: for(int k = 0; k < vector.getSize(); k++) {
			for(int k = 0; k < 5; k++) {
				System.out.printf("Vec #%2d  Row #%4d  TFIDF: %f\n", i+1, k+1, vector.getValue(k));
			}
		}
		*/
		return vectors;
	}
			
	}

 