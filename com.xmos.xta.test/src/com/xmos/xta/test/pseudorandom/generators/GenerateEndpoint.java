package com.xmos.xta.test.pseudorandom.generators;

import java.util.Set;
import java.util.Stack;

import com.xmos.xta.test.pseudorandom.Generator;
import com.xmos.xta.test.pseudorandom.PseudoRandom;
import com.xmos.xta.test.pseudorandom.ReferenceTree;
import com.xmos.xta.test.pseudorandom.XtaPragma;

public class GenerateEndpoint extends Generator {

	public void generate(StringBuffer file, PseudoRandom pseudoRandom,
			int indent, Set<Stack<String>> functionStacks, String functionName,
			ReferenceTree<XtaPragma> references) {
		
		ReferenceTree<XtaPragma> ref = addLabelPragma(file, pseudoRandom, indent, PragmaType.Endpoint, references, "endpoint");
		pseudoRandom.addEndRef( ref);
		addIndent(file, indent);
		file.append("t :> x;\n");
	}

}
