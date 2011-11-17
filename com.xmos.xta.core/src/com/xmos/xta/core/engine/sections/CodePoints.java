package com.xmos.xta.core.engine.sections;

import java.util.ArrayList;
import java.util.List;

import com.xmos.libs.util.CoreId;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;

public abstract class CodePoints extends Section  {
	protected List<CodePointInfo> m_codePoints;
	
	CodePoints(CoreId coreId, Notifier notifier) {
		super(coreId, notifier);
		m_codePoints = new ArrayList<CodePointInfo>();
	}

	List<CodePointInfo> get() {
		return m_codePoints;
	}
	
	abstract boolean load(CommandSource src, XeLoader xeLoader);
}
