package view;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.WindowConstants;

import model.Game;

public class CommonAreaView extends JPanel
{
    private Game model;
    private SpringLayout layout;
    private CenterAreaView center;
    private FactoryView[] factories;
    private final JFrame frame;

    private static final int DEFAULT_SIZE = 1040;
    private static final float FACTORY_SIZE = 240 / 1040f;
    // pre-calculated positions for factories (as percent of parent)
    //@formatter:off
    private static final float[] fx5 = { 400/1040f, 780/1040f, 635/1040f, 165/1040f,  20/1040f };
    private static final float[] fy5 = {   0/1040f, 276/1040f, 724/1040f, 724/1040f, 276/1040f };
    private static final float[] fx7 = { 400/1040f, 713/1040f, 790/1040f, 574/1040f, 226/1040f,  10/1040f,  87/1040f };
    private static final float[] fy7 = {   0/1040f, 151/1040f, 489/1040f, 760/1040f, 760/1040f, 489/1040f, 151/1040f };
    private static final float[] fx9 = { 400/1040f, 657/1040f, 794/1040f, 746/1040f, 537/1040f, 263/1040f,  54/1040f,   6/1040f, 143/1040f };
    private static final float[] fy9 = {   0/1040f,  94/1040f, 331/1040f, 600/1040f, 776/1040f, 776/1040f, 600/1040f, 331/1040f,  94/1040f };
    //@formatter:on

    // Create the view at the default size
    public CommonAreaView(Game m)
    {
        this(m, DEFAULT_SIZE);
    }

    // Create the view with the specified size (in pixels)
    public CommonAreaView(Game m, int size)
    {
        model = m;

        layout = new SpringLayout();
        this.setLayout(layout);

        this.setSize(size, size);
        this.setPreferredSize(getSize());

        center = new CenterAreaView(model);
        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, center, 0, SpringLayout.HORIZONTAL_CENTER, this);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, center, 0, SpringLayout.VERTICAL_CENTER, this);
        this.add(center);
        // Propagate any scaling from default to the center area view.
        center.updateScale(size / (float) DEFAULT_SIZE);

        switch (model.getFactoryCount())
        {
            case 5:
                factories = new FactoryView[5];
                for (int i = 0; i < 5; ++i)
                {
                    factories[i] = new FactoryView(model, i);
                    ViewUtils.setPercentage(this, factories[i], fx5[i], fy5[i], FACTORY_SIZE, FACTORY_SIZE);
                    this.add(factories[i]);
                }
                break;
            case 7:
                factories = new FactoryView[7];
                for (int i = 0; i < 7; ++i)
                {
                    factories[i] = new FactoryView(model, i);
                    ViewUtils.setPercentage(this, factories[i], fx7[i], fy7[i], FACTORY_SIZE, FACTORY_SIZE);
                    this.add(factories[i]);
                }
                break;
            case 9:
                factories = new FactoryView[9];
                for (int i = 0; i < 9; ++i)
                {
                    factories[i] = new FactoryView(model, i);
                    ViewUtils.setPercentage(this, factories[i], fx9[i], fy9[i], FACTORY_SIZE, FACTORY_SIZE);
                    this.add(factories[i]);
                }
                break;
        }

        frame = new JFrame("Common Center Area");
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.add(this);
        JMenuBar mb = new JMenuBar();
        JMenu menu = new JMenu("Game Menu");
        menu.add(new QuitAction());
        mb.add(menu);
        frame.setJMenuBar(mb);
        frame.pack();
        frame.setResizable(false);
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(ViewUtils.class.getResource("/img/TEAL.png")));
    }

    @Override
    public void setVisible(boolean b)
    {
        frame.setVisible(b);
    }

    public FactoryView getFactory(int i)
    {
        return factories[i];
    }

    public CenterAreaView getCenter()
    {
        return center;
    }

    private class QuitAction extends AbstractAction
    {
        public QuitAction()
        {
            super("Quit");
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            int really = JOptionPane.showConfirmDialog(frame, "Are you sure you want to exit?", "Exit Game",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (really == JOptionPane.YES_OPTION)
                System.exit(0);
        }

    }
}
