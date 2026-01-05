import java.awt.Color;
import java.awt.Dimension;

import javax.swing.*;

public class MPanel extends JPanel{

    public MPanel(GPanel gpanel){
        this.setPreferredSize(new Dimension(100, 50));
        this.setBackground(Color.LIGHT_GRAY);
    }
}
