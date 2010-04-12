package com.xmos.xta.core.engine.scripter;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.xmos.libs.util.IteratableSet;
import com.xmos.libs.util.NonIteratableSet;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceType;
import com.xmos.xta.core.engine.references.types.ReferenceType.Type;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.core.engine.sections.ScriptableInfo;
import com.xmos.xta.core.engine.sourcemapper.SourceFile;
import com.xmos.xta.core.engine.sourcemapper.SourceMapper;
import com.xmos.xta.core.engine.utils.CoreId;

public class ScripterReference {
	private Engine m_engine;
	private boolean m_isEnabled;
	private boolean m_isModifiable;
	private ReferenceType m_baseRef;
	private Reference m_mapsTo;
	private String m_mapsToLabel;
	private boolean m_useBaseFileLine;

	public ScripterReference(Scripter scripter, Engine engine, ReferenceType baseRef) {
		m_engine = engine;
		m_isEnabled = true;
		m_isModifiable = false;
		m_baseRef = baseRef;
		m_mapsTo = null;
		m_mapsToLabel = "";
		m_useBaseFileLine = true;

		if (!baseRef.isPortable()) {
			tryHighestLevelReference(scripter, baseRef);
		}
	}

	public void setModifiable(boolean isModifiable) {
		m_isModifiable = isModifiable;
	}
	
	public boolean isModifiable() {
    	return m_isModifiable;
    }

	public String getRef() {
		return m_baseRef.getRef();
	}
	
	public ReferenceType getBaseReference() {
    	return m_baseRef;
    }

	public String getFileName() {
		if (m_useBaseFileLine) {
			return m_baseRef.getFileName();
		} else {
			return m_mapsTo.getFileName();
		}
	}

	public long getLineNum() {
		if (m_useBaseFileLine) {
			return m_baseRef.getLineNum();
		} else {
			return m_mapsTo.getLineNum();
		}
	}

	public boolean isPortable() {
		if (m_mapsTo != null) {
			return m_mapsTo.isPortable();
		} else {
			return m_baseRef.isPortable();
		}
	}

	public String getMapsToLabel() {
		if (m_mapsTo != null) {
			return m_mapsToLabel;
		} else {
			return m_baseRef.getRef();
		}
    }

	public Type getType() {
		if (m_mapsTo != null && m_mapsTo.getResolved()) {
			return m_mapsTo.getBase().getType();
		} else {
			return m_baseRef.getType();
		}
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
			return m_baseRef.getScripterRef();
    	}
	}

	public void sourceLineInserted(SourceFile sourceFile, long lineNumber, SourceMapper sourceMapper) {
		String sourceFileName = sourceFile.getFullFileName();
		m_baseRef.sourceLineInserted(sourceFileName, lineNumber, sourceMapper);
		if (m_mapsTo != null) {
			m_mapsTo.getBase().sourceLineInserted(sourceFileName, lineNumber, sourceMapper);
		}
	}

	private void tryHighestLevelReference(Scripter scripter, ReferenceType baseRef) {

		ReferenceType.Type type = baseRef.getType();
		CoreId coreId = m_baseRef.getCoreId();
		if (type == ReferenceType.Type.ENDPOINT) {
			tryMatching(scripter, baseRef,
					m_engine.getEndpoints(coreId),
					m_engine.getEndpointTable(coreId).values(),
					ReferenceClass.ENDPOINT_REF);

		} else if (type == ReferenceType.Type.CALL) {
			tryMatching(scripter, baseRef,
					m_engine.getCalls(coreId),
					m_engine.getCallTable(coreId).values(),
					ReferenceClass.ANY_REF);

		} else {
			boolean matched = tryMatching(scripter, baseRef,
					m_engine.getSrcLabels(coreId),
					m_engine.getSrcLabelTable(coreId).values(),
					ReferenceClass.ANY_REF);

			if (!matched) {
				tryLabels(scripter, baseRef);
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

		if (baseRef.getLineNum() > 0) {
			initialise(baseRef.getRef(), refClass, scripter.getUniqueLabel(), true);
		} else {
			// Try the ones from the compiler
			names = getNames(tableInfos);
			for (String name : names) {
				Set<Long> endPointPcs = getPcs(tableInfos, name);
				if (endPointPcs.equals(baseRef.getPcs())) {
					initialise(name, refClass, scripter.getUniqueLabel(), true);
					return true;
				}
			}
		}
		return false;
	}

	private void tryLabels(Scripter scripter, ReferenceType baseRef) {
		Map<String, Set<Long>> labels = m_engine.getLabels(m_baseRef.getCoreId());
		for (String label : labels.keySet()) {
			if (labels.get(label).equals(baseRef.getPcs())) {
				initialise(label, ReferenceClass.LABEL_REF, label, false);
				return;
			}
		}
	}

	private Set<String> getNames(Collection<? extends ScriptableInfo> infos) {
		Set<String> names = new IteratableSet<String>();
		for (ScriptableInfo info : infos) {
			names.add(info.toReferenceString());
		}
		return names;
	}

	private Set<Long> getPcs(Collection<? extends ScriptableInfo> infos, String name) {
		Set<Long> pcs = new NonIteratableSet<Long>();
		for (ScriptableInfo info : infos) {
			if (info.toString().equals(name)) {
				pcs.add(info.getPc());
			}
		}
		return pcs;
	}

	private void initialise(String mapsToLabel, ReferenceClass mapsToClass, String label, boolean isModifiable) {
		m_mapsTo = ReferenceFactory.createReference(mapsToLabel, mapsToClass, m_baseRef.getCoreId());
		m_mapsTo.resolve(m_engine);

		m_mapsToLabel = label;
		m_isModifiable = isModifiable;
		
		m_useBaseFileLine = (m_mapsTo.getLineNum() == 0);
	}
}
