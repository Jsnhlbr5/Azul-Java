package model;

import java.util.ArrayList;

/**
 * Specialization of ArrayList to simulate a collection of tiles
 *
 * @author jsnhlbr5
 */
public class TileCollection extends ArrayList<Color>
{
    /**
     * Constructs an empty collection
     */
    public TileCollection()
    {
        super();
    }

    /**
     * Constructs a list containing the elements of the specified collection, in
     * the order they are returned by the collection'siterator.
     *
     * @param tc
     *            the collection whose elements are to be placed into this list
     * @throws NullPointerException
     *             if the specified collection is null
     */
    public TileCollection(TileCollection tc)
    {
        super(tc);
    }

    /**
     * A convenience method for mass-adding tiles of a specific Color.
     *
     * @param color
     *            the Color of tiles to add
     * @param count
     *            the number of tiles to add
     */
    public void addTiles(Color color, int count)
    {
        for (int i = 0; i < count; ++i)
        {
            this.add(color);
        }
    }

    /**
     * Selects the specified number of tiles (Color objects) randomly from this
     * collection. If the number of tiles to draw is larger than the size of
     * this collection, then only as many tiles as are in this collection are
     * drawn. The selected tiles are removed from this collection.
     *
     * @param num
     *            the number of tiles to draw
     * @return a new TileCollection containing the drawn tiles
     * @throws IllegalArgumentException
     *             if the parameter is negative
     */
    public TileCollection drawTiles(int num)
    {
        if (num < 0)
            throw new IllegalArgumentException("Cannot draw negative tiles.");
        TileCollection drawn = new TileCollection();
        if (num > this.size())
            num = this.size();
        for (int i = 0; i < num; ++i)
        {
            drawn.add(this.remove((int) (Math.random() * this.size())));
        }
        return drawn;
    }

    /**
     * Returns true if this collection contains Color objects that all have the
     * same value.
     *
     * @return true if this collection contains Color objects that all have the
     *         same value.
     */
    public boolean isAllOneColor()
    {
        if (this.isEmpty())
            return false;
        Color c = this.get(0);
        for (int i = 1; i < this.size(); ++i)
        {
            if (this.get(i) != c)
                return false;
        }
        return true;
    }

    /**
     * Returns the color of tiles in this collection if it is all one color,
     * null otherwise
     *
     * @return a <tt>Color</tt> object equal to those in this collection, or
     *         <tt>null</tt> if not all one color
     */
    public Color getColor()
    {
        if (isAllOneColor())
            return this.get(0);
        return null;
    }

    /**
     * Returns the color of tiles in this collection if it is all one color,
     * null otherwise; ignores WHITE tiles
     *
     * @return a <tt>Color</tt> object equal to those in this collection, or
     *         <tt>null</tt> if not all one color
     */
    public Color getColorIgnoreWhite()
    {
        TileCollection temp = new TileCollection(this);
        temp.removeTilesOfColor(Color.WHITE);
        return temp.getColor();
    }

    /**
     * Removes the tiles of the specified Color from this collection, and
     * returns them in a new collection.
     *
     * @param c
     *            the Color of tiles to remove
     * @return the removed tiles
     */
    public TileCollection removeTilesOfColor(Color c)
    {
        TileCollection tc = new TileCollection();
        for (int i = 0; i < this.size(); ++i)
        {
            if (this.get(i) == c)
                tc.add(this.remove(i--));
        }
        return tc;
    }
}
