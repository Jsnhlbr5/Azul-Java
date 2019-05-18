package view;

import java.awt.Cursor;

import javax.swing.Action;
import javax.swing.JButton;

/**
 * Convenience class for creating transparent, text-free, un-focusable JButton
 * to overlay over images via JLayeredPane
 *
 * @author jsnhlbr5
 */
public class InvisibleButton extends JButton
{
    public InvisibleButton(Action a)
    {
        super(a);

        this.setContentAreaFilled(false);
        this.setBorderPainted(false);
        this.setHideActionText(true);
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.setFocusable(false);
    }
}
