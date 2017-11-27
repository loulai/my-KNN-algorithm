import java.util.ArrayList;
import java.util.Arrays;


public class DistanceCosine extends DistanceFunction {
	
	static String NAME = "cosine";
	
	@Override
	public String getName() {
		return NAME;
	}
	
	public double calculateDistance(Vector vectorA, Vector vectorB) {
		double dotProduct = 0.0;
	    double normA = 0.0;
	    double normB = 0.0;
	    
	    for (int i = 0; i < vectorA.getSize(); i++) {
	        dotProduct += vectorA.getValue(i) * vectorB.getValue(i);
	        normA += Math.pow(vectorA.getValue(i), 2);
	        normB += Math.pow(vectorB.getValue(i), 2);
	    }   
	   
	    return (1 - dotProduct / (Math.sqrt(normA) * Math.sqrt(normB)));
	    
	}
	
	public static void main(String[] args) {
		
		// Calculate the Cosine distance between vectors
		
		// Initialize two non-identical vectors
		ArrayList<Double> arrayList1 = new ArrayList<Double>(Arrays.asList(3.0, 2.0, 0.0, 5.0, 0.0, 0.0, 0.0, 2.0, 0.0, 0.0));
		ArrayList<Double> arrayList2 = new ArrayList<Double>(Arrays.asList(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 2.0));
		Vector array1 = new Vector(arrayList1);
		Vector array2 = new Vector(arrayList2);
		System.out.println(new DistanceCosine().calculateDistance(array1,array2));
		// 0.685
		 
		
		// Initilaize two identical vectors
		ArrayList<Double> arrayList3 = new ArrayList<Double>(Arrays.asList(1.0, 0.0));
		ArrayList<Double> arrayList4 = new ArrayList<Double>(Arrays.asList(0.0, 0.0));
		Vector array3 = new Vector(arrayList3);
		Vector array4 = new Vector(arrayList4);
		System.out.println(new DistanceCosine().calculateDistance(array3,array4));
		// NaN
	}
	
	
	
	
	

}