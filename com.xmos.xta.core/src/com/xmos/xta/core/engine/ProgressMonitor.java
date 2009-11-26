package com.xmos.xta.core.engine;

public interface ProgressMonitor {
	public void beginTask(String name, int totalWork);
	public void setTaskName(String name);
	public boolean isCanceled();
	public void subTask(String name);
	public void worked(int work);
}
