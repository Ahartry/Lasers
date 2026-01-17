import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Sim{

    private int xLaser;
    private int yLaser;
    private int lSize = 40;
    private double angleLaser;

    private int bounceDepth = 0;
    private int prevMirror = -1;

    public boolean paused = false;

    private ArrayList<Mirror> mirrorList = new ArrayList<Mirror>();
    private ArrayList<Point2D.Double > pointList = new ArrayList<Point2D.Double >();

    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private double pointAngle = 10;

    GPanel panel;

    public Sim(GPanel p){
        xLaser = 80;
        yLaser = 80;
        angleLaser = Math.PI / 4;
        // angleLaser = 0.2;

        addMirror(new Mirror(200, 250, 120, 120, 0, 0.005));
        addMirror(new Mirror(300, 100, 120, 20, Math.PI / 2, 0.012));
        addMirror(new Mirror(50, 150, 60, 60, 0, 0.008));

        panel = p;
    }

    public void start(){
        tick();
    }

    public void tick(){
        for(int i = 0; i < mirrorList.size(); i++){
            mirrorList.get(i).rotate();
        }
        if(!paused){
            nextTick();
        }
    }

    public void nextTick(){
        panel.repaint();
        executorService.schedule(this::tick, 10, TimeUnit.MILLISECONDS);
    }

    public void togglePause(){
        paused = !paused;
        if(!paused){
            start();
        }
    }

    public void addMirror(Mirror mirror){
        mirrorList.add(mirror);
    }

    public void draw(Graphics2D g){

        //loads bounce points into arraylist
        pointList.clear();
        pointList.add(new Point2D.Double (xLaser, yLaser));
        bounceDepth = 0;
        prevMirror = -1;
        calcBounce(xLaser, yLaser, angleLaser);

        g.setColor(Color.BLACK);

        //draws mirrors
        for(int i = 0; i < mirrorList.size(); i++){
            AffineTransform transform = new AffineTransform();
            Mirror m = mirrorList.get(i);

            //all transforms for rotating mirror
            transform.translate(m.getPos().x, m.getPos().y);
            transform.rotate(m.getAngle());
            transform.translate(-m.getWidth() / 2.0, -m.getHeight() / 2.0);

            Shape mirror = transform.createTransformedShape(new Rectangle2D.Double(0, 0, m.getWidth(), m.getHeight()));

            g.fill(mirror);
        }

        //draws the laser
        g.setColor(Color.RED);
        for(int i = 0; i < pointList.size() - 1; i++){
            g.drawLine((int) pointList.get(i).x, (int) pointList.get(i).y, (int) pointList.get(i + 1).x, (int) pointList.get(i + 1).y);
        }

        //draws laser base
        g.setColor(Color.GRAY);
        g.fillOval(xLaser - (lSize / 2), yLaser - (lSize / 2), lSize, lSize);

    }

    public void calcBounce(double x, double y, double theta){
        Point2D.Double p = new Point2D.Double(x, y);

        //closest point of intersection. If none are found, should go off (basically) to infinity
        Point2D.Double closest = new Point2D.Double((int) (x + 1e4 * Math.cos(theta)), (int) (y + 1e4 * Math.sin(theta)));
        double closestAngle = 10;

        //checks through all mirrors
        for(int i = 0; i < mirrorList.size(); i++){

            if(i == prevMirror){
                continue;
            }

            //gets 2-3 closest points
            Point2D.Double[] ps = mirrorList.get(i).getRelPoints(p, theta);

            if(ps[0] == null){
                continue;
            }

            //resorts the list according to angle
            //sortPByAngle(p, ps);

            Point2D.Double output = new Point2D.Double();
            pointAngle = 0;

            calcIntercept(p, theta, ps[0], ps[1], output);
            if(p.distanceSq(output) < p.distanceSq(closest)){

                // System.out.println("Bounce " + bounceDepth + " in between " + strP(ps[0]) + " and " + strP(ps[1]));
                // System.out.println("This intercept is at " + strP(output) + "\n");
                closest = output;
                closestAngle = pointAngle;
                prevMirror = i;
            }
        }

        //checks if it hits the laser emitter


        //starts next bounce
        pointList.add(closest);
        if(closestAngle != 10){
            bounceDepth += 1;
            calcBounce(closest.x, closest.y, closestAngle);
        }
    }

    //calculates the point of interception and resulting angle, updating the variables
    public void calcIntercept(Point2D.Double origin, double angle, Point2D.Double p1, Point2D.Double p2, Point2D.Double output){

        //coordinates and bounce angle of intersection 
        double x = 0;
        double y = 0;
        double a = 0;

        double p1x = p1.x;
        double p1y = p1.y;
        double p2x = p2.x;
        double p2y = p2.y;

        //if points are in other order
        if(p1x > p2x){
            p1x = p2.x;
            p1y = p2.y;
            p2x = p1.x;
            p2y = p1.y;
        }

        //angle of mirror
        double mirrorAngle = Math.atan2(p2y - p1y, p2x - p1x);

        //bounce angle as derived
        a = 2 * mirrorAngle - angle;

        //slope undefined
        if(p1x == p2x){
            x = p1x;
            y = ((p1x - origin.x) * Math.tan(angle) + origin.y);
            //System.out.println("case 1");
        //slope also undefined
        }else if(angle == Math.PI / 2 || angle == -1 * Math.PI / 2){
            x = origin.x;
            y = ((origin.x - p1x) * Math.tan(mirrorAngle) + p1y);
            //System.out.println("case 2");
        //regular case
        }else{
            x = (( (p1y - origin.y) + (origin.x * Math.tan(angle) - p1x * Math.tan(mirrorAngle)) ) / (Math.tan(angle) - Math.tan(mirrorAngle)));
            y = ((x - origin.x) * Math.tan(angle) + origin.y);
        }

        //normalizes angle
        a = a - (2*Math.PI) * Math.floor((a + Math.PI) / (2*Math.PI));

        output.x = x;
        output.y = y;
        setPointAngle(a);
    }

    //calculates angle from p2 to p1
    public double pAngle(Point2D.Double p1, Point2D.Double p2){
        double x = p2.x - p1.x;
        double y = p2.y - p1.y;

        double a = Math.atan2(y,x);

        return a;
    }

    public void sortPByAngle(Point2D.Double p, Point2D.Double[] ps){
        if(ps.length == 0){
            return;
        }
        Arrays.sort(ps, Comparator.comparingDouble(e -> pAngle(p, e)));
    }

    public ArrayList<Mirror> getMirrorList(){
        return mirrorList;
    }

    public void setPointAngle(double a){
        pointAngle = a;
    }

    public int getMirrorFromPoint(Point2D.Double p){
        int ret = -1;

        //checks all mirrors for intersection
        for(int i = 0; i < mirrorList.size(); i++){
            double a = -1 * mirrorList.get(i).getAngle();
            Point2D.Double ref = mirrorList.get(i).getPos();

            //rotates the point by the opposite rotation 
            double x2 = ((p.x - ref.x) * Math.cos(a) - (p.y - ref.y) * Math.sin(a)) + ref.x;
            double y2 = ((p.x - ref.x) * Math.sin(a) + (p.y - ref.y) * Math.cos(a)) + ref.y;

            //checks if within unrotated rectangle
            if(x2 < ref.x + mirrorList.get(i).getWidth() / 2 && x2 > ref.x - mirrorList.get(i).getWidth() / 2){
                if(y2 < ref.y + mirrorList.get(i).getHeight() / 2 && y2 > ref.y - mirrorList.get(i).getHeight() / 2){
                    ret = i;
                    break;
                }
            }
        }

        return ret;
    }

    public static String strP(Point2D.Double p){
        return "[" + String.format("%.2f", p.x) + ", " + String.format("%.2f", p.y) + "]";
    }
}
