package com.xmos.xta.core.engine.points;

import java.util.Map;

import com.xmos.libs.core.utils.CoreId;
import com.xmos.libs.core.utils.IteratableMap;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;

public class PathPoints extends Points {
	private String m_fromName;
	private String m_toName;	
	private Map<CoreId, Reference> m_froms;
	private Map<CoreId, Reference> m_tos;

	public PathPoints(Engine engine) {
		super(engine, ReferenceClass.ENDPOINT);
		m_fromName = "";
		m_toName = "";
		m_froms = new IteratableMap<CoreId, Reference>();
		m_tos = new IteratableMap<CoreId, Reference>();		
	}

	public boolean setFrom(CommandSource src, String ref) {
		m_fromName = ref;				
		if (!update(ref, m_froms) && !ref.equals("")) {
			getEngine().notify(src, Notifier.NOTIFY_ERROR, String.format("'%s' is not a valid endpoint on any active core", ref));
			return false;
		}
		getEngine().notify(src, Notifier.NOTIFY_FROM_END_POINT_SET, "");
		return true;
	}

	public boolean setTo(CommandSource src, String ref) {
		m_toName = ref;
		if (!update(ref, m_tos) && !ref.equals("")) {
			getEngine().notify(src, Notifier.NOTIFY_ERROR, String.format("'%s' is not a valid endpoint on any active core", ref));
			return false;
		}
		getEngine().notify(src, Notifier.NOTIFY_FROM_END_POINT_SET, "");
		return true;		
	}

	public Map<CoreId, Reference> getFroms() {
		return m_froms;
	}

	public Reference getFrom(CoreId core) {
		return m_froms.get(core);				
	}
	
	public Map<CoreId, Reference> getTos() {
		return m_tos;
	}

	public Reference getTo(CoreId core) {
		return m_tos.get(core);				
	}
	
	public String getFromName() {
		return m_fromName;
	}

	public String getToName() {
		return m_toName;
	}

	public void clear() {
		m_fromName = "";
		m_toName = "";
		m_froms.clear();
		m_tos.clear();		
	}

	public void refresh() {
		update(m_fromName, m_froms);
		update(m_toName, m_tos);		
	}
}
