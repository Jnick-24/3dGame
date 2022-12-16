import java.awt.Point;
import java.awt.Rectangle;


public class Button {
	String name;
	Rectangle rectangle;
	Runnable action;
	
	public Button(String toName, Rectangle shape) {
		name = toName;
		rectangle = shape;
	}
	
	public Button(String toName, Rectangle shape, Runnable onPress) {
		name = toName;
		rectangle = shape;
		action = onPress;
	}
	
	public boolean isClicked(Point position) {
		return rectangle.contains(position);
	}
}
