package com.xmos.xeobjectdisplayer;


import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Style;
import com.xmos.xeobject.Line;
import com.xmos.xeobject.Line.DisplayType;

public class LineHeaders extends JTextPane implements MouseListener, MouseMotionListener {
    
    private static final long   serialVersionUID = 1L;
    private ArrayList<Line>     m_lines;
    private Style               m_style;
    private Displayer           m_displayer;
    private static final int    LINE_LENGTH = 8;
    private JTextPane           m_this;
    private Document            m_document;
    
    public LineHeaders(XTextPane textPane, Font font, ArrayList<Line> lines,  Displayer displayer, Style style) {
        super();
		addMouseListener(this);
		addMouseMotionListener(this);
		this.setPreferredSize(new Dimension(80, 10));
		m_lines = lines;
		m_style = style;
		m_displayer = displayer;
		m_this = this;
		m_document = this.getStyledDocument();
        renderFileToDoc();
    }
    
    public void renderFileToDoc(){
        //also have to add the code folding buttons and mouse over highlighting
        for(Line line : m_lines){
            insertLine(line, m_document.getLength());
        }
    }
    
    public void insertLine(Line line, int location){
        String lineNumber ="";
            if(line.getLineNumberType()==DisplayType.decimal){
                lineNumber = Integer.toString(line.getLineNumber());
            } else if(line.getLineNumberType()==DisplayType.hex){
                lineNumber = "0x" + Integer.toHexString(line.getLineNumber());
            } else if(line.getLineNumberType()==DisplayType.none){
                lineNumber = "";
            } else {
                assert(false);
            }
            while(lineNumber.length()<LINE_LENGTH){
                lineNumber+=" ";
            } 
            assert(lineNumber.length() == 8);
            try {
                m_document.insertString(location, lineNumber+"\n", m_style);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
    }
    
    public void remove(int start, int ammount){
        try {
            m_document.remove(start, ammount);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    
    public void mousePressed(MouseEvent e) {
       Document doc = m_this.getDocument();
       DefaultStyledDocument hdoc = (DefaultStyledDocument) doc;
       int pos = m_this.viewToModel(e.getPoint());
       int line =hdoc.getParagraphElement(pos).getEndOffset()/(LINE_LENGTH+1);
       
       foldChange(m_lines.get(line-1));
    }
    
    private void foldChange(Line line){
        
        if(!line.getShowChildern()){
            System.out.println("unfolding");
            m_displayer.unfoldLine(line);
        } else {
            System.out.println("folding");
            m_displayer.foldLine(line);
        }
    }
    
    
    
    public void mouseMoved(MouseEvent e) {
        //want to highlight the text that the mouse is over and
        //highlight the code folding button
        
        
    }
    
   /*
    public void paint(Graphics g) {
        
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        m_foldButtons.clear();
        
        this.setPreferredSize(new Dimension(WIDTH, m_textPane.getHeight()));
        
        // Paint the background white
        g2.setColor(Color.white);
        g2.fillRect(0, 0, WIDTH, m_textPane.getHeight());
        
        // Draw a horizontal grey line
        g2.setColor(Color.lightGray);
        g2.drawLine(getWidth()-1, 0, getWidth()-1, getHeight());
        
        // Draw the numbers
        g2.setColor(Color.black);
        g2.setFont(m_font);
        int y = 1;
        
        int height = (int)g2.getFontMetrics(m_font).getHeight();
        int lineNo = 0;
        while (lineNo < m_lines.size()){   
            Line line = m_lines.get(lineNo);
            // Draw the header string
            String lineName = m_displayer.getTitle()+ ":"+line.getLineAttributes().getHeader();
            
            Font temp = g2.getFont();
            if (line.getLineAttributes().isHeaderBold()){
                g2.setFont(new Font(g2.getFont().getName(), Font.BOLD, g2.getFont().getSize()));
            }
            
//           if (lineName.equals(GuiConfig.startLine)){
//               g2.setColor(Color.green);
//            } else if (lineName.equals(GuiConfig.endLine)){
//                g2.setColor(Color.red);
//            } else if (line.getLineAttributes().isAssembly()) {
//                g2.setColor(Color.blue);
//            } else {
//                g2.setColor(Color.black);
//            }
            
            g2.drawString(line.getLineAttributes().getHeader(), 0, height*y);
            g2.setFont(temp);
            g2.setColor(Color.black);
            
            // Draw the fold or unfold button
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            if (line.getLineAttributes().isFoldable()) {
                
                if (line.getLineAttributes().isFoldButtonHighlighted()) {
                    g2.setColor(Color.black);
                } else {
                    g2.setColor(Color.gray);
                }
                
                int y_ = (height*y)-(height/2);
                g2.drawOval(getWidth()-12, y_, 8, 8);
                g2.drawLine(getWidth()-10, y_+4, getWidth()-6, y_+4);
                if((line.getLineAttributes().isSourceFolded() && line.getLineAttributes().isAssemblyFolded()))
                    g2.drawLine(getWidth()-8, y_+2, getWidth()-8, y_+6);
                m_foldButtons.add(new FoldButton(new Rectangle(getWidth()-12, y_, 8, 8), line));
            } else if (line.getLineAttributes().isPausable()) {
                
                
                if(line.getLineAttributes().isHeaderBold()){
                    int y_ = (height*y)-(height/2);
                    g2.setColor(Color.orange.brighter());
                    g2.fillOval(getWidth()-PAUSEWIDTH, y_-2, 12, 12);
                    g2.setColor(Color.red);
                    g2.fillOval(getWidth()-12, y_, 8, 8);

                } else {
                    int y_ = (height*y)-(height/2);
                    g2.setColor(Color.red);
                    g2.fillOval(getWidth()-12, y_, 8, 8);
                }
                
            }
            
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_OFF);     
            y ++;
            lineNo++;
        }
        
        revalidate();
        
    }

	public void mousePressed(MouseEvent e) {
	   
//	    m_engine = m_displayer.getEngine();
	    boolean clickedButton = false;
	    
	    //first handle the code folding/unfolding action
	    for(FoldButton b : m_foldButtons) {
			if(b.containsPoint(e.getX(), e.getY())) {
			    clickedButton = true;
				if (b.getLine().getLineAttributes().isSourceFolded() &&b.getLine().getLineAttributes().isAssemblyFolded() ) {
					m_displayer.unfoldLine(b.getLine());
				} else {
					m_displayer.foldLine(b.getLine());
				}
				return;
			}
		}
	    if (clickedButton)
	        return;
	    //otherwise set the start or finish point
	    int lineNo = e.getY()/this.getFontMetrics(m_font).getHeight();
	    
	    final Line mouseOverLine = m_lines.get(lineNo); 
	    if(mouseOverLine.getLineAttributes().isFoldable() || mouseOverLine.getLineAttributes().isAssembly()){

	        if(e.getX()< WIDTH - PAUSEWIDTH){
	            String line = "";
	            line += m_displayer.getTitle() + ":" + mouseOverLine.getLineAttributes().getHeader();
//	            if(e.getButton()==1){
//	                GuiConfig.startLine = line; 
//	            } else if(e.getButton()==3){
//	                GuiConfig.endLine = line; 
//	            }
	        } else {
	            if(mouseOverLine.getLineAttributes().isPausable()){
    	            JPopupMenu popup = new JPopupMenu();
    	            final JSpinner cyclesSpinner = new JSpinner();
    	            final JSpinner timeSpinner = new JSpinner();
    	            int count = 4;
    	            SpinnerModel timeModel = new SpinnerNumberModel(count,0,Integer.MAX_VALUE,1); 
    	            SpinnerModel cycleModel = new SpinnerNumberModel(count,0,Integer.MAX_VALUE,1); 
    	            
    	            
    	            cyclesSpinner.setEditor(new JSpinner.NumberEditor(cyclesSpinner,"#############"));
    	            ((JSpinner.DefaultEditor)cyclesSpinner.getEditor()).getTextField().addKeyListener(new KeyAdapter(){
                        public void keyReleased(KeyEvent e) {
                            String text = ((JSpinner.DefaultEditor)cyclesSpinner.getEditor()).getTextField().getText();
                            try {
                                int cycles = Integer.parseInt(text);
                                double timeTaken = 0.0;     
//                                if (m_engine.getConfig().m_numThreads > 4) {
//                                    timeTaken = ((m_engine.getConfig().m_numThreads * 1000) / m_engine.getConfig().m_frequency) * cycles;
//                                }
//                                else {
//                                    timeTaken = ((4.0 * 1000) / m_engine.getConfig().m_frequency) * cycles;
//                                }
                                timeSpinner.setValue(timeTaken);
                            } catch (NumberFormatException e1) {
                            }
                        }

    	            });
    	            
    	            timeSpinner.setEditor(new JSpinner.NumberEditor(timeSpinner,"#############"));
    	            ((JSpinner.DefaultEditor)timeSpinner.getEditor()).getTextField().addKeyListener(new KeyAdapter(){
                        public void keyReleased(KeyEvent e) {
                            String text = ((JSpinner.DefaultEditor)timeSpinner.getEditor()).getTextField().getText();
                            try {
                                int time = Integer.parseInt(text);
                                int cycles = 0;
//                                if (m_engine.getConfig().m_numThreads > 4) {
//                                    cycles = ((int)((time * m_engine.getConfig().m_frequency) / (m_engine.getConfig().m_numThreads * 1000)));
//                                }
//                                else {
//                                    cycles =((int)((time * m_engine.getConfig().m_frequency) / (4 * 1000)));
//                                }
                                
                                cyclesSpinner.setValue(cycles);
                            } catch (NumberFormatException e1) {
                            }
                        }
                    });
    	            JPanel spinners = new JPanel(new GridLayout(2,2));
    	            
    	            JMenuItem mi = new JMenuItem("Set");
    	            cyclesSpinner.setPreferredSize(new Dimension(120, 20));
    	            
    	            cyclesSpinner.setModel(cycleModel);
    	            timeSpinner.setModel(timeModel);
    	            JLabel timeLbl = new JLabel("Time (us):");
    	            spinners.add(timeLbl);
    	            
    	            spinners.add(timeSpinner);
    	            JLabel cyclesLbl = new JLabel("Thread Cycles:");
                    spinners.add(cyclesLbl);
    	            spinners.add(cyclesSpinner);
    	            
    	            
    	            mi.addMouseListener(new MouseAdapter(){
    	                public void mousePressed(MouseEvent e) {
    	                    try {
    	                        cyclesSpinner.commitEdit();
    	                        timeSpinner.commitEdit();
    	                    } catch (ParseException e1) {
    	                        e1.printStackTrace();
    	                    }
    	                    int pc = Integer.parseInt(mouseOverLine.getLineAttributes().getHeader().substring(2), 16);
    	                    int cycleCount = (Integer)cyclesSpinner.getValue();
//    	                    boolean success = m_engine.setInstructionNumCycles(pc, cycleCount);
//    	                    if(!success){
//    	                        JOptionPane.showMessageDialog(m_lineHeaders,
//    	                                "Error: " + m_engine.getErrorString(),
//    	                                "Engine Error",
//    	                                JOptionPane.ERROR_MESSAGE);
//    	                    }
    	                }
    	            });
    	            popup.add(spinners);
                    popup.add(mi);
    	            this.add(popup);
    	            popup.setOpaque(true);
    	            popup.setLightWeightPopupEnabled(true);
    	            popup.show( (JComponent)e.getSource(), e.getX(), e.getY() );
	            }
	        }

	    }
	
	}

	public void mouseMoved(MouseEvent e) {
	   
	    for(Line line : m_lines){
	        line.getLineAttributes().setHeaderBold(false);
	    }
	    if (e.getY()>0){
	        if (e.getY()/this.getFontMetrics(m_font).getHeight() < m_lines.size()){
        	    Line mouseOverLine = m_lines.get(e.getY()/this.getFontMetrics(m_font).getHeight());
        	    if (mouseOverLine.getLineAttributes().isFoldable() || mouseOverLine.getLineAttributes().isAssembly()){
        	        mouseOverLine.getLineAttributes().setHeaderBold(true);
        	    }
    	    }
    	}
		for (FoldButton b : m_foldButtons) {
			if (b.containsPoint(e.getX(), e.getY())) {
				b.getLine().getLineAttributes().setFoldButtonHighlighted(true);
			} else {
				b.getLine().getLineAttributes().setFoldButtonHighlighted(false);
			}
		}
		repaint();
		
	}
	*/
	public void mouseEntered(MouseEvent arg0) {}

	public void mouseExited(MouseEvent arg0) {}

	public void mouseClicked(MouseEvent arg0) {	}

	public void mouseReleased(MouseEvent arg0) {}

	public void mouseDragged(MouseEvent arg0) {}



    
}

