package com.xmos.xta.test.pseudorandom;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.xmos.xta.test.pseudorandom.Generator.PragmaType;

public class CmdMaker {
	private Set<ReferenceTree<XtaPragma>> m_endRefs;
	private Map<String, Set<ReferenceTree<XtaPragma>>> m_functionToCall;
	public CmdMaker(Set<ReferenceTree<XtaPragma>> endRefs, Map<String, Set<ReferenceTree<XtaPragma>>> functionToCall) {
		m_endRefs = endRefs;
		m_functionToCall = functionToCall;
	}

	public void make() {
		try {
			
			BufferedWriter out = new BufferedWriter(new FileWriter("cmds.txt"));
			
			for (ReferenceTree<XtaPragma> ref : m_endRefs) {
				Set<List<XtaPragma>> refs = new HashSet<List<XtaPragma>>();
				List<XtaPragma> currentStack = new LinkedList<XtaPragma>();
				makeRefList(ref, refs, currentStack);
				for (List<XtaPragma> strings : refs) {
					for(int i=0;i<5;i++){
						out.write("-debug verifyreference ");
						
						StringBuffer refString = new StringBuffer();
						
						for (XtaPragma s : strings) {
							Random r = new Random();
							if(s.getType() == PragmaType.Function || strings.indexOf(s) == strings.size()-1){
								refString.append(s.getLabel() + ",");
							} else {
								
								writeOutRef(refString, s);
							}

						}
						refString.setLength(refString.length() - 1);
						refString.append("\n");
						out.write(refString.toString());
					}
				}

			}
			out.write("-echo \"finished\"\n");
			out.write("-exit");
			out.close();
		} catch (IOException e) {
		}
	}

	private void writeOutRef(StringBuffer refString, XtaPragma s) {
		Random r = new Random();
		switch(r.nextInt(6)){
		case 0:
			refString.append(s.getLabel() + ",");
			break;
		case 1:
			refString.append(s.getFileLine() + ",");
			break;
		case 2:
			refString.append("*,");
			break;
		case 3:
			break;
		case 4:
			refString.append(s.getTypeString() + s.getLabel() + ",");
			break;
		case 5:
			refString.append(s.getTypeString() + s.getFileLine() + ",");
			break;
		}
	}

	private void makeRefList(ReferenceTree<XtaPragma> ref, Set<List<XtaPragma>> stringRefs, 
			List<XtaPragma> currentStack) {
		ReferenceTree<XtaPragma> currentRef = ref;

		while (currentRef.getPrev() != null) {
			currentStack.add(0, currentRef.getObject());

			if (m_functionToCall.containsKey(currentRef.getObject())) {

				for (ReferenceTree<XtaPragma> callingRef : m_functionToCall.get(currentRef.getObject())) {

					makeRefList(callingRef, stringRefs, new LinkedList<XtaPragma>(
							currentStack));
				}
				return;

			} else {
				currentRef = currentRef.getPrev();
			}

		}
		stringRefs.add(new LinkedList<XtaPragma>(currentStack));
	}
	
	
}
