package com.xmos.xta.ui.views.binary.tables;

public class TableElement {
    private String m_name;
    private long m_address;
    private long m_size;
    
    TableElement(String name, long address, long size) {
    	m_name = name;
    	m_address = address;
    	m_size = size;
    }
    
    String getName()  { return m_name; }
    long getAddress() { return m_address; }
    long getSize()    { return m_size; }
}
