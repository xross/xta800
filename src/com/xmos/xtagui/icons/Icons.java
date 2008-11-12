package com.xmos.xtagui.icons;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

public class Icons {
    private static Map<String, Image> m_imageCache = new HashMap<String, Image>();
	
	public static Image getIcon(String iconName) {
		if (m_imageCache.containsKey(iconName)) {
			return m_imageCache.get(iconName);
		}		
		
		InputStream imageStream = Icons.class.getResourceAsStream(iconName + ".png");		
		if (imageStream != null) {
			Display display = PlatformUI.getWorkbench().getDisplay();
		    Image image = new Image(display, imageStream);
		    m_imageCache.put(iconName, image);
		    return image;
		}
		return null;
	}
}
