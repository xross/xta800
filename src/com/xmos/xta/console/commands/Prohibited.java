package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.CodeReference;
import com.xmos.xta.engine.Engine.ReferenceMode;

public class Prohibited extends Command {
	CodeReference m_ref;
    
	Prohibited() {
        m_ref = null;
    }
    
	Prohibited(CodeReference ref) {
        m_ref = ref;        
    }
    
    public int numArgs() {
        return 1;
    }        

    public String name() {
        return "prohibited";
    }        

    public String commandLine() {
        return "prohibited " + m_ref;
    }
    
    public String description(Engine engine, boolean detailed) {
        return "prohibited <ref>: Adds the given reference to the prohibited list (during analysis action)";         
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
        if ((index + numArgs()) >= args.length) 
            return null;        

        String r = args[index + 1];
        CodeReference ref = new CodeReference(r);
        return new Prohibited(ref);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
        if (!isExecutableLoaded(engine, console))
            return;    
        m_ref.resolve(engine);
        engine.addReference(ReferenceMode.PROHIBITED, m_ref);
    }  

}
