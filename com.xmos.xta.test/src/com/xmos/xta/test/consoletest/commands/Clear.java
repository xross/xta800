package com.xmos.xta.test.consoletest.commands;

import java.util.Random;

import com.xmos.xta.test.consoletest.ConsoleTest;

public class Clear implements Command{

    public String getCommand() {
        Random r = new Random();


        if(ConsoleTest.allowMess){
            if(r.nextBoolean()){
                return "clear ";
            } else {
                Mess mess = new Mess();
                return "clear " + mess.getCommand();
            }
        } else {
            return "clear ";
        }

    }

}
