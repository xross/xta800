package com.xmos.xta.swing;

public class Box {
    private int x, y, width, height;
    private Object parent;
    public Box( int x, int y, int width,int height, Object parent) {
        super();
        this.height = height;
        this.width = width;
        this.x = x;
        this.y = y;
        this.parent = parent;
    }
   
    public boolean withinBox(int x, int y){
        if(x>this.x && x<this.x + width && y > this.y && y< this.y+height){
            return true;
        }
        return false;
    }
    
    public String toString(){
        return "" + x + " " + y + " " + width + " " + height;
    }
    
    public Object getParent(){
        return parent;
    }
    
}