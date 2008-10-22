package com.xmos.xta.gui.codestructure;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import com.xmos.xta.gui.GlobalConfig;
import com.xmos.xta.gui.PathUpdater;
import com.xmos.xta.gui.core.PathStatus;
import com.xmos.xta.gui.core.RootPath;
import com.xmos.xta.gui.core.RouteNode;

public class CodeStructure implements PathUpdater {

    private JPanel                      m_circleDisplayerPanel;
    private TreePath                    m_selectedPath;
    private DefaultMutableTreeNode      m_centerNode;
    private PathStatus                  m_pathStatus;
    private ArrayList<Shape>            m_clickAreas;
    private HashMap<Shape, RouteNode>   m_shapes;
    private GlobalConfig                m_globalConfig;
    private ColourMode                  m_colourMode;
    private boolean                     m_selectLikeNodes;
    
    private enum ColourMode {
        byNodeType,
        byLikeNodes
    }
    
    public CodeStructure(PathStatus pathStatus, GlobalConfig globalConfig) {
        super();
        m_colourMode = ColourMode.byNodeType;
        m_globalConfig = globalConfig;
        m_pathStatus    = pathStatus;
        m_clickAreas    = new ArrayList<Shape>();
        m_shapes        = new HashMap<Shape, RouteNode>();
        m_circleDisplayerPanel = new JPanel(){
            private static final long serialVersionUID = 1L;
            public void paint(Graphics g){
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                draw(g2);
            }
        };

        m_circleDisplayerPanel.addMouseMotionListener(new MouseMotionAdapter(){
            public void mouseMoved(MouseEvent e) {
                mouseMotionEvent(e); 
            }
        });
        m_circleDisplayerPanel.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e) {
                mouseClickEvent(e);
            }

        });
    }

    public void mouseClickEvent(MouseEvent e){

        //get the object from the node and select the path
        if(e.getButton() == 1 ){
            for(Shape c : m_clickAreas){
                    if(c.contains(e.getPoint())){
                        DefaultMutableTreeNode selectedNode =  
                            findTreeNode((TreeNode)m_pathStatus.getTreeModel().getRoot(), m_shapes.get(c));
                        if(e.getClickCount() == 2 )m_pathStatus.setSelectedNodeAsCenterNode();
                        pushSelectedNode(selectedNode);
                        return;
                    }
                }
        } else {
            for(Shape c : m_clickAreas){
                if(c.contains(e.getPoint())){
                    DefaultMutableTreeNode selectedNode =  
                        findTreeNode((TreeNode)m_pathStatus.getTreeModel().getRoot(), m_shapes.get(c));
                    RouteNode node = (RouteNode)selectedNode.getUserObject();
                    pushSelectedNode(selectedNode);
                    
                    JPopupMenu popup = node.getPopupMenu();
                    JMenuItem  selectLikeNodes = new JMenuItem();
                    if(!m_selectLikeNodes){
                        selectLikeNodes.setText("Show like " + node.getTypeName() + "s");
                    } else {
                        selectLikeNodes.setText("Select only single " + node.getTypeName() + "s");
                    }
                    selectLikeNodes.addMouseListener(new MouseAdapter(){
                        public void mousePressed(MouseEvent e) {
                            setSelectLikeNodes();
                        }
                    });
                    
                    JMenuItem  colorMode = new JMenuItem();
                    colorMode.addMouseListener(new MouseAdapter(){
                        public void mousePressed(MouseEvent e) {
                            setColourMode();
                        }
                    });
                    if(m_colourMode == ColourMode.byNodeType){
                        colorMode.setText("Colour nodes by likeness");
                    } else {
                        colorMode.setText("Colour nodes by type");
                    }
                    
                    popup.addSeparator();
                    popup.add(selectLikeNodes);
                    popup.add(colorMode);
                    popup.setOpaque(true);
                    popup.setLightWeightPopupEnabled(true);
                    popup.show( (JComponent)e.getSource(), e.getX(), e.getY() );
                    return;
                }
            }
        }
    }
    
    private void setColourMode(){
        if(m_colourMode == ColourMode.byLikeNodes){
            m_colourMode = ColourMode.byNodeType;
        } else {
           m_colourMode = ColourMode.byLikeNodes; 
        }
        m_circleDisplayerPanel.repaint();
    }
    
    private void setSelectLikeNodes(){
        m_selectLikeNodes = !m_selectLikeNodes;
        m_circleDisplayerPanel.repaint();
    }

    private DefaultMutableTreeNode findTreeNode(TreeNode node, Object leaf ){
        if (((DefaultMutableTreeNode)node).getUserObject().equals(leaf)){
            return (DefaultMutableTreeNode) node;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            DefaultMutableTreeNode n = findTreeNode(node.getChildAt(i), leaf);
            if(n != null ) return n;
        }
        return null;
    }

    public void mouseMotionEvent(MouseEvent e){
        for(Shape c : m_clickAreas){
            if(c.contains(e.getPoint())){
                String toolTipText = m_shapes.get(c).getToolTipText();
                m_circleDisplayerPanel.setToolTipText(toolTipText);
                return;
            }
        }
    }

    public void notifyUpdateState() {
        m_circleDisplayerPanel.repaint();
    }

    public void pushSelectedNode(DefaultMutableTreeNode selectedNode) {
        m_pathStatus.setRouteSelection(selectedNode, null);
    }

    public void setSelectedPath(TreePath selectedPath, DefaultMutableTreeNode centerNode) {
        if(selectedPath == null){
            m_selectedPath = null;
            m_centerNode = null;
        } else {
            m_selectedPath = selectedPath;
            m_centerNode = centerNode;
        }
        m_circleDisplayerPanel.repaint();
    }

    public JPanel getJPanel(){
        return m_circleDisplayerPanel;
    }

    private int searchTree(DefaultMutableTreeNode node){
        int max =0;
        if(node.isLeaf()) return 1;
        for(int i=0;i<node.getChildCount();i++){
            max = Math.max(searchTree((DefaultMutableTreeNode)node.getChildAt(i)), max);
        }
        return max+1;
    }
    
    private int findTreeSize(DefaultMutableTreeNode node){
        if(node == null) return 0;
        int sum=0;
        if(node.isLeaf()) return 1;
        for(int i=0;i<node.getChildCount();i++){
            sum += findTreeSize((DefaultMutableTreeNode)node.getChildAt(i));
        }
        return sum+1;
    }

    private void draw(Graphics2D g2){
        m_clickAreas.clear();

        g2.setColor(Color.white);
        g2.fillRect(0, 0, m_circleDisplayerPanel.getWidth(),m_circleDisplayerPanel.getHeight() );

        int height = m_circleDisplayerPanel.getHeight()/2;
        int width = m_circleDisplayerPanel.getWidth()/2;

        Point center = new Point(width,height);

        if(findTreeSize(m_centerNode) > m_globalConfig.getM_codeStructureObjects()){
            Font font = new Font(g2.getFont().getFamily(), Font.PLAIN, 12);
            g2.setFont(font);
            g2.setColor(Color.black);
            String error = "Code structure too Complicated: select a smaller region";
            Rectangle r = g2.getFontMetrics().getStringBounds(error, g2).getBounds();
            g2.drawString(error, (int)(width - r.getWidth()/2), (int)(height - r.getHeight()/2));
            return;
        }

        if(m_pathStatus.getTreeModel()==null) return;

        if(m_selectedPath == null){
            m_selectedPath = m_pathStatus.getSelectedPath();
            m_centerNode = m_pathStatus.getCenterNode();
        }
        assert(m_selectedPath!=null);
        assert(m_centerNode!=null);

        int along = 30;
        int pathIndex = 0;

        ArrayList<DefaultMutableTreeNode> pathList = new ArrayList<DefaultMutableTreeNode>();
        for(Object o : m_selectedPath.getPath()){
            pathList.add((DefaultMutableTreeNode)o);
        }
        assert(pathList.size()>0);

        if(pathList.size()>1){
            while(!pathList.get(pathIndex).equals(m_centerNode)){
                Point crumCenter = new Point(along, 30);
                RouteNode crumNode = (RouteNode) pathList.get(pathIndex).getUserObject();
                Shape shape = crumNode.getCrumShape(16, crumCenter);
                g2.setColor(crumNode.getColour());
                g2.fill(shape);
                g2.setColor(crumNode.getColour().darker());
                g2.draw(shape);
                m_clickAreas.add(shape);
                m_shapes.put(shape, crumNode);
                along += 30;
                pathIndex++;
                assert(pathIndex<pathList.size());
            }   
        }
        int spacing = 0;
        int depth = searchTree(m_centerNode) - 2;
        if(depth == 0){
            spacing = (int) (Math.min(width,height)-10); 
        } else {
            spacing = (int) ((Math.min(width,height)-10)/(depth)); 
        }
        drawConcentricCircles(g2, center, depth+1, spacing);
        drawRouteNode(g2, m_centerNode, 0, center, center, center, 0, 360, spacing, false);

    }


    private void drawRouteNode(
            Graphics2D g2, 
            DefaultMutableTreeNode treeNode, 
            double currentDepth,
            Point center, 
            Point selfPoint, 
            Point originPoint,
            double startAngle, 
            double endAngle, 
            int spacing,
            boolean perpendicular){

        boolean sameNodeType = false;
        RouteNode thisNode = (RouteNode)treeNode.getUserObject();
        RouteNode centerNode = (RouteNode)((DefaultMutableTreeNode)m_selectedPath.getLastPathComponent()).getUserObject();

        if(centerNode.getId() == thisNode.getId() && ! centerNode.equals(thisNode) && thisNode.getClass().equals(centerNode.getClass())){
            sameNodeType = true && m_selectLikeNodes;
        }
        boolean childPerpendicular = false;
        if(treeNode.getUserObject() instanceof RootPath){
            childPerpendicular = true;
        }
        int totalPaths = treeNode.getChildCount();
        int nonLeafCount =0;
        for(int i = 0 ;i<totalPaths;i++){
            if(treeNode.getChildAt(i).getChildCount() > 0) nonLeafCount++;
        }
        int nonLeafInstance = 0;
        if(nonLeafCount>0){
            for(int instance=0;instance<treeNode.getChildCount();instance++){
                if(treeNode.getChildAt(instance).getChildCount() > 0){
                    DefaultMutableTreeNode child = (DefaultMutableTreeNode)treeNode.getChildAt(instance);

                    double startChildAngle = startAngle + (double)(nonLeafInstance *(endAngle-startAngle))/(double)nonLeafCount;
                    double endChildAngle = startAngle + (double)((nonLeafInstance + 1)*(endAngle-startAngle))/(double)nonLeafCount;
                    double actualChildAngle = startChildAngle + (endChildAngle-startChildAngle)/2.0;
                    double x = (currentDepth + spacing) * Math.cos(Math.toRadians(actualChildAngle));
                    double y = (currentDepth + spacing) * Math.sin(Math.toRadians(actualChildAngle));

                    Point childPoint = new Point((int)(center.getX() - x), (int)(center.getY() - y));
                    drawRouteNode(g2, child, currentDepth + spacing, center, childPoint, selfPoint, 
                            startChildAngle, endChildAngle, spacing, childPerpendicular);
                    nonLeafInstance++;
                }
            }
        }
        double width = 16;

        boolean selected = false;
        if(m_selectedPath!=null){
            for(Object o :m_selectedPath.getPath()){
                if(o.equals(treeNode)){
                    selected = true;
                }
            }
        }
        
        //now draw a line back to origin
        g2.setColor(Color.black);
        if(selected){
            g2.setStroke(new BasicStroke(4));
        } else {
            g2.setStroke(new BasicStroke(1));
        }
        
        double x = center.getX() - selfPoint.getX();
        double y = center.getY() - selfPoint.getY();
        double dist = Point.distance(center.getX(), center.getY(),selfPoint.getX(), selfPoint.getY());
        if(dist >0){
            if(!perpendicular){

                x=width*x/dist;
                y=width*y/dist;
                Line2D.Double line = new Line2D.Double(originPoint, new Point((int)(selfPoint.getX() + x), (int)(selfPoint.getY()+y)));
                g2.draw(line);
            } else{
                x=spacing*x/dist;
                y=spacing*y/dist;
                Line2D.Double line = new Line2D.Double(selfPoint, new Point((int)(selfPoint.getX() + x), (int)(selfPoint.getY()+y)));
                g2.draw(line);
            }
        }
        
            drawRouteNodePoint(
                    g2,
                    selfPoint, 
                    16, 
                    (RouteNode)treeNode.getUserObject(), 
                    selected ,
                    sameNodeType,
                    center,
                    startAngle, 
                    endAngle , 
                    currentDepth);
    }

    private void drawRouteNodePoint(
            Graphics2D g2, 
            Point startPoint, 
            int width, 
            RouteNode route, 
            boolean selected, 
            boolean sameNodeType,
            Point center, 
            double startAngle, 
            double endAngle, 
            double radius){
        
        Shape shape;
        if(radius > 0){
            shape =  route.getShape(width, startPoint, center, startAngle, endAngle, radius);
        } else {
            shape =  route.getCrumShape(width, startPoint);
        }
        if(sameNodeType){
            g2.setColor(Color.pink);
        } else {
            if(selected){
                g2.setColor(route.getColour().darker());
            } else {
                g2.setColor(route.getColour());
            } 
        }
        g2.fill(shape);
        g2.setColor(route.getColour().darker());
        g2.setStroke(new BasicStroke(1));
        g2.draw(shape);
        m_clickAreas.add(shape);
        m_shapes.put(shape, route);
    }

    private void drawConcentricCircles(Graphics2D g2, Point center, int depth, int spacing){
        float dash1[] = {10.0f};

        BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, 
                BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);
        g2.setStroke(dashed);
        g2.setColor(new Color(233, 233, 233));
        g2.setColor(Color.black);
        for(int i=0;i<depth;i++){
            int radius = 2*i*spacing;
            g2.drawOval((int)center.getX()- (radius)/2, (int)center.getY()- (radius)/2, radius, radius);
        }
    }

}


