package com.xmos.xta.test.consoletest.commands.units;

import java.util.Random;

import com.xmos.xta.test.consoletest.commands.Command;
import com.xmos.xta.test.consoletest.commands.Mess;

public class RouteId implements Command{

    public String getCommand() {
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        int i = r.nextInt(5);
        switch(i){
        case 0:
        case 1:
        case 2:
        case 3:
        case 4:
        case 5:
        case 6:
        case 7:
            sb.append(i + " ");
            break;
        case 8:
            sb.append("- ");
            break;
        case 9:
            Mess mess= new Mess();
            sb.append(mess.getCommand());
            break;
        }
        return sb.toString();
    }

}