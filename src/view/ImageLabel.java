package view;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * Creates a scalable image label
 * @author jsnhlbr5
 *
 */
public class ImageLabel extends JLabel{
    private Image myImage;

    public ImageLabel(ImageIcon image){
        super();
        myImage = image.getImage();
    }

    @Override
    public void paint(Graphics g){
        g.drawImage(myImage, 0, 0, this.getWidth(), this.getHeight(), null);
    }
}