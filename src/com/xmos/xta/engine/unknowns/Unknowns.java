package com.xmos.xta.engine.unknowns;

import java.util.HashSet;
import java.util.Set;

public class Unknowns {	
	private Set<Unknown> m_unknowns;

	public Unknowns() {
		m_unknowns = new HashSet<Unknown>();
	}
	
	public void addUnknown(Unknown unknown) {
		assert(unknown != null);
		m_unknowns.add(unknown);
	}
	
	public Set<Unknown> getAll() {
		return m_unknowns;
	}

	public HashSet<Unknown> getUnknowns() {
		HashSet<Unknown> unknowns = new HashSet<Unknown>();
		for (Unknown unknown : m_unknowns) {
			if (unknown.isUnknown())
				unknowns.add(unknown);
		}
		return unknowns;
	}

	public HashSet<Unknown> getKnowns() {
		HashSet<Unknown> knowns = new HashSet<Unknown>();
		for (Unknown unknown : m_unknowns) {
			if (!unknown.isUnknown())
				knowns.add(unknown);
		}
		return knowns;
	}

	public HashSet<Unknown> getSelectedUnknowns(HashSet<Integer> selectedBlocks) {
		HashSet<Unknown> unknowns = new HashSet<Unknown>();
		for (Unknown unknown : m_unknowns) {
			if (unknown.isUnknown() && selectedBlocks.contains(unknown.getBasicBlockId())) {
				unknowns.add(unknown);
			}
		}
		return unknowns;
	}

	public boolean isUnknownInstructionTime(int pc) {
		for (Unknown unknown : m_unknowns) {
			if ((unknown instanceof PausableInstructionUnknown) && (unknown.getPc() == pc))
				return unknown.isUnknown();
		}
		return false;
	}

	public HashSet<Unknown> getUnknownInstructionTimes() {
		HashSet<Unknown> all = new HashSet<Unknown>();
		for (Unknown unknown : m_unknowns) {
			if (unknown.isUnknown() && (unknown instanceof PausableInstructionUnknown))
				all.add(unknown);
		}
		return all;
	}
}
