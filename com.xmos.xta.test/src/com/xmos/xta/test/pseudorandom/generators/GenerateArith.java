package com.xmos.xta.test.pseudorandom.generators;

import java.util.Random;
import java.util.Set;
import java.util.Stack;

import com.xmos.xta.test.pseudorandom.Generator;
import com.xmos.xta.test.pseudorandom.PseudoRandom;
import com.xmos.xta.test.pseudorandom.ReferenceTree;
import com.xmos.xta.test.pseudorandom.XtaPragma;

public class GenerateArith extends Generator {

    public void generate(StringBuffer file, PseudoRandom pseudoRandom,
            int indent, Set<Stack<String>> functionStacks, String functionName, ReferenceTree<XtaPragma> references, int activeThreads) {
        ReferenceTree<XtaPragma> ref = addLabelPragma(file, pseudoRandom, indent, PragmaType.Label, references, "arith");
        addIndent(file, indent);
        pseudoRandom.addEndRef( ref);
        Random r = new Random();
        switch (r.nextInt(8)){
        case 0:
            file.append("x += x  - 32;\n");
            break;
        case 1:
            file.append("x++;\n");
            break;
        case 2:
            file.append("x = x + 2;\n");
            break;
        case 3:
            file.append("x -= 4332;\n");
            break;
        case 4:
            file.append("x *= x - 32;\n");
            break;
        case 5:
            file.append("x += x << 2;\n");
            break;
        case 6:
            file.append("x += (x < 5);\n");
            break;
        case 7:
            file.append("x =  5 << x;\n");
            break;
        }

    }
}
