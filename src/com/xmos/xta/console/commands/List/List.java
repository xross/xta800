package com.xmos.xta.console.commands.List;

import com.xmos.xta.console.Command;
import com.xmos.xta.console.CommandContainer;
import com.xmos.xta.console.commands.Branch;
import com.xmos.xta.console.commands.Constraint;
import com.xmos.xta.console.commands.Endpoint;
import com.xmos.xta.console.commands.Label;
import com.xmos.xta.console.commands.SrcLabel;

public class List extends CommandContainer {
	static private Command[] m_options = { 
		new Branch(Branch.Operation.LIST),		
		new Label(Label.Operation.LIST), 
		new SrcLabel(SrcLabel.Operation.LIST),		
		new Endpoint(Endpoint.Operation.LIST),		
		new AllSrcLabels(),		
		new AllEndpoints(),		
		new Unknowns(),
		new Functions(),
		new Sources(),
		new Times(),
		new Constraint(Constraint.Operation.LIST),		
	};
	
	public List() { super("list"); }
	public List(Command command) { super("list", command); }
	
	public Command[] getSubCommands() { return m_options; }
	protected String getDescription() { return "Lists the given set of items"; }
	protected Command clone(Command command) { return new List(command); }		
}
