package com.xmos.xta.console.commands.list;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.sections.CodePointInfo;

public class SrcLabels extends Command {
	public SrcLabels() {
	}

	public int numArgs() {
        return 0;
	}

	public String name() {
   		return "srclabels";
	}

	public String commandLine() {
   		return "srclabels";
	}

	public String description(Engine engine, boolean detailed) {
        return "srclabels: Lists the source labels";
	}

	public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length)
			return null;
				
   		return new SrcLabels();            
	}

	public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

       	listSrcLabels(engine, console);        	
	}
	
	private void listSrcLabels(Engine engine, Console console) {
		if (engine.getSrcLabels().entrySet().size() == 0){
			console.display("none\n");
			return;
		}

		for (CodePointInfo info : engine.getSrcLabels().values()) {
    	    console.display(String.format("Label: %s, pc: 0x%x", info.getLabel(), info.getPc())); 
    	    
    	    if (!info.getCompilationDir().equals("")) {
    	    	console.display(", compilation dir: " + info.getCompilationDir());
    	    	console.display(", filename: " + info.getFileName());
    	    	console.display(", line number: " + info.getLineNumber());
    	    }
    	    console.display("\n");
    	}		
	}
}
