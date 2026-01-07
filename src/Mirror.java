import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Comparator;

public class Mirror {

    private double angle = 0;
    private int xpos = 0;
    private int ypos = 0;

    private int width = 80;
    private int height = 10;

    private Point2D.Double [] points = new Point2D.Double [4];

    public Mirror(){
        updatePoints();
    }

    public Mirror(int x, int y, double a){
        xpos = x;
        ypos = y;
        angle = a;
        updatePoints();
    }

    public Mirror(int x, int y, double a, int w, int h){
        xpos = x;
        ypos = y;
        angle = a;
        width = w;
        height = h;
        updatePoints();
    }

    public void move(int x, int y){
        xpos = x;
        ypos = y;
        updatePoints();
    }

    public void setRotation(double a){
        angle = a;
        updatePoints();
    }

    public void rotate(double a){
        angle += a;
        updatePoints();
    }

    public Point2D.Double  getPos(){
        return new Point2D.Double (xpos, ypos);
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

    public Point2D.Double [] getPoints(){
        return points;
    }

    public Point2D.Double [] getRelPoints(Point2D.Double  ref){
        Arrays.sort(points, Comparator.comparingDouble(e -> e.distanceSq(ref)));

        Point2D.Double [] ret = new Point2D.Double [3];
        ret[0] = points[0];

        for(int i = 1; i < 3; i++){
            double xc = (points[0].x + points[i].x) / 2;
            double yc = (points[0].y + points[i].y) / 2;

            double dx = xc - xpos;
            double dy = yc - ypos;

            if(dx * (points[0].x - ref.x) + dy * (points[0].y - ref.y) < 0){
                if(ret[1] == null){
                    ret[1] = points[i];
                }else{
                    ret[2] = points[i];
                }
            }
        }

        if(ret[2] == null){
            return Arrays.copyOf(ret, 2);
        }else{
            return Arrays.copyOf(ret, 3);
        }

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
}
