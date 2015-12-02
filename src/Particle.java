import java.awt.Color;

public class Particle {

	private String name;

	private double[] position;
	private double[] velocity;
	private double mass;
	private double radius;

	private Color color;

	public Particle(String name, double[] pos, double[] vel, double m, double r, Color c) {
		this.name = name;

		this.position = pos;
		this.velocity = vel;
		this.mass = m;
		this.radius = r;

		this.color = c;
	}

	public Particle(Particle p) {
		this.name = p.getName();

		this.position = p.getPosition();
		this.velocity = p.getVelocity();
		this.mass = p.getMass();
		this.radius = p.getRadius();

		this.color = p.getColor();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double[] getPosition() {
		return position;
	}

	public void setPosition(double[] position) {
		this.position = position;
	}

	public double[] getVelocity() {
		return velocity;
	}

	public void setVelocity(double[] velocity) {
		this.velocity = velocity;
	}

	public double getMass() {
		return mass;
	}

	public void setMass(double mass) {
		this.mass = mass;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
}
