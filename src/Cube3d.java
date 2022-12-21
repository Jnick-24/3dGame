import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Cube3d {
	Model3d model;
	Image thumbnail;
	
	public Cube3d(Model3d m, String imagePath) {
		model = m;
		
		try {
			File i = new File(imagePath);
			thumbnail = ImageIO.read(i);
		}
		catch (IOException e1) {
			System.out.println("Failed to read image thumbnail " + imagePath);
		}
	}
	
	public static Cube3d testCube() {
		return new Cube3d(new Model3d("model/TestCube", "obj"), "img/cubes/missing.png");
	}
}
