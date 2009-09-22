package com.xmos.xta.core.engine.sections;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.PatternSyntaxException;

import com.xmos.elfhandler.ElfHandler;
import com.xmos.elfhandler.Symbol;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xeloader.XeLoader;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;

public class Labels extends Section  {	
	private TreeMap<String, Long> m_labels;
	
	Labels(int nodeId, int coreId, Notifier notifier) {
		super(nodeId, coreId, notifier);
		m_labels = new TreeMap<String, Long>(); 
	}
	
	boolean add(CommandSource src, String label, long pc) {
		if (!m_labels.containsKey(label)) {										
			m_labels.put(label, pc);
			return true;
		} else {
			getNotifier().notify(src , Notifier.NOTIFY_ERROR, String.format(
					"label already exists: %s", label));
			return false;
		}		
	}
	
	boolean remove(CommandSource src, String label) {
		List<String> matches = new ArrayList<String>();
		for (String mapEntry : m_labels.keySet()) {
			try {
				if (mapEntry.matches(label)) {
					matches.add(mapEntry);
				}
			} catch (PatternSyntaxException e) {
		    }
		}

		if (matches.size() > 0) {
			for (String toRemove : matches) {
				m_labels.remove(toRemove);
			}
			return true;			
		} else {
			getNotifier().notify(src, Notifier.NOTIFY_ERROR, String.format(
					"unable to remove label: %s", label));
			return false;
		}		
	}
	
	Map<String, Long> get() {
		return m_labels;
	}
	
	boolean load(CommandSource src, XeLoader xeLoader) {
        SymbolTable symbolTable = xeLoader.getSymbolTable(getNodeId(), getCoreId());
        ElfHandler elfHandler = symbolTable.getElfHandler();
        
        for (Symbol symbol : elfHandler.getSymbolsNameKeyed().values()) {
        	if (!symbol.isSpecialSection()) {
            	int sectionIndex = symbol.getShndx();        	
            	if (elfHandler.getSections().get(sectionIndex).isExecutable()) {        		
            		if (symbol.getName().charAt(0) != '.') {
            	        m_labels.put(symbol.getName(), (long)symbol.getValue());
            		}
            	}
        	}
        }
        return true;
	}
}
