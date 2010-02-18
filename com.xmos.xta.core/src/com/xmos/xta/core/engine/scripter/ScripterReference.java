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
	private Reference m_ref;
	private Reference m_mapsTo;
	private String m_mapsToLabel;

	public ScripterReference(Scripter scripter, Engine engine, Reference ref) {
		m_isEnabled = true;
		m_isModifiable = true;
		m_ref = ref;

		if (ref.isPortable()) {
			initialise(ref, ref.getRef(), false);

		} else {
			tryHighestLevelReference(scripter, engine, ref);
			if (m_mapsTo == null) {
				// Failed to find/create a valid reference to handle this
				initialise(ref, ref.getRef(), false);
			}
		}
	}

	public int compareTo(ScripterReference o) {
		return m_ref.compareTo(o.m_ref);
	}

	public boolean equals(ScripterReference o) {
		return m_ref.equals(o.m_ref);
	}

	public boolean isModifiable() {
    	return m_isModifiable;
    }

	public Reference getReference() {
    	return m_ref;
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

	private void tryHighestLevelReference(Scripter scripter, Engine engine, Reference ref) {

		ReferenceType.Type type = ref.getBase().getType();
		if (type == ReferenceType.Type.ENDPOINT) {
			tryMatching(scripter, ref,
					engine.getEndpoints(),
					engine.getEndpointTable().values(),
					ReferenceClass.ENDPOINT_REF);

		} else {
			boolean matched = tryMatching(scripter, ref,
					engine.getSrcLabels(),
					engine.getSrcLabelTable().values(),
					ReferenceClass.LABEL_REF);

			if (!matched) {
				tryLabels(engine, scripter, ref);
			}
		}
	}

	private boolean tryMatching(Scripter scripter, Reference ref,
			Collection<? extends ScriptableInfo> infos,
			Collection<? extends ScriptableInfo> tableInfos,
			ReferenceClass refClass) {

		// Try the user-defined
		Set<String> names = getNames(infos);
		for (String name : names) {
			Set<Long> endPointPcs = getPcs(infos, name);
			if (endPointPcs.equals(ref.getPcs())) {
				initialise(ReferenceFactory.createReference(name, refClass), name, false);
				return true;
			}
		}

		// Try the ones from the compiler
		names = getNames(tableInfos);
		for (String name : names) {
			Set<Long> endPointPcs = getPcs(tableInfos, name);
			if (endPointPcs.equals(ref.getPcs())) {
				initialise(ReferenceFactory.createReference(name, refClass),
						scripter.getUniqueLabel(), true);
				return true;
			}
		}
		return false;
	}

	private void tryLabels(Engine engine, Scripter scripter, Reference ref) {
		Map<String, Set<Long>> labels = engine.getLabels();
		for (String label : labels.keySet()) {
			if (labels.get(label).equals(ref.getPcs())) {
				initialise(ReferenceFactory.createReference(label, ReferenceClass.LABEL_REF),
						label, false);
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

	private void initialise(Reference ref, String label, boolean isModifiable) {
		m_mapsTo = ref;
		m_mapsToLabel = label;
		m_isModifiable = isModifiable;
	}
}
