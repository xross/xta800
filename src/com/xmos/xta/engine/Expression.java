package com.xmos.xta.engine;

public class Expression {
	enum Type{
		IMMEDIATE,
		REGISTER,
		MEMORY,
	}
	enum Status {
		UNDEFINED,
		DEFINED,
	}
	enum OperatorType{
		ADD,
		SUB,
		AND,
		ANDNOT,
		ASHR,
		ASHL,
		BITREV,
		BYTEREV,
		CLZ,
		CRC32,
		CRC8,
		DIVS,
		DIVU,
		LT,
		GT,
		NEG,
		NOT,
		OR,
        EQ,
        MACC_LOW,
        MACC_HIGH,
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
	Expression(int address, int value, Type type, Status status) {
		m_address     = address;
		m_value       = value;
		m_type        = type;
		m_status      = status;
		m_numOperands = 0;
	    m_operands    = new Expression[MAX_NUM_OPERANDS]; 
	}
	
	// Node Constructor
	Expression(OperatorType operator) {
		m_operator    = operator;
		m_numOperands = 0;
        m_operands    = new Expression[MAX_NUM_OPERANDS]; 
	}
	
	// Immediate Constructor
	public Expression(int value) {
		m_address     = 0;
		m_value       = value;
		m_type        = Type.IMMEDIATE;
		m_status      = Status.DEFINED;		
		m_numOperands = 0;	
        m_operands    = new Expression[MAX_NUM_OPERANDS]; 
	}
	
	// Copy Constructor
	Expression(Expression expression) {	
		m_address     = expression.m_address;
		m_value       = expression.m_value;
		m_type        = expression.m_type;
		m_status      = expression.m_status;
		m_operator    = expression.m_operator;
		m_numOperands = 0;
        m_operands    = new Expression[MAX_NUM_OPERANDS]; 		
        
		for (int operIndex = 0; operIndex < expression.m_numOperands; ++operIndex) {
			addOperand(expression.m_operands[operIndex]);
		}		
	}
	
	void addOperand(Expression operand) {
		assert(m_numOperands < MAX_NUM_OPERANDS);
		m_operands[m_numOperands] = operand;
		++m_numOperands;
	}

    Expression getOperand(int operandNum) {
        assert(operandNum < m_numOperands);
        return m_operands[operandNum];
    }
	
	int numOperands() {
	    return m_numOperands;
	}
	
	void resolve() {
		if ((m_numOperands > 0) && canResolve()) {
			m_address     = 0;
			m_value       = resolve(this);
			m_type        = Type.IMMEDIATE;
			m_status      = Status.DEFINED;		
			m_numOperands = 0;	
		}
	}
	
	boolean isResolved() {
		return m_type == Type.IMMEDIATE;
	}

	public int getValue() {
		assert (m_type == Type.IMMEDIATE);
		return m_value;
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
		if (expression.m_type != null) {
			return expression.m_type == Type.IMMEDIATE;
		}
		else {
    		for (int opIndex = 0; opIndex < expression.m_numOperands; ++opIndex) {
            	if (canResolve(expression.m_operands[opIndex]) == false)
            		return false;
    		}
    		return true;
		}
	}
	
	public int resolve(Expression expression) {
		if (expression.m_type != null) {
			assert (expression.m_type == Type.IMMEDIATE);
			return expression.m_value;
		}
		else {
	        long result = 0;
	        
			switch (expression.m_operator) {
            case ADD:
            	assert (expression.m_numOperands == 2);
            	return resolve(expression.m_operands[0]) + resolve(expression.m_operands[1]);

            case SUB:            	
        	    assert (expression.m_numOperands == 2);
        	    return resolve(expression.m_operands[0]) - resolve(expression.m_operands[1]);

            case AND: 
        	    assert (expression.m_numOperands == 2);
        	    return resolve(expression.m_operands[0]) & resolve(expression.m_operands[1]);

            case ANDNOT:
        	    assert (expression.m_numOperands == 2);
        	    return resolve(expression.m_operands[0]) & ~resolve(expression.m_operands[1]);

            case ASHR:  
        	    assert (expression.m_numOperands == 2);
        	    return resolve(expression.m_operands[0]) >> resolve(expression.m_operands[1]);

            case ASHL:              
        	    assert (expression.m_numOperands == 2);
        	    return resolve(expression.m_operands[0]) << resolve(expression.m_operands[1]);

            case BITREV: 
        	    assert (expression.m_numOperands == 1);
        	    return XtaInstruction.bitReverse(resolve(expression.m_operands[0]));

            case BYTEREV:
        	    assert (expression.m_numOperands == 1);
        	    return XtaInstruction.byteReverse(resolve(expression.m_operands[0]));

            case CLZ:    
                assert (expression.m_numOperands == 1);
                return XtaInstruction.countLeadingZeros(resolve(expression.m_operands[0]));

            case CRC32:  
                assert (expression.m_numOperands == 3);
                return XtaInstruction.crc32(resolve(expression.m_operands[0]), 
                        resolve(expression.m_operands[1]),
                        resolve(expression.m_operands[2]));

            case CRC8:               
                assert (expression.m_numOperands == 3);
                return XtaInstruction.crc8(
                        resolve(expression.m_operands[0]), 
                        resolve(expression.m_operands[1]),
                        resolve(expression.m_operands[2]));

            case DIVS:               
                assert (expression.m_numOperands == 2);
                return XtaInstruction.divs(
                        resolve(expression.m_operands[0]), 
                        resolve(expression.m_operands[1]));

            case DIVU:               
                assert (expression.m_numOperands == 2);
                return XtaInstruction.divu(
                        resolve(expression.m_operands[0]), 
                        resolve(expression.m_operands[1]));

            case LT:                 
        	    assert (expression.m_numOperands == 2);
        	    return ((resolve(expression.m_operands[0]) < 
        	             resolve(expression.m_operands[1])) == true) ? 1 : 0;
        	                	    
            case GT:                 
        	    assert (expression.m_numOperands == 2);
                return ((resolve(expression.m_operands[0]) > 
                        resolve(expression.m_operands[1])) == true) ? 1 : 0;

            case NEG:                
        	    assert (expression.m_numOperands == 1);
        	    return -resolve(expression.m_operands[0]);

            case NOT:                
        	    assert (expression.m_numOperands == 1);
        	    return ~resolve(expression.m_operands[0]);

            case OR:                 
        	    assert (expression.m_numOperands == 2);
        	    return resolve(expression.m_operands[0]) | resolve(expression.m_operands[1]);

            case EQ:                 
                assert (expression.m_numOperands == 2);
                return (resolve(expression.m_operands[0]) == resolve(expression.m_operands[1])) ? 1 : 0;
        	    
            case MACC_LOW:
                assert (expression.m_numOperands == 4);
                result = macc();
                return (int)result;

            case MACC_HIGH:
                assert (expression.m_numOperands == 4);
                result = macc();
                return (int)(result >> 32);

            default:
            	assert false;
                return 0;
			}				
		}		
	}
	
	
	 int resolveMax(Expression expression) {
	    
	    if(expression.canResolve()){
	        return expression.resolve(expression);
	    }
	        
        if (expression.m_type != null) {
            assert (expression.m_type == Type.IMMEDIATE);
            return expression.m_value;
        }
        else {
            long result = 0;
            
            switch (expression.m_operator) {
            case ADD:
                assert (expression.m_numOperands == 2);
                return resolveMax(expression.m_operands[0]) + resolveMax(expression.m_operands[1]);

            case SUB:               
                assert (expression.m_numOperands == 2);
                return resolveMax(expression.m_operands[0]) - resolveMax(expression.m_operands[1]);

            case AND: 
                assert (expression.m_numOperands == 2);
                return resolveMax(expression.m_operands[0]) & resolveMax(expression.m_operands[1]);

            case ANDNOT:
                assert (expression.m_numOperands == 2);
                return resolveMax(expression.m_operands[0]) & ~resolveMax(expression.m_operands[1]);

            case ASHR:  
                assert (expression.m_numOperands == 2);
                return resolveMax(expression.m_operands[0]) >> resolveMax(expression.m_operands[1]);

            case ASHL:              
                assert (expression.m_numOperands == 2);
                return resolveMax(expression.m_operands[0]) << resolveMax(expression.m_operands[1]);

            case BITREV: 
                assert (expression.m_numOperands == 1);
                return XtaInstruction.bitReverse(resolveMax(expression.m_operands[0]));

            case BYTEREV:
                assert (expression.m_numOperands == 1);
                return XtaInstruction.byteReverse(resolveMax(expression.m_operands[0]));

            case CLZ:    
                assert (expression.m_numOperands == 1);
                return XtaInstruction.countLeadingZeros(resolveMax(expression.m_operands[0]));

            case CRC32:  
                assert (expression.m_numOperands == 3);
                return XtaInstruction.crc32(resolveMax(expression.m_operands[0]), 
                        resolveMax(expression.m_operands[1]),
                        resolveMax(expression.m_operands[2]));

            case CRC8:               
                assert (expression.m_numOperands == 3);
                return XtaInstruction.crc8(
                        resolveMax(expression.m_operands[0]), 
                        resolveMax(expression.m_operands[1]),
                        resolveMax(expression.m_operands[2]));

            case DIVS:               
                assert (expression.m_numOperands == 2);
                return XtaInstruction.divs(
                        resolveMax(expression.m_operands[0]), 
                        resolveMax(expression.m_operands[1]));

            case DIVU:               
                assert (expression.m_numOperands == 2);
                return XtaInstruction.divu(
                        resolveMax(expression.m_operands[0]), 
                        resolveMax(expression.m_operands[1]));

            case LT:                 
                assert (expression.m_numOperands == 2);
                return ((resolveMax(expression.m_operands[0]) < 
                        resolveMax(expression.m_operands[1])) == true) ? 1 : 0;
                                    
            case GT:                 
                assert (expression.m_numOperands == 2);
                return ((resolveMax(expression.m_operands[0]) > 
                resolveMax(expression.m_operands[1])) == true) ? 1 : 0;

            case NEG:                
                assert (expression.m_numOperands == 1);
                return -resolveMax(expression.m_operands[0]);

            case NOT:                
                assert (expression.m_numOperands == 1);
                return ~resolveMax(expression.m_operands[0]);

            case OR:                 
                assert (expression.m_numOperands == 2);
                return resolveMax(expression.m_operands[0]) | resolveMax(expression.m_operands[1]);

            case EQ:                 
                assert (expression.m_numOperands == 2);
                return (resolveMax(expression.m_operands[0]) == resolveMax(expression.m_operands[1])) ? 1 : 0;
                
            case MACC_LOW:
                assert (expression.m_numOperands == 4);
                result = macc();
                return (int)result;

            case MACC_HIGH:
                assert (expression.m_numOperands == 4);
                result = macc();
                return (int)(result >> 32);

            default:
                assert false;
                return 0;
            }               
        }       
    }
    
	
	private String createExpressionString(Expression expression, String exprStr) {
		String retStr = exprStr;
		
		if (expression.m_type != null) {
		 	// Terminal:
			switch (expression.m_type) {
			case IMMEDIATE:
				retStr += "[i 0x" + Integer.toHexString(expression.m_value)  + "("+expression.m_value + ")]";
				break;
				
			case REGISTER:
				retStr += "[r" + expression.m_address + " ";
				if (expression.m_status == Status.UNDEFINED) {
					retStr += "UNDEF";
				}
				else {					
					retStr += expression.m_value;
				}
				retStr += "]";
				break;
				
			case MEMORY:
				assert (m_operands.length == 2);
				retStr += "m";
				retStr = createExpressionString(expression.m_operands[0], retStr);
				retStr = createExpressionString(expression.m_operands[1], retStr);
				break;
				
			default:
				assert false;
			    break;
			}		    
		}
		else {
			// Node:
            switch (expression.m_operator) {
            case ADD:       retStr += "+"; break;
            case SUB:       retStr += "-"; break;            	
            case AND:       retStr += "&"; break;
            case ANDNOT:    retStr += "&~"; break;
            case ASHR:      retStr += ">>"; break;
            case ASHL:      retStr += "<<"; break;            
            case BITREV:    retStr += "bitrev"; break;
            case BYTEREV:   retStr += "byterev"; break;
            case CLZ:       retStr += "clz"; break;
            case CRC32:     retStr += "crc32"; break;
            case CRC8:      retStr += "crc8"; break;            
            case DIVS:      retStr += "divs"; break;            
            case DIVU:      retStr += "divu"; break;            
            case LT:        retStr += "<"; break;            
            case GT:        retStr += ">"; break;            
            case NEG:       retStr += "-"; break;            
            case NOT:       retStr += "~"; break;            
            case OR:        retStr += "|"; break;            
            case EQ:        retStr += "=="; break;
            case MACC_LOW:  retStr += "macc_l"; break;
            case MACC_HIGH: retStr += "macc_h"; break;
            }
            
    		for (int opIndex = 0; opIndex < expression.m_numOperands; ++opIndex) {
            	retStr += "(";
            	retStr = createExpressionString(expression.m_operands[opIndex], retStr);
        	    retStr += ")";    			
    		}
		}       
		return retStr;
	}
	
	long macc()
	{
	    long  result = (long)resolve(m_operands[0]) * (long)resolve(m_operands[1]);
	    result = result + (((long)resolve(m_operands[2]) << 32) | (long)resolve(m_operands[3]));
	    return result;
	}
}
