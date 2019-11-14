package view;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import model.Game;

/**
 * A persistent controller to make it easier to launch sequential games. Also allows for customization of player names.
 *
 * @author jsnhlbr5
 *
 */
public class Controller extends JFrame implements GameObserver
{
    /**
     * Launches the Controller
     *
     * @param args
     *            ignored
     */
    public static void main(String[] args)
    {
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
                new Controller().setVisible(true);
            }
        });
    }

    private JSpinner numPlayers;
    private JTextField[] playerNames;
    private JButton playButton;

    private static final String[] defaultNames = { "Player 1", "Player 2", "Player 3", "Player 4" };

    /**
     * Constructs the Controller UI
     */
    public Controller()
    {
        super("Azul Controller");
        setIconImage(Toolkit.getDefaultToolkit().getImage(ViewUtils.class.getResource("/img/TEAL.png")));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLayout(new GridLayout(6, 1, 2, 2));

        JPanel players = new JPanel();
        players.setLayout(new GridLayout(1, 2));
        JLabel l = new JLabel("Number of players: ");
        players.add(l);
        numPlayers = new JSpinner(new SpinnerNumberModel(2, 2, 4, 1));
        numPlayers.addChangeListener(new PlayerCountListener());
        players.add(numPlayers);
        this.add(players);
        playerNames = new JTextField[4];
        for (int i = 0; i < 4; ++i)
        {
            playerNames[i] = new NameTextField(defaultNames[i]);
            this.add(playerNames[i]);
        }
        numPlayers.getChangeListeners()[0].stateChanged(null);

        playButton = new JButton(new PlayAction(this));
        this.add(playButton);

        // Mainly for the width, so that the title is always visible.
        setMinimumSize(new Dimension(250, 100));
        pack();
        setResizable(false);
        // Center on primary screen
        setLocationRelativeTo(null);
    }

    /**
     * Re-enables the Controller UI (disabled while a game is active). NYI: record win/loss for each player.
     */
    @Override
    public void gameEnd(String winner)
    {
        // TODO record winner
        enableUI();
        this.requestFocus();
    }

    /**
     * Disables the entire Controller UI (called when starting a game)
     */
    private void disableUI()
    {
        numPlayers.setEnabled(false);
        for (int i = 0; i < 4; ++i)
        {
            playerNames[i].setEnabled(false);
        }
        playButton.setEnabled(false);
        this.setEnabled(false);
    }

    /**
     * Enables the Controller UI, as appropriate; player name fields beyond the player count setting will remain
     * disabled.
     */
    private void enableUI()
    {
        this.setEnabled(true);
        numPlayers.setEnabled(true);
        int count = (Integer) numPlayers.getValue();
        for (int i = 0; i < count; ++i)
        {
            playerNames[i].setEnabled(true);
        }
        playButton.setEnabled(true);
    }

    /**
     * Action implementation to launch a new Game with the current settings
     *
     * @author jsnhlbr5
     *
     */
    private class PlayAction extends AbstractAction
    {
        private GameObserver observer;

        /**
         * Instantiates this action
         *
         * @param o
         *            the Controller
         */
        public PlayAction(GameObserver o)
        {
            super("Play");
            observer = o;
        }

        /**
         * Launch a new game with the current settings
         */
        @Override
        public void actionPerformed(ActionEvent e)
        {
            disableUI();
            int count = (Integer) numPlayers.getValue();
            String[] names = new String[count];
            for (int i = 0; i < count; ++i)
            {
                // Replace blank names with defaults
                if (playerNames[i].getText().isEmpty())
                    playerNames[i].setText(defaultNames[i]);
                names[i] = playerNames[i].getText();
            }

            Game g = new Game(count, names);
            g.addObserver(observer);
            g.setVisible(true);
        }
    }

    /**
     * Listens to the player count spinner and disables excess name fields
     *
     * @author jsnhlbr5
     */
    private class PlayerCountListener implements ChangeListener
    {
        public PlayerCountListener()
        {
            // Nothing to do here
        }

        /**
         * Disables/enables name fields to match the selected player count
         */
        @Override
        public void stateChanged(ChangeEvent e)
        {
            int count = (Integer) numPlayers.getValue();
            int i = 0;
            while (i < count)
            {
                playerNames[i++].setEnabled(true);
            }
            while (i < 4)
            {
                playerNames[i++].setEnabled(false);
            }
        }
    }
}
