package com.xmos.xta.core.engine.scripter;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceType;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.core.engine.sections.ScriptableInfo;

public class ScripterReference implements Comparable<ScripterReference> {
	private boolean m_isEnabled;
	private boolean m_isModifiable;
	private ReferenceType m_baseRef;
	private Reference m_mapsTo;
	private String m_mapsToLabel;

	public ScripterReference(Scripter scripter, Engine engine, ReferenceType baseRef) {
		m_isEnabled = true;
		m_isModifiable = true;
		m_baseRef = baseRef;

		if (baseRef.isPortable()) {
			initialise(baseRef.getRef(), ReferenceClass.ANY_REF, baseRef.getRef(), false);

		} else {
			tryHighestLevelReference(scripter, engine, baseRef);
			if (m_mapsTo == null) {
				// Failed to find/create a valid reference to handle this
				initialise(baseRef.getRef(), ReferenceClass.ANY_REF, baseRef.getRef(), false);
			}
		}
	}

	public int compareTo(ScripterReference o) {
		return m_baseRef.toString().compareTo(o.m_baseRef.toString());
	}

	public boolean equals(ScripterReference o) {
		return m_baseRef.equals(o.m_baseRef);
	}

	public void setModifiable(boolean isModifiable) {
		m_isModifiable = isModifiable;
	}
	
	public boolean isModifiable() {
    	return m_isModifiable;
    }

	public ReferenceType getBaseReference() {
    	return m_baseRef;
    }

	public Reference getMapsTo() {
    	return m_mapsTo;
    }

	public String getMapsToLabel() {
    	return m_mapsToLabel;
    }

	public void setEnable(boolean isEnabled) {
		m_isEnabled = isEnabled;
	}

	public boolean isEnabled() {
		return m_isEnabled;
	}

	public void rename(String name) {
		m_mapsToLabel = name;
	}

	public String getReferenceString() {
    	if (isEnabled()) {
    		return getMapsToLabel();
    		
    	} else {
			return m_baseRef.getRef();
    	}
	}

	private void tryHighestLevelReference(Scripter scripter, Engine engine, ReferenceType baseRef) {

		ReferenceType.Type type = baseRef.getType();
		if (type == ReferenceType.Type.ENDPOINT) {
			tryMatching(scripter, baseRef,
					engine.getEndpoints(m_baseRef.getNodeNumber(), m_baseRef.getCoreNumber()),
					engine.getEndpointTable(m_baseRef.getNodeNumber(), m_baseRef.getCoreNumber()).values(),
					ReferenceClass.ENDPOINT_REF);

		} else {
			boolean matched = tryMatching(scripter, baseRef,
					engine.getSrcLabels(m_baseRef.getNodeNumber(), m_baseRef.getCoreNumber()),
					engine.getSrcLabelTable(m_baseRef.getNodeNumber(), m_baseRef.getCoreNumber()).values(),
					ReferenceClass.LABEL_REF);

			if (!matched) {
				tryLabels(engine, scripter, baseRef);
			}
		}
	}

	private boolean tryMatching(Scripter scripter, ReferenceType baseRef,
			Collection<? extends ScriptableInfo> infos,
			Collection<? extends ScriptableInfo> tableInfos,
			ReferenceClass refClass) {

		// Try the user-defined
		Set<String> names = getNames(infos);
		for (String name : names) {
			Set<Long> endPointPcs = getPcs(infos, name);
			if (endPointPcs.equals(baseRef.getPcs())) {
				initialise(name, refClass, name, false);
				return true;
			}
		}

		// Try the ones from the compiler
		names = getNames(tableInfos);
		for (String name : names) {
			Set<Long> endPointPcs = getPcs(tableInfos, name);
			if (endPointPcs.equals(baseRef.getPcs())) {
				initialise(name, refClass, scripter.getUniqueLabel(), true);
				return true;
			}
		}
		return false;
	}

	private void tryLabels(Engine engine, Scripter scripter, ReferenceType baseRef) {
		Map<String, Set<Long>> labels = engine.getLabels(m_baseRef.getNodeNumber(), m_baseRef.getCoreNumber());
		for (String label : labels.keySet()) {
			if (labels.get(label).equals(baseRef.getPcs())) {
				initialise(label, ReferenceClass.LABEL_REF, label, false);
				return;
			}
		}
	}

	private Set<String> getNames(Collection<? extends ScriptableInfo> infos) {
		Set<String> names = new TreeSet<String>();
		for (ScriptableInfo info : infos) {
			names.add(info.toString());
		}
		return names;
	}

	private Set<Long> getPcs(Collection<? extends ScriptableInfo> infos, String name) {
		Set<Long> pcs = new TreeSet<Long>();
		for (ScriptableInfo info : infos) {
			if (info.toString().equals(name)) {
				pcs.add(info.getPc());
			}
		}
		return pcs;
	}

	private void initialise(String mapsToLabel, ReferenceClass mapsToClass, String label, boolean isModifiable) {
		m_mapsTo = ReferenceFactory.createReference(mapsToLabel, mapsToClass,
				m_baseRef.getNodeNumber(), m_baseRef.getCoreNumber());

		m_mapsToLabel = label;
		m_isModifiable = isModifiable;
	}
}
