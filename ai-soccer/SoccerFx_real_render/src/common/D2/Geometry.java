
package common.D2;

import common.Game.Functions;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class Geometry {
    
    public static enum SpanType {
        PLANE_BACKSIDE, PLANE_FRONT, ON_PLANE;
    }

    /**
     * given a plane and a ray this function determins how far along the ray an interestion occurs. 
     * Returns negative if the ray is parallel
     */
    public static double getDistanceToRayPlaneIntersection(
            Vector rayOrigin,
            Vector rayHeading,
            Vector planePoint,
            Vector planeNormal) 
    {
        double d = -planeNormal.dot(planePoint);
        double numer = planeNormal.dot(rayOrigin) + d;
        double denom = planeNormal.dot(rayHeading);

        // normal is parallel to vector
        if ((denom < 0.000001) && (denom > -0.000001)) {
            return (-1.0);
        }
        return -(numer / denom);
    }

    public static SpanType whereIsPoint(
            Vector point,
            Vector pointOnPlane,
            Vector planeNormal) 
    {
        Vector dir = pointOnPlane.subn(point);
        double d = dir.dot(planeNormal);

        if (d < -0.000001) {
            return SpanType.PLANE_FRONT;
        } else if (d > 0.000001) {
            return SpanType.PLANE_BACKSIDE;
        }
        return SpanType.ON_PLANE;
    }
    
    /**
     * GetRayCircleIntersec.
     */
    public static double getRayCircleIntersect(
            Vector rayOrigin,
            Vector RayHeading,
            Vector circleOrigin,
            double radius) 
    {
        Vector toCircle = circleOrigin.subn(rayOrigin);
        double length = toCircle.length();
        double v = toCircle.dot(RayHeading);
        double d = radius * radius - (length * length - v * v);

        // If there was no intersection, return -1
        if (d < 0.0) {
            return (-1.0);
        }
        // Return the dist to the [first] intersecting point
        return (v - Math.sqrt(d));
    }

    /**
     *  DoRayCircleIntersect.
     */
    public static boolean doRayCircleIntersect(
            Vector rayOrigin,
            Vector rayHeading,
            Vector circleOrigin,
            double radius) 
    {
        Vector toCircle = circleOrigin.subn(rayOrigin);
        double length = toCircle.length();
        double v = toCircle.dot(rayHeading);
        double d = radius * radius - (length * length - v * v);
        // If there was no intersection, return -1
        return (d < 0.0);
    }

    /**
     *  Given a point P and a circle of radius R centered at C this function
     *  determines the two points on the circle that intersect with the 
     *  tangents from P to the circle. Returns false if P is within the circle.
     */
    public static boolean getTangentPoints(Vector C, double R, Vector P, Vector T1, Vector T2) {
        
        Vector PmC = P.subn(C);
        double SqrLen = PmC.lengthSq();
        double RSqr = R * R;
        if (SqrLen <= RSqr) {
            // P is inside or on the circle
            return false;
        }
        double InvSqrLen = 1 / SqrLen;
        double Root = Math.sqrt(Math.abs(SqrLen - RSqr));

        T1.x = C.x + R * (R * PmC.x - PmC.y * Root) * InvSqrLen;
        T1.y = C.y + R * (R * PmC.y + PmC.x * Root) * InvSqrLen;
        T2.x = C.x + R * (R * PmC.x + PmC.y * Root) * InvSqrLen;
        T2.y = C.y + R * (R * PmC.y - PmC.x * Root) * InvSqrLen;
        return true;
    }

    /**
     * given a line segment AB and a point P, this function calculates the 
     * perpendicular dist between them.
     */
    public static double doDistToLineSegment(Vector A, Vector B, Vector P) {
        //if the angle is obtuse between PA and AB is obtuse then the closest vertex must be A
        double dotA = (P.x - A.x) * (B.x - A.x) + (P.y - A.y) * (B.y - A.y);
        if (dotA <= 0) {
            return A.dist(P);
        }
        //if the angle is obtuse between PB and AB is obtuse then the closest vertex must be B
        double dotB = (P.x - B.x) * (A.x - B.x) + (P.y - B.y) * (A.y - B.y);
        if (dotB <= 0) {
            return B.dist(P);
        }
        //calculate the point along AB that is the closest to P
        //Vector2D Point = A + ((B - A) * dotA)/(dotA + dotB);
        Vector Point = A.addn(B.subn(A).mul(dotA).div(dotA + dotB));
        
        //calculate the dist P-Point
        return P.dist(Point);
    }

    /**
     *  as above, but avoiding sqrt.
     */
    public static double getDistToLineSegmentSq(Vector A, Vector B, Vector P) {
        //if the angle is obtuse between PA and AB is obtuse then the closest vertex must be A
        double dotA = (P.x - A.x) * (B.x - A.x) + (P.y - A.y) * (B.y - A.y);

        if (dotA <= 0) {
            return A.distSq(P);
        }
        //if the angle is obtuse between PB and AB is obtuse then the closest
        //vertex must be B
        double dotB = (P.x - B.x) * (A.x - B.x) + (P.y - B.y) * (A.y - B.y);

        if (dotB <= 0) {
            return B.distSq(P);
        }
        //calculate the point along AB that is the closest to P
        //Vector2D Point = A + ((B - A) * dotA)/(dotA + dotB);
        Vector Point = A.addn(B.subn(A).mul(dotA).div(dotA + dotB));

        //calculate the dist P-Point
        return P.distSq(Point);
    }

    /**
     *	Given 2 lines in 2D space AB, CD this returns true if an intersection occurs.
     */
    public static boolean lineIntersection2D(Vector A, Vector B, Vector C, Vector D) {
        
        double rTop = (A.y - C.y) * (D.x - C.x) - (A.x - C.x) * (D.y - C.y);
        double sTop = (A.y - C.y) * (B.x - A.x) - (A.x - C.x) * (B.y - A.y);
        double Bot = (B.x - A.x) * (D.y - C.y) - (B.y - A.y) * (D.x - C.x);

        if (Bot == 0) {
            return false;
        }
        double invBot = 1.0 / Bot;
        double r = rTop * invBot;
        double s = sTop * invBot;
        //lines intersect or not
        return ((r > 0) && (r < 1) && (s > 0) && (s < 1));
    }

    /**
     *  Given 2 lines in 2D space AB, CD this returns true if an 
     * intersection occurs and sets dist to the dist the intersection occurs along AB.
     */
    public static boolean lineIntersection2D(Vector A, Vector B, Vector C, Vector D, AtomicReference<Double> dist) {
        
        double rTop = (A.y - C.y) * (D.x - C.x) - (A.x - C.x) * (D.y - C.y);
        double sTop = (A.y - C.y) * (B.x - A.x) - (A.x - C.x) * (B.y - A.y);
        double Bot = (B.x - A.x) * (D.y - C.y) - (B.y - A.y) * (D.x - C.x);

        if (Bot == 0) {
            if (Functions.isEqual(rTop, 0) && Functions.isEqual(sTop, 0)) {
                return true;
            }
            return false;
        }
        double r = rTop / Bot;
        double s = sTop / Bot;

        if ((r > 0) && (r < 1) && (s > 0) && (s < 1)) {
            dist.set(A.dist(B) * r);
            return true;
        } else {
            dist.set(0.0);
            return false;
        }
    }

    /**
     *  Given 2 lines in 2D space AB, CD this returns true if an 
     * intersection occurs and sets dist to the dist the intersection
     * occurs along AB. Also sets the 2d vector point to the point of
     *  intersection
     */
    public static boolean lineIntersection2D(Vector A,
            Vector B,
            Vector C,
            Vector D,
            AtomicReference<Double> dist,
            Vector point) 
    {
        double rTop = (A.y - C.y) * (D.x - C.x) - (A.x - C.x) * (D.y - C.y);
        double rBot = (B.x - A.x) * (D.y - C.y) - (B.y - A.y) * (D.x - C.x);
        double sTop = (A.y - C.y) * (B.x - A.x) - (A.x - C.x) * (B.y - A.y);
        double sBot = (B.x - A.x) * (D.y - C.y) - (B.y - A.y) * (D.x - C.x);

        if ((rBot == 0) || (sBot == 0)) {
            //lines are parallel
            return false;
        }
        double r = rTop / rBot;
        double s = sTop / sBot;

        if ((r > 0) && (r < 1) && (s > 0) && (s < 1)) {
            dist.set(A.dist(B) * r);
            point.set(B.subn(A).mul(r).add(A));
            return true;
        } 
        else {
            dist.set(0.0);
            return false;
        }
    }

    /**
     *  tests two polygons for intersection. *Does not check for enclosure*
     */
    public static boolean getObjectIntersection2D(ArrayList<Vector> object1, ArrayList<Vector> object2) {
        //test each line segment of object1 against each segment of object2
        for (int r = 0; r < object1.size() - 1; ++r) {
            for (int t = 0; t < object2.size() - 1; ++t) {
                if (lineIntersection2D(
                        object2.get(t),
                        object2.get(t + 1),
                        object1.get(r),
                        object1.get(r + 1))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *  tests a line segment against a polygon for intersection
     */
    public static boolean segmentObjectIntersection2D(final Vector A, final Vector B, final ArrayList<Vector> object) {
        //test AB against each segment of object
        for (int r = 0; r < object.size() - 1; ++r) {
            if (lineIntersection2D(A, B, object.get(r), object.get(r + 1))) {
                return true;
            }
        }
        return false;
    }

    /**
     *  Returns true if the two circles overlap.
     */
    public static boolean twoCirclesOverlapped(double x1, double y1, double r1, double x2, double y2, double r2) {
        double DistBetweenCenters = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        return (DistBetweenCenters < (r1 + r2)) || (DistBetweenCenters < Math.abs(r1 - r2));
    }

    /**
     * Returns true if the two circles overlap.
     */
    public static boolean twoCirclesOverlapped(Vector c1, double r1, Vector c2, double r2) {
        double DistBetweenCenters = Math.sqrt((c1.x - c2.x) * (c1.x - c2.x) + (c1.y - c2.y) * (c1.y - c2.y));
        return (DistBetweenCenters < (r1 + r2)) || (DistBetweenCenters < Math.abs(r1 - r2));
    }

    /**
     *  returns true if one circle encloses the other.
     */
    public static boolean twoCirclesEnclosed(double x1, double y1, double r1, double x2, double y2, double r2) {
        double DistBetweenCenters = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        return DistBetweenCenters < Math.abs(r1 - r2);
    }

    /**
     * Given two circles this function calculates the intersection points of any overlap.
     *  returns false if no overlap found
     */
    public static boolean twoCirclesIntersectionPoints(
            double x1, double y1, double r1,
            double x2, double y2, double r2,
            AtomicReference<Double> p3X, AtomicReference<Double> p3Y,
            AtomicReference<Double> p4X, AtomicReference<Double> p4Y) 
    {
        //first check to see if they overlap
        if (!twoCirclesOverlapped(x1, y1, r1, x2, y2, r2)) {
            return false;
        }
        //calculate the dist between the circle centers
        double d = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        //Now calculate the dist from the center of each circle to the center
        //of the line which connects the intersection points.
        double a = (r1 - r2 + (d * d)) / (2 * d);
        double b = (r2 - r1 + (d * d)) / (2 * d);

        //MAYBE A TEST FOR EXACT OVERLAP? 
        //calculate the point P2 which is the center of the line which 
        //connects the intersection points
        double p2X, p2Y;
        p2X = x1 + a * (x2 - x1) / d;
        p2Y = y1 + a * (y2 - y1) / d;

        //calculate first point
        double h1 = Math.sqrt((r1 * r1) - (a * a));
        p3X.set(p2X - h1 * (y2 - y1) / d);
        p3Y.set(p2Y + h1 * (x2 - x1) / d);


        //calculate second point
        double h2 = Math.sqrt((r2 * r2) - (a * a));
        p4X.set(p2X + h2 * (y2 - y1) / d);
        p4Y.set(p2Y - h2 * (x2 - x1) / d);

        return true;
    }

    /**
     *  Tests to see if two circles overlap and if so calculates the area defined by the union.
     */
    public static double twoCirclesIntersectionArea(double x1, double y1, double r1, double x2, double y2, double r2) {
        //first calculate the intersection points
        double iX1 = 0.0, iY1 = 0.0, iX2 = 0.0, iY2 = 0.0;

        if (!twoCirclesIntersectionPoints(x1, y1, r1, x2, y2, r2,
                new AtomicReference<Double>(iX1), new AtomicReference<Double>(iY1), new AtomicReference<Double>(iX2), new AtomicReference<Double>(iY2))) {
            return 0.0; //no overlap
        }

        //calculate the dist between the circle centers
        double d = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));

        //find the angles given that A and B are the two circle centers
        //and C and D are the intersection points
        double CBD = 2 * Math.acos((r2 * r2 + d * d - r1 * r1) / (r2 * d * 2));
        double CAD = 2 * Math.acos((r1 * r1 + d * d - r2 * r2) / (r1 * d * 2));

        //Then we find the segment of each of the circles cut off by the 
        //chord CD, by taking the area of the sector of the circle BCD and
        //subtracting the area of triangle BCD. Similarly we find the area
        //of the sector ACD and subtract the area of triangle ACD.
        double area = 0.5f * CBD * r2 * r2 - 0.5f * r2 * r2 * Math.sin(CBD)
                + 0.5f * CAD * r1 * r1 - 0.5f * r1 * r1 * Math.sin(CAD);
        return area;
    }

    /**
     *  given the radius, calculates the area of a circle.
     */
    public static double circleArea(double radius) {
        return Math.PI * radius * radius;
    }

    /**
     *  returns true if the point p is within the radius of the given circle.
     */
    public static boolean isPointInCircle(Vector pos, double radius, Vector p) {
        return p.subn(pos).lengthSq() < (radius * radius);
    }

    /**
     * returns true if the line segemnt AB intersects with a circle at position P with radius radius.
     */
    public static boolean isLineSegmentCircleIntersection(Vector A, Vector B, Vector P, double radius) {
        //first determine the dist from the center of the circle to the line segment (working in dist squared space)
        return getDistToLineSegmentSq(A, B, P) < radius * radius;  
    }

    /**
     *  given a line segment AB and a circle position and radius, this function
     *  determines if there is an intersection and stores the position of the 
     *  closest intersection in the reference IntersectionPoint
     *
     *  returns false if no intersection point is found
     */
    public static boolean getLineSegmentCircleClosestIntersectionPoint(
            Vector A,
            Vector B,
            Vector pos,
            double radius,
            Vector IntersectionPoint) 
    {
        Vector toBNorm = B.subn(A).normalize();
        //move the circle into the local space defined by the vector B-A with origin at A
        Vector LocalPos = Transformation.pointToLocalSpace(pos, toBNorm, A);
        boolean ipFound = false;

        //if the local position + the radius is negative then the circle lays behind
        //point A so there is no intersection possible. If the local x pos minus the 
        //radius is greater than length A-B then the circle cannot intersect the 
        //line segment
        if ((LocalPos.x + radius >= 0) && ((LocalPos.x - radius) * (LocalPos.x - radius) <= B.distSq(A))) {
            //if the dist from the x axis to the object's position is less
            //than its radius then there is a potential intersection.
            if (Math.abs(LocalPos.y) < radius) {
                //now to do a line/circle intersection test. The center of the 
                //circle is represented by A, B. The intersection points are 
                //given by the formulae x = A +/-sqrt(r^2-B^2), y=0. We only 
                //need to look at the smallest positive value of x.
                double a = LocalPos.x;
                double b = LocalPos.y;
                double ip = a - Math.sqrt(radius * radius - b * b);
                if (ip <= 0) {
                    ip = a + Math.sqrt(radius * radius - b * b);
                }
                ipFound = true;
                IntersectionPoint.set(toBNorm.mul(ip).add(A));
            }
        }
        return ipFound;
    }
    
}
