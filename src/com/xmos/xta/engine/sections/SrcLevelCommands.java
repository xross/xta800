package com.xmos.xta.engine.sections;

import java.util.ArrayList;
import java.util.Collection;

import com.xmos.util.ByteArrayManipulation;
import com.xmos.xeloader.XeLoader;
import com.xmos.xta.engine.Notifier;

public class SrcLevelCommands {
	private static final boolean DEBUG = false;
	
	private int m_nodeId;
	private int m_coreId;
	private Notifier m_notifier;	
	private Collection<String> m_commands;

	SrcLevelCommands(int nodeId, int coreId, Notifier notifier) {
		m_nodeId = nodeId;
		m_coreId = coreId;
		m_notifier = notifier;
		m_commands = new ArrayList<String>(); 
	}
	
	int getNodeId() {
		return m_nodeId;
	}

	int getCoreId() {
		return m_coreId;
	}
	
	Collection<String> getCommands() {
		return m_commands;
	}
	
	boolean load(XeLoader xeLoader) {
		byte[] data = xeLoader.getSectionData(
				".xtacommand", m_nodeId, m_coreId);
				
		if (DEBUG) System.out.printf(".xtacommand is of size %d bytes for node: %d, core: %d\n",
				data.length, m_nodeId, m_coreId);		

		try {
			int byteIndex = 0;
	        while (byteIndex < data.length) {        	
        		String command = ByteArrayManipulation.readString(data, byteIndex);
        		byteIndex += command.length() + 1;

	    		if (DEBUG) System.out.printf("- command: %s\n", command);        	
	    		m_commands.add(command);
	        }        
		} catch (Exception e) {
		    m_notifier.notify(Notifier.NOTIFY_ERROR,
			    	"Problem reading .xtacommand section while loading executable");
    	    return false;
        }
        return true;
	}		
}
