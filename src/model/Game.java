package model;

import java.awt.GraphicsEnvironment;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import view.CommonAreaView;
import view.GameObserver;

/**
 * The main logic class; this is the entry point for creating a single game
 *
 * @author jsnhlbr5
 */
public class Game
{
    private int numPlayers;
    private PlayerBoard[] playerBoards;
    private TileCollection bag;
    private TileCollection[] factories;
    private TileCollection centerArea;
    private TileCollection boxLid;
    private CommonAreaView cav;

    private ArrayList<GameObserver> observers;
    private int curPlayer;
    private String winner;

    /**
     * The number of factories to use for a given number of players; 0 and 1 are not valid numbers of players
     */
    private static final int[] factoryCount = { -1, -1, 5, 7, 9 };

    private static final String[] defaultNames = { "Player 1", "Player 2", "Player 3", "Player 4" };

    /**
     * Runs a single game and exits. The number of players can be set using the first command line argument, the default
     * is 2.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args)
    {
        int players = 2;
        if (args.length == 1)
        {
            try
            {
                players = Integer.parseInt(args[0]);
            }
            catch (NumberFormatException e)
            {
                JOptionPane.showMessageDialog(null, "First argument must be the number of players (2-4)", "Error",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }

        final int passablePlayers = players;

        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e)
        {// Do nothing (Can't use system look, default Java look will be used)
        }

        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                new Game(passablePlayers).setVisible(true);
            }
        });
    }

    /**
     * Sets the visibility of all the generated windows
     *
     * @param b
     *            true to set them visible
     */
    public void setVisible(boolean b)
    {
        cav.setVisible(b);
        for (PlayerBoard pb : playerBoards)
        {
            pb.pbv.setVisible(b);
        }
    }

    /**
     * Creates a new game with the given number of players, using the default names
     *
     * @param players
     *            the number of players (2-4)
     */
    public Game(int players)
    {
        this(players, defaultNames);
    }

    /**
     * Creates a new game with the given number of players, using the given names
     *
     * @param players
     *            the number of players (2-4)
     * @param names
     *            the names to use for each player
     */
    public Game(int players, String[] names)
    {
        if (players < 2 || players > 4)
            throw new IllegalArgumentException("Invalid number of players, must be 2-4.");
        numPlayers = players;
        playerBoards = new PlayerBoard[numPlayers];
        if (names.length < numPlayers)
            throw new IllegalArgumentException("Not enough names given for the number of players");
        for (int i = 0; i < numPlayers; ++i)
        {
            playerBoards[i] = new PlayerBoard(this, names[i]);
        }

        bag = new TileCollection();
        bag.addTiles(Color.BLUE, 20);
        bag.addTiles(Color.YELLOW, 20);
        bag.addTiles(Color.RED, 20);
        bag.addTiles(Color.BLACK, 20);
        bag.addTiles(Color.TEAL, 20);

        factories = new TileCollection[factoryCount[numPlayers]];
        for (int i = 0; i < factories.length; ++i)
        {
            factories[i] = new TileCollection();
        }

        centerArea = new TileCollection();

        boxLid = new TileCollection();

        winner = "none";
        // Randomize first player
        curPlayer = (int) (Math.random() * numPlayers);
        playerBoards[curPlayer].pbv.updateTitle(true);

        // Create the center area at 3/4 the available height.
        int size = (int)(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height*.75);
        cav = new CommonAreaView(this, size);

        observers = new ArrayList<GameObserver>();

        resetCenter();
    }

    /**
     * Returns the number of factories for this game
     *
     * @return the number of factories for this game
     */
    public int getFactoryCount()
    {
        return factories.length;
    }

    /**
     * Returns a TileCollection representing the specified factory's tiles
     *
     * @param i
     *            the factory for which tiles are being requested
     * @return a TileCollection representing the specified factory's tiles
     */
    public TileCollection getFactoryTiles(int i)
    {
        return new TileCollection(factories[i]);
    }

    /**
     * Returns a TileCollection representing the tiles in the center area
     *
     * @return a TileCollection representing the tiles in the center area
     */
    public TileCollection getCenterTiles()
    {
        return new TileCollection(centerArea);
    }

    /**
     * Returns the player whose turn it is
     *
     * @return the player whose turn it is
     */
    public int getCurPlayer()
    {
        return curPlayer;
    }

    /**
     * Selects tiles from the given factory of the given color. The selected tiles are transfered to the current
     * player's selected tiles buffer, and the remainder are transfered to the center area.
     *
     * @param factory
     *            the factory to select tiles from
     * @param c
     *            the color of tiles to select
     */
    public void pickTilesFromFactory(int factory, Color c)
    {
        if (c == Color.WHITE)
            throw new IllegalArgumentException("Cannot pick tiles of color WHITE.");
        if (!factories[factory].contains(c))
            throw new IllegalArgumentException("No " + c + " tiles in the chosen factory.");
        TileCollection picked = factories[factory].removeTilesOfColor(c);
        centerArea.addAll(factories[factory]);
        factories[factory].clear();
        cav.updateTiles();
        playerBoards[curPlayer].setSelectedTiles(picked);
    }

    /**
     * Selects tiles from the center area of the given color. If the white tile is present, it is added to the selected
     * tiles before they are transfered to the current player's selected tiles buffer.
     *
     * @param c
     *            the color of tiles to select
     */
    public void pickTilesFromCenter(Color c)
    {
        if (c == Color.WHITE)
            throw new IllegalArgumentException("Cannot pick tiles of color WHITE.");
        if (!centerArea.contains(c))
            throw new IllegalArgumentException("No " + c + " tiles in the center area.");
        TileCollection picked = centerArea.removeTilesOfColor(c);
        if (centerArea.contains(Color.WHITE))
            picked.addAll(centerArea.removeTilesOfColor(Color.WHITE));
        playerBoards[curPlayer].setSelectedTiles(picked);
    }

    /**
     * Ends a player's turn. If the round is over (all tiles have been picked), performs end-of-round activities
     * (tiling, scoring, and discard). If the game is not over, resets the common area for the next round; otherwise
     * tallies final bonuses and declares the winner before exiting.
     */
    public void endTurn()
    {
        playerBoards[curPlayer].pbv.updateTitle(false);
        if (roundOver())
        {
            TileCollection discard;
            for (int i = 0; i < numPlayers; ++i)
            {
                discard = playerBoards[i].finishRound();
                if (discard.contains(Color.WHITE))
                {
                    curPlayer = i;
                    discard.removeTilesOfColor(Color.WHITE);
                }
                boxLid.addAll(discard);
            }
            if (!gameOver())
            {
                resetCenter();
            }
            else
            {
                int score = -1;
                winner = "";
                for (PlayerBoard pb : playerBoards)
                {
                    int s = pb.finishGame();
                    if (s > score)
                    {
                        score = s;
                        winner = pb.player;
                    }
                }
                JOptionPane.showMessageDialog(null, winner + " wins!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }
        }
        else
        {
            curPlayer = (curPlayer + 1) % numPlayers;
        }
        playerBoards[curPlayer].pbv.updateTitle(true);
    }

    public void addObserver(GameObserver o)
    {
        observers.add(o);
    }

    /**
     * Sets up the common area for the beginning of a round.
     */
    private void resetCenter()
    {
        for (int i = 0; i < factories.length; ++i)
        {
            factories[i] = bag.drawTiles(4);
            if (factories[i].size() < 4)
            {
                if (!boxLid.isEmpty())
                {
                    bag.addAll(boxLid);
                    boxLid.clear();
                    factories[i].addAll(bag.drawTiles(4 - factories[i].size()));
                }
                else
                {
                    // No more tiles to draw, remaining factories will be empty.
                    break;
                }
            }
        }
        centerArea.add(Color.WHITE);
        cav.updateTiles();
    }

    /**
     * Returns true if the round is over (all tiles have been drawn)
     *
     * @return true if the round is over (all tiles have been drawn)
     */
    private boolean roundOver()
    {
        if (!centerArea.isEmpty())
            return false;
        for (int i = 0; i < factories.length; ++i)
        {
            if (!factories[i].isEmpty())
                return false;
        }
        return true;
    }

    /**
     * Returns true if the game is over (at least one player has at least one completed row)
     *
     * @return true if the game is over (at least one player has at least one completed row)
     */
    private boolean gameOver()
    {
        for (int i = 0; i < numPlayers; ++i)
        {
            if (playerBoards[i].hasCompleteRow())
                return true;
        }
        return false;
    }

    /**
     * Disposes of all player windows and the common area window (resulting in the application exiting if run from the
     * above main method)
     */
    public void dispose()
    {
        for (PlayerBoard pb : playerBoards)
        {
            ((JFrame) pb.pbv.getTopLevelAncestor()).dispose();
        }
        ((JFrame) cav.getTopLevelAncestor()).dispose();

        for (GameObserver o : observers)
        {
            o.gameEnd(winner);
        }
    }
}
