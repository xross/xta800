package com.xmos.xta.core.engine.sections;

import java.util.Map;
import java.util.Set;

import com.xmos.libs.elfhandler.ElfHandler;
import com.xmos.libs.elfhandler.Symbol;
import com.xmos.libs.util.CoreId;
import com.xmos.libs.util.IteratableMap;
import com.xmos.libs.util.IteratableSet;
import com.xmos.libs.xeloader.SymbolTable;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;

public class Labels extends Section  {
	private Map<String, Set<Long>> m_labels;

	Labels(CoreId coreId, Notifier notifier) {
		super(coreId, notifier);
		m_labels = new IteratableMap<String, Set<Long>>();
	}

	boolean add(CommandSource src, String label, long pc) {
		Set<Long> pcs = m_labels.get(label);
		if (pcs == null) {
			pcs = new IteratableSet<Long>();
			m_labels.put(label, pcs);
		}
		pcs.add(pc);
		return true;
	}

	Map<String, Set<Long>> get() {
		return m_labels;
	}

	boolean load(CommandSource src, XeLoader xeLoader) {
        SymbolTable symbolTable = xeLoader.getSymbolTable(getNodeChainIndex(), getCoreNumber());
        ElfHandler elfHandler = symbolTable.getElfHandler();

        for (Symbol symbol : elfHandler.getSymbolsNameKeyed().values()) {
        	if (!symbol.isSpecialSection()) {
            	int sectionIndex = symbol.getShndx();
            	if (elfHandler.getSections().get(sectionIndex).isExecutable()) {
            		if (symbol.getName().charAt(0) != '.') {
            			add(src, symbol.getName(), symbol.getValue());
            		}
            	}
        	}
        }
        return true;
	}
}
