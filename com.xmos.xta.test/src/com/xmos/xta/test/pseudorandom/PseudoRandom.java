package com.xmos.xta.test.pseudorandom;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

import com.xmos.xta.test.pseudorandom.generators.GenerateFunction;

public class PseudoRandom {

    private Set<FunctionDec> m_ungeneratedFunctions;
    private Set<FunctionDec> m_generatedFunctions;
    private Map<String, Set<ReferenceTree<XtaPragma>>> m_functionToCall;
    private Set<ReferenceTree<XtaPragma>> m_endRefs;
    private Set<XtaPragma> m_xtaPragmas;

    final int max_function_depth = 12 ;

    public static void main(String[] args) {
        PseudoRandom pr = new PseudoRandom();
        pr.go();
    }


    public void go(){
        m_functionToCall = new HashMap<String, Set<ReferenceTree<XtaPragma>>>();
        m_endRefs = new HashSet<ReferenceTree<XtaPragma>>();
        m_ungeneratedFunctions = new HashSet<FunctionDec>();
        m_generatedFunctions = new HashSet<FunctionDec>();
        m_xtaPragmas = new HashSet<XtaPragma>();
        StringBuffer file = new StringBuffer();
        StringBuffer prototype = new StringBuffer();

        prototype.append("#include <xs1.h>\n");
        m_ungeneratedFunctions.add(new FunctionDec("main"));

        while(!m_ungeneratedFunctions.isEmpty()){
            generateFunction(file, prototype);
        }

        prototype.append(file);

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("test.xc"));
            out.write(prototype.toString());
            out.close();
        } catch (IOException e) {

        }
        setLineNumbers(prototype);

        CmdMaker cmdMaker = new CmdMaker(m_endRefs, m_functionToCall);

        cmdMaker.make();


    }

    private void generateFunction(StringBuffer file, StringBuffer prototype){
        FunctionDec ungenFunc = m_ungeneratedFunctions.iterator().next();
        GenerateFunction fn = new GenerateFunction();
        prototype.append("int " + ungenFunc.m_functionName +"();\n");
        fn.generate(file, this, 0, ungenFunc.m_functionCallingStacks,
                ungenFunc.m_functionName, new ReferenceTree<XtaPragma>(new XtaPragma("root")), 1);
        m_generatedFunctions.add(ungenFunc);
        m_ungeneratedFunctions.remove(ungenFunc);
    }

    class FunctionDec {
        Set<Stack<String>> m_functionCallingStacks;
        String m_functionName;

        public FunctionDec(String name){
            m_functionName = name;
            m_functionCallingStacks = new HashSet<Stack<String>>();
            m_functionCallingStacks.add(new Stack<String>());
        }

        public FunctionDec(String name, Set<Stack<String>> stacks){
            m_functionName = name;
            m_functionCallingStacks = new HashSet<Stack<String>>();
            for(Stack<String> stack : stacks){
                Stack<String> callStack = new Stack<String>();
                callStack.addAll(stack);
                m_functionCallingStacks.add(callStack);
            }
        }
    }

    public int getMaxFunctionDepth() {
        return max_function_depth;
    }


    public void addCallToFunction(ReferenceTree<XtaPragma> ref, String function){
        if(m_functionToCall.containsKey(function)){
            m_functionToCall.get(function).add(ref);
        } else {
            Set<ReferenceTree<XtaPragma>> callingLocations = new HashSet<ReferenceTree<XtaPragma>>();
            callingLocations.add(ref);
            m_functionToCall.put(function, callingLocations);
        }
    }

    public void addEndRef(ReferenceTree<XtaPragma> ref) {
        m_endRefs.add(ref);
    }


    public String getNewFunctionName(Set<Stack<String>> functionStacks){

        //the new function cannot be a string that is in any of the stacks
        Set<String> offLimits = new HashSet<String>();
        for(Stack<String> stack : functionStacks){
            for(String fn : stack){
                offLimits.add(fn);
            }
        }

        for(FunctionDec ungeneratedFn : m_ungeneratedFunctions){
            if(!offLimits.contains(ungeneratedFn.m_functionName)){

                Set<Stack<String>> copy = new HashSet<Stack<String>>();
                for(Stack<String> stack : functionStacks){
                    Stack<String> callStack = new Stack<String>();
                    callStack.addAll(stack);
                    copy.add(callStack);
                }
                ungeneratedFn.m_functionCallingStacks.addAll(copy);

                return ungeneratedFn.m_functionName;
            }
        }

        String nextFunction = makeFunctionName();
        int tries = 0;
        while(containsFunction(functionStacks, nextFunction)){
            nextFunction = makeFunctionName();
            tries++;
            if(tries == 100000){
                return null;
            }
        }
        assert(!offLimits.contains(nextFunction));

        FunctionDec fn = new FunctionDec(nextFunction, functionStacks);
        m_ungeneratedFunctions.add(fn);

        return nextFunction;
    }

    private boolean containsFunction(Set<Stack<String>> functionStacks, String fn){
        for(Stack<String> stack : functionStacks){
            if(stack.contains(fn)){
                return true;
            }
        }
        return false;
    }

    private String makeFunctionName() {
        Random r = new Random();
        return "f" + r.nextInt(getMaxFunctionDepth()*2);
    }


    public void registerXtaPragma(XtaPragma xtapragma) {
        m_xtaPragmas.add(xtapragma);
    }

    private void setLineNumbers(StringBuffer file){
        for(XtaPragma label : m_xtaPragmas){
            int index = file.indexOf(label.getLabel());
            int pragmaLine = lineOfIndex(file, index);
            while(getLine(pragmaLine, file).contains("#pragma")){
                pragmaLine++;
            }
            label.setFileLine(pragmaLine);
        }
    }

    private int lineOfIndex(StringBuffer file, int index){
        int line = 1;
        for(int i=0;i<index;i++){
            if(file.charAt(i) == '\n'){
                line++;
            }
        }
        return line;
    }

    private String getLine(int i, StringBuffer file){
        int count = 1;
        int index = 0;
        while(count < i){
            if(file.charAt(index) == '\n'){
                count++;
            }
            index++;
        }
        String s = file.substring(index);
        int endOfLine = s.indexOf('\n');
        s = s.substring(0, endOfLine);
        return s;
    }

}
