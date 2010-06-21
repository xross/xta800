package com.xmos.xta.core.engine.sourcemapper;

import java.util.Collection;
import java.util.Set;

import com.xmos.libs.util.IteratableSet;
import com.xmos.libs.util.Pair;
import com.xmos.libs.xeloader.DebugInfo;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.analysis.binary.InstructionDecoder;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.utils.CoreCollection;
import com.xmos.xta.core.engine.utils.CoreId;

public class SourceLine {
	private String     m_text;
	private long       m_lineNumber;
	private SourceFile m_sourceFile;
	private XeLoader   m_xeLoader;
	private CoreCollection<InstructionDecoder> m_instructionDecoders;
	private CoreCollection<Collection<Long>> m_pcs;
	private CoreCollection<Collection<Long>> m_statementPcs;
	
	public SourceLine(String text, long lineNumber, SourceFile sourceFile, 
			XeLoader xeLoader, CoreCollection<InstructionDecoder> instructionDecoders) {
		m_text = text;
		m_lineNumber = lineNumber;
		m_sourceFile = sourceFile;
		m_xeLoader = xeLoader;
		m_instructionDecoders = instructionDecoders;		
		m_pcs = new CoreCollection<Collection<Long>>();
		m_statementPcs = new CoreCollection<Collection<Long>>();
	}

	public Collection<Long> getPcs(CoreId coreId) {
		if (m_pcs.get(coreId) == null) {
			initialisePcsMap(coreId);
		}		
		return m_pcs.get(coreId);
	}

	public Collection<Long> getStatementPcs(CoreId coreId) {
		if (m_statementPcs.get(coreId) == null) {
			initialiseStatementPcsMap(coreId);
		}		
		return m_statementPcs.get(coreId);
	}

    public String getText() {
        return m_text;
    }

	public long getLineNumber() {
		return m_lineNumber;
	}
    
    private void initialisePcsMap(CoreId coreId) {
    	DebugInfo debugInfo = m_xeLoader.getDebugInfo(coreId.getNodeChainIndex(), coreId.getCoreNumber());
    	assert(debugInfo != null);
    	
		Collection<Pair<Long,Long>> addressRanges = debugInfo.getDebugEngine().getAddressRangesMatchingPath(
				m_sourceFile.getFullFileName(), m_lineNumber);		
		Collection<Long> pcs = rangesToAddresses(coreId, addressRanges);
		
        m_pcs.add(coreId, pcs);
    }

    private void initialiseStatementPcsMap(CoreId coreId) {
    	DebugInfo debugInfo = m_xeLoader.getDebugInfo(coreId.getNodeChainIndex(), coreId.getCoreNumber());
    	assert(debugInfo != null);
    	
    	Collection<Long> statementPcs = debugInfo.getDebugEngine().getStatementPcsMatchingPath(
    			m_sourceFile.getFullFileName(), m_lineNumber);

       	m_statementPcs.add(coreId, statementPcs);
    }

    private Collection<Long> rangesToAddresses(CoreId coreId, Collection<Pair<Long, Long>> addressRanges) {
    	InstructionDecoder instructionDecoder = m_instructionDecoders.get(coreId);
    	assert(instructionDecoder != null);
    	
		Set<Long> addresses = new IteratableSet<Long>();
		for (Pair<Long, Long> range : addressRanges) {
			long endPc = instructionDecoder.getEndPc();
			for (long pc = range.first; (pc < range.second) && (pc <= endPc); pc += 2) {
				InstructionNode node = instructionDecoder.getNode(pc);
				if (node != null) {
					addresses.add(pc);
				}
			}
		}
		return addresses;
	}
}


