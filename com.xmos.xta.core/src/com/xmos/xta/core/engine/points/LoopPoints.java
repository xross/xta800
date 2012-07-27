package com.xmos.xta.core.engine.points;

import java.util.Map;

import com.xmos.libs.core.utils.CoreId;
import com.xmos.libs.core.utils.IteratableMap;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;

public class LoopPoints extends Points {
	private String m_refString;	
	private Map<CoreId, Reference> m_points;

	public LoopPoints(Engine engine) {
		super(engine, ReferenceClass.ANY);
		m_refString = "";
		m_points = new IteratableMap<CoreId, Reference>();		
	}

	public boolean setLoop(CommandSource src, String ref) {
		m_refString = ref;				
		if (!update(ref, m_points) && !ref.equals("")) {
			getEngine().notify(src, Notifier.NOTIFY_ERROR, String.format("'%s' is not a valid loop point on any active core", ref));
			return false;
		}
		getEngine().notify(src, Notifier.NOTIFY_LOOP_POINT_SET, "");
		return true;
	}

	public Map<CoreId, Reference> getLoops() {
		return m_points;
	}

	public Reference getLoop(CoreId core) {
		return m_points.get(core);				
	}
		
	public String getRefString() {
		return m_refString;
	}

	public void clear() {
		m_refString = "";
		m_points.clear();
	}

	public void refresh() {
		update(m_refString, m_points);
	}
}
