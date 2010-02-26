package com.xmos.xta.core.console.commands.add;

import java.util.ArrayList;
import java.util.Collection;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;

public class Branch extends Command {
	private String m_fromRefString;
	private Collection<String> m_toRefStrings;

	public Branch() {
		m_toRefStrings = new ArrayList<String>();
	}

	public Branch(String fromRefString, Collection<String> toRefStrings) {
		m_fromRefString = fromRefString;
		m_toRefStrings = toRefStrings;
	}

	public int numArgs() {
        return 1 + m_toRefStrings.size();
	}

	public ArgType[] getArgTypes() {
		// Note: From the point of view of the console command
		// completion, it wont work after the 10th argument...
		return new Command.ArgType[] {
				ArgType.LABEL, ArgType.LABEL, ArgType.LABEL, ArgType.LABEL, ArgType.LABEL,
     			ArgType.LABEL, ArgType.LABEL, ArgType.LABEL, ArgType.LABEL, ArgType.LABEL};
	}

	public String name() {
		return "branch";
	}

	public String commandLine() {
		String str = "branch " + m_fromRefString;
		for (String toRefString : m_toRefStrings) {
			str += " " + toRefString;
		}
		return str;
	}

	public String description(Engine engine, boolean detailed) {
        return "branch <from LABEL_REF> <to LABEL_REF> <to LABEL_REF> ... etc: Adds the given from/to references to the branches list";
	}

	public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) {
			return null;
		}

		int i = 2;
		Collection<String> toRefStrings = new ArrayList<String>();
		while (!areArgsDone(args, index + i)) {
			toRefStrings.add(args[index + i]);
			++i;
		}
		return new Branch(args[index + 1], toRefStrings);
	}

	public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

		Reference fromRef = ReferenceFactory.createReference(m_fromRefString, ReferenceClass.LABEL_REF);

		Collection<Reference> toRefs = new ArrayList<Reference>();
		for (String toRefString : m_toRefStrings) {
			Reference ref = ReferenceFactory.createReference(toRefString, ReferenceClass.LABEL_REF);
			toRefs.add(ref);
		}

		engine.addBranch(getSrc(), fromRef, toRefs);
	}
}
