package com.xmos.xta.gui;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


public class XtaCorePlugin extends AbstractUIPlugin {
	private static XtaCorePlugin m_plugin;
	
	public XtaCorePlugin() {
		super();
		m_plugin = this;
	}
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		getPreferenceStore().setDefault("LastDir", "");
		getPreferenceStore().setDefault("History", "");
	}

	public static XtaCorePlugin getDefault(){
		return m_plugin;
	}
	
}
