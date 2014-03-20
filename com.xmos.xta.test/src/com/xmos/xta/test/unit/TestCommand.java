package com.xmos.xta.test.unit;
import junit.framework.TestCase;

import com.xmos.xta.core.console.commands.Command;

public class TestCommand extends TestCase {

    public void testExpandTilde() throws Exception {
        String ignore = "/~/hello/~/world";
        String initial = "~/hello/~/world";
        String replaced = System.getenv("HOME") + "/hello/~/world";
        assertEquals("subsequent '~' must be ignored", ignore, Command.expandTilde(ignore));
        String os = System.setProperty("os.name","Windows");
        assertEquals("none linux ignores '~'", initial, Command.expandTilde(initial));
        System.setProperty("os.name","Linuxxxxxx");
        // Shame there is no System.setEnv()
        assertEquals("linux replaces '~'", replaced, Command.expandTilde(initial));
        System.setProperty("os.name","...OS X...");
        assertEquals("OS X replaces '~'", replaced, Command.expandTilde(initial));
        System.setProperty("os.name",os);
    }

}
