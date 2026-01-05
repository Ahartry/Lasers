import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.*;

public class GFrame extends JFrame{

    JPanel panel;
    GPanel gpanel;

    //window dimensions
    int windowW = 800;
    int windowH = 500;

    public GFrame() throws InterruptedException{
        //basic setup
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(windowW, windowH));
        setLocationRelativeTo(null);
        setResizable(true);
        setTitle("Laser thing");

        //creates starting menu
        setup();

        //final loading
        setVisible(true);

        gpanel.sim.start();
    }

    //this code sets up the main menu
    public void setup() throws InterruptedException{

        //master panel
        panel = new JPanel();

        //sets layout and adds panel to frame
        panel.setLayout(new BorderLayout());
        add(panel);

        gpanel = new GPanel();
        MPanel mpanel = new MPanel(gpanel);

        panel.add(mpanel, BorderLayout.NORTH);
        panel.add(gpanel, BorderLayout.CENTER);

        //increases and then decreases window size, to force redraw
        jiggle();
    }

    //sad that I need this
    public void jiggle(){
        setSize(windowW - 1, windowH - 1);
        setSize(windowW, windowH);
    }
}
