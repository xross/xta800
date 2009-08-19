package com.xmos.xta.engine;

public interface ProgressMonitor {
	public void beginTask(String name, int totalWork);
	public boolean isCanceled();
	public void subTask(String name);
	public void worked(int work);
}
