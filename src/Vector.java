
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Vector {

	ArrayList<Double> values;
	String articleName;
	int articleTotalNumber;
	int articleChapter;
	String articleTopic;
	double distanceFromInputVector;


	
	public static void main(String[] args) {
		/*
		System.out.println("vec");
		ArrayList<Double> arrList = new ArrayList<>();
		System.out.println(intToFilename(121));
		*/
		int c;
		c = Integer.parseInt("c15 article54".replaceAll("article[0-9][0-9]?","").replaceAll("[^0-9]", ""));
		//System.out.println("c15 article54".replaceAll("article[0-9][0-9]?","").replaceAll("[^0-9]", ""));
		System.out.println(c);
		String[] topics = new String[]{"Airline Safety", "Amphetamine", "China and Spy Plan and Captives","Hoof and Mouth Disease",
				"Iran Nuclear", "Korea and Nuclear Capability", "Mortgage Rates", "Ocean and Pollution", "Satanic Cult",
				"Store Irene", "Volcano", "Saddam Hussein", "Kim Jong-un", "Predictive Analytics", "Irma & Harvey"};
		System.out.println(topics.length);
		
	}
	
	public Vector() {
		values = new ArrayList<Double>();
	}
	public Vector(int size) {
		values = new ArrayList<Double>(Collections.nCopies(size, 0.0));
	}
	
	public Vector(ArrayList<Double> al) {
		values = al;
	}
	
	public Vector(int num, ArrayList<Double> al) {
		values = al;
		this.setAllNames(num);
		
	}

	public void setAllNames(int num) {
		int[] aggr = {0, 8, 16, 20, 28, 41, 46, 54, 64, 68, 86, 94 ,104, 111, 116, 122};
		int c=0;
		int a=0;
		for(int i = 0; i < aggr.length; i++ ) {
			if(aggr[i] >= num) {
				c = i;
				a = num - aggr[i-1];
				articleTotalNumber = num;
				articleChapter = c;
				articleName = String.format("c%d article%d", c,a);
				articleTopic = this.setTopic();
				break;
			} else {
				articleTotalNumber = 0;
				articleChapter = 0;
				articleName = "INPUT FILE";
				articleTopic = "TOPIC: INPUT FILE";
			}
		}
	}
	
	
	public String setTopic() {
		String topic;
		int c;
		String[] topics = new String[]{"Airline Safety", "Amphetamine", "China and Spy Plan and Captives","Hoof and Mouth Disease",
				"Iran Nuclear", "Korea and Nuclear Capability", "Mortgage Rates", "Ocean and Pollution", "Satanic Cult",
				"Store Irene", "Volcano", "Saddam Hussein", "Kim Jong-un", "Predictive Analytics", "Irma & Harvey"};
		// Only assigns a topic if it isn't the input article
		if(articleTotalNumber < 123) {
			c = Integer.parseInt(articleName.replaceAll("article[0-9][0-9]?","").replaceAll("[^0-9]", ""));
			topic = topics[c-1];
		} else {
			topic = "INPUT ARTICLE";
		}
		return topic;
	}

	public void setValue(int i, Double number) {
		values.set(i, number);
	}

	public void setArticleProperties(int i) {
		articleTotalNumber = i;
		setAllNames(i);
		//articleName = intToFilename(i);
	}

	public int getSize() {
		return values.size();
	}

	public double getValue(int i) {
		return values.get(i);
	}

	public String getArticleName() {
		return articleName;
	}

	public boolean isEmpty() {
		return values.isEmpty();
	}

	public void addAll(Vector vector) {
		this.addAll(vector);
	}
	
	public void setDistanceFromInputVector(double dist) {
		this.distanceFromInputVector = dist;
	}
	
	public void printVectorProperties() {
		System.out.printf("\n%10s: \n\t%f\n", this.getArticleName(), this.distanceFromInputVector);
	}



}
