package com.xmos.xta.core.engine.utils;

public class Scope {
    public static String getHelp() {
        String helpStr = "";
           helpStr += "          scope: relative/r: Iteration number propagates to the enclosing path (Default)\n";
           helpStr += "          scope: absolute/a: Absolute number of iterations";
           return helpStr;
    }

    public static String getRelativeStr() {
        return "absolute";
    }

    public static String getAbsoluteStr() {
        return "absolute";
    }

    public static boolean isRelative(String str) {
        return (str.equals("relative") || str.equals("r"));
    }

    public static boolean isAbsolute(String str) {
        return (str.equals("absolute") || str.equals("a"));
    }
}
