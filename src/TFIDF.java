import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TFIDF extends Preprocessing {
	
	public Map<String, ArrayList<Double>> columnsMap;
	public ArrayList<String> uniqueTerms;
	public int nRow;
	public int nCol;
	public File[][] fileArray; //[c1][a1,a2,a3]  [c2][a1,a2]..
	public static ArrayList<ArrayList<String>> filesAsArrays = new ArrayList<ArrayList<String>>();
	public String[] articleNames;
	static ArrayList<String> stopwords;
	static ArrayList<String> allWords = new ArrayList<String>();
	static ArrayList<String> uniqueWords = new ArrayList<String>();
	
	public static int getTF(String word, ArrayList<String> fileAsArray) throws FileNotFoundException {
		int termFreq = 0;
		for(int i = 0; i < fileAsArray.size(); i++) {
			if(word.equals(fileAsArray.get(i))) {
				termFreq++;
			}
		}
		// return termFreq/fileAsList.length // alternative flavour of IDF
		return termFreq;
	}
	
	public static double getIDF(String word, ArrayList<ArrayList<String>> corpusList) throws IOException {
		int docsContainingWord = getNumDocsContaingWord(word, corpusList);
		double idf = 0;
		if(docsContainingWord != 0) {
			idf = Math.log(corpusList.size() / docsContainingWord);
		} else {
			//this will never happen
			System.out.printf("OOV: No documents contain word: %s\n", word);
			idf=-1;
		}
		return idf;
	}
	
	public static int getNumDocsContaingWord(String word, ArrayList<ArrayList<String>> corpusList) throws IOException {
		int docsContainingWord = 0;

		// Search every doc for the target word
		for(int i = 0; i < corpusList.size(); i++) { 
			ArrayList<String> currentDoc = corpusList.get(i);
			if(currentDoc.contains(word)) {
				docsContainingWord++;
				break; 	// Found, stop searching
			}
		}
		return docsContainingWord;
	}
	
	public static double getTFIDF(String word, ArrayList<String> fileAsArray, ArrayList<ArrayList<String>> filesAsArrays2) throws IOException {
		//ArrayList<ArrayList<String>> corpusList = new ArrayList<ArrayList<String>>();
		int tf = getTF(word, fileAsArray);
		double idf = getIDF(word, filesAsArrays);
		double tfidf = tf * idf;
		return tfidf;
	}	
	
	public TFIDF(int n) throws IOException {
		File[][] fileArray = readFiles(); //[a1,a2,a3], [a1,a2]
		
		// Take a stopwords file and return stopwords in an ArrayList format
		stopwords = generateStopwords(new File("./data/corpus/stopwords.txt")); 
		
		// Flatten fileArray into filesAsArrays
		int count = 0;
		for (int i = 0; i < fileArray.length && count < n; i++) { //outer: c
			for(int k = 0; k < fileArray[i].length && count < n; k++) {//inner: articles
				// Convert each File into an ArrayList, with each element as a word
				ArrayList<String> currentArticleAsArray = convertFileToArrayList(fileArray[i][k], stopwords);
				// Add resultant ArrayList to local variable filesAsArrays
				filesAsArrays.add(currentArticleAsArray);
				// Append all in current article to running list of all words (uniquified later)
				allWords.addAll(currentArticleAsArray);
				count++;
			}
		}
	
		// Remove duplicate words
		uniqueTerms = toUnique(allWords);
		
		Map<String, ArrayList<Double>> columnsMap = new HashMap(); // each doc is a column
		
		for(int i = 0; i < filesAsArrays.size(); i++) { //create doc1:[0, 0, 0, 0] for each doc
			ArrayList<Double> tfidf = new ArrayList<>(Collections.nCopies(uniqueTerms.size(), 0.0)); //initialize all zeros
			columnsMap.put(String.valueOf(i), tfidf);
		}
		
		// Set values
		this.columnsMap = columnsMap;
		nRow = uniqueTerms.size(); // Dimensions e.g. 576 for 4 articles 
		nCol = filesAsArrays.size(); // Number of articles considered e.g. 4
	}

	/**
	 * Different from original constructor because it takes the input file into consideration and calculates TFIDF
	 */
	public TFIDF(int n, File inputFile) throws IOException {
		/** The only difference are the next two lines: */
		File[][] fileArray = readFilesAndAdd(inputFile); //[a1,a2,a3], [a1,a2]
		n = n + 1;
		
		// Take a stopwords file and return stopwords in an ArrayList format
		stopwords = generateStopwords(new File("./data/corpus/stopwords.txt")); 
		
		// Flatten fileArray into filesAsArrays
		int count = 0;
		for (int i = 0; i < fileArray.length && count < n; i++) { //outer: c
			for(int k = 0; k < fileArray[i].length && count < n; k++) {//inner: articles
				// Convert each File into an ArrayList, with each element as a word
				ArrayList<String> currentArticleAsArray = convertFileToArrayList(fileArray[i][k], stopwords);
				// Add resultant ArrayList to local variable filesAsArrays
				filesAsArrays.add(currentArticleAsArray);
				// Append all in current article to running list of all words (uniquified later)
				allWords.addAll(currentArticleAsArray);
				count++;
			}
		}
	
		// Remove duplicate words
		uniqueTerms = toUnique(allWords);
		
		Map<String, ArrayList<Double>> columnsMap = new HashMap(); // each doc is a column
		
		for(int i = 0; i < filesAsArrays.size(); i++) { //create doc1:[0, 0, 0, 0] for each doc
			ArrayList<Double> tfidf = new ArrayList<>(Collections.nCopies(uniqueTerms.size(), 0.0)); //initialize all zeros
			columnsMap.put(String.valueOf(i), tfidf);
		}
		
		// Set values
		this.columnsMap = columnsMap;
		nRow = uniqueTerms.size(); // Dimensions e.g. 576 for 4 articles 
		nCol = filesAsArrays.size(); // Number of articles considered e.g. 4
	}
	
	public void printTFIDF() {
		// Column header
		System.out.printf("%-14s ", ""); //padding for row
		
		for(int i = 0; i < columnsMap.size(); i++) {
			System.out.printf("%-14s ", i);
		}

		System.out.println();
		
		// Rows
		for(int r = 0; r < nRow; r++) {
			System.out.printf("%-14s ", uniqueTerms.get(r)); //current row (e.g. "the")
			
			for(int c = 0; c < nCol; c++) {
				System.out.printf("%-14f ", columnsMap.get(String.valueOf(c)).get(r));
			}
			
			System.out.println();
		}
	}
	
	public TFIDF addTFIDF() throws IOException {
		for(int i = 0; i < nCol; i++) {
			for(int k=0; k < nRow; k++) {
				String currentWord = uniqueTerms.get(k);
				double tfidf = getTFIDF(currentWord, filesAsArrays.get(i),  filesAsArrays);
				
				// Update
				columnsMap.get(String.valueOf(i)).set(k,  tfidf);
			}
		}
		return this;
	}
	
	public void printToCSV(String fileName) throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(fileName);
		
		// Column header
		for(int i = 0; i < columnsMap.size(); i++) {
			writer.print("," + i);
		}
		writer.println();
	
		// Rows
		for(int r = 0; r < nRow; r++) {
			writer.print(uniqueTerms.get(r) + ","); //each term
		
			for(int c = 0; c < nCol; c++) { //iterates over all columns for that row
				writer.print(columnsMap.get(String.valueOf(c)).get(r) + ","); //TFIDF score
			}
			writer.println();
		}
		writer.close();
	}
	
	/** Read all 15 folders and save as a 2D array of Files */
	public static File[][] readFiles(){
		// Read files
		File[] corpraNames = new File("./data/corpus").listFiles();
		File[][] fileArray = new File[15][];
		
		int count = 0;
		for(File file:corpraNames) {
			
			if(file.isDirectory()) {
				int numArticles = 0;
				
			//System.out.println(file);
			int cIndex = Integer.valueOf(file.getName().replaceAll("[^0-9]","")) - 1; // C1 -> 0
			//System.out.println(cIndex);
			
			for(File subfile:file.listFiles()) {
				numArticles++; //counting
			}
			
			File[] articles = new File[numArticles];
			
			//System.out.println("num articles " + numArticles);
			for(File subfile:file.listFiles()) {
				int articleIndex = Integer.valueOf(subfile.getName().replaceAll("[^0-9]","")) - 1; // Article 2 -> 1
				articles[articleIndex] = subfile;
				count++;
					}
				fileArray[cIndex] = articles;
				}
			}
			
			/*//proves fileArray is sorted
			for(int i = 0; i < fileArray.length; i++) {
				for(int k = 0; k < fileArray[i].length; k++) {
					System.out.println(i + " " + fileArray[i][k]);
				}
			}*/
		return fileArray;
	}
	
	/** Read all 15 folders + new input file and save as a 2D array of Files 
	 * @throws IOException */
	public static File[][] readFilesAndAdd(File newFile) throws IOException{
		// Create a new folder in Corpus for the new input file to live (so it matches with rest of the code)
		new File("./data/corpus/C16").mkdir(); 
		// Create the new File for the inputFile
		File inputFile = new File("./data/corpus/C16/" + newFile.getName());
		// Copy given input File to this new File (this is to line up with rest of coe, not the most efficient)
	    Files.copy(newFile.toPath(), inputFile.toPath(), StandardCopyOption.REPLACE_EXISTING); 
	    
		// Create an array with all the file names, once the new folder has been added
		File[] corpraNames = new File("./data/corpus").listFiles();
		//System.out.println(corpraNames);
		
		File[][] fileArray = new File[16][];
		
		// Loop through each folder. Using its contents, create new Files
		for(File currentFolder:corpraNames) {
			if(currentFolder.isDirectory()) {
				// Get the number of articles in each folder
				int numArticles = currentFolder.listFiles().length;
				// Intialize empty array of Files
				File[] currentArticles = new File[numArticles];
				
				// Get folder position according to its name e.g. C1 -> 0, C14 -> 13
				int cIndex = Integer.valueOf(currentFolder.getName().replaceAll("[^0-9]","")) - 1; 
				
				// Place current files in the correct order
				for(File currentFile:currentFolder.listFiles()) {
					int articleIndex = Integer.valueOf(currentFile.getName().replaceAll("[^0-9]","")) - 1; // Article 2 -> 1
					currentArticles[articleIndex] = currentFile;
				}
				fileArray[cIndex] = currentArticles;
				}
			}
		
			/*
			// For debugging: proves array is sorted
			for(int i = 0; i < fileArray.length; i++) {
				for(int k = 0; k < fileArray[i].length; k++) {
					System.out.println(i + " " + fileArray[i][k]);
				}
			}
			*/
			return fileArray;
	}
	
	public static void main(String[] args) throws IOException {
		
		File inputFile = new File("./data/testData/1/b1.txt");
	
		long startTime = System.nanoTime();
		int numArticlesToEvaluate = 122;
		System.out.printf(">>>>>>>>>>> TFIDF calculation began for %d articles\n" , numArticlesToEvaluate);
		TFIDF myTFIDF = new TFIDF(numArticlesToEvaluate, inputFile); //evaluate n articles
		myTFIDF.addTFIDF();
		//myTFIDF.printTFIDF();
		myTFIDF.printToCSV("tfidfMatrixWithInput.csv");
		long endTime = System.nanoTime();
		long duration = (endTime - startTime); 
		System.out.println("========= TFIDF complete\n========= Duration (secs): " + (duration/1000000000));
		
		/*
		
		readFiles();
		long startTime = System.nanoTime();
		int numArticlesToEvaluate = 12;
		System.out.printf(">>>>>>>>>>> TFIDF calculation began for %d articles\n" , numArticlesToEvaluate);
		TFIDF myTFIDF = new TFIDF(numArticlesToEvaluate); //evaluate n articles
		myTFIDF.addTFIDF();
		//myTFIDF.printTFIDF();
		myTFIDF.printToCSV("tfidfMatrixTest.csv");
		long endTime = System.nanoTime();
		long duration = (endTime - startTime); 
		System.out.println("========= TFIDF complete\n========= Duration (secs): " + (duration/1000000000));
		*/
	}
}

//  4 articles = 0 sec
//  16 articles = 0 sec
//  64 articles = 34 sec
// 122 articles = 156 sec
