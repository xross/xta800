package com.xmos.xta.core.engine.sections;

public class BranchInfo implements Comparable<BranchInfo> {	
	private long m_pc;
	private boolean m_isUser;

	BranchInfo(long pc, boolean isUser) {
		 m_pc = pc;
		 m_isUser = isUser;
	}
	
	public long getPc() {
		return m_pc;	
	}
	
	public boolean isUser() {
		return m_isUser;
	}

	public int compareTo(BranchInfo o) {
		return Long.signum(m_pc - o.m_pc);
	}
}
