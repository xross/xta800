package com.xmos.xta.test.consoletest.commands;

import java.util.Random;

import com.xmos.xta.test.consoletest.ConsoleTest;

public class Pwd implements Command {

	public String getCommand() {
		Random r = new Random();
		if(r.nextBoolean() || ConsoleTest.allowMess){
			return "pwd ";
		} else {
			Mess mess = new Mess();
			return "pwd " + mess.getCommand();
		}
	}

}
