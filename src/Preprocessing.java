import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Scanner;

public class Preprocessing {

	public static void main(String[] args) throws IOException {
		// Store files
		File testFile = new File("./data/corpus/C1/article01.txt");
		File stopwordsFile = new File("./data/corpus/stopwords.txt");
		
		// Extract stopwords
		ArrayList<String> stopwordsArray = generateStopwords(stopwordsFile);
				
		ArrayList<String> test = convertFileToArrayList(testFile, stopwordsArray);
	}
	
	/** Take a file and stopwords return one cleaned ArrayList of words per file **/
	public static ArrayList<String> convertFileToArrayList(File file, ArrayList<String> stopwordsArray) throws IOException  { 
        /** file **/
		ArrayList<String> finalArrayList = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String currentLine;
		
		// Examine each word, clean, and add to return ArrayList
		while((currentLine = br.readLine()) != null) {
			// Remove punctuation
			currentLine = currentLine.replaceAll("[[0-9]-!~,.():\\\"\\'\\]\\[]+", ""); 
			
			String[] currentWords = currentLine.split(" ");
			
			// Add each word from each line to the final ArrayList
			for(String word:currentWords) {
				word = word.toLowerCase();
				if(!stopwordsArray.contains(word) && !word.isEmpty()) {
					finalArrayList.add(word);
				}
			}
		}
		//System.out.println(finalArrayList);
		return finalArrayList;
	}
	
	// Take a stopwords file and return stopwords in an ArrayList format
	public static ArrayList<String> generateStopwords(File stopwordsFile) throws IOException {
		ArrayList<String> stopwordsArray = new ArrayList<String>();
    	BufferedReader br = new BufferedReader(new FileReader(stopwordsFile));
        String currentStopword;
    	while((currentStopword = br.readLine()) != null) {
        	stopwordsArray.add(currentStopword);
        }
        return stopwordsArray;
	}
	
	public static ArrayList<String> toUnique(ArrayList<String> evaluatedArticles){
		ArrayList<String> uniqueArrayList;
		uniqueArrayList = new ArrayList<String>(new LinkedHashSet<String>(evaluatedArticles)); //unique tokens 
		return uniqueArrayList;
	}

}
