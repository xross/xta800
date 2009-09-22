package com.xmos.xta.ui.dialogs;

import org.eclipse.jface.viewers.LabelProvider;

public class FunctionsLabelProvider extends LabelProvider {
	public String getText(Object element) {
		return (String)element;
	}
}
