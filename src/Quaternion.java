
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
}
