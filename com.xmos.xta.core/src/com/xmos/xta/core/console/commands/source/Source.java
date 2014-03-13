package com.xmos.xta.core.console.commands.source;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import org.python.core.PyList;
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

import com.xmos.libs.core.utils.StringUtil;
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

	private enum ScriptType { XTA, JYTHON, }
	
	private String m_fileName;
	private ScriptType m_scriptType;
	private String[] m_args;
	

	public Source() {
		super(COMMAND_NAMES);
		m_fileName = "";
		m_scriptType = ScriptType.XTA;
		m_args = new String[]{};
	}

	public Source(String fileName, ScriptType scriptType, String[] args) {
		super(COMMAND_NAMES);
		m_fileName = fileName;
		m_scriptType = scriptType;
		m_args = args;
	}

    public int numArgs() {
    	
    	switch (m_scriptType) {
    	case JYTHON: return m_args.length;    		
    	case XTA:    return 1;    		
    	default:
            assert(false);
            return 0;
    	}    	    
    }

	public ArgType[] getArgTypes() {
		return new Command.ArgType[] {ArgType.FILE};
	}

    public String commandLine() {
    	switch (m_scriptType) {
    	case JYTHON:
        	String commandLine = name();
        	for (String arg : m_args) {
        		commandLine += " \"" + arg + "\"";
        	}
        	return commandLine; 
            
    	case XTA:    		
    		return name() + " \"" + m_fileName + "\"";
    		
    	default:
            assert(false);
            return null;
    	}    	
    }

    public String description(Engine engine, boolean detailed) {
        return name() + " <file name> [args]: Sources the given script file";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length)
			return null;

    	String fileName = expandTilde(args[index + 1]);
    	ScriptType scriptType = getScriptType(fileName);
    	
    	switch (scriptType) {
    	case JYTHON:
    		Collection<String> scriptArgs = getScriptArgs(args, index);
            return new Source(fileName, scriptType, scriptArgs.toArray(new String[]{}));
            
    	case XTA:
    		return new Source(fileName, scriptType, new String[]{});
    		
    	default: 
    		assert(false);
    		return null;
    	}
    }

    public void execute(Shell shell, Engine engine, Console console) {
    	switch (m_scriptType) {
    	case JYTHON:
    		sourceJythonScript(shell, engine, console);
    		break;
            
    	case XTA:
    		sourceXtaScript(shell, engine, console);
    		break;
    	}
    }
    
    private void sourceJythonScript(Shell shell, Engine engine, Console console) {
    	Properties postProperties = new Properties();
    	Properties systemProperties = System.getProperties();    	

    	String pythonHome = System.getenv("PYTHON_HOME");
    	if (pythonHome == null) {
    		engine.notify(getSrc(), Notifier.NOTIFY_ERROR, "PYTHON_HOME environment variable not set");
    		return;    		
    	}
    	
    	String pythonVerbose = System.getenv("PYTHON_VERBOSE");  
    	if (pythonVerbose == null) {
    		engine.notify(getSrc(), Notifier.NOTIFY_ERROR, "PYTHON_VERBOSE environment variable not set");
    		return;    		
    	}

    	systemProperties.setProperty("python.home", pythonHome);
    	systemProperties.setProperty("python.verbose", pythonVerbose);

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
                            
                            String[] tokens = StringUtil.extractTokens(commandString);

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
                
            } catch (IOException e) {
       			engine.notify(getSrc(), Notifier.NOTIFY_ERROR, String.format(
       					"problem reading the file: %s", m_fileName));
            }
        } catch (IOException e) {
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
    
    private ScriptType getScriptType(String fileName) {
    	String extension = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
    	if (extension.equals("py")) {   
    		return ScriptType.JYTHON;
    	} else {
    		return ScriptType.XTA;
    	}
    }
    
    private Collection<String> getScriptArgs(String[] args, int index) {  		
        Collection<String> scriptArgs = new ArrayList<String>();
	    int i = 1;
	    while (!areArgsDone(args, index + i)) {
		    scriptArgs.add(args[index + i]);
		    ++i;
	    }    	
        return scriptArgs;
    }
}
