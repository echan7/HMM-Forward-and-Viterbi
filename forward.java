import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class forward {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HashMap<Integer, emissionState> statesMap = new HashMap<Integer, emissionState>();
		int statesNum = Integer.parseInt(args[0].trim());
		File transitionsFile = new File(args[1].trim());
		File emissionsFile = new File(args[2].trim());
		String seq = args[3].trim();
		
		try(Scanner scnr = new Scanner(emissionsFile)){
			while(scnr.hasNextLine()) {
				String[] str = scnr.nextLine().split(" ");
				String[] str2 = scnr.nextLine().split(" ");
				String[] str3 = scnr.nextLine().split(" ");
				String[] str4 = scnr.nextLine().split(" ");
				int stateNum = Integer.parseInt(str[0]);
				double[] emmProbs = {Double.parseDouble(str[2]), Double.parseDouble(str2[2]), Double.parseDouble(str3[2]), Double.parseDouble(str4[2])}; 
				statesMap.put(stateNum, new emissionState(emmProbs, false));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try(Scanner scnr = new Scanner(transitionsFile)){
			while(scnr.hasNextLine()) {
				String[] str = scnr.nextLine().split(" ");
				int prev = Integer.parseInt(str[0]);
				int post = Integer.parseInt(str[1]);
				double prob = Double.parseDouble(str[2]);
				
				if(!statesMap.containsKey(prev)) {
					emissionState emm = new emissionState(null, true);
					statesMap.put(prev, emm);
				}
				
				if(!statesMap.containsKey(post)) {
					emissionState emm = new emissionState(null, true);
					statesMap.put(post, emm);
				}
				
				emissionState prevState = statesMap.get(prev);
				prevState.transMap.put(post, prob);	
				emissionState postState = statesMap.get(post);
				postState.parents.add(prev);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		Double[][] forwardProb = new Double[statesNum + 2][seq.length()+1];
		forwardProb[0][0] = 1.0;
		for(int i =1; i < forwardProb[0].length; i ++) {
			forwardProb[0][i] = 0.0;
		}
		
		for(int i=1; i < forwardProb.length; i ++) {
			forwardProb[i][0] = 0.0;
		}
		
		for(int t = 1; t < forwardProb[0].length; t ++ ) {
			for(int i = 1; i < forwardProb.length-1; i ++) {
				emissionState stateI = statesMap.get(i);
				double emmIT = stateI.emmMap.get(seq.charAt(t-1));
				double sum = 0.0;
				for(int j =0; j< stateI.parents.size(); j++) {
					 sum += forwardProb[stateI.parents.get(j)][t-1] * statesMap.get(stateI.parents.get(j)).transMap.get(i);
				}
				forwardProb[i][t] = emmIT * sum;
			}
		}
		
		double sum = 0.0;
		emissionState endState = statesMap.get(forwardProb.length-1);
		for(int i =0; i< endState.parents.size(); i ++) {
			sum += forwardProb[endState.parents.get(i)][forwardProb[forwardProb.length-1].length-1] * statesMap.get(endState.parents.get(i)).transMap.get(forwardProb.length-1);
		}
		forwardProb[forwardProb.length-1][forwardProb[forwardProb.length-1].length-1] = sum;
		
		for(int i = 1; i < forwardProb[0].length; i ++) {
			for(int j = 1; j< forwardProb.length-1; j++) {
				if(forwardProb[j][i] != 0.0) { 
					System.out.printf("%.2f", Math.log(forwardProb[j][i]));
				} else {
					System.out.printf("%.2f", -1000000.00);
				}
				System.out.print(" ");
			}
			System.out.println();
		}
		System.out.printf("%.2f", Math.log(forwardProb[forwardProb.length-1][forwardProb[forwardProb.length-1].length-1]));
	}

}

