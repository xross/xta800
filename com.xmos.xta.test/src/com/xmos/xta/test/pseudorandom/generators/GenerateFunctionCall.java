package com.xmos.xta.test.pseudorandom.generators;

import java.util.Set;
import java.util.Stack;

import com.xmos.xta.test.pseudorandom.Generator;
import com.xmos.xta.test.pseudorandom.PseudoRandom;
import com.xmos.xta.test.pseudorandom.ReferenceTree;
import com.xmos.xta.test.pseudorandom.XtaPragma;

public class GenerateFunctionCall extends Generator {

	public void generate(StringBuffer file, PseudoRandom pseudoRandom, int indent, 
			Set<Stack<String>> functionStacks, String functionName, ReferenceTree<XtaPragma> references) {
		
		if(longestStack(functionStacks) < pseudoRandom.getMaxFunctionDepth()){
			for(Stack<String> stack : functionStacks){
				stack.push(functionName);
			}

			String newFunction = pseudoRandom.getNewFunctionName(functionStacks);
			if(newFunction != null){
				ReferenceTree<XtaPragma> ref = addLabelPragma(file, pseudoRandom, indent, PragmaType.Call, references, newFunction);
				
				pseudoRandom.addEndRef(ref);
				pseudoRandom.addCallToFunction(ref, newFunction);
				addIndent(file, indent);
				file.append("x = "+ newFunction + "();\n");
			} 
			for(Stack<String> stack : functionStacks){
				stack.pop();
			}
		}
	}
	
	private int longestStack(Set<Stack<String>> functionStacks){
		int longest = 0;
		for(Stack<String> stack : functionStacks){
			longest = Math.max(longest, stack.size());
		}
		return longest;
	}
}
