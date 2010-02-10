package com.xmos.xta.ui.dialogs;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.Engine;

class SettingsContentProvider implements IStructuredContentProvider {
	public Object[] getElements(Object inputElement) {
		Engine engine = (Engine)inputElement;
		XeLoader xeLoader = engine.getXeLoader();

		Set<Integer> coreNumbers = new TreeSet<Integer>();
		coreNumbers.add(-1); // Add a row for the frequency
		for (int nodeNumber : xeLoader.getNodeNumbers()) {
			coreNumbers.addAll(xeLoader.getCoreNumbers(nodeNumber));
		}
		
		Integer ret[] = new Integer[coreNumbers.size()];
		coreNumbers.toArray(ret);
		return ret;
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		
	}
}
