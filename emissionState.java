import java.util.ArrayList;
import java.util.HashMap;

public class emissionState {
	public HashMap<Integer, Double> transMap;
	public HashMap<Character, Double> emmMap;
	public ArrayList<Integer> parents;
	public boolean silent;
	
	public emissionState(double[] emms, boolean silent){
		if(silent == false){
			emmMap = new HashMap<Character, Double>();
			emmMap.put('A', emms[0]);
			emmMap.put('C', emms[1]);
			emmMap.put('G', emms[2]);
			emmMap.put('T', emms[3]);
		} 
		this.silent = silent;
		transMap = new HashMap<Integer, Double>();
		parents = new ArrayList<Integer>();
	}
}
