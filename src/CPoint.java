import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class CPoint {
	public double x;
	public double y;
	public double x2;
	public double y2;
	
	public CPoint(double xnew, double ynew) {
		x = xnew;
		y = ynew;
		y2 = 0;
		x2 = 0;
	}
	
	public CPoint(double xnew, double ynew, double xnew2, double ynew2) {
		x = xnew;
		y = ynew;
		y2 = ynew2;
		x2 = xnew2;
	}
	
	public CPoint(CPoint a, double xnew2, double ynew2) {
		x = a.x;
		y = a.y;
		y2 = ynew2;
		x2 = xnew2;
	}
	
	public CPoint(Point inPoint) {
		x = inPoint.x;
		y = inPoint.y;
	}
	
	public CPoint() {
		x = 0;
		y = 0;
	}
	
	// Rotate (iX, iY) angle radians around (rX, rY)
	public static List<CPoint> rotatePoints(List<CPoint> allPoints, double angle, double rX, double rY) {
    	double sA = Math.sin(angle);
    	double cA = Math.cos(angle);
        
    	for (int i = 0; i < allPoints.size(); i++) {
    		
    		// translate point back to origin:
    		double iX = allPoints.get(i).x - rX;
    		double iY = allPoints.get(i).y - rY;

    		// rotate point
    		double xnew = iX * cA - iY * sA;
    		double ynew = iX * sA + iY * cA;

    		// translate point back, output
    		allPoints.set(i, new CPoint((double)(xnew + rX), (double)(ynew + rY)));
    	}
    	return allPoints;
    }
	
	public static List<double[]> rotatePoints(double[] xPoints, double[] yPoints, double angle, double rX, double rY) {
    	double sA = Math.sin(angle);
    	double cA = Math.cos(angle);
    	List<double[]> out = new ArrayList<double[]>();
        
    	for (int i = 0; i < xPoints.length; i++) {
    		
    		// translate point back to origin
    		double iX = xPoints[i] - rX;
    		double iY = yPoints[i] - rY;

    		// rotate point
    		double xnew = iX * cA - iY * sA;
    		double ynew = iX * sA + iY * cA;

    		// translate point back, output
    		xPoints[i] = (double)(xnew + rX);
    		yPoints[i] = (double)(ynew + rY);
    	}
    	out.add(xPoints);
    	out.add(yPoints);
    	return out;
    }
	
	public static List<int[]> rotatePointsInt(double[] xPoints, double[] yPoints, double angle, double rX, double rY) {
    	double sA = Math.sin(angle);
    	double cA = Math.cos(angle);
    	int[] xPointsI = new int[xPoints.length];
    	int[] yPointsI = new int[yPoints.length];
    	List<int[]> out = new ArrayList<int[]>();
        
    	for (int i = 0; i < xPoints.length; i++) {
    		
    		// translate point back to origin
    		double iX = xPoints[i] - rX;
    		double iY = yPoints[i] - rY;

    		// rotate point
    		double xnew = iX * cA - iY * sA;
    		double ynew = iX * sA + iY * cA;

    		// translate point back, output
    		xPointsI[i] = (int)(xnew + rX);
    		yPointsI[i] = (int)(ynew + rY);
    	}
    	out.add(xPointsI);
    	out.add(yPointsI);
    	return out;
    }
	
	public static CPoint rotatePoint(CPoint pos, double angle, CPoint origin) {
		double sA = Math.sin(angle);
    	double cA = Math.cos(angle);
        
    	// translate point back to origin
    	double iX = pos.x - origin.x;
    	double iY = pos.y - origin.y;

    	// rotate point
    	double xnew = iX * cA - iY * sA;
    	double ynew = iX * sA + iY * cA;

    	// translate point back, output
    	pos.x = (double)(xnew + origin.x);
    	pos.y = (double)(ynew + origin.y);
    	
    	return pos;
	}
	
	public static CPoint rotatePoint(CPoint pos, double angle, double x, double y) {
		return rotatePoint(pos, angle, new CPoint(x, y));
	}
	
	public double distance(CPoint distanceTo) {
		return Math.sqrt(distanceSquared(distanceTo));
	}
	
	public double distanceSquared(CPoint distanceTo) {
		double dX = (distanceTo.x - x);
		double dY = distanceTo.y - y;
		return Math.abs(dX*dX+dY*dY);
	}
	
	public Point2D toPoint2D() {
		return new Point2D.Double((double)x, (double)y);
	}
	
	public String toString() {
		return "(" + x + ", " + y + ")" + (x2 != 0 || y2 != 0 ? ":(" + x2 + ", " + y2 + ")" : "");
	}
	
	public String toIntSet() {
		String out = "";
		if (x < 10) {
			out += "0";
		}
		if (x < 100) {
			out += "0";
		}
		out += x + ",";
		
		if (y < 10) {
			out += "0";
		}
		if (y < 100) {
			out += "0";
		}
		out += y;
		
		return out;
	}
	
	public CPoint add(CPoint toAdd) {
		return new CPoint(x + toAdd.x, y + toAdd.y);
	}
	
	public CPoint subtract(CPoint toSubtract) {
		double tX = x - toSubtract.x;
		double tY = y - toSubtract.y;
		return new CPoint(tX, tY);
	}
	
	public CPoint multiply(double toMultiply) {
		return new CPoint(x*toMultiply, y*toMultiply);
	}
	
	public CPoint multiply(CPoint toMultiply) {
		return new CPoint(x*toMultiply.x, y*toMultiply.y);
	}
	
	public boolean equals(CPoint doesEqual) {
		return x == doesEqual.x && y == doesEqual.y;
	}
	
	public void clamp(double maxLength) {
		double distance = this.distance(new CPoint());
		if (distance > maxLength) {
			distance /= maxLength;
			x /= distance;
			y /= distance;
		}
	}
	
	public CPoint clamp(double minLength, double maxLength) {
		double distance = this.distance(new CPoint());
		double ratio = 1;
		
		if (distance > maxLength) ratio = distance/maxLength;
		else if (distance < minLength) ratio = distance/minLength;
		
		x /= ratio;
		y /= ratio;
		
		return this;
	}
	
	public static double GetAngleDegree(CPoint origin, CPoint target) {
	    //double n = 90 - (Math.atan2(origin.y - target.y, origin.x - target.x)) * 180 / Math.PI;
	    //return n % 360;
		
		// UP = 0deg
		return (270 + Math.toDegrees(Math.atan2(origin.y - target.y, origin.x - target.x))) % 360;
	}
	
	public CPoint toDegrees() {
		return new CPoint(Math.toDegrees(x), Math.toDegrees(y));
	}
	
	public CPoint round() {
		return new CPoint(Math.round(x), Math.round(y));
	}

	public CPoint add(double d) {
		return this.add(new CPoint(d, d));
	}
}