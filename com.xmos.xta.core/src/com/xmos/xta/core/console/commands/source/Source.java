package com.xmos.xta.core.console.commands.source;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.python.util.PythonInterpreter;
import org.python.core.PyList;
import org.python.core.PyString;
import org.python.core.PySystemState;

import com.xmos.libs.util.StringUtil;
import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.CommandFactory;
import com.xmos.xta.core.console.commands.Invalid;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.CommandSource.Type;

public class Source extends Command {
	private static final String[] COMMAND_NAMES = { "source" };

	private String m_fileName;
	private String[] m_args;

	public Source() {
		super(COMMAND_NAMES);
		m_fileName = "";
		m_args = new String[]{};
	}

	public Source(String fileName, String[] args) {
		super(COMMAND_NAMES);
		m_fileName = fileName;
		m_args = args;
	}

    public int numArgs() {
    	return 1 + (m_args.length - 1);
    }

	public ArgType[] getArgTypes() {
		return new Command.ArgType[] {ArgType.FILE};
	}

    public String commandLine() {
    	String commandLine = name();
    	for (String arg : m_args) {
    		commandLine += " \"" + arg + "\"";
    	}
    	return commandLine; 
    }

    public String description(Engine engine, boolean detailed) {
        return name() + " <file name> [args]: Sources the given script file";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length)
			return null;

    	String fileName = args[index + 1];
		Collection<String> scriptArgs = new ArrayList<String>();
		int i = 1;
		while (!areArgsDone(args, index + i)) {
			scriptArgs.add(args[index + i]);
			++i;
		}    	
        return new Source(fileName, scriptArgs.toArray(new String[]{}));
    }

    public void execute(Shell shell, Engine engine, Console console) {
    	String extension = m_fileName.substring(m_fileName.lastIndexOf('.') + 1, m_fileName.length());
    	if (extension.equals("py")) {
    		sourceJythonScript(shell, engine, console);
    		
    	} else {
    		sourceXtaScript(shell, engine, console);
    	}
    }
    
    private void sourceJythonScript(Shell shell, Engine engine, Console console) {
    	Properties postProperties = new Properties();
    	Properties systemProperties = System.getProperties();
    	
    	if (systemProperties.getProperty("python.home") == null) {
    		engine.notify(getSrc(), Notifier.NOTIFY_ERROR, "python.home property not set");
    		return;
    	}    	
	    PythonInterpreter.initialize(systemProperties, postProperties, new String[] {""});        
        PythonInterpreter interp = getPythonInterpreter(m_args);

        interp.setOut(new ScriptConsoleWriter(console));
        interp.setErr(new ScriptConsoleWriter(console));
        
        interp.set(ScriptInterface.XTA_OBJECT_REFERENCE, new ScriptInterface(shell, engine, console, m_fileName));        
        try {
            interp.execfile(m_fileName);
        } catch (Exception e) {
        	console.display(e.toString() + "\n");
        }
    }
    
    private void sourceXtaScript(Shell shell, Engine engine, Console console) {    	    
        try {
            BufferedReader fp = new BufferedReader(new FileReader(m_fileName));
            int location = shell.getLocation(this);
            try {
            	int lineNumber = 1;
                String line;
                String commandString = "";
                boolean multilineCommand = false;

                while ((line = fp.readLine()) != null) {
                    line = line.trim();
                    if (line.length() > 0) {

                        if (line.startsWith("#")) {
                        	// Ignore comment lines...

                        } else if (line.endsWith("\\")) {
                        	// Handles the line continuation character...
                        	commandString += " " + line.substring(0, line.length() - 2).trim();
                        	multilineCommand = true;

                        } else {
                            if (multilineCommand) {
                            	commandString += " " + line;
                            } else {
                            	commandString = line;
                            }
                            multilineCommand = false;

                            String[] tokens = commandString.trim().split("\\s+");
                            ArrayList<String> tokenGroups = StringUtil.groupQuotedTokens(tokens);
                            if (tokenGroups.size() > 0) {
                                tokens = tokenGroups.toArray(new String[0]);

	                    		CommandSource src = new CommandSource(Type.FILE, m_fileName, lineNumber);
	                            Command command = CommandFactory.buildCommand(src,
	                            		tokens, 0, shell.getCommandHistory(), console);

	                            boolean invalidCommand = false;
	                            if (command != null) {
	                            	if (command.getFileRedirect() != null) {
	                            		invalidCommand = ((command.numArgs() + 2) != (tokens.length - 1));
	                            	} else {
	                            		invalidCommand = (command.numArgs() != (tokens.length - 1));
	                            	}
	                                if (!invalidCommand) {
	                            	    shell.addCommand(command, location + 1);
	                            	    location++;
	                                }
	                            } else {
	                            	invalidCommand = true;
	                            }

	                            if (invalidCommand) {
	                            	String errorMessage = String.format("invalid command");
	                            	shell.addCommand(new Invalid(commandString, errorMessage, src), location+1);
	                            	location++;
	                            }
	                            commandString = "";
                            }
                    	}
                    }
                    ++lineNumber;
                }
                fp.close();
            }
            catch (IOException e) {
       			engine.notify(getSrc(), Notifier.NOTIFY_ERROR, String.format(
       					"problem reading the file: %s", m_fileName));
            }
        }
        catch (IOException e) {
   			engine.notify(getSrc(), Notifier.NOTIFY_ERROR, String.format(
   					"unable to open file for reading: %s", m_fileName));
        }
    }
    
    private PythonInterpreter getPythonInterpreter(String[] args) {
        PySystemState pySystemState = new PySystemState();        
       	PyList argv = new PyList();
        for (String arg : args) {
            argv.append(new PyString(arg));
        }
        pySystemState.argv = argv;        
        return new PythonInterpreter(null, pySystemState);    	
    }    
}
