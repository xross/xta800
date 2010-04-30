package com.xmos.xta.core.console.commands.debug;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.CommandContainer;
import com.xmos.xta.core.console.commands.CommandFactory;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.references.types.ReferenceClassType;
import com.xmos.xta.core.engine.references.types.ReferenceType;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.core.engine.utils.TimeUnit;

public class DumpManual extends Command {
	private static final String[] COMMAND_NAMES = { "dumpmanual" };

	public DumpManual() {
		super(COMMAND_NAMES);
	}

    public int numArgs() {
    	return 0;
    }

    public String commandLine() {
    	return name();
    }

    public String description(Engine engine, boolean detailed) {
        return name() + ": Dump the console reference chapter of the manual in tex format";
    }

    public Command clone(String[] args, int index) {
        return new DumpManual();
    }

    public void execute(Shell shell, Engine engine, Console console) {
    	dumpCommandsSection(engine, console);
    	dumpTimingModesSection(engine, console);
    	dumpLoopScopesSection(engine, console);    	
    	dumpReferenceClassesSection(engine, console);    	
    }
    
    private void dumpCommandsSection(Engine engine, Console console) {
    	Command[] avaliableCommands = CommandFactory.getCommands();
    	for (Command command : avaliableCommands) {    		
    		console.display(String.format("\\section{%s}\n", command.name()));    		
    		console.display(String.format("\\label{section:%s}\n\n", command.name()));    		
    		
    		if (command instanceof CommandContainer) {
               	for (Command subCommand : command.getSubCommands()) {
               		console.display("\\command{" + command.name() + " " + getCommandLine(engine, subCommand) + "}\n");           		
               		console.display(getCommandDesc(engine, subCommand) + "\n\n");
               	}    			
    		} else {
           		console.display("\\command{" + getCommandLine(engine, command) + "}\n");           		
           		console.display(getCommandDesc(engine, command) + "\n\n");    			
    		}    		
    	}    	
    }
    
    private void dumpTimingModesSection(Engine engine, Console console) {
    	console.display("\\section{Timing Modes}\n");
    	console.display("\\label{section:TimingModes}\n\n");
    	console.display("The avaliable timing modes are:\n");
    	int length = TimeUnit.getUnits().length;
    	for (int i = 0; i < length; ++i) {
    		console.display(String.format("\\command{%s}\n", TimeUnit.getUnits()[i]));
    		console.display(String.format("%s\n\n", TimeUnit.getUnitsDesc()[i]));
    	}
    }
    
    private void dumpLoopScopesSection(Engine engine, Console console) {    	
    	console.display("\\section{Loop Scopes}\n");
    	console.display("\\label{section:LoopScopes}\n\n");
    	console.display("Supported values for scope are:\n");
    	console.display("\\command{relative/r}\n\n");
    	console.display("Iteration number propagates to the enclosing path (Default)\n");
    	console.display("\\command{absolute/a}\n");
    	console.display("Absolute number of iterations\n\n");
    }

    private void dumpReferenceClassesSection(Engine engine, Console console) {    	
    	console.display("\\section{Reference Classes}\n");
    	console.display("\\label{section:ReferenceClasses}\n\n");
    
		for (ReferenceClass referenceClass : ReferenceTypeFactory.getReferenceClasses().keySet()) {
			String refClassName = referenceClass.toString().replaceAll("\\_", "\\\\_");
			
			console.display(String.format("\\subsection{%s}\n\n", refClassName));
			ReferenceClassType classType = ReferenceTypeFactory.getReferenceClass(referenceClass);
			ReferenceType[] referenceTypes = classType.getReferenceTypes();
			for (ReferenceType referenceType : referenceTypes) {
		    	console.display(String.format("\\command{%s}\n", referenceType.name()));
		    	console.display(String.format("%s\n\n", referenceType.description()));
			}
		}
    }
    
    private String getCommandLine(Engine engine, Command command) {
		return getCommandStrSectionAtIndex(engine, command, 0);
    }

    private String getCommandDesc(Engine engine, Command command) {
		return getCommandStrSectionAtIndex(engine, command, 1);
    }
    
    private String getCommandStrSectionAtIndex(Engine engine, Command command, int index) {
		String commandStr = command.description(engine, false);
		String[] commandArray = commandStr.split(":", 2);		        		
		assert(commandArray.length == 2);
		return commandArray[index].trim();    	
    }
}
