package com.xmos.xta.ui.dialogs;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public interface XtaImpl {
    Control createDialogArea(Composite parent, ButtonEnabler enabler);
    void buttonPressed(int id);
    void updateButtonStatus();
}
