package com.xmos.xta.test.consoletest.commands.units;

import java.math.BigDecimal;
import java.util.Random;

import com.xmos.xta.test.consoletest.commands.Command;
import com.xmos.xta.test.consoletest.commands.Mess;

public class Number implements Command {

    public String getCommand() {
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        int i = r.nextInt(4);
        switch(i){
        case 0:
            makeInt(sb);
            sb.append(" ");
            break;
        case 1:
            makeDouble(sb);
            sb.append(" ");
            break;
        case 2:
            sb.append("- ");
            break;
        case 3:
            Mess mess= new Mess();
            sb.append(mess.getCommand());
            break;
        }
        return sb.toString();
    }

    private void makeDouble(StringBuffer sb) {
        Random r = new Random();
        int i = r.nextInt(2);
        switch(i){
        case 0:
            sb.append("0.0");
            break;
        case 1:
            sb.append(roundToDecimals(r.nextDouble() * Double.MAX_VALUE, r.nextInt(20)));
            break;
        }
        return;
    }

    private void makeInt(StringBuffer sb) {
        Random r = new Random();
        int i = r.nextInt(5);
        switch(i){
        case 0:
        case 1:
        case 2:
            sb.append("" + i);
            break;
        case 3:
            sb.append(Integer.MAX_VALUE);
            break;
        case 4:
            sb.append(r.nextInt());
            break;
        }
        return;
    }

    public double roundToDecimals(double d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(d);
        bd = bd.setScale(decimalPlace,BigDecimal.ROUND_UP);
        d = bd.doubleValue();
        return d;
    }

}
