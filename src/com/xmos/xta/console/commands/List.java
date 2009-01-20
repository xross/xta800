package com.xmos.xta.console.commands;

class List extends CommandContainer {
	static private Command[] m_options = { 
		new Label(Label.Operation.LIST), 
		new Filter(Filter.Operation.LIST), 
		new Prohibited(Prohibited.Operation.LIST), 
		new Required(Required.Operation.LIST), 
		new Unknowns(),
		new Functions(),
		new Sources(),
		new Queries(),
	};
	
	List() { super("list"); }
	List(Command command) { super("list", command); }
	
	protected String getDescription() { return "Lists the given set of items"; }
	protected Command[] getSubCommands() { return m_options; }
	protected Command clone(Command command) { return new List(command); }		
}
