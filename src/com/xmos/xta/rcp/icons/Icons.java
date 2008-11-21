package com.xmos.xta.rcp.icons;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class Icons {
    private static Map<String, Image> m_imageCache = new HashMap<String, Image>();
	
	public static Image getIcon(String iconName) {
		if (m_imageCache.containsKey(iconName)) {
			return m_imageCache.get(iconName);
		}		
		
		ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
				"tools_xta", "icons/" + iconName + ".png");

		if (imageDescriptor != null) {
		    Image image = imageDescriptor.createImage();
		    m_imageCache.put(iconName, imageDescriptor.createImage());
		    return image;
		}
		return null;
	}
}
