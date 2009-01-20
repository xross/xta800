package com.xmos.xta.console.commands;

class Remove extends CommandContainer {
	static private Command[] m_options = { 
		new Label(Label.Operation.REMOVE), 
		new Filter(Filter.Operation.REMOVE), 
		new Prohibited(Prohibited.Operation.REMOVE), 
		new Required(Required.Operation.REMOVE), 
	};
	
	Remove() { super("remove"); }
	Remove(Command command) { super("remove", command); }
	
	protected String getDescription() { return "Remove item from given list"; }
	protected Command[] getSubCommands() { return m_options; }
	protected Command clone(Command command) { return new Remove(command); }	
}
