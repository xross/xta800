package com.xmos.xta.core.console.commands.set;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class LoopPath extends Command {
	private int m_routeId;
	private String m_refString;
	private int m_iterations;

	public LoopPath() {
		m_routeId = 0;
		m_refString = "";
		m_iterations = 0;
	}

	public LoopPath(int routeId, String refString, int iterations) {
		m_routeId = routeId;
		m_refString = refString;
		m_iterations = iterations;
	}

    public int numArgs() {
   		return 3;
    }

	public ArgType[] getArgTypes() {
		return new Command.ArgType[] {ArgType.IGNORE, ArgType.ALLREFS};
	}

    public String name() {
    	return "looppath";
    }

    public String commandLine() {
       	return "looppath " + " " + getRouteId(m_routeId) + " " + m_refString + " " + m_iterations;
    }

    public String description(Engine engine, boolean detailed) {
   		return "looppath <route id> <ANY_REF> <iterations>: Sets the number of iterations for the path identified";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length)
			return null;

		int routeId;
    	try { routeId = getRouteId(args[index + 1]);
	    } catch (NumberFormatException e) {
	        return null;
	    }
   	    int iterations;
       	try { iterations = Integer.parseInt(args[index + 3]); }
   	    catch (NumberFormatException e) {
   	        return null;
   	    }
        return new LoopPath(routeId, args[index + 2], iterations);
    }

    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

		Node node = getReferencedNode(engine, m_routeId, m_refString);
		if (node != null) {
   		    engine.setLoopPath(getSrc(), node, m_iterations);

		} else {
   		    engine.setLoopPath(getSrc(), m_routeId, m_refString, m_iterations);
		}
    }
 }
