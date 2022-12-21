
public class Quaternion {
	double q0 = 0;
	double q1 = 0;
	double q2 = 0;
	double q3 = 0;
	
	public Quaternion(Vector3d euler) { // x = pitch, y = roll, z = yaw
		q0 = Math.cos(euler.y/2)*Math.cos(euler.x/2)*Math.cos(euler.z/2) + Math.sin(euler.y/2)*Math.sin(euler.x/2)*Math.sin(euler.z/2);
		q1 = Math.sin(euler.y/2)*Math.cos(euler.x/2)*Math.cos(euler.z/2) - Math.cos(euler.y/2)*Math.sin(euler.x/2)*Math.sin(euler.z/2);
		q2 = Math.cos(euler.y/2)*Math.sin(euler.x/2)*Math.cos(euler.z/2) + Math.sin(euler.y/2)*Math.cos(euler.x/2)*Math.sin(euler.z/2);
		q3 = Math.cos(euler.y/2)*Math.cos(euler.x/2)*Math.sin(euler.z/2) - Math.sin(euler.y/2)*Math.sin(euler.x/2)*Math.cos(euler.z/2);
	}
	
	public Quaternion() {}
	
	public Quaternion(double qu0, double qu1, double qu2, double qu3) {
		q0 = qu0;
		q1 = qu1;
		q2 = qu2;
		q3 = qu3;
	}
	
	public Quaternion(Vector3d axis, double angle) {
		q0 = Math.cos(angle/2);
		q1 = axis.x*Math.sin(angle/2);
		q2 = axis.y*Math.sin(angle/2);
		q3 = axis.z*Math.sin(angle/2);
	}
	
	public Quaternion multiply(Quaternion m) {
		return new Quaternion(
				q0*m.q0 - q1*m.q1 - q2*m.q2 - q3*m.q3,
				q0*m.q1 + q1*m.q0 - q2*m.q3 + q3*m.q2,
				q0*m.q2 + q1*m.q3 + q2*m.q0 - q3*m.q1,
				q0*m.q3 - q1*m.q2 + q2*m.q1 + q3*m.q0
				);
	}
	
	public Quaternion invert() {
		return new Quaternion(q0, q1*-1, q2*-1, q3*-1);
	}
	
	public Quaternion rotate(Quaternion q, boolean active) {
		if (active) return q.invert().multiply(this.multiply(q));
		return q.multiply(this.multiply(q.invert()));
	}
	
	public Quaternion normalize() {
		double m = 1/Math.sqrt(q0*q0 + q1*q1 + q2*q2 + q3*q3);
		
		return new Quaternion(q0*m, q1*m, q2*m, q3*m);
	}
	
	public Vector3d toEulerAngle() {
		Vector3d o = new Vector3d();
		Quaternion q = this.normalize();
		
		double sinr_cosp = 2 * (q.q3 * q.q0 + q.q1 * q.q2);
	    double cosr_cosp = 1 - 2 * (q.q0 * q.q0 + q.q1 * q.q1);
	    o.x = Math.atan2(sinr_cosp, cosr_cosp);

	    // pitch (y-axis rotation)
	    double sinp = Math.sqrt(1 + 2 * (q.q3 * q.q0 - q.q1 * q.q2));
	    double cosp = Math.sqrt(1 - 2 * (q.q3 * q.q0 - q.q1 * q.q2));
	    o.y = 2 * Math.atan2(sinp, cosp) - Math.PI / 2;

	    // yaw (z-axis rotation)
	    double siny_cosp = 2 * (q.q3 * q.q2 + q.q0 * q.q1);
	    double cosy_cosp = 1 - 2 * (q.q1 * q.q1 + q.q2 * q.q2);
	    o.z = Math.atan2(siny_cosp, cosy_cosp);
		
		return o;
	}
	
	static Quaternion fromEuler(double x, double y, double z) {
		double c1 = Math.cos(x/2);
	    double s1 = Math.sin(x/2);
	    double c2 = Math.cos(y/2);
	    double s2 = Math.sin(y/2);
	    double c3 = Math.cos(z/2);
	    double s3 = Math.sin(z/2);
	    double c1c2 = c1*c2;
	    double s1s2 = s1*s2;
	    return new Quaternion(
	    	  	c1c2*s3 + s1s2*c3,
	    		s1*c2*c3 + c1*s2*s3,
	    		c1*s2*c3 - s1*c2*s3,
	    		c1c2*c3 - s1s2*s3
	    		);
	}
	
	static Quaternion fromEuler(Vector3d euler) {
		return fromEuler(euler.x, euler.y, euler.z);
	}
	
	public Quaternion copy() {
		return new Quaternion(q0, q1, q2, q3);
	}
	
	public String toString() {
		return "(" + q0 + ", " + q1 + ", " + q2 + ", " + q3 + ")";
	}
}
