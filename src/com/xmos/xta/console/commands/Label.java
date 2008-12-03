package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.CodeReference;
import com.xmos.xta.engine.Engine;

public class Label extends Command {

	private String m_label;
	private CodeReference m_ref;

	Label() {
		m_label = null;
	}

	Label(String label, CodeReference ref) {
		m_label = label;
		m_ref = ref;
	}

	public int numArgs() {
		return 2;
	}

	public String name() {
		return "label";
	}

	public String commandLine() {
		return "label " + m_ref.getRef() + " " + m_label;
	}

	public String description(Engine engine, boolean detailed) {
		return "label <CodeRef> <label>: adds a label to the given pc";
	}

	public Command clone(String[] args, int index, boolean commandLineMode) {
		if ((index + numArgs()) >= args.length)
			return null;
		CodeReference codeRef = new CodeReference(args[index+1]);
		return new Label(args[index + 2], codeRef);
	}

	public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;
		m_ref.resolve(engine);
		if(m_ref.isResolved()){
			engine.addLabel(m_ref.getPc(), m_label);
		} else {
			console.display("Error: could not resolve reference: " + m_ref);
		}
		
	}
}
