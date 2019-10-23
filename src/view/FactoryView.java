package view;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.SpringLayout;

import model.Color;
import model.Game;
import model.TileCollection;

/**
 * Visual representation of a single factory (or "coaster")
 *
 * @author jsnhlbr5
 */
public class FactoryView extends JLayeredPane
{
    // Percentage-based size/position constants based on factory and tile image sizes, which reflect IRL relative sizes
    // 60/240
    private static final float TILE_SIZE = 0.25f;
    // 57/240 (60-3 distance from edge if touching middle - half gap)
    private static final float FIRST_POSITION = 0.2375f;
    // 123/240 (120+3 half + half gap)
    private static final float SECOND_POSITION = 0.5125f;

    private Game model;
    private int index;
    private JButton[] buttons;

    /**
     * Creates a new factory view tied to the given game representing the factory of the given index.
     *
     * @param g
     *            the Game to use as a logical model
     * @param index
     *            the factory index in the Game's list of factories
     */
    public FactoryView(Game g, int index)
    {
        model = g;
        this.index = index;
        SpringLayout layout = new SpringLayout();
        this.setLayout(layout);

        ImageLabel background = new ImageLabel(ViewUtils.getImageIcon("/img/factory.png"));
        ViewUtils.setPercentage(this, background, 0f, 0f, 1f, 1f);
        this.add(background, DEFAULT_LAYER);

        buttons = new JButton[4];
        for (int i = 0; i < 4; ++i)
        {
            buttons[i] = new InvisibleButton(new PickTilesAction(index, null));
            // 0 1
            // 2 3
            float x = (i % 2 == 0) ? FIRST_POSITION : SECOND_POSITION;
            float y = (i < 2) ? FIRST_POSITION : SECOND_POSITION;
            ViewUtils.setPercentage(this, buttons[i], x, y, TILE_SIZE, TILE_SIZE);
            this.add(buttons[i], ViewUtils.INTERFACE_LAYER);
        }

        updateTiles();
    }

    /**
     * Removes old tiles (if any) and creates new ones; updates buttons to match.
     */
    public void updateTiles()
    {
        // Remove old tiles and disable buttons
        for (Component c : getComponentsInLayer(ViewUtils.TILE_LAYER))
            this.remove(c);
        for (int i = 0; i < 4; ++i)
            buttons[i].setEnabled(false);

        // Get new tiles and enable buttons (as appropriate)
        TileCollection tc = model.getFactoryTiles(index);
        for (int i = 0; i < tc.size(); ++i)
        {
            JLabel tilePanel = new ImageLabel(ViewUtils.getImageIcon("/img/" + tc.get(i).name() + ".png"));
            float x = (i % 2 == 0) ? FIRST_POSITION : SECOND_POSITION;
            float y = (i < 2) ? FIRST_POSITION : SECOND_POSITION;
            ViewUtils.setPercentage(this, tilePanel, x, y, TILE_SIZE, TILE_SIZE);
            this.add(tilePanel, ViewUtils.TILE_LAYER);
            ((PickTilesAction) buttons[i].getAction()).setColor(tc.get(i));
            buttons[i].setEnabled(true);
        }
    }

    private class PickTilesAction extends AbstractAction
    {
        private int factory;
        private Color color;

        public PickTilesAction(int f, Color c)
        {
            factory = f;
            color = c;
        }

        public void setColor(Color c)
        {
            color = c;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            model.pickTilesFromFactory(factory, color);
            updateTiles();
        }

    }
}
