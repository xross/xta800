package com.xmos.xta.console.commands.Remove;

import com.xmos.xta.console.Command;
import com.xmos.xta.console.CommandContainer;
import com.xmos.xta.console.commands.Branch;
import com.xmos.xta.console.commands.Endpoint;
import com.xmos.xta.console.commands.Filter;
import com.xmos.xta.console.commands.Label;
import com.xmos.xta.console.commands.Prohibited;
import com.xmos.xta.console.commands.Required;

public class Remove extends CommandContainer {
	static private Command[] m_options = { 
		new Filter(Filter.Operation.REMOVE), 
		new Prohibited(Prohibited.Operation.REMOVE), 
		new Required(Required.Operation.REMOVE), 
		new Branch(Branch.Operation.REMOVE),		
		new Endpoint(Endpoint.Operation.REMOVE),		
		new Label(Label.Operation.REMOVE), 
	};
	
	public Remove() { super("remove"); }
	public Remove(Command command) { super("remove", command); }
	
	public Command[] getSubCommands() { return m_options; }
	protected String getDescription() { return "Remove item from given list"; }
	protected Command clone(Command command) { return new Remove(command); }	
}
