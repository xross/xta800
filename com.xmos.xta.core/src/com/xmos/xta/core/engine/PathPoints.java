package com.xmos.xta.core.engine;

import java.util.Map;

import com.xmos.libs.util.IteratableMap;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.core.engine.utils.NodeCorePair;

public class PathPoints {
	private Engine m_engine;
	private String m_fromName;
	private String m_toName;	
	private Map<NodeCorePair, Reference> m_froms;
	private Map<NodeCorePair, Reference> m_tos;

	PathPoints(Engine engine) {
		m_engine = engine;
		m_fromName = "";
		m_toName = "";
		m_froms = new IteratableMap<NodeCorePair, Reference>();
		m_tos = new IteratableMap<NodeCorePair, Reference>();		
	}

	public boolean setFrom(CommandSource src, String ref) {
		m_fromName = ref;				
		if (!update(ref, m_froms) && !ref.equals("")) {
			m_engine.notify(src, Notifier.NOTIFY_ERROR, String.format("'%s' is not a valid endpoint on any active core", ref));
			return false;
		}
		m_engine.notify(src, Notifier.NOTIFY_FROM_END_POINT_SET, "");
		return true;
	}

	public boolean setTo(CommandSource src, String ref) {
		m_toName = ref;
		if (!update(ref, m_tos) && !ref.equals("")) {
			m_engine.notify(src, Notifier.NOTIFY_ERROR, String.format("'%s' is not a valid endpoint on any active core", ref));
			return false;
		}
		m_engine.notify(src, Notifier.NOTIFY_FROM_END_POINT_SET, "");
		return true;		
	}

	public Map<NodeCorePair, Reference> getFroms() {
		return m_froms;
	}

	public Reference getFrom(NodeCorePair core) {
		return m_froms.get(core);				
	}
	
	public Map<NodeCorePair, Reference> getTos() {
		return m_tos;
	}

	public Reference getTo(NodeCorePair core) {
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

	private boolean update(String refStr, Map<NodeCorePair, Reference> endpoints) {
		endpoints.clear();

		boolean hit = false;		
		for (NodeCorePair activeCore : m_engine.getActiveCores()) {
			Reference reference = ReferenceFactory.createReference(refStr, 
					ReferenceClass.ENDPOINT_REF, activeCore.getNode(), activeCore.getCore());

			if (reference.resolve(m_engine)) {
				endpoints.put(activeCore, reference);
				hit = true;
			}
		}
		return hit;
	}
}
