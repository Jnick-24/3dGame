
public class Orientation3d {
	Vector3d forward = new Vector3d(0, 0, 1);
	Vector3d right = new Vector3d(1, 0, 0);
	Vector3d up = new Vector3d(0, 1, 0);
	
	public Orientation3d(Vector3d forwardVec, Vector3d rightVec, Vector3d upVec) {
		forward = forwardVec.normalize();
		right = rightVec.normalize();
		up = upVec.normalize();
	}
	
	public Orientation3d () {}
	
	public Vector3d back() {
		return forward.multiply(-1);
	}
	
	public Vector3d left()	{
		return right.multiply(-1);
	}
	
	public Vector3d down() {
		return up.multiply(-1);
	}
	
	public String toString() {
		return "F" + forward + " R" + right + " U" + up;
	}
 }
