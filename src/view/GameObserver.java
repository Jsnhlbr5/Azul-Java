package view;

public interface GameObserver
{
    /**
     * Notifies the GameObserver that the game has ended and the name of the winner
     * @param winner
     */
    public void gameEnd(String winner);
}
