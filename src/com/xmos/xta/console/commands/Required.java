package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Reference;
import com.xmos.xta.engine.Engine.ReferenceMode;

public class Required extends Command {
    Reference m_ref;
    
    Required() {
        m_ref = null;
    }
    
    Required(Reference ref) {
        m_ref = ref;        
    }
    
    public int numArgs() {
        return 1;
    }        

    public String name() {
        return "required";
    }        

    public String commandLine() {
        return "required " + m_ref;
    }
    
    public String description(Engine engine, boolean detailed) {
        return "required <ref>: Adds the given reference to the required list (post analysis action)";         
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
        if ((index + numArgs()) >= args.length) 
            return null;        

        String r = args[index + 1];
        Reference ref = new Reference(r);
        return new Required(ref);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
        if (!isExecutableLoaded(engine, console))
            return;    
        m_ref.resolve(engine);
        engine.addReference(ReferenceMode.REQUIRED, m_ref);
    }  

}
