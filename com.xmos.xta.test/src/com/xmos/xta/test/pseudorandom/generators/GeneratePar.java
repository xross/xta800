package com.xmos.xta.test.pseudorandom.generators;

import java.util.Random;
import java.util.Set;
import java.util.Stack;

import com.xmos.xta.test.pseudorandom.Generator;
import com.xmos.xta.test.pseudorandom.PseudoRandom;
import com.xmos.xta.test.pseudorandom.ReferenceTree;
import com.xmos.xta.test.pseudorandom.XtaPragma;

public class GeneratePar extends Generator {

    public void generate(StringBuffer file, PseudoRandom pseudoRandom,
            int indent, Set<Stack<String>> functionStacks, String functionName, ReferenceTree<XtaPragma> references, int activeThreads) {
        int nextIndent = indent + 1;
        addIndent(file, indent);
        file.append("par {\n");
        Random r = new Random();
        int count = r.nextInt(2) + 1;
        for(int i=0;i<count; i++){
            makeFunction(file, pseudoRandom, functionStacks, functionName, nextIndent, new ReferenceTree<XtaPragma>(new XtaPragma("root")), activeThreads);
        }
        addIndent(file, indent);
        file.append("}\n");
    }
}
