package com.xmos.xta.test.pseudorandom.generators;

import java.util.Random;
import java.util.Set;
import java.util.Stack;

import com.xmos.xta.test.pseudorandom.Generator;
import com.xmos.xta.test.pseudorandom.PseudoRandom;
import com.xmos.xta.test.pseudorandom.ReferenceTree;
import com.xmos.xta.test.pseudorandom.XtaPragma;

public class GenerateIf extends Generator {
	
	public void generate(StringBuffer file, PseudoRandom pseudoRandom, int indent, 
			Set<Stack<String>> functionStacks, String functionName, ReferenceTree<XtaPragma> references) {
		int nextIndent = indent + 1;
		Random r = new Random();
		addIndent(file, indent);
		file.append("if(x==0){\n");
		ReferenceTree<XtaPragma> ifRef =  addLabelPragma(file, pseudoRandom, nextIndent, PragmaType.Label, references, "if");
		makeNext(file, pseudoRandom, functionStacks, functionName, nextIndent, ifRef);
		addIndent(file, indent);
		file.append("}");
		if(r.nextBoolean()){
			file.append(" else {\n");
			ReferenceTree<XtaPragma> elseRef =  addLabelPragma(file, pseudoRandom, nextIndent, PragmaType.Label, references, "else");
			makeNext(file, pseudoRandom, functionStacks, functionName, nextIndent, elseRef);
			addIndent(file, indent);
			file.append("}\n");
		} else {
			file.append("\n");
		}
	}
	
}
