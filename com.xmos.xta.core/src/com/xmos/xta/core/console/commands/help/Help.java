package com.xmos.xta.core.console.commands.help;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.CommandFactory;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.references.types.ReferenceClassType;
import com.xmos.xta.core.engine.references.types.ReferenceType;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.core.engine.utils.Scope;
import com.xmos.xta.core.engine.utils.TimeUnit;

public class Help extends Command {
    private static final String[] COMMAND_NAMES = { "help" };

    public static final String[] HELP_OPTIONS = { "MODE", "SCOPE" };

    private Command m_command;
    private Command m_subCommand;
    private ReferenceClass m_referenceClass;
    private String m_option;

    public Help() {
        super(COMMAND_NAMES);
        initialise(null, null, null, null);
    }

    public Help(Command command) {
        super(COMMAND_NAMES);
        initialise(command, null, null, null);
    }

    public Help(Command command, Command subCommand) {
        super(COMMAND_NAMES);
        initialise(command, subCommand, null, null);
    }

    public Help(ReferenceClass referenceClass) {
        super(COMMAND_NAMES);
        initialise(null, null, referenceClass, null);
    }

    public Help(String option) {
        super(COMMAND_NAMES);
        initialise(null, null, null, option);
    }

    public int numArgs() {
        if ((m_command != null) && (m_subCommand != null)) {
            return 2;
        }
        if (m_command != null) {
            return 1;
        }
        if (m_referenceClass != null) {
            return 1;
        }
        if (m_option != null) {
            return 1;
        }
           return 0;
    }

    public ArgType[] getArgTypes() {
        return new Command.ArgType[] {ArgType.HELP, ArgType.HELPCOMMAND};
    }

    public String commandLine() {
        if ((m_command != null) && (m_subCommand != null)) {
            return name() + " " + m_command.name() + " " + m_subCommand.name();
        }
        if (m_command != null) {
            return name() + " " + m_command.name();
        }
        if (m_referenceClass != null) {
            return name() + " " + m_referenceClass.name();
        }
        if (m_option != null) {
            return name() + " " + m_option;
        }
           return name();
    }

    public String description(Engine engine, boolean detailed) {
        StringBuffer desc = new StringBuffer();
        desc.append(name());
        desc.append(" [command|command subcommand|option]: Displays help message for the given arguments");

        if (detailed) {
            desc.append("\n      options: ");
            for (ReferenceClass referenceClass : ReferenceClass.values()) {
                if (ReferenceTypeFactory.getReferenceClass(referenceClass).isVisible()) {
                    desc.append(String.format("%s, ", referenceClass.name()));
                }
            }
            for (String option : HELP_OPTIONS) {
                desc.append(String.format("%s, ", option));
            }
            desc.setLength(desc.length() - 2);
        }
        return desc.toString();
    }

    public Command clone(String[] args, int index) {
         if (areArgsDone(args, index + 1)) {
            return new Help();
        }

        String arg = args[index + 1];
        // Checks for matching commands..
        Command[] avaliableCommands = CommandFactory.getCommands();
        for (Command command : avaliableCommands) {
            if (command.name().equals(arg)) {
                if (areArgsDone(args, index + 2)) {
                    return new Help(command);
                }

                String subArg = args[index + 2];
                for (Command subCommand : command.getSubCommands()) {
                    if (subCommand.name().equals(subArg)) {
                        return new Help(command, subCommand);
                    }
                }
            }
        }

        // Checks for matching reference classes..
        for (ReferenceClass referenceClass : ReferenceClass.values()) {
            if (referenceClass.name().equals(arg)) {
                return new Help(referenceClass);
            }
        }

        // Checks for matching any other option..
        for (String option : HELP_OPTIONS) {
            if (arg.equals(option)) {
                return new Help(option);
            }
        }

        // Error..
        return null;
    }

    public void execute(Shell shell, Engine engine, Console console) {
        if ((m_command != null) && (m_subCommand != null)) {
            // Displays help for a specific command + sub-command.
            console.display(String.format("  [-]%s %s\n", m_command.name(),
                    m_subCommand.description(engine, true)));

        } else if (m_command != null) {
            // Displays help for a specific command.
            console.display(String.format("  [-]%s\n", m_command.description(engine, true)));

        } else if (m_referenceClass != null) {
            // Displays help for a reference class.
            console.display("  Any of the following types are permitted in this reference class:\n");

            ReferenceClassType classType = ReferenceTypeFactory.getReferenceClass(m_referenceClass);
            ReferenceType[] referenceTypes = classType.getReferenceTypes();
            for (ReferenceType referenceType : referenceTypes) {
                console.display(String.format("    %s: %s\n",
                        referenceType.name(), referenceType.description()));
            }
        } else if (m_option != null) {
            // Displays the help for any other option.
            if (m_option.equals("MODE")) {
                console.display(String.format("%s\n", TimeUnit.getHelp()));

            } else if (m_option.equals("SCOPE")) {
                console.display(String.format("%s\n", Scope.getHelp()));

            } else {
                assert(false);
            }

        } else {
            // Displays help for all commands.
            Command[] avaliableCommands = CommandFactory.getCommands();
            for (Command command : avaliableCommands) {
                console.display(String.format("  [-]%s\n", command.description(engine, false)));
            }
        }
    }

    private void initialise(Command command, Command subCommand,
            ReferenceClass referenceClass, String option) {
        m_command = command;
        m_subCommand = subCommand;
        m_referenceClass = referenceClass;
        m_option = option;
    }
}
