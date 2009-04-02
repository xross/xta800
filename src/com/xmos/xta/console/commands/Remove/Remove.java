package com.xmos.xta.console.commands.Remove;

import com.xmos.xta.console.commands.Command;
import com.xmos.xta.console.commands.CommandContainer;
import com.xmos.xta.console.commands.List.Branch;
import com.xmos.xta.console.commands.List.Endpoint;
import com.xmos.xta.console.commands.List.Label;
import com.xmos.xta.console.commands.List.SrcLabel;

public class Remove extends CommandContainer {
	static private Command[] m_options = { 
		new Branch(Branch.Operation.REMOVE),		
		new Label(Label.Operation.REMOVE), 
		new SrcLabel(SrcLabel.Operation.REMOVE),		
		new Endpoint(Endpoint.Operation.REMOVE),		
		new Route(),		
	};
	
	public Remove() { super("remove"); }
	public Remove(Command command) { super("remove", command); }
	
	public Command[] getSubCommands() { return m_options; }
	protected String getDescription() { return "Remove item from given list"; }
	protected Command clone(Command command) { return new Remove(command); }	
}
