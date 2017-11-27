import java.util.ArrayList;
import java.util.Arrays;


public class DistanceEuclidian extends DistanceFunction {
	
	static String NAME = "euclidian";
	
	@Override
	public String getName() {
		return NAME;
	}
	
	public double calculateDistance(Vector vectorA, Vector vectorB) {
		double sum =0;	
		for(int i=0; i< vectorA.getSize(); i++){
			sum += Math.pow(vectorA.getValue(i) - vectorB.getValue(i), 2);
		}
		return Math.sqrt(sum);
	}
	
	public static void main(String[] args) {
		ArrayList<Double> arrayList1 = new ArrayList<Double>(Arrays.asList(3.0, 2.0, 0.0, 5.0, 0.0, 0.0, 0.0, 2.0, 0.0, 0.0));
		ArrayList<Double> arrayList2 = new ArrayList<Double>(Arrays.asList(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 2.0));
		Vector array1 = new Vector(arrayList1);
		Vector array2 = new Vector(arrayList2);
		System.out.println(new DistanceEuclidian().calculateDistance(array1,array2));
		// 6.16
		
		ArrayList<Double> arrayList3 = new ArrayList<Double>(Arrays.asList(0.0, 0.0));
		ArrayList<Double> arrayList4 = new ArrayList<Double>(Arrays.asList(0.0, 0.0));
		Vector array3 = new Vector(arrayList3);
		Vector array4 = new Vector(arrayList4);
		System.out.println(new DistanceEuclidian().calculateDistance(array3,array4));
		// 0
	}
	
	
	

}