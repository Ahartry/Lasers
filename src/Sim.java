import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
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

    private ArrayList<Mirror> mirrorList = new ArrayList<Mirror>();
    private ArrayList<Point> pointList = new ArrayList<Point>();

    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private double pointAngle = 10;

    GPanel panel;

    public Sim(GPanel p){
        xLaser = 80;
        yLaser = 80;
        angleLaser = Math.PI / 4;
        // angleLaser = 0.2;

        addMirror(new Mirror(200, 200, Math.PI / 4, 120, 120));

        panel = p;
    }

    public void start(){
        
        tick();
    }

    public void tick(){
        for(int i = 0; i < mirrorList.size(); i++){
            mirrorList.get(i).rotate(0.001);
        }
        panel.repaint();
        executorService.schedule(this::tick, 10, TimeUnit.MILLISECONDS);
    }

    public void addMirror(Mirror mirror){
        mirrorList.add(mirror);
    }

    public void draw(Graphics2D g){

        //loads bounce points into arraylist
        pointList.clear();
        pointList.add(new Point(xLaser, yLaser));
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
            g.drawLine(pointList.get(i).x, pointList.get(i).y, pointList.get(i + 1).x, pointList.get(i + 1).y);
        }

        //draws laser base
        g.setColor(Color.GRAY);
        g.fillOval(xLaser - (lSize / 2), yLaser - (lSize / 2), lSize, lSize);

    }

    public void calcBounce(int x, int y, double theta){
        Point p = new Point(x, y);

        //closest point of intersection. If none are found, should go off (basically) to infinity
        Point closest = new Point((int) (x + 2e3 * Math.cos(theta)), (int) (y + 2e3 * Math.sin(theta)));
        double closestAngle = 10;

        //checks through all mirrors
        for(int i = 0; i < mirrorList.size(); i++){
            //gets 2-3 closest points
            Point[] ps = mirrorList.get(i).getRelPoints(p);

            //resorts the list according to angle
            sortPByAngle(p, ps);

            //gets angles to each point
            double[] angles = getAngles(p, ps);

            Point output = new Point();
            pointAngle = 0;

            for(int j = 0; j < angles.length - 1; j++){
                if(theta > angles[j] && theta < angles[j + 1]){
                    calcIntercept(p, theta, ps[j], ps[j + 1], output);
                    if(p.distanceSq(output) < p.distanceSq(closest)){
                        closest = output;
                        closestAngle = pointAngle;
                    }
                }
            }
        }

        //starts next bounce
        pointList.add(closest);
        if(closestAngle != 10){
            calcBounce(p.x, p.y, closestAngle);
        }
    }

    //calculates the point of interception and resulting angle, updating the variables
    public void calcIntercept(Point origin, double angle, Point p1, Point p2, Point output){

        //coordinates and bounce angle of intersection 
        int x = 0;
        int y = 0;
        double a = 0;

        int p1x = p1.x;
        int p1y = p1.y;
        int p2x = p2.x;
        int p2y = p2.y;

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
            y = (int) ((p1x - origin.x) * Math.tan(angle) + origin.y);
            System.out.println("case 1");
        //slope also undefined
        }else if(angle == Math.PI / 2 || angle == -1 * Math.PI / 2){
            x = origin.x;
            y = (int) ((origin.x - p1x) * Math.tan(mirrorAngle) + p1y);
            System.out.println("case 2");
        //regular case
        }else{
            x = (int) (( (p1y - origin.y) + (origin.x * Math.tan(angle) - p1x * Math.tan(mirrorAngle)) ) / (Math.tan(angle) - Math.tan(mirrorAngle)));
            y = (int) ((x - origin.x) * Math.tan(angle) + origin.y);
        }

        output.x = x;
        output.y = y;
        setPointAngle(a);
    }

    //calculates angle from p2 to p1
    public double pAngle(Point p1, Point p2){
        int x = p2.x - p1.x;
        int y = p2.y - p1.y;

        double a = Math.atan2(y,x);

        return a;
    }

    //returns array of points with furthest point(s) removed
    public Point[] getPByDist(Point p, Point[] ps) {
        Arrays.sort(ps, Comparator.comparingDouble(e -> e.distanceSq(p)));
        return Arrays.copyOf(ps, ps.length - 1);
    }

    public void sortPByAngle(Point p, Point[] ps){
        Arrays.sort(ps, Comparator.comparingDouble(e -> pAngle(p, e)));
    }

    public double[] getAngles(Point p1, Point[] ps){
        double[] angles = new double[ps.length];
        for(int i = 0; i < ps.length; i++){
            angles[i] = pAngle(p1, ps[i]);
        }
        return angles;
    }

    public ArrayList<Mirror> getMirrorList(){
        return mirrorList;
    }

    public void setPointAngle(double a){
        pointAngle = a;
    }
}
