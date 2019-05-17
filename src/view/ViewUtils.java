package view;

import java.awt.Container;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.Spring;
import javax.swing.SpringLayout;

public class ViewUtils
{
    //Common JLayeredPane layers
    public static final Integer TILE_LAYER = new Integer(10);
    public static final Integer SCORE_LAYER = new Integer(20);
    public static final Integer INTERFACE_LAYER = new Integer(30);
    
    //A cache of the images loaded via getImageIcon()
    private static HashMap<String,ImageIcon> iconCache = new HashMap<String,ImageIcon>();

    //Not instantiable
    private ViewUtils(){}
    
    //Sets position and size of comp as percentages of parent
    public static void setPercentage(Container parent, JComponent comp, float x, float y, float w, float h)
    {
        if(!(parent.getLayout() instanceof SpringLayout))
            throw new IllegalArgumentException("The parent container must be using a SpringLayout.");
        SpringLayout layout = (SpringLayout) parent.getLayout();
        Spring width = layout.getConstraint(SpringLayout.WIDTH, parent);
        Spring height = layout.getConstraint(SpringLayout.HEIGHT, parent);
        SpringLayout.Constraints c = layout.getConstraints(comp);
        c.setX(Spring.scale(width, x));
        c.setY(Spring.scale(height, y));
        c.setWidth(Spring.scale(width, w));
        c.setHeight(Spring.scale(height, h));
    }

    //Centralized image loading code, with caching to reduce file access time.
    public static ImageIcon getImageIcon(String path)
    {
        if(iconCache.containsKey(path))
            return iconCache.get(path);
        java.net.URL uri = ViewUtils.class.getResource(path);
        if(uri == null)
            throw new IllegalArgumentException("Invalid path " + path + ": file not found.");
        ImageIcon icon = new ImageIcon(uri);
        iconCache.put(path, icon);
        return icon;
    }
}
