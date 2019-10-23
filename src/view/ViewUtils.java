package view;

import java.awt.Container;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.Spring;
import javax.swing.SpringLayout;

/**
 * A collection of static constants and methods used by multiple interface classes
 *
 * @author jsnhlbr5
 *
 */
public class ViewUtils
{
    /**
     * Used by JLayeredPanes as the layer to insert tile images on
     */
    public static final Integer TILE_LAYER = new Integer(10);

    /**
     * Used by JLayeredPanes as the layer to insert buttons on
     */
    public static final Integer INTERFACE_LAYER = new Integer(30);

    /**
     * A cache of the images loaded via getImageIcon()
     */
    private static HashMap<String, ImageIcon> iconCache = new HashMap<String, ImageIcon>();

    /**
     * This class cannot be instantiated; all of its members are static
     */
    private ViewUtils()
    {
    }

    /**
     * Sets position and size of a component as percentages of its parent
     *
     * @param parent
     *            the parent Container
     * @param comp
     *            the JComponent to be positioned/sized
     * @param x
     *            the horizontal position
     * @param y
     *            the vertical position
     * @param w
     *            the horizontal size
     * @param h
     *            the vertical size
     */
    public static void setPercentage(Container parent, JComponent comp, float x, float y, float w, float h)
    {
        if (!(parent.getLayout() instanceof SpringLayout))
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

    /**
     * Centralized image loading code, with caching to reduce file access time.
     *
     * @param path
     *            the resource path
     * @return an ImageIcon constructed from the resource at the specified path
     */
    public static ImageIcon getImageIcon(String path)
    {
        if (iconCache.containsKey(path))
            return iconCache.get(path);
        java.net.URL uri = ViewUtils.class.getResource(path);
        if (uri == null)
            throw new IllegalArgumentException("Invalid path " + path + ": file not found.");
        ImageIcon icon = new ImageIcon(uri);
        iconCache.put(path, icon);
        return icon;
    }
}
