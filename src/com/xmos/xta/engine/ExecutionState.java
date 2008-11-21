package com.xmos.xta.engine;

import com.xmos.xta.engine.Status.BlockStatus;
import com.xmos.xta.engine.Status.ExecutionStatus;
import com.xmos.xta.engine.resources.Resources;

public class ExecutionState {
	private MachineState m_machineState;
	private Route m_route;
	private Block[] m_threadBlocks;
	private int m_currentThread;
	private ExecutionStatus m_status;

	public ExecutionState(Route route, MachineState machineState,
			BlockCommonComponents bc, int fromPc) {
		m_route = route;
		m_machineState = machineState;
		initialise();
		m_threadBlocks[0] = new Block(bc, fromPc, 0, m_machineState, this);
		m_machineState.setPc(fromPc, 0);
	}

	public ExecutionState(Route route, MachineState machineState,
			BlockCommonComponents bc, int fromPc, int toPc) {
		m_route = route;
		m_machineState = machineState;
		initialise();
		m_threadBlocks[0] = new Block(bc, fromPc, toPc, m_machineState, this);
		m_machineState.setPc(fromPc, 0);
	}

	public ExecutionState(ExecutionState executionState,
			MachineState machineState) {
		m_route = executionState.m_route;
		m_machineState = machineState;
		m_threadBlocks = new Block[Resources.NUM_THREADS];
		for (int i = 0; i < Resources.NUM_THREADS; i++) {
			if (executionState.m_threadBlocks[i] == null) {
				m_threadBlocks[i] = null;
			} else {
				m_threadBlocks[i] = new Block(executionState.m_threadBlocks[i],
						m_machineState, this);
			}
		}
		m_status = Status.ExecutionStatus.SEARCHING;
		m_currentThread = 0;
		m_route.addExecutionState(this);
	}

	public Block[] getThreadBlocks(){
		return m_threadBlocks;
	}
	
	private void initialise() {
		m_threadBlocks = new Block[Resources.NUM_THREADS];
		for (int i = 0; i < Resources.NUM_THREADS; i++) {
			m_threadBlocks[i] = null;
		}
		m_status = Status.ExecutionStatus.SEARCHING;
		m_currentThread = 0;
		m_route.addExecutionState(this);
	}

	public void build() {

		assert (m_threadBlocks != null);
		Block currentThread = getNextThread();

		BlockStatus currentStatus = currentThread.getStatus();
		while (m_status == ExecutionStatus.SEARCHING) {
			if (currentStatus == BlockStatus.SEARCHING) {
				currentThread.build();
				currentStatus = currentThread.getStatus();
			} else if (currentStatus == BlockStatus.PAUSED) {

				if (currentThread.canUnpause()) {
					currentThread.setStatus(BlockStatus.SEARCHING);
				} else {
					if (anyThreadsSearching()) {
						currentThread = getNextThread();
					} else {
						makeUnpauseSlection();
					}
				}

			} else if (currentStatus == BlockStatus.SYNCHRONISING) {
				/*
				 * if (allThreadsSyncing()) {
				 * setAllThreadsStatus(BlockStatus.SEARCHING); } else {
				 * currentThread = getNextThread(); }
				 */
			} else if (currentStatus == BlockStatus.JOINING) {
				/*
				 * if (allOtherThreadsSyncing(currentThread)) {
				 * 
				 * //set all threads but the joining one to complete
				 * 
				 * //setAllThreadsStatus(Status.BlockStatus.COMPLETE); } else {
				 * currentThread = getNextThread(); }
				 */
			} else if (currentStatus == BlockStatus.EXCLUDED
					|| currentStatus == BlockStatus.ERROR
					|| currentStatus == BlockStatus.RECURSION
					|| currentStatus == BlockStatus.UNRESOLVED
					|| currentStatus == BlockStatus.MAX_NUM_PATHS) {
				this.setStatus(ExecutionStatus.UNRESOLVED);
				return;
			} else if (currentStatus == BlockStatus.COMPLETE) {
				if (allThreadsComplete()) {
					this.setStatus(ExecutionStatus.COMPLETE);
					return;
				} else {
					currentThread = getNextThread();
				}
			} else {
				System.out.println(currentStatus);
				assert (false);
			}
		}
	}

	private void makeUnpauseSlection() {
		for (Block thread : m_threadBlocks) {
			if (thread == null)
				continue;
			if (thread.getStatus() == BlockStatus.PAUSED) {
				// unpause the first one you find!
				thread.setStatus(BlockStatus.SEARCHING);
				return;
			}
		}

	}

	private boolean anyThreadsSearching() {
		for (Block thread : m_threadBlocks) {
			if (thread == null)
				continue;
			if (thread.getStatus() == BlockStatus.COMPLETE) {
				return true;
			}
		}
		return false;
	}

	private boolean allThreadsComplete() {
		for (Block thread : m_threadBlocks) {
			if (thread == null)
				continue;
			if (thread.getStatus() != BlockStatus.COMPLETE) {
				return false;
			}
		}
		return true;
	}

	public ExecutionStatus getStatus() {
		return m_status;
	}

	public void setStatus(ExecutionStatus newStatus) {
		m_status = newStatus;
		if (m_status == ExecutionStatus.COMPLETE) {
			m_machineState = null;
		}
	}

	private Block getNextThread() {
		Block block = null;

		int nextThread = m_currentThread;
		do {
			block = m_threadBlocks[nextThread];
			nextThread = (nextThread + 1) % Resources.NUM_THREADS;
		} while ((block == null) && (nextThread != m_currentThread));

		m_currentThread = nextThread;
		return block;
	}

	public boolean isComplete() {
		if (m_status == Status.ExecutionStatus.COMPLETE)
			return true;
		else
			return false;
	}
}
