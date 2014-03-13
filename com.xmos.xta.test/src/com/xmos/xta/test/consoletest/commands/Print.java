package com.xmos.xta.test.consoletest.commands;

import java.util.Random;

import com.xmos.xta.test.consoletest.ConsoleTest;
import com.xmos.xta.test.consoletest.commands.units.Number;

public class Print implements Command {

    public String getCommand() {
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        sb.append("print ");
        int options = 11;
        if(!ConsoleTest.allowMess){
            options--;
        }
        int i = r.nextInt(options);
        switch(i){
        case 0 :
            addSummary(sb);
            break;
        case 1 :
            addStructure(sb);
            break;
        case 2 :
            addAsm(sb);
            break;
        case 3 :
            addSrc(sb);
            break;
        case 4 :
            addMix(sb);
            break;
        case 5 :
            addTrace(sb);
            break;
        case 6 :
            addRouteInfo(sb);
            break;
        case 7 :
            addNodeInfo(sb);
            break;
        case 8 :
            addWarnings(sb);
            break;
        case 9 :
            addDistribution(sb);
            break;
        case 10 :
            Mess mess = new Mess();
            sb.append(mess.getCommand());
            break;
        }
        return sb.toString();
    }

    private void addNodeInfo(StringBuffer sb) {
        sb.append("nodeinfo ");
        routeNode(sb);
    }

    private void addWarnings(StringBuffer sb) {
        sb.append("warnings ");
    }

    private void addDistribution(StringBuffer sb) {
        sb.append("distribution ");
        routeNode(sb);
    }

    private void addRouteInfo(StringBuffer sb) {
        sb.append("routeinfo ");
        Number n = new Number();
        sb.append(n.getCommand());
    }

    private void addTrace(StringBuffer sb) {
        sb.append("trace ");
        routeNode(sb);
    }

    private void addMix(StringBuffer sb) {
        sb.append("mix ");
        routeNode(sb);
    }

    private void addSrc(StringBuffer sb) {
        sb.append("src ");
        routeNode(sb);
    }

    private void addAsm(StringBuffer sb) {
        sb.append("asm ");
        routeNode(sb);
    }

    private void addStructure(StringBuffer sb) {
        sb.append("structure ");
        routeNode(sb);
    }

    private void routeNode(StringBuffer sb) {
        Random r = new Random();
        Number n = new Number();
        switch(r.nextInt(3)){
        case 0:
            sb.append(n.getCommand());
            break;
        case 1:
            sb.append(n.getCommand());
            sb.append(n.getCommand());
            break;
        case 2:
            Mess mess = new Mess();
            sb.append(mess.getCommand());
            break;
        }
    }

    private void addSummary(StringBuffer sb) {
        sb.append("summary ");
    }

}
