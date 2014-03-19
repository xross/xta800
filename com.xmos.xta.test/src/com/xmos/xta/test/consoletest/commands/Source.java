package com.xmos.xta.test.consoletest.commands;

import java.util.Random;

public class Source implements Command {

    private boolean formLoops = false;

    public String getCommand() {
        Random r = new Random();
        if(formLoops && r.nextBoolean() ){
            return "source cmds.txt ";
        } else {
            Mess mess = new Mess();
            return "source " +mess.getCommand();
        }
    }

}
