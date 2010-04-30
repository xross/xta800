package com.xmos.xta.core.console.commands.add;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.utils.Scope;

public class LoopScope extends Command {
	private static final String[] COMMAND_NAMES = { "loopscope" };

	private String m_refString;
	private boolean m_absolute;

	public LoopScope() {
		super(COMMAND_NAMES);
	}

	public LoopScope(String refString, boolean absolute) {
		super(COMMAND_NAMES);
		m_refString = refString;
		m_absolute = absolute;
	}

    public int numArgs() {
    	return 2;
    }

	public ArgType[] getArgTypes() {
		return new Command.ArgType[] {ArgType.ALLREFS};
	}

    public String commandLine() {
       	return name() + " " + " " + m_refString + " " + 
       	    (m_absolute ? Scope.getAbsoluteStr() : Scope.getRelativeStr());
    }

    public String description(Engine engine, boolean detailed) {
    	String descString = name() + " <ANY> <SCOPE>: Adds the given loop scope define to the list of defines";
    	if (detailed) {
        	descString += "\n";
        	descString += Scope.getHelp();
    	}
    	return descString;
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) {
			return null;
		}

    	boolean absolute;
	    if (Scope.isRelative(args[index + 2])){
	    	absolute = false;
	    }
	    else if (Scope.isAbsolute(args[index + 2])){
	    	absolute = true;

	    } else {
	    	return null;
	    }
        return new LoopScope(args[index + 1], absolute);
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console)) {
			return;
		}
		engine.addLoopScope(getSrc(), m_refString, m_absolute);
    }
 }