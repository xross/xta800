package com.xmos.xta.test.pseudorandom.generators;

import java.util.Set;
import java.util.Stack;

import com.xmos.xta.test.pseudorandom.Generator;
import com.xmos.xta.test.pseudorandom.PseudoRandom;
import com.xmos.xta.test.pseudorandom.ReferenceTree;
import com.xmos.xta.test.pseudorandom.XtaPragma;

public class GenerateFunction extends Generator {

	public void generate(StringBuffer file, PseudoRandom pseudoRandom,
			int indent, Set<Stack<String>> functionStacks, String functionName,
			ReferenceTree<XtaPragma> references) {
		int nextIndent = indent + 1;
		file.append("int " + functionName +"() {\n");
		file.append("  timer t;\n");
		file.append("  int x = 0;\n");
		ReferenceTree<XtaPragma> fnRef =  addLabelPragma(file, pseudoRandom, nextIndent, PragmaType.Function, references, functionName);
		makeNext(file, pseudoRandom, functionStacks, functionName, nextIndent, fnRef);
		file.append("  return x;\n");
		file.append("}\n");
		
	}

}
