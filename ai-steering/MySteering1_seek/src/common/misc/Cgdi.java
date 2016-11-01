
package common.misc;

import common.D2.Vector2D;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.List;

public class Cgdi {
    
    public static final Cgdi gdi = new Cgdi();
    private Graphics2D hdc;
   
    private Color PenColor = Color.BLACK;
      
    //constructor is private
    private Cgdi() {}

    public static Cgdi getInstance() {
        return gdi;
    }

    //ALWAYS call this before drawing
    public void StartDrawing(Graphics2D hdc) { 
        this.hdc = hdc;
    }
  
    public void fillRect(Color c, int left, int top, int width, int height) {
        Color old = hdc.getColor();
        hdc.setColor(c);
        hdc.fillRect(left, top, width, height);
        hdc.setColor(old);
    }

    public void Line(int a, int b, int x, int y) {
        hdc.setColor(PenColor);
        hdc.drawLine(a, b, x, y);
    }

    public void Line(double a, double b, double x, double y) {
        Line((int) a, (int) b, (int) x, (int) y);
    }

    public void ClosedShape(List<Vector2D> points) {
        Polygon pol = new Polygon();

        for (Vector2D p : points) {
            pol.addPoint((int) p.x, (int) p.y);
        }
        hdc.setColor(PenColor);
        hdc.drawPolygon(pol);
    }

    public void Circle(Vector2D pos, double radius) {
        Circle(pos.x, pos.y, radius);
    }

    public void Circle(double x, double y, double radius) {
        hdc.setColor(PenColor);
        hdc.drawOval(
                (int) (x - radius),
                (int) (y - radius),
                (int) (radius * 2),
                (int) (radius * 2));
    }

    public void Circle(int x, int y, double radius) {
        Circle((double) x, (double) y, radius);
    }

}
