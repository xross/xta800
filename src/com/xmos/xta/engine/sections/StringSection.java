package com.xmos.xta.engine.sections;

import java.util.ArrayList;
import java.util.Collection;

import com.xmos.xeloader.XeLoader;
import com.xmos.xta.engine.CommandSource;
import com.xmos.xta.engine.Notifier;

public class StringSection extends Section {
	private Collection<String> m_strings;

	StringSection(int nodeId, int coreId, Notifier notifier) {
		super(nodeId, coreId, notifier);
		m_strings = new ArrayList<String>(); 
	}
	
	Collection<String> get() {
		return m_strings;
	}
	
	boolean load(CommandSource src, XeLoader xeLoader) {
        return true;
	}
}
