package com.xmos.xta.core.engine.sections;

import java.io.File;
import java.util.Map;

import com.xmos.libs.util.ByteArrayManipulation;
import com.xmos.libs.util.CoreId;
import com.xmos.libs.util.IteratableMap;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;

public class FileTable extends Section {
	private String m_sectionName;
	private Map<Long, File> m_files;
	private File m_compilationDir;
	
	private static final boolean Debug = false;
	FileTable(CoreId coreId, String sectionName, Notifier notifier) {
		super(coreId, notifier);
		m_sectionName = sectionName;
		m_files = new IteratableMap<Long, File>();
	}

	Map<Long, File> get() {
		return m_files;
	}
	
	boolean load(CommandSource src, XeLoader xeLoader) {
		
		byte[] data = xeLoader.getSectionData(m_sectionName, getCoreId());
		try {
			int byteIndex = 0;
	        while (byteIndex < data.length) {
	        	
	        	int size = ByteArrayManipulation.readInt(data, byteIndex);
	        	byteIndex += 4;
	        	
	        	//int version = 
	        	ByteArrayManipulation.readInt(data, byteIndex);
	        	byteIndex += 4;
	        	int tableEnd = byteIndex + size;
	           	while (byteIndex < tableEnd-4) {
	           		long address = byteIndex;
	        		String fileName = ByteArrayManipulation.readString(data, byteIndex);
	        		byteIndex += fileName.length() + 1;
	        		if(Debug) System.out.println(address + "\t" + fileName);
	        		m_files.put(address, new File(fileName));
	        	}
		    }
        } catch (Exception e) {
			getNotifier().notify(src, Notifier.NOTIFY_ERROR, String.format(
					"problem reading %s section while loading executable", m_sectionName));
        	return false;
        }
        
        String compDir = "";
        //now figure out the compilation directory
        for(long l : m_files.keySet()){
        	File f = m_files.get(l);
        	if(compDir.length() == 0){
        		compDir = f.getAbsolutePath();
        		compDir = compDir.substring(0, compDir.length() - f.getName().length());
        	} else {
        		String s = f.getAbsolutePath();
        		int i=0;
        		while(i < s.length() && i <compDir.length()){
        			if(!s.substring(0, i).equals(compDir.substring(0, i))){
        				break;
        			}
        			i++;
        		}
        		compDir = compDir.substring(0, i);
        	}
        }
        
        m_compilationDir = new File(compDir);
        return true;
	}
	public File getCompilationDir() {
		return m_compilationDir;
	}
	public Map<Long, File> getFileMap() {
		return m_files;
	}
	
}
