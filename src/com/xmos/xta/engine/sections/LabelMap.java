package com.xmos.xta.engine.sections;

import java.util.Map;
import java.util.TreeMap;

import com.xmos.elfhandler.ElfHandler;
import com.xmos.elfhandler.Symbol;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xeloader.XeLoader;

public class LabelMap {	
	private int m_nodeId;
	private int m_coreId;
	private TreeMap<String, Integer> m_labels;
	
	LabelMap(int nodeId, int coreId) {
		m_nodeId = nodeId;
		m_coreId = coreId;
		m_labels = new TreeMap<String, Integer>(); 
	}
	
	int getNodeId() {
		return m_nodeId;
	}

	int getCoreId() {
		return m_coreId;
	}
	
	Map<String, Integer> getMap() {
		return m_labels;
	}
	
	boolean load(XeLoader xeLoader) {
        SymbolTable symbolTable = xeLoader.getSymbolTable(m_nodeId, m_coreId);
        ElfHandler elfHandler = symbolTable.getElfHandler();
        
        for (Symbol symbol : elfHandler.getSymbolsNameKeyed().values()) {
        	if (!symbol.isSpecialSection()) {
            	int sectionIndex = symbol.getShndx();        	
            	if (elfHandler.getSections().get(sectionIndex).isExecutable()) {        		
            		if (symbol.getName().charAt(0) != '.') {
            	        m_labels.put(symbol.getName(), symbol.getValue());
            		}
            	}
        	}
        }
        return true;
	}
}
