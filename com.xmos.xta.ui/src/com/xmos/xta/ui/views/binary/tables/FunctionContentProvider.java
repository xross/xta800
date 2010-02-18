package com.xmos.xta.ui.views.binary.tables;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.xmos.libs.xeloader.SymbolTable;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.ui.GuiEngine;

public class FunctionContentProvider implements IStructuredContentProvider {
	public Object[] getElements(Object inputElement) {
	    
		int nodeNumber = GuiEngine.getEngine().getNodeNumber();
		int coreNumber = GuiEngine.getEngine().getCoreNumber();
		
		assert(inputElement instanceof XeLoader);
		SymbolTable symbolTable = ((XeLoader)inputElement).getSymbolTable(nodeNumber, coreNumber);

	    Collection<TableElement> elements = new ArrayList<TableElement>();
		for (String name : symbolTable.getFunctionNames()) {
			long address = symbolTable.symbolAddress(name);
			long size = symbolTable.functionSize(address);			
			elements.add(new TableElement(name, address, size));
		}
		return elements.toArray();
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}
