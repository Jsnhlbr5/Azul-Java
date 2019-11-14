package view;

public interface GameObserver
{
    /**
     * Notifies the GameObserver that the game has ended and the name of the winner
     *
     * @param winner
     *            the name of the winning player
     */
    public void gameEnd(String winner);
}
