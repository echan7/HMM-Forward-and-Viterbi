import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class viterbi {

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
		
		Double[][] viterbiProb = new Double[statesNum + 2][seq.length() + 1];
		Integer[][] ptr = new Integer[statesNum + 2][seq.length() + 1];
	//	Integer[] ptrStates = new Integer[seq.length()+1];
		viterbiProb[0][0] = 1.0;
		ptr[0][0] = 0;
		for(int i =1; i < viterbiProb[0].length; i ++) {
			viterbiProb[0][i] = 0.0;
			ptr[0][i] = 0;
		}
		
		for(int i=1; i < viterbiProb.length; i ++) {
			viterbiProb[i][0] = 0.0;
			ptr[i][0] = 0;
		}
		
		for(int t = 1; t < viterbiProb[0].length; t ++ ) {
			for(int i = 1; i < viterbiProb.length-1; i ++) {
				emissionState stateI = statesMap.get(i);
				double emmIT = stateI.emmMap.get(seq.charAt(t-1));
				double max = 0.0;
				int maxState = 0;
				for(int j =0; j< stateI.parents.size(); j++) {
					 double curr = viterbiProb[stateI.parents.get(j)][t-1] * statesMap.get(stateI.parents.get(j)).transMap.get(i);
					 if(curr > max){
						 max = curr;
						 maxState = stateI.parents.get(j);
					 }
				}
				viterbiProb[i][t] = emmIT * max;
				ptr[i][t] = maxState;
			}
		}
		
		double max = 0.0;
		int maxState = 0;
		emissionState endState = statesMap.get(viterbiProb.length-1);
		for(int i =0; i< endState.parents.size(); i ++) {
			double curr = viterbiProb[endState.parents.get(i)][viterbiProb[viterbiProb.length-1].length-1] * statesMap.get(endState.parents.get(i)).transMap.get(viterbiProb.length-1);
			if(curr > max) {
				max = curr;
				maxState = endState.parents.get(i);
			}
		}
		viterbiProb[viterbiProb.length-1][viterbiProb[viterbiProb.length-1].length-1] = max;
		ptr[ptr.length - 1][ptr[0].length -1] = maxState;
		
		int currState = ptr[ptr.length - 1][ptr[0].length -1];
		StringBuilder finalStr = new StringBuilder();
		finalStr.append(currState);
		finalStr.append(" ");
		for(int i = seq.length(); i > 1; i --) {
			finalStr.append(ptr[currState][i]);
			finalStr.append(" ");			
			currState = ptr[currState][i];
			
		}
		System.out.println(finalStr.reverse().toString().trim());
		
		
		
		/*
		
		int i = ptrStates.length -1;
		emissionState currState = statesMap.get(viterbiProb.length-1);
		int currStateID = viterbiProb.length -1;
		while (i > 0) {
			double currMax = viterbiProb[currState.parents.get(0)][i] * statesMap.get(currState.parents.get(0)).transMap.get(currStateID);
			ptrStates[i] = currState.parents.get(0);
			for(int j = 1; j < currState.parents.size(); j ++) {
				double curr = viterbiProb[currState.parents.get(j)][i] * statesMap.get(currState.parents.get(j)).transMap.get(currStateID);
				if(curr > currMax) {
					ptrStates[i] = currState.parents.get(j);
					currMax = curr;
				}
			}
			currState = statesMap.get(ptrStates[i]);
			currStateID = ptrStates[i];
			i--;
		}
		
		for(int c = 1 ; c < ptrStates.length; c ++) {
			System.out.print(ptrStates[c]);
			System.out.print(" ");
		}
		
		*/

	}

}

