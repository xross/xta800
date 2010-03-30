package com.xmos.xta.test.pseudorandom.generators;

import java.util.Set;
import java.util.Stack;

import com.xmos.xta.test.pseudorandom.Generator;
import com.xmos.xta.test.pseudorandom.PseudoRandom;
import com.xmos.xta.test.pseudorandom.ReferenceTree;
import com.xmos.xta.test.pseudorandom.XtaPragma;

public class GenerateWhile extends Generator {

	public void generate(StringBuffer file, PseudoRandom pseudoRandom,
			int indent, Set<Stack<String>> functionStacks, String functionName, ReferenceTree<XtaPragma> references) {
		int nextIndent = indent + 1;
		addIndent(file, indent);
		file.append("while(x != 42){\n");
		ReferenceTree<XtaPragma> currentRef = addLabelPragma(file, pseudoRandom, nextIndent, PragmaType.Label, references, "while");
		makeNext(file, pseudoRandom, functionStacks, functionName, nextIndent, currentRef);
		addIndent(file, indent);
		file.append("}\n");
	}
}
