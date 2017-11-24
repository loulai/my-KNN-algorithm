import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Corpus {
	
	public Map<String, ArrayList<Vector>> columnsMap;

	public static void main(String[] args) throws IOException {
		try {
		File inputFile = new File("./data/testData/1/b1.txt");
		// Initializing buffered reader
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		int counter = 0;
		String line;
		while((line = br.readLine()) != null) {
			System.out.println(line);
			counter++;
		}
		System.out.println(counter);
		} catch (FileNotFoundException e) {e.printStackTrace();}
	}
	
	/*
	public static void generateTFIDF(File inputFile, File[] folders) {
		for(int i = 0; i < nCol; i++) {
			for(int k=0; k < nRow; k++) {
				String currentWord = uniqueTerms.get(k);
				ArrayList<String> targetFile = convertFileToArrayList(evaluatedArticles[i]);
				double tfidf = getTFIDF(currentWord, targetFile, evaluatedArticles);
				
				//update
				columnsMap.get(String.valueOf(i)).set(k,  tfidf);
			}
		}
		return this;
	}
	*/
	
	/**
	 * To find the TFIDF value for a given document and a given term in the whole set of documents
	 * @param document The Text record in the input file.
	 * @param term The term in the allWords
	 * @param records The whole record collection of the input file.
	 * @return tfidf value of the given document and the term
	 */
	private  double FindTFIDF(String document, String term, ArrayList<Record> records)
	{
	   	double tf = this.FindTermFrequency(document, term);
	    float idf = this.FindInverseDocumentFrequency(term,records);
	    return tf * idf;
	}
	/**
	 * To find the  no. of document that contains the term in whole document collection
	 * i.e.; log of the ratio of  total no of document in the collection to the no. of document containing the term
	 * we can also use Math.Log(occurance/(1+documentCollection.size)) to deal with divide by zero case; 
	 *@param term The term in the allWords
	 *@param records The whole record collection of the input file.
	 *@return the inverse document frequency
	 */

	private  float FindInverseDocumentFrequency(String term,
		ArrayList<Record> records) {
		   int occurance=0;
		   for(Record record:records)
		   {
			   if(record.getAttribute().contains(term))
			   {
				   occurance++;
			   }
		   }
		    return (float)Math.log((float)occurance / (1+(float)records.size()));
		}

	/**
	 * To find the ratio of no of occurance of term t in document d to the total no of terms in the document
	 *@param document The Text record in the input file
	 *@param term The term in the allWords
	 *@return the term frequency of term in the document
	 */
	private double FindTermFrequency(String document, String term) {
		   int occurance=0;
		   String[] words=document.split(" ");
		   for(String word:words)
		   {
			   if(word.equalsIgnoreCase(term))
			   {
				   occurance++;
			   }
		   }
		   return (double)((float)occurance / (float)(words.length));
	}

}
