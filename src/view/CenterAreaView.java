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

public class CenterAreaView extends JLayeredPane
{
    private static final int DEFAULT_BASE_SIZE = 60;

    private Game model;
    private FlowLayout layerLayout;
    private JPanel tileLayer, interfaceLayer;
    private float scale;
    private int calculatedTileSize;
    
    public CenterAreaView(Game model)
    {
        this.model = model;
        
        SpringLayout layout = new SpringLayout();
        this.setLayout(layout);
        
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
    
    public void updateScale()
    {
        CommonAreaView parent = (CommonAreaView) this.getParent();
        scale = parent.getHeight()/(float)CommonAreaView.DEFAULT_SIZE;
        calculatedTileSize = (int)Math.floor(DEFAULT_BASE_SIZE*scale);
        layerLayout.setHgap(calculatedTileSize/10);
        layerLayout.setVgap(calculatedTileSize/10);
        
        updateTiles();
    }
    
    public void updateTiles()
    {
        TileCollection tc = model.getCenterTiles();
        tc.sort(null);
        int size = (int)Math.ceil(Math.sqrt(tc.size()));
        int pixels = (int)Math.floor(DEFAULT_BASE_SIZE*1.1*size*scale) + layerLayout.getHgap();
        this.setSize(pixels, pixels);
        this.setPreferredSize(getSize());
        
        for(Component c : tileLayer.getComponents())
            tileLayer.remove(c);
        for(Component c : interfaceLayer.getComponents())
            interfaceLayer.remove(c);
        
        for(Color tile : tc)
        {
            JLabel tilePanel = new ImageLabel(ViewUtils.getImageIcon("/img/" + tile.name() + ".png"));
            tilePanel.setPreferredSize(new Dimension(calculatedTileSize, calculatedTileSize));
            tileLayer.add(tilePanel);
            JButton tileButton = new InvisibleButton(new PickTilesAction(tile));
            if(tile == Color.WHITE)
                tileButton.setEnabled(false);
            tileButton.setPreferredSize(new Dimension(calculatedTileSize, calculatedTileSize));
            interfaceLayer.add(tileButton);
        }
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
