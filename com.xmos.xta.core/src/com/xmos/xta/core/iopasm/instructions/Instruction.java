package com.xmos.xta.core.iopasm.instructions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.xmos.xta.core.iopasm.Program;

public abstract class Instruction {

	private int m_lineNumber;
	private String m_comment;

	private List<String> m_warnings;

	private String m_label;
	private String m_labelOffset;
	
	private String m_memoryUnitString;
	private String m_arithmeticUnitString;

	private String m_resourceUnitString;
	private String m_resourceUnitResource;

	private String m_flagsString;
	private String m_controlUnitString;
	private String m_controlUnitTimer;
	private String m_controlUnitOperation;
	
	private String m_extensionUnitString;
	
	private int m_cycleDepth;

	protected Set<Instruction> m_successors;
	
	public abstract boolean isWait();
	public abstract void connect(Program program);
	public abstract String getConnectorLabel(Instruction next);

	public Instruction(String label, String labelOffset, List<String> unitStrings, int lineNumber, String comment) {
		m_lineNumber = lineNumber;
		m_comment = comment;
		m_warnings = new LinkedList<String>();
		m_cycleDepth = 0;
		
		m_label = label;
		m_labelOffset = labelOffset;

		initMemoryUnit(unitStrings);
		initArithmeticUnit(unitStrings);
		initResourceUnit(unitStrings);
		initFlagsUnit(unitStrings);
		initControlUnit(unitStrings);
		initExtensionUnit(unitStrings);

		m_successors = new HashSet<Instruction>();
	}

	private void initMemoryUnit(List<String> strings) {
		m_memoryUnitString = "";
		if (strings.size() > 0) {
			m_memoryUnitString = strings.get(0);
		}
	}

	private void initArithmeticUnit(List<String> strings) {
		m_arithmeticUnitString = "";
		if (strings.size() > 1) {
			m_arithmeticUnitString = strings.get(1);
		}
	}

	private void initResourceUnit(List<String> strings) {
		m_resourceUnitString = "";
		m_resourceUnitResource = "";
		if (strings.size() > 2) {
			m_resourceUnitString = strings.get(2);
			
			if (m_resourceUnitString.length() > 0) {
				int inIndex = m_resourceUnitString.indexOf(":>");
				int outIndex = m_resourceUnitString.indexOf("<:");
				int eqIndex = m_resourceUnitString.indexOf("=");
				
				if (inIndex != -1) {
					m_resourceUnitResource = m_resourceUnitString.substring(0, inIndex).trim();
					
				} else if (outIndex != -1) {
					m_resourceUnitResource = m_resourceUnitString.substring(0, outIndex).trim();
					
				} else if (eqIndex != -1) {
					String dest =m_resourceUnitString.substring(0, eqIndex).trim();
					if (dest.equals("tr0") || dest.equals("tr1")) {
						// Assignment - no resource
					} else {
						error(String.format("unable to parse resource unit '%s'", m_resourceUnitString));
					}
					
				} else {
					int openIndex = m_resourceUnitString.indexOf('(');
					int commaIndex = m_resourceUnitString.indexOf(',');
					if (openIndex == -1 || commaIndex == -1 || commaIndex < openIndex) {
						error(String.format("unable to parse resource unit '%s'", m_resourceUnitString));
					}
					m_resourceUnitResource = m_resourceUnitString.substring(openIndex + 1, commaIndex).trim();
				}
			}
		}
	}

	private void initFlagsUnit(List<String> strings) {
		m_flagsString = "";
		if (strings.size() > 3) {
			m_flagsString = strings.get(3);
		}
	}

	private void initControlUnit(List<String> strings) {
		m_controlUnitString = "";
		m_controlUnitTimer = "";
		m_controlUnitOperation = "";
		if (strings.size() > 4) {
			m_controlUnitString = strings.get(4);

			int inIndex = m_controlUnitString.indexOf(":>");
			int outIndex = m_controlUnitString.indexOf("<:");
			if (inIndex != -1) {
				m_controlUnitTimer = m_controlUnitString.substring(0, inIndex).trim();
			} else if (outIndex != -1) {
				m_controlUnitTimer = m_controlUnitString.substring(0, outIndex).trim();
			}
			
			if (!m_controlUnitString.startsWith("bra") && !m_controlUnitString.startsWith("wait")) {
				m_controlUnitOperation = m_controlUnitString;
			}
		}
	}

	private void initExtensionUnit(List<String> strings) {
		m_extensionUnitString = "";
		if (strings.size() > 5) {
			m_extensionUnitString = strings.get(5);

			int semiIndex = m_extensionUnitString.indexOf(';');
			if (semiIndex != -1) {
				m_extensionUnitString = m_extensionUnitString.substring(0, semiIndex);
			}
		}
	}

	public int getLineNumber() {
		return m_lineNumber;
	}

	public String toString() {
		String str = String.format("%-5d", m_lineNumber);;
		if (m_label.length() > 0) {
			str += String.format("%-13s", m_label + ":");
		} else {
			str += String.format("%13s", m_label);
		}
		str += String.format("%-8s, %-15s, %-15s, %-10s, %-25s, %-5s;",
				m_memoryUnitString, m_arithmeticUnitString, m_resourceUnitString,
				m_flagsString, m_controlUnitString, m_extensionUnitString);

		if (m_comment.length() > 0) {
			str += " //" + m_comment;
		} 

		return str;
	}

	public void emitGraph(Set<Instruction> hit, boolean summary, BufferedWriter out) throws IOException {
		if (hit.contains(this)) {
			return;
		}
		
		if (!summary || m_label.length() > 0) {
			String graphName = getGraphName();
			String shapeDescription = graphName + " [shape=" + getGraphShape() +
					", label=\"" + getGraphLabel(summary) + "\"";
			
			if (m_warnings.size() > 0) {
				shapeDescription += ",style=filled,color=red";
			} 
			if (isWait()) {
				shapeDescription += ",peripheries=2";
			}
			
			shapeDescription += "];\n";
			out.write(shapeDescription);
			
			for (Instruction next : getSuccessors(summary)) {
				out.write(graphName + " -> " + next.getGraphName());
				if (!summary) {
					String connectorLabel = getConnectorLabel(next);
					out.write(connectorLabel);
				}
				out.write(";\n");
			}
		}
		hit.add(this);
		
		for (Instruction next : getSuccessors(summary)) {
			next.emitGraph(hit, summary, out);
		}
	}

	public String getGraphName() {
		return "box" + m_lineNumber;
	}
	
	public String getGraphLabel(boolean summary) {
		String label = "";
		if (summary) {
			assert(m_label.length() > 0);
			label += m_label;
			if (m_labelOffset.length() > 0) {
				label += "|" + m_labelOffset;
			}
		
		} else {
			if (m_label.length() > 0) {
				label += m_label;
				if (m_labelOffset.length() > 0) {
					label += "|" + m_labelOffset;
				}
				label += "\\n";
			}
			label += String.format("%d: %s, %s, %s, %s", m_lineNumber, 
					m_memoryUnitString, m_arithmeticUnitString, m_resourceUnitString,
					m_flagsString);
			
			if (m_controlUnitOperation.length() > 0) {
				label += String.format(", %s", m_controlUnitOperation);
			}
			if (m_extensionUnitString.length() > 0) {
				label += String.format(", %s", m_extensionUnitString);
			}
		}
		label += String.format(" (%d)", m_cycleDepth);
		return label;
	}

	public String getGraphShape() {
		if (isWait()) {
			return "box";
		} else {
			return "ellipse";
		}
	}

	public String getLabel() {
		return m_label;
	}

	public String getLabelOffset() {
		return m_labelOffset;
	}

	public String getMemoryUnitString() {
		return m_memoryUnitString;
	}

	public String getArithmeticUnitString() {
		return m_arithmeticUnitString;
	}

	public String getResourceUnitString() {
		return m_resourceUnitString;
	}

	public String getResourceUsed() {
		return m_resourceUnitResource;
	}

	public String getFlagsString() {
		return m_flagsString;
	}

	public String getControlUnitString() {
		return m_controlUnitString;
	}
	
	public String getControlUnitTimer() {
		return m_controlUnitTimer;
	}

	public String getExtensionUnitString() {
		return m_extensionUnitString;
	}

	public int getCycleDepth() {
		return m_cycleDepth;
	}

	public void setCycleDepth(int cycleDepth) {
		m_cycleDepth = cycleDepth;
	}

	public void error(String message) {
		System.err.println(m_lineNumber + ":error: " + message);
		System.exit(1);
	}

	public void warning(String message) {
		System.err.println(m_lineNumber + ":warning: " + message);
	}

	public Set<Instruction> getSuccessors(boolean summary) {
		if (summary) {
			// Find only the successors which have labels
			Set<Instruction> summarySuccessors = new HashSet<Instruction>();
			for (Instruction next : m_successors) {
				if (next.getLabel().length() > 0) {
					summarySuccessors.add(next);
				} else {
					summarySuccessors.addAll(next.getSuccessors(summary));
				}
			}
			return summarySuccessors;
		} else {
			return m_successors;
		}
	}
	
	public void addWarning(String warning, Stack<Instruction> backTrail) {
		// Ignore duplicate warnings
		if (!m_warnings.contains(warning)) {
			if (!backTrail.empty()) {
				warning += " (";
				for (Instruction instruction : backTrail) {
					warning += instruction.getLineNumber() + ", ";
				}
				warning += ")";
			}
			m_warnings.add(warning);
			warning(warning);
		}
	}
}
