import java.util.Comparator;

public class VectorDistanceComparer implements Comparator<Vector> {
	  @Override
		public int compare(Vector x, Vector y) {
		    int startComparison = compare(x.distanceFromInputVector, y.distanceFromInputVector);
		    return startComparison != 0 ? startComparison
		                                : compare(x.distanceFromInputVector, y.distanceFromInputVector);
		  }
		
		private static int compare(double a, double b) {
		    return a > b ? -1
		         : a < b ? 1
		         : 0;
		  }
	}