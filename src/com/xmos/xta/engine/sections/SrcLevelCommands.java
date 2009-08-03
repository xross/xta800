package com.xmos.xta.engine.sections;

import java.util.ArrayList;
import java.util.Collection;

import com.xmos.util.ByteArrayManipulation;
import com.xmos.xeloader.XeLoader;
import com.xmos.xta.engine.CommandSource;
import com.xmos.xta.engine.Notifier;

public class SrcLevelCommands extends Section {
	private Collection<SrcCommandInfo> m_commands;

	SrcLevelCommands(int nodeId, int coreId, Notifier notifier) {
		super(nodeId, coreId, notifier);
		m_commands = new ArrayList<SrcCommandInfo>(); 
	}
	
	Collection<SrcCommandInfo> get() {
		return m_commands;
	}
	
	boolean load(CommandSource src, XeLoader xeLoader) {
		byte[] data = xeLoader.getSectionData(".xtacommand", getNodeId(), getCoreId());
				
		try {
			int byteIndex = 0;
	        while (byteIndex < data.length) {
	        	int base = byteIndex;
	        	
	        	int size = ByteArrayManipulation.readInt(data, byteIndex);
	        	byteIndex += 4;

	        	// Ignores the version field.
	       		ByteArrayManipulation.readInt(data, byteIndex);
	       		byteIndex += 4;
	        		        		
	       		String compilationDir = ByteArrayManipulation.readString(data, byteIndex);
	       		byteIndex += compilationDir.length() + 1;
	       		
	           	while (byteIndex < (base + size)) {
	        		String commandString = ByteArrayManipulation.readString(data, byteIndex);
	        		byteIndex += commandString.length() + 1;
	        		
	        		String fileName = ByteArrayManipulation.readString(data, byteIndex);
	        		byteIndex += fileName.length() + 1;
	        		
	        		int lineNumber = ByteArrayManipulation.readInt(data, byteIndex);
	        		byteIndex += 4;
	        				
	        		SrcCommandInfo command = new SrcCommandInfo(commandString, compilationDir, fileName, lineNumber);
	        		m_commands.add(command);
	        	}			
		    }
        } catch (Exception e) {
			getNotifier().notify(src, Notifier.NOTIFY_ERROR, String.format(
					"problem reading .xtacommand while loading executable"));
        	return false;
        }
        return true;
	}
}
