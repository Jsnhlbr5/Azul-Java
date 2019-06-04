package view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import model.Color;
import model.Game;
import model.TileCollection;

/**
 * Visual representation of the collection of tiles in the center of the common area. It resizes itself based on the
 * number of tiles present to keep them as centered as possible
 *
 * @author jsnhlbr5
 */
public class CenterAreaView extends JLayeredPane
{
    private static final int DEFAULT_BASE_SIZE = 60;

    private Game model;
    private FlowLayout layerLayout;
    private JPanel tileLayer, interfaceLayer;
    private float scale;
    private int calculatedTileSize;

    /**
     * Constructs a new center area visual representation connected to the given Game
     *
     * @param model
     *            the Game to use as a logical model
     */
    public CenterAreaView(Game model)
    {
        this.model = model;

        SpringLayout layout = new SpringLayout();
        this.setLayout(layout);

        // A single layout shared by the tile & interface layers to ensure that tiles and buttons stack correctly
        layerLayout = new FlowLayout(FlowLayout.CENTER, 6, 6);

        tileLayer = new JPanel();
        tileLayer.setLayout(layerLayout);
        ViewUtils.setPercentage(this, tileLayer, 0f, 0f, 1f, 1f);
        this.add(tileLayer, ViewUtils.TILE_LAYER);

        interfaceLayer = new JPanel();
        interfaceLayer.setLayout(layerLayout);
        interfaceLayer.setOpaque(false);
        ViewUtils.setPercentage(this, interfaceLayer, 0f, 0f, 1f, 1f);
        this.add(interfaceLayer, ViewUtils.INTERFACE_LAYER);
    }

    /**
     * Updates the scaling used by this representation so that it's consistent with its parent
     *
     * @param s
     *            the scaling factor to use
     */
    public void updateScale(float s)
    {
        scale = s;
        calculatedTileSize = (int) Math.floor(DEFAULT_BASE_SIZE * scale);
        layerLayout.setHgap(calculatedTileSize / 10);
        layerLayout.setVgap(calculatedTileSize / 10);

        updateTiles();
    }

    /**
     * Updates the UI to match the underlying logical model
     */
    public void updateTiles()
    {
        // Retrieve and sort tiles
        TileCollection tc = model.getCenterTiles();
        tc.sort(null);
        // Calculate appropriate size based on tile count
        int size = (int) Math.ceil(Math.sqrt(tc.size()));
        int pixels = (int) Math.floor(DEFAULT_BASE_SIZE * 1.1 * size * scale) + layerLayout.getHgap();
        this.setPreferredSize(new Dimension(pixels, pixels));

        // Remove existing tiles and buttons
        for (Component c : tileLayer.getComponents())
            tileLayer.remove(c);
        for (Component c : interfaceLayer.getComponents())
            interfaceLayer.remove(c);

        // Create new tiles and buttons
        for (Color tile : tc)
        {
            JLabel tilePanel = new ImageLabel(ViewUtils.getImageIcon("/img/" + tile.name() + ".png"));
            tilePanel.setPreferredSize(new Dimension(calculatedTileSize, calculatedTileSize));
            tileLayer.add(tilePanel);
            JButton tileButton = new InvisibleButton(new PickTilesAction(tile));
            // White tiles cannot be selected
            if (tile == Color.WHITE)
                tileButton.setEnabled(false);
            tileButton.setPreferredSize(new Dimension(calculatedTileSize, calculatedTileSize));
            interfaceLayer.add(tileButton);
        }
        // Force parent to re-do layout and visually remove objects that no longer exist
        this.getParent().validate();
        this.getParent().repaint();
    }

    private class PickTilesAction extends AbstractAction
    {
        private Color color;

        public PickTilesAction(Color c)
        {
            color = c;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            model.pickTilesFromCenter(color);
            updateTiles();
        }

    }
}
