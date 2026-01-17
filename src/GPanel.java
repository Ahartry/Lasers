import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

public class GPanel extends JPanel{

    Sim sim;

    int selected = -1;
    Point2D.Double selectedPoint;
    Point2D.Double startPoint;
    double selectedAngle;

    public GPanel(){
        sim = new Sim(this);

        //gets mouse inputs
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) { 
                if(selected != -1){
                    sim.getMirrorList().get(selected).enableRot();
                    selectedPoint = null;
                    startPoint = null;
                    selected = -1;
                }
            }
        }); 
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e){
                if(selected == -1){
                    selected = sim.getMirrorFromPoint(new Point2D.Double(e.getX(), e.getY()));
                    if(selected == -1){
                        return;
                    }
                    selectedPoint = sim.getMirrorList().get(selected).getPos();
                    selectedAngle = Math.atan2(e.getY() - selectedPoint.y, e.getX() - selectedPoint.x);
                    startPoint = new Point2D.Double(e.getX(), e.getY());
                    sim.getMirrorList().get(selected).disableRot();
                }else{
                    if(SwingUtilities.isRightMouseButton(e)){
                        double angle = Math.atan2(e.getY() - selectedPoint.y, e.getX() - selectedPoint.x);
                        double dAngle = (angle - selectedAngle)/2;
                        sim.getMirrorList().get(selected).setRotation(dAngle);
                    }else if(SwingUtilities.isLeftMouseButton(e)){
                        sim.getMirrorList().get(selected).moveTo(selectedPoint.x + (e.getX() - startPoint.x), selectedPoint.y + (e.getY() - startPoint.y));
                    }

                    repaint();
                }
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { 
                int s = sim.getMirrorFromPoint(new Point2D.Double(e.getX(), e.getY()));

                if(s == -1){
                    return;
                }
                
                Point2D.Double ps[] = sim.getMirrorList().get(s).getPoints();
                
                System.out.println("Points on mirror " + s + " are:");
                System.out.println(Sim.strP(ps[0]) + ", " + Sim.strP(ps[1]) + ", " + Sim.strP(ps[2]) + ", " + Sim.strP(ps[3]) + "\n");
            }
        }); 
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
