package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;
import javax.swing.text.Highlighter;

/**
 * Extension of JTextField to display a gray, italicized default/fallback text when not focused and void of user input
 *
 * @author jsnhlbr5
 *
 */
public class NameTextField extends JTextField implements FocusListener
{
    private final Highlighter defaultHighlighter;
    private final Font defaultFont, hintFont;
    private final String fallback;
    private boolean showingHint;

    /**
     * Initializes the text field with the fallback text, and saves the original font and highlighter so they can be
     * restored when the field is focused or contains user text.
     *
     * @param fallback
     *            the initial value/fallback text
     */
    public NameTextField(final String fallback)
    {
        super(fallback);
        defaultHighlighter = this.getHighlighter();
        defaultFont = this.getFont();
        hintFont = defaultFont.deriveFont(Font.ITALIC);
        this.fallback = fallback;
        this.showingHint = true;
        changeFont();
        super.addFocusListener(this);
    }

    /**
     * If there is no user text (showing fallback text), remove the text and update font
     */
    @Override
    public void focusGained(FocusEvent e)
    {
        if (this.getUserText().isEmpty())
        {
            setText("");
            showingHint = false;
            changeFont();
        }
    }

    /**
     * If there is no user text (should show fallback text), add the fallback text and update font
     */
    @Override
    public void focusLost(FocusEvent e)
    {
        if (this.getUserText().isEmpty())
        {
            setText(fallback);
            showingHint = true;
            changeFont();
        }
    }

    /**
     * Gets the text entered by the user (if any)
     *
     * @return the text entered by the user (empty String if none)
     */
    public String getUserText()
    {
        return showingHint ? "" : getText();
    }

    /**
     * Switches back and forth between default and fallback text styles
     */
    private void changeFont()
    {
        setHighlighter(showingHint ? null : defaultHighlighter);
        setFont(showingHint ? hintFont : defaultFont);
        setForeground(showingHint ? Color.GRAY : Color.BLACK);
    }
}
