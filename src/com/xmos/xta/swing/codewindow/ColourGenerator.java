package com.xmos.xta.swing.codewindow;

import java.awt.Color;

public class ColourGenerator {
    private double dR, dG, dB;
    public enum Policy {Gradient, HighlightMax};
    
    public Color m_darkColour;
    public Color m_lightColour;
    private Policy m_colourPolicy;
    
    public ColourGenerator(Color startColour, Color endColour){
        m_darkColour = endColour;
        m_lightColour = startColour;
        m_colourPolicy = Policy.Gradient;
    }
    public ColourGenerator(){
        m_colourPolicy = Policy.Gradient;
    }
    public Color getHighlightedColour(double shade){
        
        if(shade == 0) return Color.white;
        if (shade <0 || shade > 1) return Color.black;
        
        if (m_colourPolicy==Policy.HighlightMax){
            return highlightMax(shade);
        } else if (m_colourPolicy==Policy.Gradient){
            return smoothGradient(shade);
        }
        else {
            System.err.println("No colour selection policy selected.");
            return Color.green;
        }  
    }
    
    private Color smoothGradient(double shade){
        dR = m_darkColour.getRed()   - m_lightColour.getRed();
        dG = m_darkColour.getGreen() - m_lightColour.getGreen();
        dB = m_darkColour.getBlue()  - m_lightColour.getBlue();
        
        
        int r = m_lightColour.getRed()   + (int)(dR * shade);
        int g = m_lightColour.getGreen() + (int)(dG * shade);
        int b = m_lightColour.getBlue()  + (int)(dB * shade);
        return new Color(r,g,b);
    }
    
    private Color highlightMax(double shade){
        if(shade == 1){
            return m_darkColour;
        } else {
            return smoothGradient(0.1);
        }
    }

	public void setColours(Color c1, Color c2) {
    	this.m_lightColour = c1;
    	this.m_darkColour = c2;
	}
}
