package com.xmos.xta.test.pseudorandom;

import java.util.Random;
import java.util.Set;
import java.util.Stack;

import com.xmos.xta.test.pseudorandom.generators.GenerateArith;
import com.xmos.xta.test.pseudorandom.generators.GenerateEndpoint;
import com.xmos.xta.test.pseudorandom.generators.GenerateFor;
import com.xmos.xta.test.pseudorandom.generators.GenerateFunctionCall;
import com.xmos.xta.test.pseudorandom.generators.GenerateIf;
import com.xmos.xta.test.pseudorandom.generators.GeneratePar;
import com.xmos.xta.test.pseudorandom.generators.GenerateWhile;

public abstract class Generator {
    private static int m_callId;

    public abstract void generate(StringBuffer file, PseudoRandom pseudoRandom, int indent,
            Set<Stack<String>> functionStacks, String functionName, ReferenceTree<XtaPragma> currentReference, int activeThreads);

    public void addIndent(StringBuffer file, int indent){
        for(int i=0;i<indent;i++){
            file.append("  ");
        }
    }

    public void makeFunction(StringBuffer file, PseudoRandom pseudoRandom, Set<Stack<String>> functionStacks,
            String functionName, int indent, ReferenceTree<XtaPragma> references, int activeThreads) {
        GenerateFunctionCall gfc = new GenerateFunctionCall();
        gfc.generate(file, pseudoRandom, indent, functionStacks, functionName, references, activeThreads);
    }

    public void makeNext(StringBuffer file, PseudoRandom pseudoRandom, Set<Stack<String>> functionStacks,
            String functionName, int indent, ReferenceTree<XtaPragma> references, int activeThreads) {
        Random r = new Random();
        if(indent == 10){
            GenerateArith ga = new GenerateArith();
            ga.generate(file, pseudoRandom, indent, functionStacks, functionName, references, activeThreads);
            return;
        }
        int count = r.nextInt(4) +1;
        for(int i=0;i<count; i++){
            switch(r.nextInt(7)){
            case 0:
                GenerateIf gi = new GenerateIf();
                gi.generate(file, pseudoRandom, indent, functionStacks, functionName, references, activeThreads);
                break;
            case 1:
                GenerateFor gf = new GenerateFor();
                gf.generate(file, pseudoRandom, indent, functionStacks, functionName, references, activeThreads);
                break;
            case 2:
                GenerateWhile gw = new GenerateWhile();
                gw.generate(file, pseudoRandom, indent, functionStacks, functionName, references, activeThreads);
                break;
            case 3:
                GenerateArith ga = new GenerateArith();
                ga.generate(file, pseudoRandom, indent, functionStacks, functionName, references, activeThreads);
                break;
            case 4:
                GenerateFunctionCall gfc = new GenerateFunctionCall();
                gfc.generate(file, pseudoRandom, indent, functionStacks, functionName, references, activeThreads);
                break;
            case 5:
                GenerateEndpoint gep = new GenerateEndpoint();
                gep.generate(file, pseudoRandom, indent, functionStacks, functionName, references, activeThreads);
                break;
            case 6:
                GeneratePar gp = new GeneratePar();
                gp.generate(file, pseudoRandom, indent, functionStacks, functionName, references, activeThreads);
                break;
            }
        }
    }

    public enum PragmaType {
        Root,
        Call,
        Label,
        Function,
        Endpoint,
    }

    protected ReferenceTree<XtaPragma> addLabelPragma(StringBuffer file, PseudoRandom pseudoRandom, int indent, PragmaType type, ReferenceTree<XtaPragma> prev, String data) {
        addIndent(file, indent);

        String refString = "";

        switch(type){
        case Call:
            refString = "call_" +data + "_"+ m_callId++ ;
            file.append("#pragma xta call \""+refString+"\"\n");
            break;
        case Label:
            refString = "label_" + data + "_" + m_callId++ ;
            file.append("#pragma xta label \""+refString+"\"\n");
            break;
        case Endpoint:
            refString = "endppoint_" + data + "_" + m_callId++ ;
            file.append("#pragma xta endpoint \""+refString+"\"\n");
            break;
        case Function:
            refString = data ;
            break;
        }

        XtaPragma xtapragma = new XtaPragma(type, refString, "test.xc");

        pseudoRandom.registerXtaPragma(xtapragma);
        ReferenceTree<XtaPragma> newRef = new ReferenceTree<XtaPragma>(xtapragma, prev);
        return newRef;
    }

}
