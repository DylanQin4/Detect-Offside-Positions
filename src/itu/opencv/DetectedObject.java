package itu.opencv;

import org.opencv.core.Point;

public class DetectedObject {
    private Point position;
    private double radius;

    public DetectedObject(Point position, double radius) {
        this.position = position;
        this.radius = radius;
    }

    public Point getPosition() {
        return position;
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public String toString() {
        return "DetectedObject{" +
                "position=" + position +
                ", radius=" + radius +
                '}';
    }
}