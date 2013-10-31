package com.xmos.xta.core.console.commands.analyze;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Config;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.analysis.Analyzer.Status;
import com.xmos.xta.core.engine.references.types.ReferenceType.Result;

public class Endpoints extends Command {
	private static final String[] COMMAND_NAMES = { "endpoints", "path" };

	private String m_fromRef;
	private String m_toRef;

	public Endpoints() {
		super(COMMAND_NAMES);
		m_fromRef = "";
		m_toRef = "";
	}

	public Endpoints(String fromRef, String toRef) {
		super(COMMAND_NAMES);
		m_fromRef = fromRef;
		m_toRef = toRef;
	}

    public int numArgs() {
    	if (hasEndPoints()) {
    		return 2;
    	} else {
    		return 0;
    	}
    }

	public ArgType[] getArgTypes() {
		return new Command.ArgType[] {ArgType.ENDPOINT, ArgType.ENDPOINT};
	}

    public String commandLine() {
    	if (hasEndPoints()) {
        	return name() + " " + m_fromRef + " " + m_toRef;
    	} else {
    		return name();
    	}
    }

    public String description(Engine engine, boolean detailed) {
        return name() + " <from ENDPOINT> <to ENDPOINT>: Analyze between the specified endpoints";
    }

    public Command clone(String[] args, int index) {
		if (areArgsDone(args, index + 1)) {
			return new Endpoints();

		} else {
			if ((index + 2) >= args.length)
				return null;

			String fromRef = args[index + 1];
			String toRef = args[index + 2];
			return new Endpoints(fromRef, toRef);
		}
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

		if (hasEndPoints()) {
			Result fromResult = engine.getPathPoints().setFrom(getSrc(), m_fromRef);
			Result toResult = engine.getPathPoints().setTo(getSrc(), m_toRef);
					
			if ((fromResult == Result.UNRESOLVED) || (toResult == Result.UNRESOLVED)) {			    
			    engine.setLastAnalysisStatus(Status.ERROR);
				return;

			} else if ((fromResult == Result.REMOVED) && (toResult == Result.REMOVED)) {
			    if (engine.getConfig().getWarningEnabled(Config.WARNING_REMOVED)) {
	                engine.notify(getSrc(), Notifier.NOTIFY_WARNING, 
	                        String.format("Both endpoints have been removed", m_fromRef));			        
			    }
			    engine.setLastAnalysisStatus(Status.ELIMINATED);
			    return;

            } else if (fromResult == Result.REMOVED) {
                engine.notify(getSrc(), Notifier.NOTIFY_WARNING, 
                        String.format("Endpoint '%s' has been removed", m_fromRef));
                engine.setLastAnalysisStatus(Status.PARTIALLY_ELIMINATED);
                return;			    

		    } else if (toResult == Result.REMOVED) {
                engine.notify(getSrc(), Notifier.NOTIFY_WARNING, 
                        String.format("Endpoint '%s' has been removed", m_toRef));
                engine.setLastAnalysisStatus(Status.PARTIALLY_ELIMINATED);
                return;             		                        
			}
		}
		engine.analyzeEndpoints(getSrc());
    }

	private boolean hasEndPoints() {
    	if ((m_fromRef.length() > 0) && (m_toRef.length() > 0)) {
    		return true;
    	} else {
    		return false;
    	}
	}
}
