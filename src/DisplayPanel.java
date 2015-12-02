import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class DisplayPanel extends JPanel implements Comparable<Particle> {
	private static final long serialVersionUID = -7487542960033773348L;

	// Preferred display size settings
	private int WIDTH = 1900;
	private int HEIGHT = 1050;
	private double scale;

	// Visual effect settings
	private boolean showTrail = false;
	private boolean showLabel = true;
	private boolean foregroundValidation = true;
	/* Not yet implemented */ // private int[] cameraPos = {300, 300, 300};
	/* Not yet implemented */ // private int[] cameraTarget = { 0, 0, 0 };

	private JFrame frame;
	private List<Particle> visibleParticles;

	// [Constructor] Automatically runs on initialization of a DisplayPanel;
	public DisplayPanel(List<Particle> particles) {
		verifySettings();
		
		// General JFrame initialization
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(this);
		frame.pack();
		centerFrame();

		visibleParticles = particles;
		calculateScale();
		repaint();
	}

	// Translates frame to center of effective screen
	private void centerFrame() {
		GraphicsEnvironment screen = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Dimension effectiveScreenSize = screen.getMaximumWindowBounds().getSize();
		Dimension frameSize = frame.getSize();
		Point preferredNW = new Point((effectiveScreenSize.width - frameSize.width) / 2,
				(effectiveScreenSize.height - frameSize.height) / 2);
		frame.setLocation(preferredNW);
	}

	// Calculates scale such that MAX_SPREAD fits in screen
	private void calculateScale() {
		int restrictingLength = HEIGHT < WIDTH ? HEIGHT : WIDTH;
		scale = restrictingLength / Simulation.getMaxInitialSpread() / 1.5D;
	}

	// Graphics drawing method
	@Override
	public void paintComponent(Graphics g) {
		// Clears background if showTrail is set to false
		if (!showTrail)
			super.paintComponent(g);

		// Sorts particle array by ascending p_z
		if (foregroundValidation) {
			Collections.sort(visibleParticles, new Comparator<Particle>() {
				@Override
				public int compare(Particle p1, Particle p2) {
					return Double.compare(p1.getPosition()[2], p2.getPosition()[2]);
				}
			});
		}

		// Calculates and draws particle geometries
		for (Particle p : visibleParticles) {
			g.setColor(p.getColor());
			double[] pos = p.getPosition();
			double visualRadius = p.getRadius() * scale * (1.2 * pos[2] / Simulation.getMaxInitialSpread() + 1);
			if (!Simulation.getPreset().equals("random") && visualRadius < 1)
				visualRadius = 1;
			int screenX = (int) (pos[0] * scale - visualRadius + WIDTH / 2);
			int screenY = (int) (pos[1] * scale - visualRadius + HEIGHT / 2);
			g.fillOval(screenX, screenY, (int) (2 * visualRadius), (int) (2 * visualRadius));

			if (!showTrail && showLabel) {
				g.setColor(Color.WHITE);
				g.drawString(p.getName(), screenX + 2 * (int) visualRadius, screenY);
			}
		}

		g.setColor(Color.WHITE);
		if (showTrail) {
			g.fillRect(0, HEIGHT - 30, 350, 30);
			g.setColor(Color.BLACK);
		}
		g.drawString(Long.toString((long) (Simulation.getFrame() * Simulation.getFramelength())) + " secs ( Frame "
				+ Integer.toString((int) Simulation.getFrame()) + " / " + (int) Simulation.getMaxFrames() + " )", 10,
				HEIGHT - 10);
		setBackground(Color.BLACK);
	}

	// Sets default DisplayPanel size
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(WIDTH, HEIGHT);
	}

	// Shows DisplayPanel
	@Override
	public void show() {
		frame.setVisible(true);
	}

	// IGNORE THIS METHOD
	@Override
	public int compareTo(Particle o) {
		return 0;
	}

	public void verifySettings() {
		try {
			Scanner fileIn = new Scanner(new File("VisualSettings.txt"));
			fileIn.nextLine();
			fileIn.nextLine();
			WIDTH = Integer.parseInt(fileIn.nextLine());
			fileIn.nextLine();
			fileIn.nextLine();
			fileIn.nextLine();
			HEIGHT = Integer.parseInt(fileIn.nextLine());
			fileIn.nextLine();
			fileIn.nextLine();
			fileIn.nextLine();
			showTrail = Boolean.parseBoolean(fileIn.nextLine());
			fileIn.nextLine();
			fileIn.nextLine();
			fileIn.nextLine();
			showLabel = Boolean.parseBoolean(fileIn.nextLine());
			fileIn.nextLine();
			fileIn.nextLine();
			fileIn.nextLine();
			foregroundValidation = Boolean.parseBoolean(fileIn.nextLine());
			fileIn.close();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "SimulationSettings.txt file not found.");
			e.printStackTrace();
		}
	}
}
