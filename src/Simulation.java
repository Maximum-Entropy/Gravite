import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class Simulation {

	// Simulation settings
	private static double FRAMELENGTH;
	private static double MAX_FRAMES;
	private static double NUM_PARTICLES;
	private static double G;
	private static String PRESET;

	// Particle property settings
	private static double MAX_INITIAL_SPREAD;
	private static double MAX_INITIAL_SPEED;
	private static double MASS;
	private static double RADIUS;

	// Static object declarations
	private static DisplayPanel display;
	private static List<Particle> particles;
	private static List<Particle> particleWorkset;

	private static double frame;

	// TEMP
	private static void adjustPosition(Particle particle) {
		for (int i = 0; i <= 2; i++) {
			particle.getPosition()[i] += (particle.getVelocity()[i] * FRAMELENGTH);
		}
	}

	// TEMP
	private static void adjustVelocity(Particle focusParticle, Particle periParticle) {
		double[] accelerationArray = getAccelerationArray(focusParticle, periParticle);

		for (int i = 0; i <= 2; i++) {
			focusParticle.getVelocity()[i] += (accelerationArray[i] * FRAMELENGTH);
		}

		if (getMagnitude(focusParticle.getVelocity()) > 2.998e8) {
			diluteVelocity(focusParticle);
		}
	}

	// Adjust velocity matrix to consider approximate effects of Lorentz Force
	private static void diluteVelocity(Particle focusParticle) {

		// focusParticle.getVelocity()
		double proportionalityConstant = 2.998e8D / getMagnitude(focusParticle.getVelocity());

		for (int i = 0; i <= 2; i++) {
			focusParticle.getVelocity()[i] *= proportionalityConstant;
		}
		// ^WARNING: Possible source of gradual inaccuracy
	}

	// Returns set of particles generated as specified by settings
	private static void generateInitialParticles(String preset) {
		if (preset.equalsIgnoreCase("random"))
			for (int p = 0; p < NUM_PARTICLES; p++) {
				double[] pos = new double[3];
				// NOTE: Potential point of optimization
				while (true) {
					for (int i = 0; i <= 2; i++)
						pos[i] = MAX_INITIAL_SPREAD * (Math.random() - .5);
					if (getMagnitude(pos) <= MAX_INITIAL_SPREAD / 2)
						break;

				}
				double[] vel = new double[3];
				// NOTE: Potential point of optimization
				while (true) {
					for (int i = 0; i <= 2; i++)
						vel[i] = MAX_INITIAL_SPEED * (Math.random() - .5);
					if (getMagnitude(vel) <= MAX_INITIAL_SPEED / 2)
						break;
				}
				double m = MASS;
				double r = RADIUS;
				Color c = Color.getHSBColor((float) Math.random(), (float) .9, (float) .8);

				particles.add(new Particle("", pos, vel, m, r, c));
			}
		else if (preset.equalsIgnoreCase("lunar_orbit")) {
			Particle earth = new Particle("Earth", new double[] { 0, 0, 0 }, new double[] { 0, -12.5492098406, 0 },
					5.97219e24, 6.37e6 * RADIUS, Color.GREEN);
			particles.add(earth);
			Particle luna = new Particle("Luna", new double[] { 3.844e8, 0, 0 }, new double[] { 0, 1.02e3, 0 },
					7.34767309e22, 1.74e6 * RADIUS, Color.GRAY);
			particles.add(luna);
			MAX_INITIAL_SPREAD = 3.844e8 * 2;
		} else if (preset.equalsIgnoreCase("solar_system")) {
			Particle sun = new Particle("Sun",
					new double[] { 5.533370574162924E8, 1.719764218706217E8, -2.394355279700192E7 },
					new double[] { 2.154235968365963E0, 1.170733862732091E1, -6.552565366745988E-2 }, 1.9891e30,
					696.30e6 * RADIUS, Color.YELLOW);
			particles.add(sun);
			Particle mercury = new Particle("Mercury",
					new double[] { -1.719783953330445E10, -6.722700821933581E10, -3.902556788378648E9 },
					new double[] { 3.733062781933711E4, -9.950365963011306E3, -4.238750640966847E3 }, 3.285e23,
					2.44e6 * RADIUS, Color.LIGHT_GRAY);
			particles.add(mercury);
			Particle venus = new Particle("Venus",
					new double[] { -6.042817183699661E10, 8.855499922465855E10, 4.706837645957295E9 },
					new double[] { -2.894258159881888E4, -2.006944453002275E4, 1.394912448399850E3 }, 4.867e24,
					6.05e6 * RADIUS, Color.WHITE);
			particles.add(venus);
			Particle earth = new Particle("Earth",
					new double[] { 6.951046043033120E10, 1.307689288062220E11, -2.866788262154907E7 },
					new double[] { -2.681509068917485E4, 1.380071513673651E4, 3.174402804440035E-1 }, 5.97219e24,
					6.37e6 * RADIUS, Color.GREEN);
			particles.add(earth);
			Particle mars = new Particle("Mars",
					new double[] { -2.302530730012161E11, 9.389574404446831E10, 7.604608230551708E9 },
					new double[] { -8.205980950829959E3, -2.036927430100603E4, -2.256904606733148E2 }, 6.4169e23,
					3.39e6 * RADIUS, Color.RED);
			particles.add(mars);
			Particle jupiter = new Particle("Jupiter",
					new double[] { -7.600079611096961E11, 2.757278510217374E11, 1.585028176078553E10 },
					new double[] { -4.607072040775546E3, -1.166779708055769E4, 1.515938383766935E2 }, 1.8983e27,
					69.91e6 * RADIUS, Color.ORANGE);
			particles.add(jupiter);
			Particle saturn = new Particle("Saturn",
					new double[] { -5.819747915860022E11, -1.377872311935240E12, 4.711936607246059E10 },
					new double[] { 8.369285756002805E3, -3.786152152309342E3, -2.678015865784866E2 }, 5.683e26,
					58.23e6 * RADIUS, Color.YELLOW);
			particles.add(saturn);
			Particle uranus = new Particle("Uranus",
					new double[] { 2.830450775622581E12, 9.610136080758057E11, -3.309996752955711E10 },
					new double[] { -2.239139207037803E3, 6.131010178799026E3, 5.182920898016752E1 }, 8.681e25,
					25.36e6 * RADIUS, Color.CYAN);
			particles.add(uranus);
			Particle neptune = new Particle("Neptune",
					new double[] { 4.175781066442594E12, -1.628373599715604E12, -6.270190532392907E10 },
					new double[] { 1.937841224539395E3, 5.095544765204155E3, -1.496424990733420E1 }, 1.024e26,
					24.62e6 * RADIUS, Color.BLUE);
			particles.add(neptune);
			Particle pluto = new Particle("Pluto",
					new double[] { 1.259702024375546E12, -4.769604667555388E12, 1.459976915160525E11 },
					new double[] { 5.374815047076445E3, 2.673978699416193E2, -1.602957219578406E3 }, 1.30900e22,
					1.19e6 * RADIUS, Color.GRAY);
			particles.add(pluto);

			// Change screen fitting target
			Particle POV_PLANET = uranus;
			MAX_INITIAL_SPREAD = 2
					* Math.sqrt(Math.pow(POV_PLANET.getPosition()[0], 2) + Math.pow(POV_PLANET.getPosition()[1], 2))
					- 2e12;
		}
	}

	// TEMP
	private static double[] getAccelerationArray(Particle focusParticle, Particle periParticle) {
		double m = focusParticle.getMass();
		double[] FGravityArray = getFGravityArray(focusParticle, periParticle);

		return new double[] { FGravityArray[0] / m, FGravityArray[1] / m, FGravityArray[2] / m };
	}

	// TEMP
	private static double[] getDisplacementMatrix(Particle focusParticle, Particle periParticle) {
		return new double[] { periParticle.getPosition()[0] - focusParticle.getPosition()[0],
				periParticle.getPosition()[1] - focusParticle.getPosition()[1],
				periParticle.getPosition()[2] - focusParticle.getPosition()[2] };
	}

	// TEMP
	private static double[] getFGravityArray(Particle focusParticle, Particle periParticle) {
		double m1 = focusParticle.getMass();
		double m2 = periParticle.getMass();

		double[] displacementArray = getDisplacementMatrix(focusParticle, periParticle);
		double r = getMagnitude(displacementArray);

		double fGravityMagnitude = G * (m1 * m2) / (Math.pow(r, 2));
		double proportionalityConstant = fGravityMagnitude / r;

		return new double[] { proportionalityConstant * displacementArray[0],
				proportionalityConstant * displacementArray[1], proportionalityConstant * displacementArray[2] };
	}

	public static double getFrame() {
		return frame;
	}

	public static double getFramelength() {
		return FRAMELENGTH;
	}

	// Returns length of parameter vector
	private static double getMagnitude(double[] vec) {
		return Math.sqrt(Math.pow(vec[0], 2) + Math.pow(vec[1], 2) + Math.pow(vec[2], 2));
	}

	public static double getMaxFrames() {
		return MAX_FRAMES;
	}

	public static double getMaxInitialSpread() {
		return MAX_INITIAL_SPREAD;
	}

	public static String getPreset() {
		return PRESET;
	}

	// Main simulation flow
	public static void main(String[] args) {
		long startTime = System.nanoTime();

		verifySettings();

		// General initialization
		particles = new ArrayList<Particle>();
		generateInitialParticles(PRESET);
		display = new DisplayPanel(particles);
		display.show();

		// TODO
		int barProgress = 0;
		System.out.println("|--------------------PROGRESS--------------------|");

		for (frame = 1; frame <= MAX_FRAMES; frame++) {

			// try {
			// Thread.sleep(1);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }

			particleWorkset = new ArrayList<Particle>(particles);

			for (int i = 0; i <= particleWorkset.size() - 2; i++)
				for (int j = i + 1; j <= particleWorkset.size() - 1; j++) {
					adjustVelocity(particleWorkset.get(i), particleWorkset.get(j));
					adjustVelocity(particleWorkset.get(j), particleWorkset.get(i));
				}

			for (Particle p : particleWorkset)
				adjustPosition(p);

			display.repaint();

			double progress = 100 * (frame / MAX_FRAMES);
			while (barProgress < progress) {
				System.out.print(">");
				barProgress += 2;
			}
		}

		System.out.println("\nElapsed Time (ns): " + (System.nanoTime() - startTime));
	}

	private static void verifySettings() {
		try {
			Scanner fileIn = new Scanner(new File("SimulationSettings.txt"));
			fileIn.nextLine();
			fileIn.nextLine();
			FRAMELENGTH = Double.parseDouble(fileIn.nextLine());
			fileIn.nextLine();
			fileIn.nextLine();
			fileIn.nextLine();
			MAX_FRAMES = Double.parseDouble(fileIn.nextLine());
			fileIn.nextLine();
			fileIn.nextLine();
			fileIn.nextLine();
			NUM_PARTICLES = Double.parseDouble(fileIn.nextLine());
			fileIn.nextLine();
			fileIn.nextLine();
			fileIn.nextLine();
			G = Double.parseDouble(fileIn.nextLine());
			fileIn.nextLine();
			fileIn.nextLine();
			fileIn.nextLine();
			PRESET = fileIn.nextLine();
			fileIn.nextLine();
			fileIn.nextLine();
			fileIn.nextLine();
			MAX_INITIAL_SPREAD = Double.parseDouble(fileIn.nextLine());
			fileIn.nextLine();
			fileIn.nextLine();
			fileIn.nextLine();
			MAX_INITIAL_SPEED = Double.parseDouble(fileIn.nextLine());
			fileIn.nextLine();
			fileIn.nextLine();
			fileIn.nextLine();
			MASS = Double.parseDouble(fileIn.nextLine());
			fileIn.nextLine();
			fileIn.nextLine();
			fileIn.nextLine();
			RADIUS = Double.parseDouble(fileIn.nextLine());
			fileIn.close();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "SimulationSettings.txt file not found.");
			e.printStackTrace();
		}
	}

}
