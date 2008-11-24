package com.xmos.xta.swing.trees;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import com.xmos.xta.swing.core.RouteNode;

public class TreeBarRenderer  implements TreeCellRenderer {
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean selected, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        if ((value != null) && (value instanceof DefaultMutableTreeNode)) {
          Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
          if(userObject instanceof RouteNode){
              RouteNode rn = (RouteNode)userObject;
              renderer.setLeafIcon(rn.getIcon());
              renderer.setOpenIcon(rn.getIcon());
              renderer.setClosedIcon(rn.getIcon());
              renderer.setToolTipText(rn.getToolTipText());
              value = rn.getDescription();
          } else if(userObject instanceof String){
              value = (String)userObject;
          }
        }
        return renderer.getTreeCellRendererComponent(tree,
            value, selected, expanded, leaf, row, hasFocus);
 
    }

}

