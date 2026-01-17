import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Comparator;

public class Mirror {

    private double angle = 0;
    private double omega = 0.005;
    private int xpos = 0;
    private int ypos = 0;

    private int width = 80;
    private int height = 10;

    private boolean enabled = true;

    private Point2D.Double[] points = new Point2D.Double[4];

    public Mirror(){
        updatePoints();
    }

    public Mirror(int x, int y, double a){
        xpos = x;
        ypos = y;
        angle = a;
        updatePoints();
    }

    public Mirror(int x, int y, int w, int h, double a, double o){
        xpos = x;
        ypos = y;
        width = w;
        height = h;
        angle = a;
        omega = o;
        updatePoints();
    }

    public void moveTo(int x, int y){
        xpos = x;
        ypos = y;
        updatePoints();
    }

    public void moveTo(double x, double y){
        xpos = (int) x;
        ypos = (int) y;
        updatePoints();
    }

    public void setRotation(double a){
        angle = a;
        updatePoints();
    }

    // public void rotate(double a){
    //     angle += a;
    //     updatePoints();
    // }

    public void rotate(){
        if(enabled){
            angle += omega;
        }
        updatePoints();
    }

    public void setOmega(double o){
        omega = o;
    }

    public void enableRot(){
        enabled = true;
    }

    public void disableRot(){
        enabled = false;
    }

    public Point2D.Double getPos(){
        return new Point2D.Double(xpos, ypos);
    }

    public double getAngle(){
        return angle;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    public Point2D.Double[] getPoints(){
        return points;
    }

    public Point2D.Double[] getRelPoints(Point2D.Double ref, double theta){
        Arrays.sort(points, Comparator.comparingDouble(e -> e.distanceSq(ref)));

        double[] angles = getAngles(ref, points);
        Point2D.Double[] ret = new Point2D.Double[2];

        //checks the two faces made by p0 and each p1 and p2
        for(int i = 1; i < 3; i++){
            //only possible if the angles are across the discontinuity
            if(Math.abs(angles[0] - angles[i]) > Math.PI){
                if(isInRange(theta, angles[0], angles[i])){
                    continue;
                }
            }else if(!isInRange(theta, angles[0], angles[i])){
                continue;
            }

            if(isFacing(0, i, theta)){
                ret[0] = points[0];
                ret[1] = points[i];
            }
        }

        return ret;
    }

    public void updatePoints(){
        //updates the four corners of the rectangle
        //points go counter clockwise:
        // 2 1
        // 3 4
        for(int i = 0; i < 4; i++){
            int xsign = ((((i + 1) / 2) % 2) * 2 - 1) * -1;
            int ysign = ((i / 2) * 2 - 1) * -1;

            double x = xsign * ( (double) width / 2);
            double y = ysign * ( (double) height / 2);

            double x2 = (x * Math.cos(angle) - y * Math.sin(angle));
            double y2 = (x * Math.sin(angle) + y * Math.cos(angle));

            double x3 = xpos + x2;
            double y3 = ypos + y2;

            points[i] = new Point2D.Double(x3, y3);

            //System.out.println("Point2D.Double  " + i + " is at " + x3 + ", " + y3);
        }
    }

    //calculates angle from p2 to p1
    public double pAngle(Point2D.Double p1, Point2D.Double p2){
        double x = p2.x - p1.x;
        double y = p2.y - p1.y;

        double a = Math.atan2(y,x);

        return a;
    }

    public double[] getAngles(Point2D.Double p1, Point2D.Double[] ps){
        double[] angles = new double[ps.length];
        for(int i = 0; i < ps.length; i++){
            angles[i] = pAngle(p1, ps[i]);
        }
        return angles;
    }

    public boolean isInRange(double val, double b1, double b2){
        if( (b1 < val && val < b2) || (b2 < val && val < b1) ){
            return true;
        }else{
            return false;
        }
    }

    public boolean isFacing(int p1, int p2, double theta){
        double xc = (points[p1].x + points[p2].x) / 2;
        double yc = (points[p1].y + points[p2].y) / 2;

        double dx = xc - xpos;
        double dy = yc - ypos;

        return Math.cos(theta) * dx + Math.sin(theta) * dy < 0;
    }

}
