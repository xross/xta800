package com.xmos.xta.core.engine.sections;

import com.xmos.libs.util.CoreId;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;

public abstract class Section {
	private CoreId m_coreId;
	private Notifier m_notifier;

	Section(CoreId coreId, Notifier notifier) {
		m_coreId = coreId;
		m_notifier = notifier;
	}

	abstract boolean load(CommandSource src, XeLoader xeLoader);

	public CoreId getCoreId() {
		return m_coreId;
	}

	public int getNodeChainIndex() {
		return m_coreId.getNodeChainIndex();
	}

	public int getCoreNumber() {
		return m_coreId.getCoreNumber();
	}

	Notifier getNotifier() {
		return m_notifier;
	}
}
