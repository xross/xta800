package com.xmos.xta.core.console.commands.list;

import java.util.Map;
import java.util.Set;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.sections.BranchInfo;
import com.xmos.xta.core.engine.utils.NodeCorePair;

public class Branches extends Command {

	public Branches() {
	}

	public int numArgs() {
        return 0;
	}

	public ArgType[] getArgTypes() {
        return super.getArgTypes();
	}

	public String name() {
		return "branches";
	}

	public String commandLine() {
		return "branches";
	}

	public String description(Engine engine, boolean detailed) {
        return "branches: Lists the branches";
	}

	public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) {
			return null;
		}

		return new Branches();
	}

	public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console)) {
			return;
		}

		listBranches(engine, console);
	}

	private void listBranches(final Engine engine, final Console console) {
		for (NodeCorePair activeCore : engine.getActiveCores()) {			
			Map<Reference, Set<BranchInfo>> branches = engine.getBranches(activeCore.getNode(), activeCore.getCore());
			for (Reference fromRef : branches.keySet()) {
				String to = getTo(branches, fromRef, false);
				if (to.length() > 0) {
		    	    console.display(String.format("- %s =>%s (binary)\n", fromRef.toString(), to));
				}
				to = getTo(branches, fromRef, true);
				if (to.length() > 0) {
		    	    console.display(String.format("- %s =>%s (user)\n", fromRef.toString(), to));
				}
			}
		}
	}

	private String getTo(Map<Reference, Set<BranchInfo>> branches, Reference fromRef, boolean isUser) {
		String result = "";
		for (BranchInfo info : branches.get(fromRef)) {
			if (isUser == info.isUser()) {
				result += String.format(" %s", info.getRef().toString());
			}
		}
		return result;
	}
}
