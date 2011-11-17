package com.xmos.xta.core.engine.sections;

import java.util.Map;

import com.xmos.libs.util.CoreId;
import com.xmos.libs.util.IteratableMap;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;

public abstract class CodePointTable extends Section {
	protected Map<Long, CodePointTableInfo> m_locations;

	CodePointTable(CoreId coreId, Notifier notifier) {
		super(coreId, notifier);
		m_locations = new IteratableMap<Long, CodePointTableInfo>();
	}

	Map<Long, CodePointTableInfo> get() {
		return m_locations;
	}
	
	abstract boolean load(CommandSource src, XeLoader xeLoader);

	
}
