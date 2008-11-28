package com.xmos.xta.console.commands;

import java.util.ArrayList;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;

public class Label extends Command {

	enum Mode {
		PROHIBITED,
		REQUIRED,
		STATUS,
		REMOVE_PROHIBITED,
		REMOVE_REQUIRED,
		CLEAR_ALL
	}
	private Mode m_mode;
	private String m_label;
	
	Label() {
		m_mode = Mode.STATUS;
		m_label = null;
	}

	Label(Mode mode, String label) {
		m_mode = mode;
		m_label = label;
	}
	
    public int numArgs() {
    	return 1;
    }        

    public String name() {
    	return "label";
    }        

    public String commandLine() {
    	return "label " + m_mode + " "+ m_label;
    }
    
    public String description(Engine engine, boolean detailed) {
        return "label <Mode> <label>: PROHIBIT or REQUIRE a label to be present in the completed paths\n" +
        		"                           STATUS to report the current labels\n" +
        		"                           REMOVE_PROHIBITED or REMOVE_REQUIRED to remove a label from a list\n" +
        		"                           CLEAR_ALL to remove all label requirements";      	
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
		if ((index + numArgs()) >= args.length) 
			return null;		
    	Mode mode;
    	String label;
    	try {
    		if(args[index + 1].equals("PROHIBITED")){
    			mode = Mode.PROHIBITED;
    		} else if(args[index + 1].equals("REQUIRED")){
    			mode = Mode.REQUIRED;
    		} else if(args[index + 1].equals("REMOVE_PROHIBITED")){
    			mode = Mode.REMOVE_PROHIBITED;
    		} else if(args[index + 1].equals("REMOVE_REQUIRED")){
    			mode = Mode.REMOVE_REQUIRED;
    		} else if(args[index + 1].equals("CLEAR_ALL")){
    			mode = Mode.CLEAR_ALL;
    			return new Label(mode, null);
    		} else if(args[index + 1].equals("STATUS")){
    			mode = Mode.STATUS;
    			return new Label(mode, null);
    		} else {
    			return null;
    		}
    		
    	    label = args[index + 2];
	    }
	    catch (NumberFormatException e) {
	        return null;
	    }    	
    	return new Label(mode, label);
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	
		if(m_mode == Mode.STATUS){
    		ArrayList<String> pl = engine.getProhibitedLabels();
    		console.display("Prohibited labels:\n");
    		for(String label : pl){
    			 console.display("\t" + label + "\n");
    		}
    		ArrayList<String> rl = engine.getRequiredLabels();
    		console.display("Required labels:\n");
    		for(String label : rl){
    			 console.display("\t" + label+"\n");
    		}
    	} else if(m_mode == Mode.PROHIBITED){
    		engine.addProhibitedLable(m_label);
    	} else if(m_mode == Mode.REQUIRED){
    		engine.addRequiredLabel(m_label);
    	} else if(m_mode == Mode.REMOVE_PROHIBITED){
    		engine.removeProhibitedLable(m_label);
    	} else if(m_mode == Mode.REMOVE_REQUIRED){
    		engine.removeRequiredLabel(m_label);
    	} else if(m_mode == Mode.CLEAR_ALL){
    		engine.clearProhibitedLables();
    		engine.clearRequiredLabels();
    	} else{
    		assert(false);
    	}
   }    
}
