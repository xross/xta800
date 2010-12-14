package com.xmos.xta.core.engine.scripter;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.xmos.libs.util.CoreId;
import com.xmos.libs.util.IteratableSet;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.TraverseStatus;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.UnknownFactory;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceType;
import com.xmos.xta.core.engine.references.types.ReferenceType.Type;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.core.engine.sections.ScriptableInfo;
import com.xmos.xta.core.engine.sections.SrcLabelTableInfo;
import com.xmos.xta.core.engine.sourcemapper.SourceFile;
import com.xmos.xta.core.engine.sourcemapper.SourceMapper;

public class ScripterReference {
	private Engine m_engine;
	private boolean m_isEnabled;
	private boolean m_isModifiable;
	private ReferenceType m_baseRef;
	private Reference m_mapsTo;
	private String m_mapsToLabel;
	private boolean m_useBaseFileLine;

	public ScripterReference(Scripter scripter, Engine engine, ReferenceType baseRef,
			UnknownFactory.Type type, Node node) {
		m_engine = engine;
		m_isEnabled = true;
		m_isModifiable = false;
		m_baseRef = baseRef;
		m_mapsTo = null;
		m_mapsToLabel = "";
		m_useBaseFileLine = true;

		if (!baseRef.isPortable()) {
			assert(type != null);
			String refPrefix = chooseRefPrefix(type, baseRef);
			tryHighestLevelReference(scripter, baseRef, type, node, refPrefix);
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
		m_baseRef.sourceLineInserted(sourceFileName, lineNumber, 
				m_engine.getWorkspacePath(), sourceMapper);
		
		if (m_mapsTo != null) {
			m_mapsTo.getBase().sourceLineInserted(sourceFileName, lineNumber, 
					m_engine.getWorkspacePath(), sourceMapper);
		}
	}

	private void tryHighestLevelReference(Scripter scripter, ReferenceType baseRef,
			UnknownFactory.Type type, Node node, String refPrefix) {
		
		ReferenceType.Type refType = baseRef.getType();
		CoreId coreId = m_baseRef.getCoreId();
		boolean done = false;
		
		if (refType == ReferenceType.Type.ENDPOINT) {
			done = tryMatching(scripter, baseRef,
					m_engine.getEndpoints(coreId),
					m_engine.getEndpointTable(coreId).values(),
					ReferenceClass.ENDPOINT, refPrefix);

		} else if (refType == ReferenceType.Type.CALL) {
			done = tryMatching(scripter, baseRef,
					m_engine.getCalls(coreId),
					m_engine.getCallTable(coreId).values(),
					ReferenceClass.ANY, refPrefix);

		} else if (refType == ReferenceType.Type.FUNCTION) {
			tryFunctions(scripter, baseRef);
			done = true; // Don't try any other matching

		} else {
			done = tryMatching(scripter, baseRef,
					m_engine.getSrcLabels(coreId),
					m_engine.getSrcLabelTable(coreId).values(),
					ReferenceClass.ANY, refPrefix);
		}
		
		if (done)
			return;
		
		done = tryMatchingSource(scripter, baseRef, type, node, refPrefix);
		if (done)
			return;
		
		tryLabels(scripter, baseRef);
	}

	private boolean tryMatching(Scripter scripter, ReferenceType baseRef,
			Collection<? extends ScriptableInfo> infos,
			Collection<? extends ScriptableInfo> tableInfos,
			ReferenceClass refClass, String refPrefix) {

		// Try the user-defined
		Set<String> names = getNames(infos);
		for (String name : names) {
			Set<Long> userPcs = getPcs(infos, name);
			if (userPcs.equals(baseRef.getPcs())) {
				initialise(name, refClass, name, false);
				return true;
			}
		}

		if (baseRef.getLineNum() > 0) {
			initialise(baseRef.getRef(), refClass, scripter.getUniqueLabel(refPrefix), true);
			return true;
		} else {
			// Try the ones from the compiler
			names = getNames(tableInfos);
			for (String name : names) {
				Set<Long> tablePcs = getPcs(tableInfos, name);
				if (tablePcs.equals(baseRef.getPcs())) {
					initialise(name, refClass, scripter.getUniqueLabel(refPrefix), true);
					return true;
				}
			}
		}
		return false;
	}

	private void tryFunctions(Scripter scripter, ReferenceType baseRef) {
		CoreId coreId = baseRef.getCoreId();
		Set<String> functionNames = m_engine.getFunctionNames(coreId);
		for (String functionName : functionNames) {
			if (m_engine.getFunctionStartPcs(functionName, coreId).equals(baseRef.getPcs())) {
				initialise(functionName, ReferenceClass.FUNCTION, functionName, false);
				return;
			}
		}
	}

	private boolean tryMatchingSource(Scripter scripter, ReferenceType baseRef,
			UnknownFactory.Type type, Node node, String refPrefix) {
		
		if (node == null) {
			return false;
		}
		
		Set<Long> pcs = getPcsForNode(type, node);
		CoreId coreId = baseRef.getCoreId();
		Set<Reference> options = getSourceLinesForPcs(pcs, coreId);
		Reference reference = chooseBest(options);
		
		if (reference != null) {
			m_mapsTo = reference;
			m_mapsTo.resolve(m_engine);
			m_mapsToLabel = scripter.getUniqueLabel(refPrefix);
			m_isModifiable = true;
			m_useBaseFileLine = false;
			return true;
		}
		return false;
	}

	private Reference chooseBest(Set<Reference> options) {
		if (options.isEmpty()) {
			return null;
		}
		return options.iterator().next();
	}

	private Set<Reference> getSourceLinesForPcs(Set<Long> pcs, CoreId coreId) {
		Collection<SrcLabelTableInfo> table = m_engine.getSrcLabelTable(coreId).values();
		Set<Reference> references = new IteratableSet<Reference>();
		
		Set<String> names = getNames(table);
		for (String name : names) {
			Set<Long> tablePcs = getPcs(table, name);
			if (pcs.containsAll(tablePcs)) {
				Reference ref = ReferenceFactory.createReference(name, ReferenceClass.ANY, coreId);
				references.add(ref);
			}
		}

		return references;
	}

	private Set<Long> getPcsForNode(UnknownFactory.Type type, Node node) {
		assert(type != null);
		final Set<Long> pcs = new IteratableSet<Long>();
		final boolean isLoop = ((type == UnknownFactory.Type.LOOP_ITERATIONS) || (type == UnknownFactory.Type.LOOP_SCOPE));

		Node enclosingNode = null;
		if (isLoop) {
			enclosingNode = node.getEnclosingLoopNode();
		} else if ((type == UnknownFactory.Type.LOOP_PATH_ITERATIONS) || (type == UnknownFactory.Type.EXCLUSION)) {
			enclosingNode = node.getEnclosingOptionNode();
		}
		
		if (enclosingNode != null) {
			enclosingNode.traverse(new DefaultNodeTraverser() {
				public TraverseStatus foundInstructionNode(Node node) {
					pcs.add(((InstructionNode)node).getPC());
					return TraverseStatus.CONTINUE;
				}

				public TraverseStatus foundInfiniteLoopNode(Node node) {
					if (isLoop) {
						// Prevent loops from finding references within inner loops
						return TraverseStatus.NO_CHILDREN;
					}
					return TraverseStatus.CONTINUE;
				}

				public TraverseStatus foundSelfLoopNode(Node node) {
					if (isLoop) {
						// Prevent loops from finding references within inner loops
						return TraverseStatus.NO_CHILDREN;
					}
					return TraverseStatus.CONTINUE;
				}

				public TraverseStatus foundInfiniteSelfLoopNode(Node node) {
					if (isLoop) {
						// Prevent loops from finding references within inner loops
						return TraverseStatus.NO_CHILDREN;
					}
					return TraverseStatus.CONTINUE;
				}
			});
		}
		
		return pcs;
	}

	private void tryLabels(Scripter scripter, ReferenceType baseRef) {
		Map<String, Set<Long>> labels = m_engine.getLabels(m_baseRef.getCoreId());
		for (String label : labels.keySet()) {
			if (labels.get(label).equals(baseRef.getPcs())) {
				initialise(label, ReferenceClass.INSTRUCTION, label, false);
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
		Set<Long> pcs = new IteratableSet<Long>();
		for (ScriptableInfo info : infos) {
			if (info.toReferenceString().equals(name)) {
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

	private String chooseRefPrefix(UnknownFactory.Type type, ReferenceType baseRef) {
		if (type != UnknownFactory.Type.OTHER) {
			return type.toString().toLowerCase();
		} else {
			ReferenceType.Type refType = baseRef.getType();
			if (refType == ReferenceType.Type.ENDPOINT) {
				return "endpoint";
			} else if (refType == ReferenceType.Type.CALL) {
				return "call";
			}
		}
		return "label";
	}
}
