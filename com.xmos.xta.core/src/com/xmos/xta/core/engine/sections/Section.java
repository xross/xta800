package com.xmos.xta.core.engine.sections;

import com.xmos.xeloader.XeLoader;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;

public abstract class Section {
	private int m_nodeId;
	private int m_coreId;
	private Notifier m_notifier;
	
	Section(int nodeId, int coreId, Notifier notifier) {
		m_nodeId = nodeId;
		m_coreId = coreId;
		m_notifier = notifier;
	}

	abstract boolean load(CommandSource src, XeLoader xeLoader);
	
	int getNodeId() {
		return m_nodeId;
	}

	int getCoreId() {
		return m_coreId;
	}
	
	Notifier getNotifier() {
		return m_notifier;
	}	
}
