package model;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import view.CommonAreaView;

public class Game
{
    private int numPlayers;
    private PlayerBoard[] playerBoards;
    private TileCollection bag;
    private TileCollection[] factories;
    private TileCollection centerArea;
    private TileCollection boxLid;
    private CommonAreaView cav;
    
    private int curPlayer;
    
    private static final int[] factoryCount = {-1,-1,5,7,9};
    
    public static void main(String[] args)
    {
        int players = 2;
        if(args.length == 1)
        {
            try
            {
                players = Integer.parseInt(args[0]);
            }
            catch(NumberFormatException e)
            {
                JOptionPane.showMessageDialog(null, "First argument must be the number of players (2-4)", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e)
        {//Do nothing (Can't use system look, default Java look will be used)
        }
        
        Game theGame = new Game(players);
        theGame.setVisible(true);
    }
    
    public void setVisible(boolean b)
    {
        cav.setVisible(b);
        for(PlayerBoard pb : playerBoards)
        {
            pb.pbv.setVisible(b);
        }
    }

    public Game(int players)
    {
        if(players < 2 || players > 4)
            throw new IllegalArgumentException("Invalid number of players, must be 2-4.");
        numPlayers = players;
        playerBoards = new PlayerBoard[numPlayers];
        for(int i = 0; i < numPlayers; ++i)
        {
            playerBoards[i] = new PlayerBoard(this, i);
        }
        
        bag = new TileCollection();
        bag.addTiles(Color.BLUE, 20);
        bag.addTiles(Color.YELLOW, 20);
        bag.addTiles(Color.RED, 20);
        bag.addTiles(Color.BLACK, 20);
        bag.addTiles(Color.TEAL, 20);
        
        factories = new TileCollection[factoryCount[numPlayers]];
        for(int i = 0; i < factories.length; ++i)
        {
            factories[i] = new TileCollection();
        }
        
        centerArea = new TileCollection();
        
        boxLid = new TileCollection();
        
        //Randomize first player
        curPlayer = (int)(Math.random()*numPlayers);
        playerBoards[curPlayer].pbv.updateTitle(true);
        
        cav = new CommonAreaView(this, 780);//TODO take this as an argument?
        resetCenter();
    }
    
    public int getFactoryCount()
    {
        return factories.length;
    }
    
    public TileCollection getFactoryTiles(int i)
    {
        return new TileCollection(factories[i]);
    }
    
    public TileCollection getCenterTiles()
    {
        return new TileCollection(centerArea);
    }
    
    public int getCurPlayer()
    {
        return curPlayer;
    }
    
    public void pickTilesFromFactory(int factory, Color c)
    {
        if(c == Color.WHITE)
            throw new IllegalArgumentException("Cannot pick tiles of color WHITE.");
        if(!factories[factory].contains(c))
            throw new IllegalArgumentException("No " + c + " tiles in the chosen factory.");
        TileCollection picked = factories[factory].removeTilesOfColor(c);
        centerArea.addAll(factories[factory]);
        factories[factory].clear();
        cav.getCenter().updateTiles();
        playerBoards[curPlayer].setSelectedTiles(picked);
    }
    
    public void pickTilesFromCenter(Color c)
    {
        if(c == Color.WHITE)
            throw new IllegalArgumentException("Cannot pick tiles of color WHITE.");
        if(!centerArea.contains(c))
            throw new IllegalArgumentException("No " + c + " tiles in the center area.");
        TileCollection picked = centerArea.removeTilesOfColor(c);
        if(centerArea.contains(Color.WHITE))
            picked.addAll(centerArea.removeTilesOfColor(Color.WHITE));
        playerBoards[curPlayer].setSelectedTiles(picked);
    }
    
    public void endTurn()
    {
        playerBoards[curPlayer].pbv.updateTitle(false);
        if(roundOver())
        {
            TileCollection discard;
            for(int i = 0; i < numPlayers; ++i)
            {
                discard = playerBoards[i].finishRound();
                if(discard.contains(Color.WHITE))
                {
                    curPlayer = i;
                    discard.removeTilesOfColor(Color.WHITE);
                }
                boxLid.addAll(discard);
            }
            if(!gameOver())
            {
                resetCenter();
            }
            else
            {
                int score = -1;
                int winner = -1;
                for(PlayerBoard pb : playerBoards)
                {
                    int s = pb.finishGame();
                    if(s > score)
                    {
                        score = s;
                        winner = pb.player;
                    }
                }
                JOptionPane.showMessageDialog(null, "Player " + winner + " wins!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            }
        }
        else
        {
            curPlayer = (curPlayer + 1) % numPlayers;
        }
        playerBoards[curPlayer].pbv.updateTitle(true);
    }
    
    private void resetCenter()
    {
        for(int i = 0; i < factories.length; ++i)
        {
            factories[i] = bag.drawTiles(4);
            if(factories[i].size() < 4)
            {
                if(boxLid.size() > 0)
                {
                    bag.addAll(boxLid);
                    boxLid.clear();
                    bag.drawTiles(4 - factories[i].size());
                }
                else
                {
                    //No more tiles to draw, remaining factories will be empty.
                    break;
                }
            }
            cav.getFactory(i).updateTiles();
        }
        centerArea.add(Color.WHITE);
        cav.getCenter().updateTiles();
    }
    
    //Returns true if all tiles have been drawn.
    private boolean roundOver()
    {
        if(!centerArea.isEmpty())
            return false;
        for(int i = 0; i < factories.length; ++i)
        {
            if(!factories[i].isEmpty())
                return false;
        }
        return true;
    }
    
    private boolean gameOver()
    {
        for(int i = 0; i < numPlayers; ++i)
        {
            if(playerBoards[i].hasCompleteRow())
                return true;
        }
        return false;
    }
}
