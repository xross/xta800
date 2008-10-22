package com.xmos.xta.console.displayers;

import java.util.ArrayList;

import com.xmos.isa.Instruction;
import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xta.console.Console;
import com.xmos.xta.engine.Displayer;
import com.xmos.xta.engine.Route;

public class GraphDisplayer extends Displayer {
	Console m_console;

	public GraphDisplayer(Console console) {		
		m_console = console;
	}
	
    public void displayRoute(Route route, DebugInfo debugInfo, 
    		MemoryImage memoryImage, SymbolTable symbolTable) {
        /*
        FileOutputStream fOutStream;
        PrintStream printStream;
        try {
        	String dotFileName = route.id() + ".dot";
            fOutStream = new FileOutputStream(dotFileName);
            printStream = new PrintStream(fOutStream);
            printStream.printf("digraph G {\n");
            printStream.printf("rankdir =LR;\n");
            ArrayList<Integer> loop_pc = new ArrayList<Integer>();
            
            for (Path path : route.loopPaths()) {
                for (Instruction instruction : path.getInstructions()) {
                    if (!loop_pc.contains(instruction.getPc())) { 
                	    loop_pc.add(instruction.getPc());
                    }
                }
            }            
            
            ArrayList<block_data> blocktable = new ArrayList<block_data>();
            ArrayList<Integer> linkcount = new ArrayList<Integer>();
            ArrayList<instruction_node> links = new ArrayList<instruction_node>();
            int shortestpath = 99999;
            int longestpath = 0;
            for (Path path : route.completePaths()) {  
                shortestpath= Math.min(shortestpath, path.getNumCycles());
                longestpath= Math.max(longestpath, path.getNumCycles());
            }
            
            boolean longest=false;
            for (Path path : route.completePaths()) {  
                if (path.getNumCycles() == longestpath)
                    longest = true;
                else
                    longest = false;
                
                boolean first = true;
                Instruction previousInstruction = null;
                             
                for (Instruction instruction : path.getInstructions()) {
                    if (!first) {          
                      instruction_node temp = new instruction_node(previousInstruction, instruction, longest);
                      int instno =  listcontains(links, temp);
                      if(instno == -1){
                          links.add(temp);
                          linkcount.add(1);
                      } else {
                          linkcount.set(instno, linkcount.get(instno)+1);
                      }
                    } else {
                        first = false;
                    }                    
                    previousInstruction = instruction;
                }
            }

            LinkedList<Instruction> block = new LinkedList<Instruction>();
            longest = false;
            for(int i = 0; i < links.size() - 1; i++) {
                longest = links.get(i).longest;
                
                if (links.get(i).to.getPc() == links.get(i+1).from.getPc() && linkcount.get(i) == linkcount.get(i+1)){
                    block.add(links.get(i).from);
                } 
                else {
                    block.add(links.get(i).from);
                    block.add(links.get(i).to);
                    printStream.printf(blocktable.size() + " [");
                    
                    if (longest) 
                    	printStream.printf("color = red,");
                    
                    printStream.printf("label = \"{");
                    boolean firstloop = true;
                    int start =0;
                    for (int j = start; j < block.size();j++) {
                        if (loop_pc.contains(block.get(j).getPc())){
                            if (firstloop) {
                                if (j!=start) 
                                	printStream.printf("| loop code\\n");
                                else  
                                	printStream.printf("loop code\\n");
                                firstloop = false;
                            }
                            printStream.printf(block.get(j).getNmenonic()+"\\n");
                        } 
                        else {
                            if (firstloop==false) {
                                printStream.printf("|");
                                firstloop = true;
                            }
                            printStream.printf(block.get(j).getNmenonic()+"\\n");
                        }
                        
                    }
                    printStream.printf("}\", shape = record");
                    
                    printStream.printf("];\n");
                    blocktable.add(new block_data(block.getFirst().getPc(), block.getLast().getPc()));
                    block.clear();
                    longest = false;
                }
            }
            for (int i=0;i<blocktable.size();i++){
                for (int j=0;j<blocktable.size();j++){
                    if (blocktable.get(i).startPC == blocktable.get(j).endPC && i!=j) {
                        printStream.printf(j + " -> " + i+"\n");
                    }
                }
            }
            
            printStream.printf("\n}\n");            
            printStream.close();            
            m_console.display(String.format("Dumped file: %s\n", dotFileName));
        }
        catch (Exception e) {
            System.err.println(e);
            System.err.println ("Error writing to file");
        }    
        */                   
    }

	public void notifyAnalysePaths() {
	}

	public void notifyLoadExecutable() {
	}	       

    public void notifyUpdateState() {    	
    }
	
    private int listcontains(ArrayList<instruction_node> links, instruction_node t){
        
        for (int i=0;i<links.size();i++){
            if (links.get(i).from.getPc() == t.from.getPc() && links.get(i).to.getPc() == t.to.getPc()
                    && links.get(i).from.isFnop() == t.from.isFnop()){
                return i;
            }
        }
        return -1;
    }
    
}
class block_data {
    int startPC;
    int endPC;
    public block_data(int startPC, int endPC){
        this.startPC = startPC;
        this.endPC = endPC;
    }
}
class instruction_node {
    boolean longest=false;
    Instruction from;
    Instruction to;
    public instruction_node(Instruction from, Instruction to, boolean longest ){
        this.from = from;
        this.to = to;
        this.longest = longest;
    }
}
