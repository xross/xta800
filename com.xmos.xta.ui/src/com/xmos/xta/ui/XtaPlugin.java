package com.xmos.xta.ui;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


public class XtaPlugin extends AbstractUIPlugin {
	private static XtaPlugin m_plugin;
	
	public XtaPlugin() {
		super();
		m_plugin = this;
	}
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		getPreferenceStore().setDefault("LastDir", "");
		getPreferenceStore().setDefault("History", "");
	}

	public static XtaPlugin getDefault(){
		return m_plugin;
	}
	
}
