package model;

import java.util.Arrays;

import view.PlayerBoardView;

/**
 * A representation of a single player's board
 *
 * @author jsnhlbr5
 */
public class PlayerBoard
{
    private boolean[][] wall;
    private int score;
    private BuildRow[] buildRows;
    private TileCollection floorLine;

    private TileCollection selectedTiles;
    private Game game;
    public final PlayerBoardView pbv;
    /**
     * The numeric position from an arbitrary first position (adjusted up 1 from the zero-indexed input)
     */
    public final int player;

    /**
     * The total penalty for having the given quantity of tiles on your floor line (0 tiles = 0 penalty).
     */
    private static final int[] floorLineScores = { 0, -1, -2, -4, -6, -8, -11, -14 };

    /**
     * Constructs a new player board logical representation connected to the given Game, using the given player index
     *
     * @param g
     *            the Game this board belongs to
     * @param p
     *            the 0-indexed player number given by the Game
     */
    public PlayerBoard(Game g, int p)
    {
        game = g;
        player = ++p;
        wall = new boolean[5][5];
        score = 0;
        buildRows = new BuildRow[5];
        for (int i = 0; i < 5; ++i)
        {
            buildRows[i] = new BuildRow(i);
        }
        floorLine = new TileCollection();

        pbv = new PlayerBoardView(this);
    }

    // ---- Get/check state methods ----

    /**
     * Returns a TileCollection representing the tiles currently on the given build row
     *
     * @param row
     *            the build row
     * @return a TileCollection representing the tiles currently on the given build row
     */
    public TileCollection getBuildRowTiles(int row)
    {
        TileCollection tc = new TileCollection();
        BuildRow br = buildRows[row];
        tc.addTiles(br.color, br.count);
        return tc;
    }

    /**
     * Returns a TileCollection representing the tiles currently on the floor line
     *
     * @return a TileCollection representing the tiles currently on the floor line
     */
    public TileCollection getFloorLineTiles()
    {
        return new TileCollection(floorLine);
    }

    /**
     * Returns a 2d boolean array representing which wall positions have been tiled
     *
     * @return a 2d boolean array representing which wall positions have been tiled
     */
    public boolean[][] getWall()
    {
        boolean[][] w = new boolean[5][];
        for (int i = 0; i < 5; ++i)
        {
            w[i] = Arrays.copyOf(wall[i], 5);
        }
        return w;
    }

    /**
     * Returns this player's current score
     *
     * @return this player's current score
     */
    public int getScore()
    {
        return score;
    }

    /**
     * Returns true if this player can add their currently selected tiles to the given build row
     *
     * @param row
     *            the build row
     * @return true if this player can add their currently selected tiles to the given build row
     */
    public boolean canAddTilesToRow(int row)
    {
        return buildRows[row].canAddTiles(selectedTiles.getColorIgnoreWhite());
    }

    /**
     * Returns true if this player has at least one row of their wall completed
     *
     * @return true if this player has at least one row of their wall completed
     */
    public boolean hasCompleteRow()
    {
        boolean complete;
        for (int row = 0; row < 5; ++row)
        {
            complete = true;
            for (int col = 0; col < 5; ++col)
            {
                if (!wall[row][col])
                {
                    // This row is not complete, check the next one.
                    complete = false;
                    break;
                }
            }
            if (complete)
                return true;
        }
        return false;
    }

    /**
     * Returns true if this player currently has selected tiles that they have not placed on their board
     *
     * @return true if this player currently has selected tiles that they have not placed on their board
     */
    public boolean hasSelectedTiles()
    {
        return selectedTiles != null;
    }

    // ---- Mutator methods ----

    /**
     * Sets this player's selected tiles buffer to the given TileCollection
     *
     * @param tc
     *            the TileCollection representing the tiles this player selected
     */
    public void setSelectedTiles(TileCollection tc)
    {
        selectedTiles = tc;
        pbv.updateButtons();
    }

    /**
     * Adds tiles (from the selectedTiles buffer) to the given build row, overflowing to the floor line
     *
     * @param row
     *            the 0-indexed row number (>4 places tiles directly on the floor line)
     */
    public void addTilesToRow(int row)
    {
        if (selectedTiles == null)
            throw new IllegalStateException("Tiles must be selected before they can be added to a row.");
        if (row > 4)
        {
            floorLine.addAll(selectedTiles);
        }
        else
        {
            if (selectedTiles.contains(Color.WHITE))
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

    /**
     * Invokes tileRow() for each build row and scoreFloor(), collecting the discard tiles into a single collection, and
     * triggers UI updates
     *
     * @return a TileCollection representing all of the tiles discarded by this player
     */
    public TileCollection finishRound()
    {
        TileCollection discard = new TileCollection();
        for (int i = 0; i < 5; ++i)
        {
            discard.addAll(tileRow(i));
        }
        discard.addAll(scoreFloor());
        pbv.updateTiles();
        pbv.updateScore();
        return discard;
    }

    /**
     * Calculates end-of-game bonuses and returns this player's final score
     *
     * @return this player's final score
     */
    public int finishGame()
    {
        rowBonus();
        colBonus();
        colorBonus();
        pbv.updateScore();
        return score;
    }

    // ---- Private methods ----

    // Tiles a build row onto the wall (if it's complete) and returns the discarded tiles from doing so
    private TileCollection tileRow(int row)
    {
        BuildRow br = buildRows[row];
        if (br.isFull())
        {
            wall[row][br.column()] = true;
            scoreTile(row, br.column());
            return br.getDiscard();
        }
        return new TileCollection();
    }

    // Calculates the score for a single tile, by counting the contiguous row and/or column that it is part of
    private void scoreTile(int row, int col)
    {
        boolean inARow = ((col + 1 < 5) ? wall[row][col + 1] : false) || ((col - 1 > -1) ? wall[row][col - 1] : false);
        boolean inACol = ((row + 1 < 5) ? wall[row + 1][col] : false) || ((row - 1 > -1) ? wall[row - 1][col] : false);
        if (!inARow && !inACol)
        {
            ++score;
        }
        else
        {
            if (inARow)
            {
                int rowLen = 1;
                int i = col;
                while (++i < 5 && wall[row][i])
                    ++rowLen;
                i = col;
                while (--i > -1 && wall[row][i])
                    ++rowLen;
                score += rowLen;
            }
            if (inACol)
            {
                int colLen = 1;
                int i = row;
                while (++i < 5 && wall[i][col])
                    ++colLen;
                i = row;
                while (--i > -1 && wall[i][col])
                    ++colLen;
                score += colLen;
            }
        }
    }

    // Calculates the score penalty based on the floor line and returns all of the tiles that were placed there
    private TileCollection scoreFloor()
    {
        int numTiles = floorLine.size();
        if (numTiles > 7)
            numTiles = 7;
        score += floorLineScores[numTiles];
        if (score < 0)
            score = 0;
        TileCollection discard = new TileCollection(floorLine);
        floorLine.clear();
        return discard;
    }

    // Adds 2 to this player's score for every complete row
    private void rowBonus()
    {
        boolean complete;
        for (int row = 0; row < 5; ++row)
        {
            complete = true;
            for (int col = 0; col < 5; ++col)
            {
                if (!wall[row][col])
                {
                    // This row is not complete, check the next one.
                    complete = false;
                    break;
                }
            }
            if (complete)
                score += 2;
        }
    }

    // Adds 7 to this player's score for every complete column
    private void colBonus()
    {
        boolean complete;
        for (int col = 0; col < 5; ++col)
        {
            complete = true;
            for (int row = 0; row < 5; ++row)
            {
                if (!wall[row][col])
                {
                    // This column is not complete, check the next one.
                    complete = false;
                    break;
                }
            }
            if (complete)
                score += 7;
        }
    }

    // Adds 10 to this player's score for every complete color (all 5 tiles of one color)
    private void colorBonus()
    {
        boolean complete;
        for (int color = 0; color < 5; ++color)
        {
            complete = true;
            for (int row = 0; row < 5; ++row)
            {
                if (!wall[row][(row + color) % 5])
                {
                    // This color is not complete, check the next one.
                    complete = false;
                    break;
                }
            }
            if (complete)
                score += 10;
        }
    }

    /**
     * A representation of a single build row (to centralize some of the related logic)
     *
     * @author jsnhlbr5
     */
    private class BuildRow
    {
        // The row this BuildRow represents (0-indexed)
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
            // The row number is 0-indexed, so +1
            return count == row + 1;
        }

        public boolean canAddTiles(Color color)
        {
            // Not already tiled in this row, matching existing tiles (if any), and there is space left
            return !(wall[row][column(color)]) && (this.color == null || this.color == color) && !this.isFull();
        }

        // Adds the given tiles to this row, returning any overflow
        public TileCollection addTiles(TileCollection tc)
        {
            if (!tc.isAllOneColor())
                throw new IllegalArgumentException("Invalid tile collection for build row: not all one color");
            if (tc.getColor() != this.color && this.color != null)
                return tc;
            this.color = tc.getColor();
            this.count += tc.size();
            int overflow = this.count - (row + 1);
            if (this.count > row + 1)
                this.count = row + 1;
            if (overflow < 0)
                overflow = 0;
            return tc.drawTiles(overflow);
        }

        // Gets the discard tiles from tiling this row
        public TileCollection getDiscard()
        {
            TileCollection discard = new TileCollection();
            // Equal to the row number because one is kept for the wall
            discard.addTiles(color, row);
            this.count = 0;
            this.color = null;
            return discard;
        }

        // Returns the column this row will tile to, based on its current color
        public int column()
        {
            return (row + color.ordinal()) % 5;
        }

        // Returns the column for this row and the given color
        public int column(Color color)
        {
            return (row + color.ordinal()) % 5;
        }
    }
}
