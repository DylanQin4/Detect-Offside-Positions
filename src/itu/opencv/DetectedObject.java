package itu.opencv;

import org.opencv.core.Point;

public record DetectedObject(Point position, double radius) {

    @Override
    public String toString() {
        return "DetectedObject{" +
                "position=" + position +
                ", radius=" + radius +
                '}';
    }
}