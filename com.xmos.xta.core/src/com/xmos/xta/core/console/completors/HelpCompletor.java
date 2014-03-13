package com.xmos.xta.core.console.completors;

import java.util.List;

import jline.Completor;

import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.CommandFactory;
import com.xmos.xta.core.console.commands.help.Help;
import com.xmos.xta.core.engine.references.types.ReferenceClassType;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;

public class HelpCompletor implements Completor {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public int complete(String buffer, int cursor, List candidates) {
        String start = (buffer == null) ? "" : buffer;

        // Checks for matching top level commands..
        for (Command command : CommandFactory.getCommands()) {
             if (command.name().startsWith(start)) {
                candidates.add(command.name());
               }
        }

         // Checks for matching reference class options..
        for (ReferenceClass referenceClass : ReferenceClass.values()) {
            ReferenceClassType classType = ReferenceTypeFactory.getReferenceClass(referenceClass);
            if (classType.isVisible()) {
                if (referenceClass.name().startsWith(start)) {
                       candidates.add(referenceClass.name());
                }
            }
        }

        // Checks for any other help options..
        for (String helpOption : Help.HELP_OPTIONS) {
            if (helpOption.startsWith(start)) {
                candidates.add(helpOption);
            }
        }
        return 0;
    }
}
