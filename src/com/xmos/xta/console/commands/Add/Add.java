package com.xmos.xta.console.commands.Add;

import com.xmos.xta.console.commands.Command;
import com.xmos.xta.console.commands.CommandContainer;
import com.xmos.xta.console.commands.List.Branch;
import com.xmos.xta.console.commands.List.Endpoint;
import com.xmos.xta.console.commands.List.Label;
import com.xmos.xta.console.commands.List.SrcLabel;
import com.xmos.xta.console.commands.List.ThreadStartPoint;
import com.xmos.xta.console.commands.List.ThreadStopPoint;

public class Add extends CommandContainer {
	static private Command[] m_options = { 
		new Branch(Branch.Operation.ADD),
		new Label(Label.Operation.ADD),
		new SrcLabel(SrcLabel.Operation.ADD),
		new Endpoint(Endpoint.Operation.ADD),
		new ThreadStartPoint(ThreadStartPoint.Operation.ADD),		
		new ThreadStopPoint(ThreadStopPoint.Operation.ADD),		
	};
	
	public Add() { super("add"); }
	public Add(Command command) { super("add", command); }
	
	public Command[] getSubCommands() { return m_options; }
	protected String getDescription() { return "Add item to given list"; }
	protected Command clone(Command command) { return new Add(command); }	
}
