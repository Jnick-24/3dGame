import java.util.HashMap;
import java.util.Map;

public class Object3d {
	Model3d model;
	Orientation3d orient = new Orientation3d();
	Quaternion rotation = new Quaternion(0, 0, 1, 0);
	Vector3d pos = new Vector3d();
	Vector3d oldAngle = new Vector3d();
	Vector3d angle = new Vector3d();
	Vector3d angleVel = new Vector3d();
	Vector3d posVel = new Vector3d();
	String name = "";
	Map<Vector3d, Cube3d> blocks = new HashMap<Vector3d, Cube3d>();
	double throttle = 0; // from 0 to 1
	double maxSpeed = 10 /60; // divide by 60 for m/s to m/tick
	
	private double halfPi = Math.PI/2;
	
	public Object3d(Model3d m, String id) {
		model = m;
		name = id;
		blocks.put(new Vector3d(), Cube3d.testCube());
	}
	
	public Object3d() {};
	
	
	
	
	
	
	public void update() {
		//rotation = rotation.multiply(new Quaternion(orient.up, 0.00));
		if (model != null) model.rotate(rotation);
		
		//pos = pos.add(posVel);
		angle = angle.add(angleVel);
		posVel.divide(1.1);
		angleVel.divide(1.1);
		//System.out.println("\n\n\n" + posVel + " : " + orient.forward.multiply(throttle).limit(maxSpeed));
		posVel = posVel.add(orient.forward.multiply(throttle).limit(maxSpeed));
		
		//Vector3d sAngle = angle.copy();
		//angle.x *= (angle.y+halfPi)/Math.PI;
		//angle.z *= angle.y/Math.PI;
		
		//orient.forward = new Vector3d(0, 0, -1).qRotate(new Quaternion(angle)).normalize();
		//orient.forward = orient.forward.qRotate(new Quaternion(angle.subtract(oldAngle)));
		//orient.forward = new Vector3d(0, 0, -1).rotate(new Vector3d(1, 0, 0), angle.y).rotate(new Vector3d(0, 1, 0), angle.x).normalize();
		
		//orient.right = orient.forward.cross(new Vector3d(0, angle.y >= halfPi && angle.y <= halfPi*3 ? -1 : 1, 0)).rotate(orient.forward, -angle.z).normalize();
		
		//orient.up = orient.forward.cross(angle.x > 90 && angle.x < 270 ? orient.right : orient.left()).normalize();
		
		//angle = sAngle;
		oldAngle = angle.copy();
		
		pos = pos.add(posVel);
	}
	

	public Vector3d move(Vector3d posVelDelta) {
		posVel = posVel.add(posVelDelta);
		return pos;
	}
	
	public void rotateX(double amt) {
		amt *= -1;
		rotation = rotation.multiply(new Quaternion(orient.up, amt));
		orient.forward = orient.forward.rotate(orient.up, amt);
		orient.right = orient.right.rotate(orient.up, amt);
	}
	public void rotateY(double amt) {
		amt *= -1;
		rotation = rotation.multiply(new Quaternion(orient.right, amt));
		orient.forward = orient.forward.rotate(orient.right, amt);
		orient.up = orient.up.rotate(orient.right, amt);
	}
	public void rotateZ(double amt) {
		amt *= -1;
		rotation = rotation.multiply(new Quaternion(orient.forward, amt));
		orient.up = orient.up.rotate(orient.forward, amt);
		orient.right = orient.right.rotate(orient.forward, amt);
	}
	
	public void rotate(Vector3d angleVelDelta) {
		angleVel = angleVel.add(angleVelDelta);
		
		angle.x %= 2*Math.PI;
		angle.y %= 2*Math.PI;
		angle.z %= 2*Math.PI;
	}
}
