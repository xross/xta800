package com.xmos.xta.console.commands;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Reference;
import com.xmos.xta.engine.Engine.ReferenceMode;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.Console;

class Exclude extends Command {
    Reference m_ref;
    
    Exclude() {
        m_ref = null;
    }
    
    Exclude(Reference ref) {
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
        return "exclude <ref>: Adds the given reference to the exclusion list (post analysis action)";         
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
        if ((index + numArgs()) >= args.length) 
            return null;        

        String r = args[index + 1];
        Reference ref = new Reference(r);
        return new Exclude(ref);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
        if (!isExecutableLoaded(engine, console))
            return;                 
        m_ref.resolve(engine);
        engine.addReference(ReferenceMode.EXCLUSION, m_ref);
    }   
 }
