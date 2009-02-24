package com.xmos.xta.console.commands.Add;

import com.xmos.xta.console.Command;
import com.xmos.xta.console.CommandContainer;
import com.xmos.xta.console.commands.Branch;
import com.xmos.xta.console.commands.Endpoint;
import com.xmos.xta.console.commands.Filter;
import com.xmos.xta.console.commands.Label;
import com.xmos.xta.console.commands.Prohibited;
import com.xmos.xta.console.commands.Required;
import com.xmos.xta.console.commands.SrcLabel;

public class Add extends CommandContainer {
	static private Command[] m_options = { 
		new Filter(Filter.Operation.ADD), 
		new Prohibited(Prohibited.Operation.ADD), 
		new Required(Required.Operation.ADD), 
		new Branch(Branch.Operation.ADD),
		new Label(Label.Operation.ADD),
		new SrcLabel(SrcLabel.Operation.ADD),
		new Endpoint(Endpoint.Operation.ADD),
	};
	
	public Add() { super("add"); }
	public Add(Command command) { super("add", command); }
	
	public Command[] getSubCommands() { return m_options; }
	protected String getDescription() { return "Add item to given list"; }
	protected Command clone(Command command) { return new Add(command); }	
}
