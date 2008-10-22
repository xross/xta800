package com.xmos.xta.console.commands;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.Console;

class Exclude extends Command {
    String m_ref;
    
    Exclude() {
        m_ref = "";
    }
    
    Exclude(String ref) {
        m_ref = ref;        
    }
    
    public int numArgs() {
        return 1;
    }        

    public String name() {
        return "exclude";
    }        

    public String commandLine() {
        return "exclude " + m_ref;
    }
    
    public String description(Engine engine, boolean detailed) {
        return "exclude <ref>: Adds the given reference to the exclusion list";         
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
        if ((index + numArgs()) >= args.length) 
            return null;        

        String ref = args[index + 1];
        return new Exclude(ref);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
        if (!isExecutableLoaded(engine, console))
            return;                 

        if (!engine.setExclusion(m_ref)) {
            console.display(String.format("Error: %s\n", engine.getErrorString()));
        }
    }   
 }
