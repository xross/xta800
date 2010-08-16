package com.xmos.xta.core.iopasm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.xmos.xta.core.iopasm.instructions.Instruction;
import com.xmos.xta.core.iopasm.instructions.Wait;

public class Program {

	private String m_fileName;
	private List<Instruction> m_instructions;
	private Instruction m_startInstruction;

	public Program(String fileName) {
		m_fileName = fileName;
		m_instructions = new ArrayList<Instruction>();
		m_startInstruction = null;
	}

	public void addInstruction(Instruction instruction) {
		assert(instruction != null);
		
		if (m_startInstruction == null) {
			m_startInstruction = instruction; 
		}
		m_instructions.add(instruction);
	}

	public void printReport() {
		connectInstructions();
		checkAllInstructionsReached();
		checkResourceUsage();

		int worstCycleDepth = calculateWorstCycleDepth();
		dumpInstructionInfo(worstCycleDepth);

		generateGraph(m_fileName + "_summary.dot", true);
		generateGraph(m_fileName + "_complete.dot", false);
	}

	public Instruction getInstructionFollowing(Instruction instruction) {
		int index = m_instructions.indexOf(instruction);
		assert(index >= 0);
		
		if ((index + 1) >= m_instructions.size()) {
			instruction.error("last instruction does not branch");
		}
		return m_instructions.get(index + 1);
	}

	public Instruction getTarget(Instruction source, String label, String labelOffst) {
		label = label.trim();
		for (Instruction instruction : m_instructions) {
			if (instruction.getLabel().equals(label) && instruction.getLabelOffset().equals(labelOffst)) {
				return instruction;
			}
		}

		source.error("can't find target for label '" + label + "'");
		return null;
	}

	public Set<Instruction> getTargets(Instruction source, String label) {
		Set<Instruction> targets = new HashSet<Instruction>();
		label = label.trim();
		for (Instruction instruction : m_instructions) {
			if (instruction.getLabel().equals(label)) {
				targets.add(instruction);
			}
		}
		
		if (targets.isEmpty()) {
			source.error("can't find target for label '" + label + "'");
		}
		return targets;
	}

	private void dumpInstructionInfo(int worstCycleDepth) {
		int numMemory = 0;
		int numArithmetic = 0;
		int numResource = 0;
		int numFlags = 0;
		int numControl = 0;
		int numExtension = 0;
		
		for (Instruction instruction : m_instructions) {
			if (!instruction.getMemoryUnitString().equals("")) {
				numMemory++;
			}
			if (!instruction.getArithmeticUnitString().equals("")) {
				numArithmetic++;
			}
			if (!instruction.getResourceUnitString().equals("")) {
				numResource++;
			}
			if (!instruction.getFlagsString().equals("")) {
				numFlags++;
			}
			if (!instruction.getControlUnitString().equals("")) {
				numControl++;
			}
			if (!instruction.getExtensionUnitString().equals("")) {
				numExtension++;
			}
		}
		System.out.print(m_fileName + ": has " + m_instructions.size() + " instructions ");
		System.out.print("and worst-case cycle depth of " + worstCycleDepth + " (");
		System.out.print(numMemory + " mem, ");
		System.out.print(numArithmetic + " arith, ");
		System.out.print(numResource + " res, ");
		System.out.print(numFlags + " flag, ");
		System.out.print(numControl + " ctrl, ");
		System.out.println(numExtension + " ext)");
	}

	private void generateGraph(String fileName, boolean summary) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(new File(fileName)));
			try {
				out.write("digraph G {");
				Set<Instruction> hit = new HashSet<Instruction>();
				m_startInstruction.emitGraph(hit, summary, out);
				out.write("}");
				out.close();
			} catch (IOException e) {
	   			System.err.println("error: error writing to file: " + fileName);
			}
		} catch (IOException e) {
   			System.err.println("error: unable to open file for writing: " + fileName);
		}
	}

	private void checkAllInstructionsReached() {
		Set<Instruction> hit = new HashSet<Instruction>();
		collectInstructions(m_startInstruction, hit);
		
		if (hit.size() != m_instructions.size()) {
			for (Instruction instruction : m_instructions) {
				if (!hit.contains(instruction)) {
					instruction.warning("line cannot be reached");
				}
			}
		}
	}

	private void collectInstructions(Instruction instruction, Set<Instruction> hit) {
		if (hit.contains(instruction)) {
			return;
		} 

		hit.add(instruction);
		for (Instruction next : instruction.getSuccessors(false)) {
			collectInstructions(next, hit);
		}
	}

	private int calculateWorstCycleDepth() {
		int worstCycleDepth = 0;
		for (Instruction instruction : m_instructions) {
			if (instruction.isWait() || (instruction == m_startInstruction)) {
				Set<Instruction> hit = new HashSet<Instruction>();
				int instructionCycleDepth = calculateInstructionCycleDepth(instruction, hit, 1);
				worstCycleDepth = Math.max(worstCycleDepth, instructionCycleDepth);
			}
		}
		return worstCycleDepth;
	}

	private int calculateInstructionCycleDepth(Instruction instruction, Set<Instruction> hit, int depth) {
		int worstDepth = depth;
		if (hit.contains(instruction)) {
			return worstDepth;
		}
		hit.add(instruction);

		for (Instruction next : instruction.getSuccessors(false)) {
			if (next.getCycleDepth() < depth) {
				next.setCycleDepth(depth);
				int nextWorstDepth = depth;
				if (!next.isWait()) {
					nextWorstDepth = calculateInstructionCycleDepth(next, hit, depth + 1);
				}
				worstDepth = Math.max(worstDepth, nextWorstDepth);
			}
		}
		hit.remove(instruction);
		return worstDepth;
	}

	private void connectInstructions() {
		for (Instruction instruction : m_instructions) {
			instruction.connect(this);
		}
	}

	// Ensure that resources are used exactly once between every event
	private void checkResourceUsage() {
		for (Instruction instruction : m_instructions) {
			if (instruction.isWait() || (instruction == m_startInstruction)) {
				for (Instruction next : instruction.getSuccessors(false)) {
					String resource = "";
					if (instruction.isWait()) {
						resource = ((Wait)instruction).getResourceName(next);
					}

					Stack<Instruction> backTrail = new Stack<Instruction>();
					backTrail.add(instruction);

					// Check the wait instruction itself
					int count = 0;
					count = checkResourceUsage(instruction, backTrail, resource, count);

					Set<Instruction> hit = new HashSet<Instruction>();
					hit.add(instruction);
					count = checkResourceUsage(next, hit, backTrail, resource, count);
					
					if (!resource.equals("") && (count == 0)) {
						String message = String.format("resource '%s' never used when ready", resource);
						instruction.addWarning(message, backTrail);
					}
				}
			}
		}
	}

	private int checkResourceUsage(Instruction instruction, Stack<Instruction> backTrail, String resource, int count) {
		String instResource = instruction.getResourceUsed();
		
		if (resource.equals("ct0") || resource.equals("ct1")) {
			// Is a timer, check the control unit
			instResource = instruction.getControlUnitTimer();
		}

		if (resource.equals(instResource) && !instResource.equals("")) {
			count++;
			if (count != 1) {
				String message = String.format("resource '%s' used >1 times", resource);
				instruction.addWarning(message, backTrail);
			}
		} else if (!instResource.equals("")) {
			String message = String.format("resource '%s' used when not ready", instResource);
			instruction.addWarning(message, backTrail);
		}
		return count;
	}
	
	private int checkResourceUsage(Instruction instruction, Set<Instruction> hit,
			Stack<Instruction> backTrail, String resource, int count) {
		
		if (hit.contains(instruction) || instruction.isWait()) {
			return count;
		}
		hit.add(instruction);
		count = checkResourceUsage(instruction, backTrail, resource, count);
		
		backTrail.push(instruction);
		for (Instruction next : instruction.getSuccessors(false)) {
			count = checkResourceUsage(next, hit, backTrail, resource, count);
		}
		backTrail.pop();
		
		return count;
	}
}
