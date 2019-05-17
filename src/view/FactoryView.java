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

public class FactoryView extends JLayeredPane
{
    //Percentage-based size/position constants (Based on factory and tile image sizes, which reflect IRL relative sizes)
    private static final float TILE_SIZE = 0.25f; // 60/240
    private static final float FIRST_POSITION = 0.2375f; // 57/240 (60-3 distance from edge if touching middle - half gap)
    private static final float SECOND_POSITION = 0.5125f; // 123/240 (120+3 half + half gap)
    
    private SpringLayout layout;
    private Game model;
    private int index;
    private JButton[] buttons;
    
    public FactoryView(Game g, int index)
    {
        model = g;
        this.index = index;
        layout = new SpringLayout();
        this.setLayout(layout);
        
        ImageLabel background = new ImageLabel(ViewUtils.getImageIcon("/img/factory.png"));
        ViewUtils.setPercentage(this, background, 0f, 0f, 1f, 1f);
        this.add(background, DEFAULT_LAYER);
        
        buttons = new JButton[4];
        for(int i = 0; i < 4; ++i)
        {
            buttons[i] = new InvisibleButton(new PickTilesAction(index, null));
            float x = (i%2 == 0) ? FIRST_POSITION : SECOND_POSITION;
            float y = (i < 2) ? FIRST_POSITION : SECOND_POSITION;
            ViewUtils.setPercentage(this, buttons[i], x, y, TILE_SIZE, TILE_SIZE);
            this.add(buttons[i], ViewUtils.INTERFACE_LAYER);
        }
        
        updateTiles();
    }
    
    public void updateTiles()
    {
        //Remove old tiles
        Component[] ca = this.getComponentsInLayer(ViewUtils.TILE_LAYER);
        for(int i = 0; i < ca.length; ++i)
        {
            this.remove(ca[i]);
        }
        
        TileCollection tc = model.getFactoryTiles(index);
        disableButtons();
        if(tc.isEmpty())
        {
            return;
        }
        for(int i = 0; i < tc.size(); ++i)
        {
            JLabel tilePanel = new ImageLabel(ViewUtils.getImageIcon("/img/" + tc.get(i).name() + ".png"));
            float x = (i%2 == 0) ? FIRST_POSITION : SECOND_POSITION;
            float y = (i < 2) ? FIRST_POSITION : SECOND_POSITION;
            ViewUtils.setPercentage(this, tilePanel, x, y, TILE_SIZE, TILE_SIZE);
            this.add(tilePanel, ViewUtils.TILE_LAYER);
            ((PickTilesAction)buttons[i].getAction()).setColor(tc.get(i));
            buttons[i].setEnabled(true);
        }
    }
    
    private void disableButtons()
    {
        for(int i = 0; i < 4; ++i)
        {
            buttons[i].setEnabled(false);
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
