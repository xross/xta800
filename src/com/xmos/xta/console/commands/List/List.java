package com.xmos.xta.console.commands.List;

import com.xmos.xta.console.Command;
import com.xmos.xta.console.CommandContainer;
import com.xmos.xta.console.commands.Branch;
import com.xmos.xta.console.commands.Endpoint;
import com.xmos.xta.console.commands.Filter;
import com.xmos.xta.console.commands.Label;
import com.xmos.xta.console.commands.Prohibited;
import com.xmos.xta.console.commands.Required;
import com.xmos.xta.console.commands.SrcLabel;

public class List extends CommandContainer {
	static private Command[] m_options = { 
		new Filter(Filter.Operation.LIST), 
		new Prohibited(Prohibited.Operation.LIST), 
		new Required(Required.Operation.LIST), 
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
	};
	
	public List() { super("list"); }
	public List(Command command) { super("list", command); }
	
	public Command[] getSubCommands() { return m_options; }
	protected String getDescription() { return "Lists the given set of items"; }
	protected Command clone(Command command) { return new List(command); }		
}
