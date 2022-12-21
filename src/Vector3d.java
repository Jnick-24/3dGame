
public class Vector3d implements Comparable<Vector3d>{
	// why
	
	double x = 0;
	double y = 0;
	double z = 0;
	int w = 0;

	public Vector3d(double xCoord, double yCoord, double zCoord) {
		x = xCoord;
		y = yCoord;
		z = zCoord;
	}
	
	public Vector3d(double xCoord, double yCoord, double zCoord, int wCoord) {
		x = xCoord;
		y = yCoord;
		z = zCoord;
		w = wCoord;
	}
	
	public Vector3d() {
	}
	
	public Vector3d normalize() {
		if (this.lengthSquared() == 1) return this;
		return this.clamp(1);
	}

	
	public Vector3d add(Vector3d toAdd) {
		return new Vector3d(x + toAdd.x, y + toAdd.y, z + toAdd.z, w + toAdd.w);
	}
	
	public Vector3d subtract(Vector3d toSub) {
		return new Vector3d(x - toSub.x, y - toSub.y, z - toSub.z, w - toSub.w);
	}
	
	public void divide(double toDivide) {
		this.x /= toDivide;
		this.y /= toDivide;
		this.z /= toDivide;
	}
	
	public Vector3d divideR(double toDivide) {
		return new Vector3d(x/toDivide, y/toDivide, z/toDivide);
	}
	
	public Vector3d multiply(double toMult) {
		return new Vector3d(x * toMult, y * toMult, z * toMult);
	}
	
	public Vector3d clamp(double length) {
		double l = this.length();
		if (l == 0) return this;
		double magnitude = length/l;
		
		return new Vector3d(x*magnitude, y*magnitude, z*magnitude, w);
	}
	
	public Vector3d limit(double length) {
		double l = this.length();
		if (l < length || length == 0) return this;
		
		
		double magnitude = length/l;
		
		return new Vector3d(x*magnitude, y*magnitude, z*magnitude);
	}
	
	public double length() {
		return Math.sqrt(this.lengthSquared());
	}
	
	public double lengthSquared() {
		return Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2);
	}
	
	public double distance(Vector3d point) {
		return Math.sqrt(this.distanceSq(point));
	}
	
	public double distanceSq(Vector3d point) {
		return Math.pow(x-point.x, 2) + Math.pow(y-point.y, 2) + Math.pow(z-point.z, 2);
	}
	
	public double dot(Vector3d vector) {
		return this.x*vector.x + this.y*vector.y + this.z*vector.z;
	}
	
	public Vector3d cross(Vector3d vector) {
		return new Vector3d(
				y*vector.z - z*vector.y,
				z*vector.x - x*vector.z,
				x*vector.y - y*vector.x
				);
	}
	
	public Vector3d rotate(Vector3d axis, double angle) {
		return this.multiply(Math.cos(angle))
					.add(axis.cross(this)
					.multiply(Math.sin(angle)))
					.add(axis.multiply((axis.dot(this))*(1-Math.cos(angle))));
	}
	
	public Vector3d qRotate(Quaternion q) {
		Quaternion p = new Quaternion(0, x, y, z);
		p = p.rotate(q, true);
		return new Vector3d(p.q1, p.q2, p.q3);
	}
	
	public double angleBetween(Vector3d vec) {
		return Math.acos(this.copy().dot(vec)/(this.copy().length()*vec.length()));
	}
	
	public Vector3d round(double place) {
		place = Math.pow(10, place);
		return new Vector3d((int)(x*place)/place, (int)(y*place)/place, (int)(z*place)/place, w);
	}
	
	public static Vector3d random(double magnitude) {
		return new Vector3d(Math.random()*magnitude, Math.random()*magnitude, Math.random()*magnitude, (int)Math.round(Math.random()*4));
	}
	
	public String toString() {
		return "(" + x + ", " + y + ", " + z + (w == 0 ? "" : ", " + w) + ")";
	}
	
	public boolean equals(Vector3d obj) {
		return x == obj.x && y == obj.y && z == obj.z && obj.w == w;
	}
	
	public Vector3d copy() {
		return new Vector3d(x, y, z, w);
	}
	
	public int compareTo(Vector3d v) {
		return (int)(this.lengthSquared() - v.lengthSquared());
	}
}
