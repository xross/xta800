package com.xmos.xta.core.engine.sections;

import com.xmos.libs.core.utils.CoreId;
import com.xmos.libs.core.xeloader.XeLoader;
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

	Notifier getNotifier() {
		return m_notifier;
	}
}
