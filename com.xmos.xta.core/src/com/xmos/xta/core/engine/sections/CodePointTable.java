package com.xmos.xta.core.engine.sections;

import com.xmos.libs.util.CoreId;
import com.xmos.libs.util.IteratableSet;
import com.xmos.xta.core.engine.Notifier;

public abstract class CodePointTable extends Section {
	protected IteratableSet<CodePointTableInfo> m_locations;

	CodePointTable(CoreId coreId, Notifier notifier) {
		super(coreId, notifier);
		m_locations = new IteratableSet<CodePointTableInfo>();
	}

	IteratableSet< CodePointTableInfo> get() {
		return m_locations;
	}

}
