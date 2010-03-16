package com.xmos.xta.core.engine.analysis.routes;

import java.util.LinkedList;
import java.util.List;

import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.types.ReferenceType;

public class ReferenceMatcher {
	void matchLabel(Reference reference, long pc, List<ReferenceType> refList){
		List<String> backTrailForPc = new LinkedList<String>(reference.getPcBackTrail(pc));
		for(int i=reference.getBackTrail().size()-1; i>=0; i--){
			ReferenceType refType = reference.getBackTrail().get(i);
			int index = getIndexOf(backTrailForPc, refType.getRef());
			if(index != -1){
				backTrailForPc = backTrailForPc.subList(0, index);
			} else {
				refList.add(0, refType);
			}
		}
	}

	private int getIndexOf(List<String> backTrailForPc, String string){
		int i=0;
		for(String backTrail : backTrailForPc){
			int rightBra = backTrail.indexOf(")");
			int leftBra = backTrail.indexOf("(");

			if(rightBra != -1 && leftBra != -1){
				String bracketedString = backTrail.substring(leftBra+1, rightBra);
				String [] parts = bracketedString.split(",");
				for(String part : parts){
					if(part.equals(string)){
						return i;
					}
				}
			}

			if(backTrail.equals(string)){
				return i;
			}

			i++;
		}


		return -1;
	}
}
