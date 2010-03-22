package com.xmos.xta.core.engine.sections;

import java.util.Map;

import com.xmos.libs.elfhandler.ElfHandler;
import com.xmos.libs.elfhandler.Symbol;
import com.xmos.libs.util.NonIteratableMap;
import com.xmos.libs.xeloader.SymbolTable;
import com.xmos.libs.xeloader.XeLoader;

public class LabelMap {
	private int m_nodeNumber;
	private int m_coreNumber;
	private NonIteratableMap<String, Long> m_labels;

	LabelMap(int nodeNumber, int coreNumber) {
		m_nodeNumber = nodeNumber;
		m_coreNumber = coreNumber;
		m_labels = new NonIteratableMap<String, Long>();
	}

	int getNodeNumber() {
		return m_nodeNumber;
	}

	int getCoreNumber() {
		return m_coreNumber;
	}

	Map<String, Long> getMap() {
		return m_labels;
	}

	public boolean load(XeLoader xeLoader) {
        SymbolTable symbolTable = xeLoader.getSymbolTable(m_nodeNumber, m_coreNumber);
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
