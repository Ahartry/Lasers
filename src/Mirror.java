import java.awt.Point;
import java.util.Arrays;
import java.util.Comparator;

public class Mirror {

    private double angle = 0;
    private int xpos = 0;
    private int ypos = 0;

    private int width = 80;
    private int height = 10;

    private Point[] points = new Point[4];

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

    public Point getPos(){
        return new Point(xpos, ypos);
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

    public Point[] getPoints(){
        return points;
    }

    public Point[] getRelPoints(Point ref){
        Arrays.sort(points, Comparator.comparingDouble(e -> e.distanceSq(ref)));

        Point[] ret = new Point[3];
        ret[0] = points[0];

        for(int i = 1; i < 3; i++){
            int xc = (points[0].x + points[i].x) / 2;
            int yc = (points[0].y + points[i].y) / 2;

            int dx = xc - xpos;
            int dy = yc - ypos;

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

            int x = xsign * (width / 2);
            int y = ysign * (height / 2);

            int x2 = (int) (x * Math.cos(angle) - y * Math.sin(angle));
            int y2 = (int) (x * Math.sin(angle) + y * Math.cos(angle));

            int x3 = xpos + x2;
            int y3 = ypos + y2;

            points[i] = new Point(x3, y3);

            //System.out.println("Point " + i + " is at " + x3 + ", " + y3);
        }
    }
}
