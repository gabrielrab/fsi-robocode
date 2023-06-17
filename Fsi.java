package fsi;
import java.awt.*;
import robocode.*;
import static robocode.util.Utils.normalRelativeAngleDegrees;
//import java.awt.Color;

// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * Fsi - a robot by (your name here)
 */
public class Fsi extends TeamRobot
{
	int count = 0;
	
	double gunTurnAmt;
	String rival;

	public void setSkin() {
		setColors(Color.red,Color.blue,Color.white); // body,gun,radar
	}
	
	public boolean isBorderGuard(String rival) {
		return rival.contains("BorderGuard");
	}
	
	public boolean isTeam(String rival) {
		return rival.contains("Fsi");
	}

	public boolean moveToSafeArea() {
		double x = getX();
		double y = getY();

		boolean isSafe = false;

		if (x <= 200) {
			isSafe = false;
			out.println("[X - 200] Dentro da zona de perigo");
			rival = null;
			back(200);
			return isSafe;

		} else if (x >= 800) {
			isSafe = false;
			out.println("[X - 800] Dentro da zona de perigo");
			rival = null;
			back(200);
			return isSafe;
		}

		if (y <= 200) {
			isSafe = false;
			out.println("[y - 200] Dentro da zona de perigo");
			rival = null;
			back(200);
			return isSafe;
		} else if (y >= 800) {
			isSafe = false;
			out.println("[y - 800] Dentro da zona de perigo");
			rival = null;
			back(200);
			return isSafe;
		}
		return true;
	}
	
	public void run() {
		setSkin();
		
		// Prepara a arma
		rival = null; // Initialize to not tracking anyone
		setAdjustGunForRobotTurn(true); // Keep the gun still when we turn
		gunTurnAmt = 10; // Initialize gunTurn to 10
		
		moveToSafeArea();
		scan();

		// Robot main loop
		while(true) {
			// turn the Gun (looks for enemy)
			turnGunRight(gunTurnAmt);
			// Keep track of how long we've been looking
			count++;
			// If we've haven't seen our target for 2 turns, look left
			if (count > 2) {
				gunTurnAmt = -10;
			}
			// If we still haven't seen our target for 5 turns, look right
			if (count > 5) {
				gunTurnAmt = 10;
			}
			// If we *still* haven't seen our target after 10 turns, find another target
			if (count > 11) {
				rival = null;
				scan();
				continue;
			}
			moveToSafeArea();
			scan();
		}

	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		// If we have a target, and this isn't it, return immediately
		// so we can get more ScannedRobotEvents.
		if (rival != null && !e.getName().equals(rival)) {
			return;
		}

		if (isBorderGuard(e.getName()) || isTeam(e.getName())) {
			rival = null;
			out.println("[No scan] Estou gastando energia atoa...");
			return;
		}

		// If we don't have a target, well, now we do!
		if (rival == null) {
			rival = e.getName();
			out.println("Seguindo o: " + rival);
		}
		

		// This is our target.  Reset count (see the run method)
		count = 0;
		// If our target is too far away, turn and move toward it.
		if (e.getDistance() > 150) {
			gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));

			turnGunRight(gunTurnAmt); // Try changing these to setTurnGunRight,
			turnRight(e.getBearing()); // and see how much Tracker improves...
			// (you'll have to make Tracker an AdvancedRobot)
			ahead(e.getDistance() - 100);
			return;
		}

		// Our target is close.
		gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));
		turnGunRight(gunTurnAmt);
		fire(2);

		// Our target is too close!  Back up.
		if (e.getDistance() < 100) {
			if (e.getBearing() > -90 && e.getBearing() <= 90) {
				back(40);
			} else {
				ahead(40);
			}
		}
		scan();
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
		out.println("aiii aiiii");
		if (moveToSafeArea()) {
			turnRight(90);
			turnLeft(180);
			ahead(100);
		}
		
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		turnRight(90);
        ahead(100);
	}
	
	public void onHitRobot(HitRobotEvent e) {
		
		// Only print if he's not already our target.
		if (rival != null && !rival.equals(e.getName())) {
			fire(1);
			back(60);
		}

		if (isBorderGuard(e.getName()) || isTeam(e.getName())) {
			rival = null;
			out.println("[No hit] Estou gastando energia atoa...");
			return;
		}


		// Set the target
		rival = e.getName();
		// Back up a bit.
		// Note:  We won't get scan events while we're doing this!
		// An AdvancedRobot might use setBack(); execute();
		gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));
		turnGunRight(gunTurnAmt);
		fire(3);
		back(70);
	}
			
}
