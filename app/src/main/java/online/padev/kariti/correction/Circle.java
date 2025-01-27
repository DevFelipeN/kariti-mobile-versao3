package online.padev.kariti.correction;

import org.opencv.core.MatOfPoint;

public class Circle {

    public double x, y, radius, xR, yR, wR, hR, perimeter;
    public MatOfPoint contour;

    public Circle(double x, double y, double radius, double xR, double yR, double wR, double hR, MatOfPoint contour, double perimeter) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.xR = xR;
        this.yR = yR;
        this.wR = wR;
        this.hR = hR;
        this.contour = contour;
        this.perimeter = perimeter;
    }
}
