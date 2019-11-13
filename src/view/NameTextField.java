package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;
import javax.swing.text.Highlighter;

public class NameTextField extends JTextField implements FocusListener
{
    private final Highlighter defaultHighlighter;
    private final Font defaultFont, hintFont;
    private final String fallback;
    private boolean showingHint;

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

    public String getUserText()
    {
        return showingHint ? "" : getText();
    }
    
    private void changeFont()
    {
        setHighlighter(showingHint ? null : defaultHighlighter);
        setFont(showingHint ? hintFont : defaultFont);
        setForeground(showingHint ? Color.GRAY : Color.BLACK);
    }
}