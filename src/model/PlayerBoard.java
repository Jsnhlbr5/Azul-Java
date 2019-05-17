package model;

import java.util.Arrays;

import view.PlayerBoardView;

//Represents a single player's board
public class PlayerBoard
{
    private boolean[][] wall;
    private int score;
    private BuildRow[] buildRows;
    private TileCollection floorLine;
    
    private TileCollection selectedTiles;
    private Game game;
    public final PlayerBoardView pbv;
    //This is adjusted up 1 from the zero-indexed input
    public final int player;

    //The total penalty for having the given quantity of tiles on your floor line.
    private static final int[] floorLineScores = {0,-1,-2,-4,-6,-8,-11,-14};
    
    public PlayerBoard(Game g, int p)
    {
        game = g;
        player = ++p;
        wall = new boolean[5][5];
        score = 0;
        buildRows = new BuildRow[5];
        for(int i = 0; i < 5; ++i)
        {
            buildRows[i] = new BuildRow(i);
        }
        floorLine = new TileCollection();
        
        pbv = new PlayerBoardView(this);
    }
    
    //---- Get/check state methods ----
    
    public TileCollection getBuildRowTiles(int row)
    {
        TileCollection tc = new TileCollection();
        BuildRow br = buildRows[row];
        tc.addTiles(br.color, br.count);
        return tc;
    }
    
    public TileCollection getFloorLineTiles()
    {
        return new TileCollection(floorLine);
    }
    
    public boolean[][] getWall()
    {
        boolean[][] w = new boolean[5][];
        for(int i = 0; i < 5; ++i)
        {
            w[i] = Arrays.copyOf(wall[i], 5);
        }
        return w;
    }
    
    public int getScore()
    {
        return score;
    }
    
    public boolean canAddTilesToRow(Color c, int row)
    {
        return buildRows[row].canAddTiles(c);
    }

    public boolean canAddTilesToRow(int row)
    {
        return buildRows[row].canAddTiles(selectedTiles.getColorIgnoreWhite());
    }
    
    public boolean hasCompleteRow()
    {
        boolean complete;
        for(int row = 0; row < 5; ++row)
        {
            complete = true;
            for(int col = 0; col < 5; ++col)
            {
                if(!wall[row][col])
                {
                    //This row is not complete, check the next one.
                    complete = false;
                    break;
                }
            }
            if(complete)
                return true;
        }
        return false;
    }
    
    public boolean hasSelectedTiles()
    {
        return selectedTiles != null;
    }
    
    //---- Mutator methods ----
    
    public void setSelectedTiles(TileCollection tc)
    {
        selectedTiles = tc;
        pbv.updateButtons();
    }
    
    //Add tiles (from the selectedTiles buffer) to a build row, overflowing to the floor line
    public void addTilesToRow(int row)
    {
        if(selectedTiles == null)
            throw new IllegalStateException("Tiles must be selected before they can be added to a row.");
        if(row > 4)
        {
            floorLine.addAll(selectedTiles);
        }
        else
        {
            if(selectedTiles.contains(Color.WHITE))
            {
                floorLine.addAll(selectedTiles.removeTilesOfColor(Color.WHITE));
            }
            floorLine.addAll(buildRows[row].addTiles(selectedTiles));
        }
        selectedTiles = null;
        pbv.updateButtons();
        pbv.updateTiles();
        game.endTurn();
    }
    
    public TileCollection finishRound()
    {
        TileCollection discard = new TileCollection();
        for(int i = 0; i < 5; ++i)
        {
            discard.addAll(tileRow(i));
        }
        discard.addAll(scoreFloor());
        pbv.updateTiles();
        pbv.updateScore();
        return discard;
    }
    
    public int finishGame()
    {
        rowBonus();
        colBonus();
        colorBonus();
        pbv.updateScore();
        return score;
    }
    
    //---- Private methods ----
    
    //Tiles a build row onto the wall
    private TileCollection tileRow(int row)
    {
        BuildRow br = buildRows[row];
        if(br.isFull())
        {
            wall[row][br.column()] = true;
            scoreTile(row, br.column());
            return br.getDiscard();
        }
        return new TileCollection();
    }
    
    private void scoreTile(int row, int col)
    {
        boolean inARow = ((col+1 < 5) ? wall[row][col+1] : false) || ((col-1 > -1) ? wall[row][col-1] : false);
        boolean inACol = ((row+1 < 5) ? wall[row+1][col] : false) || ((row-1 > -1) ? wall[row-1][col] : false);
        if(!inARow && !inACol)
        {
            ++score;
        }
        else
        {
            if(inARow)
            {
                int rowLen = 1;
                int i = col;
                while(++i < 5 && wall[row][i])
                    ++rowLen;
                i = col;
                while(--i > -1 && wall[row][i])
                    ++rowLen;
                score += rowLen;
            }
            if(inACol)
            {
                int colLen = 1;
                int i = row;
                while(++i < 5 && wall[i][col])
                    ++colLen;
                i = row;
                while(--i > -1 && wall[i][col])
                    ++colLen;
                score += colLen;
            }
        }
    }
    
    private TileCollection scoreFloor()
    {
        int numTiles = floorLine.size();
        if(numTiles > 7)
            numTiles = 7;
        score += floorLineScores[numTiles];
        if(score < 0)
            score = 0;
        return floorLine.removeAll();
    }
    
    private void rowBonus()
    {
        boolean complete;
        for(int row = 0; row < 5; ++row)
        {
            complete = true;
            for(int col = 0; col < 5; ++col)
            {
                if(!wall[row][col])
                {
                    //This row is not complete, check the next one.
                    complete = false;
                    break;
                }
            }
            if(complete)
                score += 2;
        }
    }
    
    private void colBonus()
    {
        boolean complete;
        for(int col = 0; col < 5; ++col)
        {
            complete = true;
            for(int row = 0; row < 5; ++row)
            {
                if(!wall[row][col])
                {
                    //This column is not complete, check the next one.
                    complete = false;
                    break;
                }
            }
            if(complete)
                score += 7;
        }
    }
    
    private void colorBonus()
    {
        boolean complete;
        for(int color = 0; color < 5; ++color)
        {
            complete = true;
            for(int row = 0; row < 5; ++row)
            {
                if(!wall[row][(row + color) % 5])
                {
                    //This color is not complete, check the next one.
                    complete = false;
                    break;
                }
            }
            if(complete)
                score += 10;
        }
    }
    
    private class BuildRow
    {
        //The row this BuildRow represents (0-indexed)
        private final int row;
        
        private Color color;
        private int count;
        
        public BuildRow(int number)
        {
            row = number;
            color = null;
            count = 0;
        }
        
        public boolean isFull()
        {
            return count == row + 1;
        }
        
        public boolean canAddTiles(Color color)
        {
            return !(wall[row][column(color)]) && (this.color == null || this.color == color) && !this.isFull();
        }
        
        public TileCollection addTiles(TileCollection tc)
        {
            if(!tc.isAllOneColor())
                throw new IllegalArgumentException("Invalid tile collection for build row: not all one color");
            if(tc.getColor() != this.color && this.color != null)
                return tc;
            this.color = tc.getColor();
            this.count += tc.size();
            int overflow = this.count - (row + 1);
            if(this.count > row + 1)
                this.count = row + 1;
            if(overflow < 0)
                overflow = 0;
            return tc.drawTiles(overflow);
        }
        
        public TileCollection getDiscard()
        {
            TileCollection discard = new TileCollection();
            discard.addTiles(color, row);
            this.count = 0;
            this.color = null;
            return discard;
        }
        
        public int column()
        {
            return (row + color.ordinal()) % 5;
        }
        
        public int column(Color color)
        {
            return (row + color.ordinal()) % 5;
        }
    }
}
