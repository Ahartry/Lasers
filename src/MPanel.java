import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.*;

public class MPanel extends JPanel{

    private final GPanel gpanel;

    public MPanel(GPanel g){
        this.setPreferredSize(new Dimension(100, 50));
        this.setBackground(Color.LIGHT_GRAY);

        gpanel = g;

        GButton pauseButton = new GButton("Pause/Play");
        pauseButton.setPreferredSize(new Dimension(200, 40));
        pauseButton.setFont(new Font("Sans Serif", Font.PLAIN, 20));
        pauseButton.setColor(Color.DARK_GRAY);

        PauseAction pauseAction = new PauseAction();
        pauseButton.addActionListener(pauseAction);

        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "pauseAction");
        this.getActionMap().put("pauseAction", pauseAction);

        add(pauseButton);
    }

    private class PauseAction extends AbstractAction{

        @Override
        public void actionPerformed(ActionEvent e) {
            gpanel.sim.togglePause();
        }

    }
}
