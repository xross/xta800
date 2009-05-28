package com.xmos.xta.console.commands.add;

import com.xmos.xta.console.commands.Command;
import com.xmos.xta.console.commands.CommandContainer;
import com.xmos.xta.console.commands.list.Branch;
import com.xmos.xta.console.commands.list.Call;
import com.xmos.xta.console.commands.list.Endpoint;
import com.xmos.xta.console.commands.list.Label;
import com.xmos.xta.console.commands.list.SrcLabel;
import com.xmos.xta.console.commands.list.ThreadStartPoint;
import com.xmos.xta.console.commands.list.ThreadStopPoint;

public class Add extends CommandContainer {
	static private Command[] m_options = { 
		new Branch(Branch.Operation.ADD),
		new Label(Label.Operation.ADD),
		new SrcLabel(SrcLabel.Operation.ADD),
		new Endpoint(Endpoint.Operation.ADD),
		new Call(Call.Operation.ADD),
		new ThreadStartPoint(ThreadStartPoint.Operation.ADD),		
		new ThreadStopPoint(ThreadStopPoint.Operation.ADD),		
	};
	
	public Add() { super("add"); }
	public Add(Command command) { super("add", command); }
	
	public Command[] getSubCommands() { return m_options; }
	protected String getDescription() { return "Add item to given list"; }
	protected Command clone(Command command) { return new Add(command); }	
}
