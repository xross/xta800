package com.xmos.xta.engine.sections;

import java.util.ArrayList;
import java.util.Collection;

import com.xmos.util.ByteArrayManipulation;
import com.xmos.xeloader.XeLoader;
import com.xmos.xta.engine.Notifier;

public class SrcLevelCommands extends Section {
	private Collection<String> m_commands;

	SrcLevelCommands(int nodeId, int coreId, Notifier notifier) {
		super(nodeId, coreId, notifier);
		m_commands = new ArrayList<String>(); 
	}
	
	Collection<String> get() {
		return m_commands;
	}
	
	boolean load(XeLoader xeLoader) {
		byte[] data = xeLoader.getSectionData(".xtacommand", getNodeId(), getCoreId());
				
		try {
			int byteIndex = 0;
	        while (byteIndex < data.length) {        	
        		String command = ByteArrayManipulation.readString(data, byteIndex);
        		byteIndex += command.length() + 1;
	    		m_commands.add(command);
	        }        
		} catch (Exception e) {
		    getNotifier().notify(Notifier.NOTIFY_ERROR,
			    	"Problem reading .xtacommand section while loading executable");
    	    return false;
        }
        return true;
	}		
}
