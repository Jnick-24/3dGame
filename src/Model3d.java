import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Model3d {
	Poly3d[] triangles;
	Poly3d[] statTriangles;
	Map<String, Vector3d> subObjects = new HashMap<String, Vector3d>();			
	Vector3d rotCenter = new Vector3d();
	Color color = Color.pink;
	
	public Model3d(String path, String extension) {
		long time = System.currentTimeMillis();
		try {
			switch (extension) {
				case "stl":
					readSTL(path + "." + extension);
					break;
				case "obj":
					readOBJ(path + "." + extension);
					break;
			}
			
			statTriangles = triangles.clone();
			
			System.out.println("Read model \"" + path + "\" of size " + this.triangles.length + ". Took " + (System.currentTimeMillis()-time) + "ms");
	    } catch (Exception e) {
	        System.out.println("Unable to read model " + path);
		    e.printStackTrace();
	    }
		
	}
	
	public Model3d(String path, String extension, Color colour) {
		color = colour;
		
		long time = System.currentTimeMillis();
		try {
			switch (extension) {
				case "stl":
					readSTL(path + "." + extension);
					break;
				case "obj":
					readOBJ(path + "." + extension);
					break;
			}
			
			statTriangles = triangles.clone();
			System.out.println("Read model \"" + path + "\" of size " + this.triangles.length + ". Took " + (System.currentTimeMillis()-time) + "ms");
	    } catch (Exception e) {
	        System.out.println("Unable to read model " + path);
		    e.printStackTrace();
	    }
	}
	
	private void readOBJ(String path) throws IOException { // today I will spaghetti code
		File model = new File(path);
	    byte[] bytes = new byte[(int) model.length()];
	    FileInputStream mStream = null;
	    mStream = new FileInputStream(model);
	    mStream.read(bytes);
	    mStream.close();
	    
	    String file = new String(bytes);
	    file = file.substring(file.indexOf("mt"));
    	File matLib = new File("model/" + file.substring(file.indexOf("mtllib ")+7, file.indexOf(".mtl")+4));

    	bytes = new byte[(int) matLib.length()];
	    mStream = new FileInputStream(matLib);
	    mStream.read(bytes);
	    mStream.close();
    	
    	String[] matFile = new String(bytes).split("newmtl");
    	Map<String, Color> mats = new HashMap<String, Color>();
    	
    	for (int i = 0; i < matFile.length-1; i++) {
    		String mat = matFile[i+1];
    		String matName = mat.substring(1, mat.indexOf("Ns "));
    		
    		mat = mat.substring(mat.indexOf("Kd ")+3);
    		mat = mat.substring(0, mat.indexOf("Ks "));
    		String[] values = mat.split(" ");
    		
    		int r = (int)(Double.parseDouble(values[0])*255);
    		int g = (int)(Double.parseDouble(values[1])*255);
    		int b = (int)(Double.parseDouble(values[2])*255);
    		
    		mats.put(matName, new Color(r, g, b));
    	}
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
	    Vector3d[] verts = new Vector3d[file.substring(file.indexOf('v')).split("v ").length];

    	String[] vertsString = file.substring(file.indexOf('v')).split("v ");
	    String vec = "";
	    for (int i = 0; i < vertsString.length; i++) {
	    	vec = vertsString[i];
	    	if (vec.contains("vn")) vec = vec.substring(0, vec.indexOf("vn"));
	    	if (vec == "") continue;
	    	
	    	String[] axes = vec.split(" ");	    	
	    	verts[i-1] = new Vector3d(Double.parseDouble(axes[0]), Double.parseDouble(axes[1]), Double.parseDouble(axes[2]));
	    }
    	
	    List<String> normalSet = new ArrayList<String>();
	    for (String str : file.substring(file.indexOf("vn ")+3).split("vn ")) {
	    	String[] a = str.split("vt ");
	    	if (a[0].length() < 25) normalSet.add(a[0]);
	    }
	    
	    Vector3d[] normals = new Vector3d[normalSet.size()];
	    for (int i = 0; i < normalSet.size(); i++) {
	    	String[] vecSet = normalSet.get(i).split(" ");
	    	normals[i] = new Vector3d(Double.parseDouble(vecSet[0]), Double.parseDouble(vecSet[1]), Double.parseDouble(vecSet[2]));
	    }
	    
	    System.out.println(normals.length);
	    
    	
    	String[] objects = file.split("o ");
    	
	    List<Poly3d> bufferPolys = new ArrayList<Poly3d>();

	    for (String object : objects) {
	    	int vPos = object.indexOf("v ");
	    	if (vPos == -1) {
	    		
	    		continue;
	    	}
	    	String objectName = object.substring(0, vPos).trim();
	    	Vector3d objectPos = new Vector3d();
	    	int objectVerts = 0;
		    object = object.substring(vPos);
		    
		    
		    
		    
		    
		    
		    
		    String[] matFaceGroups = object.split("usemtl ");
	
		    for (String str : matFaceGroups) {
		    	if (str.contains("v ")) continue;
		    	//System.out.println("NEW STRING " + str);
		    	String[] facesString = str.split("f "); // Pass all faces
		    	
		    	System.out.println("Material: " + facesString[0]);
		    	
			    triangles = new Poly3d[facesString.length-1];
			    for (int i = 1; i < facesString.length; i++) {
			    	String[] vertsWithModifiers = facesString[i].split(" ");
			    	Poly3d polygon = new Poly3d(new Vector3d[vertsWithModifiers.length], mats.containsKey(facesString[0]) ? mats.get(facesString[0]) : Color.pink); //TODO: Add color from MATLIB
			    	Vector3d normal = new Vector3d();
			    	
			    	for (int j = 0; j < vertsWithModifiers.length; j++) {
			    		String pos = vertsWithModifiers[j];
			    		int endIndex = pos.indexOf('/');
			    		normal = normal.add(normals[Integer.parseInt(pos.substring(pos.lastIndexOf('/')+1).trim())-1]);
			    		pos = pos.substring(0, endIndex);
			    		polygon.points[j] = verts[Integer.parseInt(pos)-1];
			    		objectVerts++;
			    		objectPos = objectPos.add(polygon.points[j]);
			    	}
			    	
			    	polygon.normal = normal.normalize();
			    	bufferPolys.add(polygon);
			    }
			    
			  objectPos = objectPos.divideR(objectVerts);
			  subObjects.put(objectName, objectPos);
			  System.out.println("Object " + objectName + " @ " + objectPos);
		    }
	    }
	    
	    triangles = bufferPolys.toArray(new Poly3d[] {});
	}
	
	
	public Vector3d getSubobject(String name, Vector3d offset, Vector3d position, Vector3d angle, Vector3d newAngle) {
		try {
			return subObjects.get(name).add(offset).qRotate(new Quaternion(angle)).add(position);
		}
		catch (Exception e) {
			System.out.println("Unable to find subobject " + name + "! in list" + subObjects.keySet());
			e.printStackTrace();
			return new Vector3d();
		}
	}
	
	public void rotate(Quaternion angle) {
		for (int i = 0; i < statTriangles.length; i++) {
			triangles[i] = statTriangles[i].add(rotCenter).rotate(angle).subtract(rotCenter);
		}
	}
	
	private void readSTL(String path) throws IOException {
		    File model = new File(path);
		    byte[] bytes = new byte[(int) model.length()];
		    FileInputStream mStream = null;
		    mStream = new FileInputStream(model);
		    mStream.read(bytes);
		    mStream.close();
		    triangles = new Poly3d[ByteBuffer.wrap(new byte[] {bytes[80], bytes[81], bytes[82], bytes[83], }).order(ByteOrder.LITTLE_ENDIAN).getInt()];
		    for (int i = 0; i < triangles.length; i++) {
		    	triangles[i] = new Poly3d(new Vector3d[] {
		    			byteToVec3d(trimBytes(bytes, i*50 + 96, 12)),
		    			byteToVec3d(trimBytes(bytes, i*50 + 108, 12)),
		    			byteToVec3d(trimBytes(bytes, i*50 + 120, 12))
		    			}, color);
		    	System.out.println("    Triangle read " + triangles[i]);
		    }
	}
	
	
	private float byteToInt(byte[] bytes) {
		return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
	}
	
	private Vector3d byteToVec3d(byte[] bytes) {
		byte[] xB = new byte[12];
		System.arraycopy(bytes, 0, xB, 0, 4);
		byte[] yB = new byte[12];
		System.arraycopy(bytes, 4, yB, 0, 4);
		byte[] zB = new byte[12];
		System.arraycopy(bytes, 8, zB, 0, 4);
		
		Vector3d outVec = new Vector3d(byteToInt(xB), byteToInt(yB), byteToInt(zB));
		return outVec.round(5);
	}
	
	private byte[] trimBytes(byte[] bytes, int startPos, int length) {
		byte[] newArray = new byte[length];
		System.arraycopy(bytes, startPos, newArray, 0, length);
		return newArray;
	}
}