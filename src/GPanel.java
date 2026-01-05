import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;

import javax.swing.*;

public class GPanel extends JPanel{

    Sim sim;

    public GPanel(){
        sim = new Sim(this);
    }

    public Sim getSim(){
        return sim;
    }

    @Override
    public void paintComponent(Graphics g1) {
        g1.setColor(Color.WHITE);
        g1.clearRect(0,0,getWidth(), getHeight());
        this.getParent().revalidate();
        super.paintComponent(g1);
        Graphics2D g = (Graphics2D) g1;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        sim.draw(g);
        
        this.getParent().revalidate();
        Toolkit.getDefaultToolkit().sync();
    }
}
