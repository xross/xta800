package com.xmos.xta.engine.unknowns;

import java.util.HashSet;

public class Unknowns {
	
	HashSet<Unknown> m_unknowns;

	public Unknowns() {
		m_unknowns = new HashSet<Unknown>();
	}
	
	public void addUnknown(Unknown unknown) {
		assert(unknown != null);
		if (!contains(unknown))
			m_unknowns.add(unknown);
	}
	
	private boolean contains(Unknown u) {
		for (Unknown unknown : m_unknowns) {
			if (unknown.getDescription().equals(u.getDescription()))
				return true;
		}
		return false;
	}

	public HashSet<Unknown> getAll() {
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
			if ((unknown instanceof InstructionUnknown) && (unknown.getPc() == pc))
				return unknown.isUnknown();
		}
		return false;
	}

	public HashSet<Unknown> getUnknownInstructionTimes() {
		HashSet<Unknown> all = new HashSet<Unknown>();
		for (Unknown unknown : m_unknowns) {
			if (unknown.isUnknown() && (unknown instanceof InstructionUnknown))
				all.add(unknown);
		}
		return all;
	}
}
