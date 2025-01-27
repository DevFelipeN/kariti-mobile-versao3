package online.padev.kariti.correction;

import org.opencv.core.Point;

import java.util.List;

public class SquaresCircles{
    Point quetions;
    List<Point> listCircles;
    SquaresCircles(Point quetions, List<Point> listCircles){
        this.quetions = quetions;
        this.listCircles = listCircles;
    }
}
