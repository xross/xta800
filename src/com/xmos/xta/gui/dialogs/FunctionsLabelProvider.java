package com.xmos.xta.gui.dialogs;

import org.eclipse.jface.viewers.LabelProvider;

public class FunctionsLabelProvider extends LabelProvider {
	public String getText(Object element) {
		return (String)element;
	}
}
