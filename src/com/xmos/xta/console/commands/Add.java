package com.xmos.xta.console.commands;

class Add extends CommandContainer {
	static private Command[] m_options = { 
		new Label(Label.Operation.ADD), 
		new Excluded(Excluded.Operation.ADD), 
		new Prohibited(Prohibited.Operation.ADD), 
		new Required(Required.Operation.ADD), 
	};
	
	Add() { super("add"); }
	Add(Command command) { super("add", command); }
	
	protected String getDescription() { return "Add item to given list"; }
	protected Command[] getSubCommands() { return m_options; }
	protected Command clone(Command command) { return new Add(command); }	
}
