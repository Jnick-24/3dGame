import java.awt.Color;

public class Poly3d {
	public Vector3d[] points;
	public Vector3d normal = new Vector3d();
	public Color color;
	
	public Poly3d(Vector3d[] vecs, Color clr) {
		points = vecs;
		color = clr;
	}
	
	public Poly3d(Vector3d[] vecs, Color clr, Vector3d norm) {
		points = vecs;
		color = clr;
		normal = norm;
	}
	
	public Poly3d() {
		points = new Vector3d[] {};
		//color = new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255));
		color = Color.pink;
	}
	
	
	public Poly3d rotate(Quaternion angle) {
		//Quaternion x = new Quaternion(orient.up, angle.x);
		//Quaternion y = new Quaternion(orient.right, angle.y);
		//Quaternion z = new Quaternion(orient.forward, angle.z);
		
		Poly3d outPoly = this.copy();
		for (int i = 0; i < points.length; i++) {
			//outPoly.points[i] = points[i].qRotate(y).qRotate(x).qRotate(z);
			outPoly.points[i] = points[i].qRotate(angle);
		}
		outPoly.normal = this.normal.qRotate(angle);
		return outPoly;
	}
	
	
	public Poly3d copy() {
		return new Poly3d(this.points.clone(), this.color, this.normal);
	}
	
	public String toString() {
		String o = "POLY [";
		boolean i = false;
		for (Vector3d point : points) {
			if (i) o += ", ";
			else i = true;
			o += point;
		}
		o += "]";
		return o;
	}
	
	public Vector3d center() {
		Vector3d avg = new Vector3d();
		
		for (Vector3d vec : points) {
			avg = avg.add(vec);
		}
		return avg.divideR(points.length);
	}
	
	public Poly3d add(Vector3d a) {
		Vector3d[] oPoints = new Vector3d[points.length];
		for (int i = 0; i < points.length; i++) {
			oPoints[i] = points[i].add(a);
		}
		
		return new Poly3d(oPoints, color, normal);
	}
	
	public Poly3d subtract(Vector3d a) {
		Vector3d[] oPoints = new Vector3d[points.length];
		for (int i = 0; i < points.length; i++) {
			oPoints[i] = points[i].subtract(a);
		}
		
		return new Poly3d(oPoints, color, normal);
	}
}
