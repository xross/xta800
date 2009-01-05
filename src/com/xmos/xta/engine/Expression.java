package com.xmos.xta.engine;

import com.xmos.isa.Instruction;
import com.xmos.xta.engine.InstructionExecption.Error;

public class Expression {
	
	public final static boolean m_keepExpressions = false;
	
	public enum Type {
		IMMEDIATE, REGISTER, MEMORY,
	}

	public enum Status {
		UNDEFINED, DEFINED,
	}

	public enum OperatorType {
		ADD, SUB, AND, ANDNOT, ASHR, ASHL, BITREV, BYTEREV, CLZ, CRC32, CRC8_SCRDEST, CRC8_DEST, DIVS, DIVU, LTS, LTU, GT, NEG, NOT, OR, EQ, MACCU_LOW, MACCU_HIGH, MACCS_LOW, MACCS_HIGH, SHL, SHR, REMS, REMU, SEXT, ZEXT, XOR, MUL, LSUB_LOWER, LSUB_CARRY, LADD_LOWER, LADD_CARRY, LDIVU_LOW, LDIVU_HIGH,
	}

	final static int MAX_NUM_OPERANDS = 6;
	private int m_address;
	private int m_value;
	private Type m_type;
	private Status m_status;
	private OperatorType m_operator;
	private Expression[] m_operands;
	private int m_numOperands;

	// Terminal Constructor
	public Expression(int address, int value, Type type, Status status) {
		m_address = address;
		m_value = value;
		m_type = type;
		m_status = status;
		m_numOperands = 0;
		m_operands = new Expression[MAX_NUM_OPERANDS];
	}

	// Node Constructor
	Expression(OperatorType operator) {
		m_operator = operator;
		m_numOperands = 0;
		m_operands = new Expression[MAX_NUM_OPERANDS];
	}

	// Immediate Constructor
	public Expression(int value) {
		m_address = 0;
		m_value = value;
		m_type = Type.IMMEDIATE;
		m_status = Status.DEFINED;
		m_numOperands = 0;
		m_operands = new Expression[MAX_NUM_OPERANDS];
	}

	// Copy Constructor
	public Expression(Expression expression) {
		m_address = expression.m_address;
		m_value = expression.m_value;
		m_type = expression.m_type;
		m_status = expression.m_status;
		m_operator = expression.m_operator;
		m_numOperands = 0;
		m_operands = new Expression[MAX_NUM_OPERANDS];

		for (int operIndex = 0; operIndex < expression.m_numOperands; ++operIndex) {
			addOperand(expression.m_operands[operIndex]);
		}
	}

	void addOperand(Expression operand) {
		assert (m_numOperands < MAX_NUM_OPERANDS);
		m_operands[m_numOperands] = operand;
		++m_numOperands;
	}

	Expression getOperand(int operandNum) {
		assert (operandNum < m_numOperands);
		return m_operands[operandNum];
	}

	int numOperands() {
		return m_numOperands;
	}

	void resolve() throws Exception {
		if ((m_numOperands > 0) ){
			if(canResolve()) {
				m_address = 0;
				m_value = resolve(this);
				m_type = Type.IMMEDIATE;
				m_status = Status.DEFINED;
				m_numOperands = 0;
				if(!m_keepExpressions){
					m_operands = null;
				}
			} else {
				if (!m_keepExpressions){
					m_address = 0;
					m_value = 0;
					m_type = Type.MEMORY;
					m_status = Status.UNDEFINED;
					m_numOperands = 0;
					m_operands = null;
				}
			}
		}
		
	}

	boolean isResolved() {
		return m_type == Type.IMMEDIATE;
	}

	public int getValue() throws Exception {
		resolve();
		assert (m_type == Type.IMMEDIATE);
		return m_value;
	}

	public String getStringValue() throws Exception{
		if(this.canResolve()){
			resolve();
			return "" + this.getValue();
		} else {
			return "undefined";
		}
	}
	
	String expressionString() {
		// Builds up a string describing the expression state.
		String expr = createExpressionString(this, "");
		return expr;
	}

	public boolean canResolve() {
		return canResolve(this);
	}

	boolean canResolve(Expression expression) {
		if (expression == null) {
			// assert(false);
			return false;
		}
		if (expression.m_type != null) {
			return expression.m_type == Type.IMMEDIATE;
		} else {
			for (int opIndex = 0; opIndex < expression.m_numOperands; ++opIndex) {
				if (canResolve(expression.m_operands[opIndex]) == false)
					return false;
			}
			return true;
		}
	}

	public int resolve(Expression expression) throws Exception {
		if (expression.m_type != null) {
			assert (expression.m_type == Type.IMMEDIATE);
			return expression.m_value;
		} else {
			switch (expression.m_operator) {
			case ADD:
				assert (expression.m_numOperands == 2);
				return resolve(expression.m_operands[0])
						+ resolve(expression.m_operands[1]);

			case SUB:
				assert (expression.m_numOperands == 2);
				return resolve(expression.m_operands[0])
						- resolve(expression.m_operands[1]);

			case AND:
				assert (expression.m_numOperands == 2);
				return resolve(expression.m_operands[0])
						& resolve(expression.m_operands[1]);

			case ANDNOT:
				assert (expression.m_numOperands == 2);
				return resolve(expression.m_operands[0])
						& ~resolve(expression.m_operands[1]);

			case ASHR:
				assert (expression.m_numOperands == 2);

				int shiftVal = resolve(expression.m_operands[1]);
				if (shiftVal < 0
						|| shiftVal >= (8 << Instruction.BITS_PER_WORD)) {
					if (resolve(expression.m_operands[1]) < 0) {
						return ~0;
					} else {
						return 0;
					}
				} else {
					return resolve(expression.m_operands[0]) >> resolve(expression.m_operands[1]);
				}
			case ASHL:
				assert (expression.m_numOperands == 2);
				return resolve(expression.m_operands[0]) << resolve(expression.m_operands[1]);

			case BITREV:
				assert (expression.m_numOperands == 1);
				return XtaInstruction
						.bitReverse(resolve(expression.m_operands[0]));

			case BYTEREV:
				assert (expression.m_numOperands == 1);
				return XtaInstruction
						.byteReverse(resolve(expression.m_operands[0]));

			case CLZ:
				assert (expression.m_numOperands == 1);
				return XtaInstruction
						.countLeadingZeros(resolve(expression.m_operands[0]));

			case CRC32:
				assert (expression.m_numOperands == 3);
				return XtaInstruction.crc32(resolve(expression.m_operands[0]),
						resolve(expression.m_operands[1]),
						resolve(expression.m_operands[2]));

			case CRC8_SCRDEST:
				assert (expression.m_numOperands == 3);

				return XtaInstruction.crc8_scrdest(
						resolve(expression.m_operands[0]),
						resolve(expression.m_operands[1]),
						resolve(expression.m_operands[2]));
			case CRC8_DEST:
				assert (expression.m_numOperands == 3);

				return XtaInstruction.crc8_dest(
						resolve(expression.m_operands[0]),
						resolve(expression.m_operands[1]),
						resolve(expression.m_operands[2]));
			case DIVS:
				assert (expression.m_numOperands == 2);
				return XtaInstruction.divs(resolve(expression.m_operands[0]),
						resolve(expression.m_operands[1]));

			case DIVU:
				assert (expression.m_numOperands == 2);
				return XtaInstruction.divu(resolve(expression.m_operands[0]),
						resolve(expression.m_operands[1]));

			case LTU:
				assert (expression.m_numOperands == 2);
				long first = (long)resolve(expression.m_operands[0]);
				first = first & 0xffffffffL;
				
				long second = resolve(expression.m_operands[1]);
				second = second & 0xffffffffL;
				return ((first < second) == true) ? 1: 0;
			case LTS:
				assert (expression.m_numOperands == 2);
				return ((resolve(expression.m_operands[0]) < resolve(expression.m_operands[1])) == true) ? 1
						: 0;
			case GT:
				assert (expression.m_numOperands == 2);
				return ((resolve(expression.m_operands[0]) > resolve(expression.m_operands[1])) == true) ? 1
						: 0;

			case NEG:
				assert (expression.m_numOperands == 1);
				return -resolve(expression.m_operands[0]);

			case NOT:
				assert (expression.m_numOperands == 1);
				return ~resolve(expression.m_operands[0]);

			case OR:
				assert (expression.m_numOperands == 2);
				return resolve(expression.m_operands[0])
						| resolve(expression.m_operands[1]);

			case EQ:
				assert (expression.m_numOperands == 2);
				return (resolve(expression.m_operands[0]) == resolve(expression.m_operands[1])) ? 1
						: 0;

			case MACCU_LOW:
				assert (expression.m_numOperands == 4);
				long maccu_low = (long) resolve(expression.m_operands[2])
						* (long) resolve(expression.m_operands[3]);
				maccu_low = maccu_low
						+ ((((long) resolve(expression.m_operands[0])) << 32) | (long) resolve(expression.m_operands[1]));
				return (int) maccu_low;

			case MACCU_HIGH:
				assert (expression.m_numOperands == 4);
				long maccu_high = (long) resolve(expression.m_operands[2])
						* (long) resolve(expression.m_operands[3]);
				maccu_high = maccu_high
						+ ((((long) resolve(expression.m_operands[0])) << 32) | (long) resolve(expression.m_operands[1]));
				return (int) (maccu_high >> 32);
			case MACCS_LOW:
				assert (expression.m_numOperands == 4);
				long laccl = (((long) resolve(expression.m_operands[0])) << 32L | (((long) resolve(expression.m_operands[1])) & 0xffffffffL));
				long lprodl = (((long) resolve(expression.m_operands[3])) * ((long) resolve(expression.m_operands[2])));
				laccl += lprodl;
				return (int) (laccl & 0xffffffffL);

			case MACCS_HIGH:
				assert (expression.m_numOperands == 4);
				long lacch = (((long) resolve(expression.m_operands[0])) << 32L | (((long) resolve(expression.m_operands[1])) & 0xffffffffL));
				long lprodh = (((long) resolve(expression.m_operands[3])) * ((long) resolve(expression.m_operands[2])));
				lacch += lprodh;
				return (int) (lacch >> 32L);
			case SHR:
				assert (expression.m_numOperands == 2);
				return resolve(expression.m_operands[0]) >> resolve(expression.m_operands[1]);

			case SHL:
				assert (expression.m_numOperands == 2);
				return resolve(expression.m_operands[0]) << resolve(expression.m_operands[1]);

			case REMS:
				assert (expression.m_numOperands == 2);
				int remsA = resolve(expression.m_operands[0]);
				int remsB = resolve(expression.m_operands[1]);
				if (remsB == 0) {
					throw new Exception("ET_ARITHMETIC");
				}
				return rems(remsA, remsB);

			case REMU:
				assert (expression.m_numOperands == 2);
				int remuA = resolve(expression.m_operands[0]);
				int remuB = resolve(expression.m_operands[1]);
				if (remuB == 0) {
					throw new Exception("ET_ARITHMETIC");
				}
				return remu(remuA, remuB);

			case SEXT:
				assert (expression.m_numOperands == 2);
				int a = resolve(expression.m_operands[0]);
				int b = resolve(expression.m_operands[1]);
				return sext(a, b);
			case ZEXT:
				assert (expression.m_numOperands == 2);
				return zext(resolve(expression.m_operands[0]),
						resolve(expression.m_operands[1]));
			case XOR:
				assert (expression.m_numOperands == 2);
				return resolve(expression.m_operands[0])
						^ resolve(expression.m_operands[1]);
			case MUL:
				assert (expression.m_numOperands == 2);
				return (int) (resolve(expression.m_operands[0]) * resolve(expression.m_operands[1]));
			case LADD_CARRY:
				long ladd_carry = ((long) resolve(expression.m_operands[0]) + (long) resolve(expression.m_operands[1]))
						+ ((long) resolve(expression.m_operands[2]) & 0x1);
				return (int) ((ladd_carry >> 32) & 0x1);
			case LADD_LOWER:
				long ladd_lower = ((long) resolve(expression.m_operands[0]) + (long) resolve(expression.m_operands[1]))
						+ ((long) resolve(expression.m_operands[2]) & 0x1);
				return (int) (ladd_lower);
			case LSUB_CARRY:
				assert (expression.m_numOperands == 3);
				long lsub_carry = ((long) resolve(expression.m_operands[0]) - (long) resolve(expression.m_operands[1]))
						- ((long) resolve(expression.m_operands[2]) & 0x1);
				return (int) ((lsub_carry >> 32) & 0x1);
			case LSUB_LOWER:
				assert (expression.m_numOperands == 3);
				long lsub_dest = ((long) resolve(expression.m_operands[0]) - (long) resolve(expression.m_operands[1]))
						- ((long) resolve(expression.m_operands[2]) & 0x1);
				return (int) lsub_dest;
			case LDIVU_LOW:
				assert (expression.m_numOperands == 4);

				long dlacc = ((((long) resolve(expression.m_operands[3])) & 0xffffffffL) << 32L | (((long) resolve(expression.m_operands[1])) & 0xffffffffL));
				long dlprod = ((long) resolve(expression.m_operands[2])) & 0xffffffffL;
				if (dlprod == 0) {
					// exception(ET_ARITHMETIC, 0, oldProgIndex);
					return 0;
				}
				int dresult = 0;
				for (int i = 32; i >= 0; i--) {
					long tmp = (dlacc >> i) & 0xffffffffL;
					boolean carry = i < 32 && ((dlacc >> (i + 32)) & 1) == 1;
					if (dresult < 0) {
						// exception(ET_ARITHMETIC, 0, oldProgIndex);
						return 0;
					}
					dresult <<= 1;
					if (tmp >= dlprod || carry) {
						dlacc -= (dlprod << i);
						dresult |= 1;
					}
				}
				return (int) dresult;
			case LDIVU_HIGH:
				assert (expression.m_numOperands == 4);

				long hlacc = ((((long) resolve(expression.m_operands[3])) & 0xffffffffL) << 32L | (((long) resolve(expression.m_operands[1])) & 0xffffffffL));
				long hlprod = ((long) resolve(expression.m_operands[2])) & 0xffffffffL;
				if (hlprod == 0) {
					// exception(ET_ARITHMETIC, 0, oldProgIndex);
					return 0;
				}
				int hresult = 0;
				for (int i = 32; i >= 0; i--) {
					long tmp = (hlacc >> i) & 0xffffffffL;
					boolean carry = i < 32 && ((hlacc >> (i + 32)) & 1) == 1;
					if (hresult < 0) {
						// exception(ET_ARITHMETIC, 0, oldProgIndex);
						return 0;
					}
					hresult <<= 1;
					if (tmp >= hlprod || carry) {
						hlacc -= (hlprod << i);
						hresult |= 1;
					}
				}
				return (int) (hresult>>32);
			default:
				assert false;
				return 0;
			}
		}
	}

	private final int zext(int x, int n) {
		if (n == 0) {
			return x; // FIXME - not in documentation
		}
		return x & ((1 << n) - 1);
	}

	private int remu(int remsA, int remsB) throws InstructionExecption {
		double d1 = ((double) (((long) remsA) & 0xffffffffl));
		double d2 = ((double) (((long) remsB) & 0xffffffffl));
		if (d2 == 0) {
			throw new InstructionExecption(Error.ET_ARITHMETIC);
		} else {
			int div = (int) (d1 / d2);
			return (int) (d1 - div * d2);
		}
	}

	private int rems(int remsA, int remsB) throws InstructionExecption {
		if (remsB == 0) {
			throw new InstructionExecption(Error.ET_ARITHMETIC);
		} else {
			if (remsB < 0 && remsA < 0 && remsA / remsB < 0) { // overflow on /
				throw new InstructionExecption(Error.ET_ARITHMETIC);
			} else {
				return remsA % remsB;
			}
		}
	}

	private String createExpressionString(Expression expression, String exprStr) {
		String retStr = exprStr;

		if (expression.m_type != null) {
			// Terminal:
			switch (expression.m_type) {
			case IMMEDIATE:
				retStr += "[i 0x" + Integer.toHexString(expression.m_value)
						+ "(" + expression.m_value + ")]";
				break;

			case REGISTER:
				retStr += "[r" + expression.m_address + " ";
				if (expression.m_status == Status.UNDEFINED) {
					retStr += "UNDEF";
				} else {
					retStr += expression.m_value;
				}
				retStr += "]";
				break;

			case MEMORY:
				assert (expression.m_numOperands == 2);
				retStr += "m";
				retStr = createExpressionString(expression.m_operands[0],
						retStr);
				retStr = createExpressionString(expression.m_operands[1],
						retStr);
				break;

			default:
				assert false;
				break;
			}
		} else {
			// Node:
			switch (expression.m_operator) {
			case ADD:
				retStr += "+";
				break;
			case SUB:
				retStr += "-";
				break;
			case AND:
				retStr += "&";
				break;
			case ANDNOT:
				retStr += "&~";
				break;
			case ASHR:
				retStr += ">>";
				break;
			case ASHL:
				retStr += "<<";
				break;
			case BITREV:
				retStr += "bitrev";
				break;
			case BYTEREV:
				retStr += "byterev";
				break;
			case CLZ:
				retStr += "clz";
				break;
			case CRC32:
				retStr += "crc32";
				break;
			case CRC8_SCRDEST:
				retStr += "crc8_scrdest";
				break;
			case CRC8_DEST:
				retStr += "crc8_dest";
				break;
			case DIVS:
				retStr += "divs";
				break;
			case DIVU:
				retStr += "divu";
				break;
			case LTS:
				retStr += "<";
				break;
			case LTU:
				retStr += "<";
				break;
			case GT:
				retStr += ">";
				break;
			case NEG:
				retStr += "-";
				break;
			case NOT:
				retStr += "~";
				break;
			case OR:
				retStr += "|";
				break;
			case EQ:
				retStr += "==";
				break;
			case MACCU_LOW:
				retStr += "maccu_l";
				break;
			case MACCU_HIGH:
				retStr += "maccu_h";
				break;
			case MACCS_LOW:
				retStr += "maccs_l";
				break;
			case MACCS_HIGH:
				retStr += "maccs_h";
				break;
			}

			for (int opIndex = 0; opIndex < expression.m_numOperands; ++opIndex) {
				retStr += "(";
				retStr = createExpressionString(expression.m_operands[opIndex],
						retStr);
				retStr += ")";
			}
		}
		return retStr;
	}

	final int sext(int x, int n) {
		if (n > 0) {
			long signBit = (x >> (n - 1)) & 0x1;
			long mask = 0xffffffff << n;
			if (signBit == 1)
				x = (int) (x | mask);
			else
				x = (int) (x & ~mask);
		}
		return x;
	}

	long macc() throws Exception {
		long result = (long) resolve(m_operands[0])
				* (long) resolve(m_operands[1]);
		result = result
				+ (((long) resolve(m_operands[2]) << 32) | (long) resolve(m_operands[3]));
		return result;
	}
}
