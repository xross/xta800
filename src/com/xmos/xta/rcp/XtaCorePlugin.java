package com.xmos.xta.rcp;
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
		getPreferenceStore().setDefault("LastDir", "/home/");
	}

	public static XtaCorePlugin getDefault(){
		return m_plugin;
	}
	
}
