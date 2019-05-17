package model;

import java.util.ArrayList;

public class TileCollection extends ArrayList<Color>
{
    public TileCollection()
    {
        super();
    }

    public TileCollection(TileCollection tc)
    {
        super((ArrayList<Color>)tc);
    }

    public void addTiles(Color color, int count)
    {
        for(int i = 0; i < count; ++i)
        {
            this.add(color);
        }
    }
    
    /**
     * Selects the specified number of tiles (Color objects) randomly from this
     * collection.  The selected tiles are removed from this collection.
     * 
     * @param num the number of tiles to draw
     * @return a new TileCollection containing the drawn tiles
     */
    public TileCollection drawTiles(int num)
    {
        if(num < 0)
            throw new IllegalArgumentException("Cannot draw negative tiles.");
        TileCollection drawn = new TileCollection();
        if(num > this.size())
            num = this.size();
        for(int i = 0; i < num; ++i)
        {
            drawn.add(this.remove((int)(Math.random()*this.size())));
        }
        return drawn;
    }
    
    /**
     * Returns true if this collection contains Color objects that all have
     * the same value.
     * @return true if this collection contains Color objects that all have
     * the same value.
     */
    public boolean isAllOneColor()
    {
        if(this.isEmpty())
            return false;
        Color c = this.get(0);
        for(int i = 1; i < this.size(); ++i)
        {
            if(this.get(i) != c)
                return false;
        }
        return true;
    }
    
    /**
     * Returns the color of tiles in this collection if it is all one color, null otherwise
     * @return a <tt>Color</tt> object equal to those in this collection, or <tt>null</tt> if not all one color
     */
    public Color getColor()
    {
        if(isAllOneColor())
            return this.get(0);
        return null;
    }
    
    public Color getColorIgnoreWhite()
    {
        TileCollection temp = new TileCollection(this);
        temp.removeTilesOfColor(Color.WHITE);
        if(temp.isAllOneColor())
            return temp.get(0);
        return null;
    }
    
    public TileCollection removeTilesOfColor(Color c)
    {
        TileCollection tc = new TileCollection();
        for(int i = 0; i < this.size(); ++i)
        {
            if(this.get(i) == c)
                tc.add(this.remove(i--));
        }
        return tc;
    }
    
    public TileCollection removeAll()
    {
        TileCollection tc = new TileCollection(this);
        this.clear();
        return tc;
    }
}
