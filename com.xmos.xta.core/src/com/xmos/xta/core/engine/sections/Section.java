package com.xmos.xta.core.engine.sections;

import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;

public abstract class Section {
	private int m_nodeNumber;
	private int m_coreNumber;
	private Notifier m_notifier;
	
	Section(int nodeNumber, int coreNumber, Notifier notifier) {
		m_nodeNumber = nodeNumber;
		m_coreNumber = coreNumber;
		m_notifier = notifier;
	}

	abstract boolean load(CommandSource src, XeLoader xeLoader);
	
	int getNodeNumber() {
		return m_nodeNumber;
	}

	int getCoreNumber() {
		return m_coreNumber;
	}
	
	Notifier getNotifier() {
		return m_notifier;
	}	
}
