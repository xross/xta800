package com.xmos.xta.test.pseudorandom.generators;

import java.util.Set;
import java.util.Stack;

import com.xmos.xta.test.pseudorandom.Generator;
import com.xmos.xta.test.pseudorandom.PseudoRandom;
import com.xmos.xta.test.pseudorandom.ReferenceTree;
import com.xmos.xta.test.pseudorandom.XtaPragma;

public class GenerateFor extends Generator {

	public void generate(StringBuffer file, PseudoRandom pseudoRandom,
			int indent, Set<Stack<String>> functionStacks, String functionName, ReferenceTree<XtaPragma> references, int activeThreads) {
		int nextIndent = indent + 1;
		addIndent(file, indent);
		file.append("for(x=0;x<5;x++){\n");
		ReferenceTree<XtaPragma> forRef = addLabelPragma(file, pseudoRandom, nextIndent, PragmaType.Label, references, "for");
		makeNext(file, pseudoRandom, functionStacks, functionName, nextIndent, forRef, activeThreads);
		addIndent(file, indent);
		file.append("}\n");
	}

}
