package com.xmos.xta.swing.codewindow;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;

public class LinkController
implements MouseMotionListener, MouseListener {

	public LinkController() {
		
	}
	
  public void mouseMoved(MouseEvent ev) {
    JTextPane editor = (JTextPane) ev.getSource();            
    if (! editor.isEditable()) {

      Point pt = new Point(ev.getX(), ev.getY());
      int pos = editor.viewToModel(pt);
      
      if (pos >= 0) {                    
        Document doc = editor.getDocument();
        if (doc instanceof DefaultStyledDocument) {
          //DefaultStyledDocument hdoc = (DefaultStyledDocument) doc;
          //Element e = hdoc.getCharacterElement(pos);
          //AttributeSet a = e.getAttributes();
          //String href = (String) a.getAttribute(HTML.Attribute.HREF);
          
          //if(href!=null)
          //System.out.println(href);
          
          /*if (href != null) {
            if (isShowToolTip())
              setToolTipText(href);

            if (isShowCursorFeedback()) {    
              if (getCursor() != handCursor)
                setCursor(handCursor);
            }
          } 
          else {
            if (isShowToolTip())
              setToolTipText(null);

            if (isShowCursorFeedback()) {    
              if (getCursor() != defaultCursor)
                setCursor(defaultCursor);
            }*/
          }
        }
      }
      /*else {
        setToolTipText(null);
      } */    
    }

  public void mouseClicked(MouseEvent e) {
    JTextPane editor = (JTextPane) e.getSource();
    Document doc = editor.getDocument();
    DefaultStyledDocument hdoc = (DefaultStyledDocument) doc;
            
    if (! editor.isEditable()) {
      Point pt = new Point(e.getX(), e.getY());
      int pos = editor.viewToModel(pt);
      if (pos >= 0) {
    	  Element el = hdoc.getCharacterElement(pos);
          AttributeSet a = el.getAttributes();
          String href = (String) a.getAttribute(HTML.Attribute.HREF);
          
          if(href!=null)
    	  System.out.println(href);
        // get the element at the pos
// check if the elemnt has the HREF
// attribute defined
// if so notify the HyperLinkListeners
      }
    }
  }


	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}


