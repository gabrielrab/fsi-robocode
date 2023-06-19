package fsi;
import java.awt.*;
import robocode.*;
import static robocode.util.Utils.normalRelativeAngleDegrees;
//import java.awt.Color;

// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * Fsi - a robot by (Gabriel Rabelo, Arthur Henrique, João Vitor Brandão, Gabriel Cavalcante)
 */
public class Fsi extends TeamRobot
{
	int count = 0;
	
	double gunTurnAmt;
	String rival;

	public void setSkin() {
		setColors(Color.red,Color.blue,Color.white);
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
		rival = null; // Começa iniciando o rival como nulo
		setAdjustGunForRobotTurn(true);
		gunTurnAmt = 10;
		
		moveToSafeArea();
		scan();

		while(true) {
			// Inicia a movimentação do canhão
			turnGunRight(gunTurnAmt);
			count++;
			// Faz uma oscilação da arma
			if (count > 2) {
				gunTurnAmt = -10;
			}
			if (count > 5) {
				gunTurnAmt = 10;
			}
			// se não encontrou ninguém, seta o rival como nulo para uma nova busca
			if (count > 11) {
				rival = null;
				scan();
				continue;
			}
			
		}

	}


	public void onScannedRobot(ScannedRobotEvent e) {
		if (rival != null && !e.getName().equals(rival)) {
			return;
		}

		// Verifica se é um oponente válido
		if (isBorderGuard(e.getName()) || isTeam(e.getName())) {
			rival = null;
			out.println("[No scan] Estou gastando energia atoa...");
			return;
		}

		// Marca um oponente
		if (rival == null) {
			rival = e.getName();
			out.println("Seguindo o: " + rival);
		}
		
		count = 0;

		// movimentação quando o oponente está um pouco longe
		if (e.getDistance() > 150) {
			gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));
			turnGunRight(gunTurnAmt);
			turnRight(e.getBearing());
			ahead(e.getDistance() - 150);
			return;
		}

		// movimentação quando o oponente está perto
		gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));
		turnGunRight(gunTurnAmt);
		fire(2);

		if (e.getDistance() < 100) {
			if (e.getBearing() > -90 && e.getBearing() <= 90) {
				back(50);
			} else {
				ahead(50);
			}
		}
		scan();
	}

	// Quando fomos atingidos
	public void onHitByBullet(HitByBulletEvent e) {
		
		if (rival != null) {
			scan();
			return;
		} 

		// Verifica se estamos na area de segurança
		if (moveToSafeArea()) {
			turnRight(90);
			back(50);
			turnLeft(180);
			ahead(50);
			return;
		}
		

	}
	
	// Função que identifica colisão e inibe que fiquemos presos ao muro
	public void onHitWall(HitWallEvent e) {
		turnRight(90);
        ahead(100);
	}
	
	public void onHitRobot(HitRobotEvent e) {
		if (rival != null && !rival.equals(e.getName())) {
			fire(1);
			back(60);
		}

		// Não desperdiça energia 
		if (isBorderGuard(e.getName()) || isTeam(e.getName())) {
			rival = null;
			out.println("[No hit] Estou gastando energia atoa...");
			return;
		}

		// Marca como rival e realiza o ataque
		rival = e.getName();
		gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));
		turnGunRight(gunTurnAmt);
		fire(3);
		back(70);
	}
	
	// Dança da vitória
	public void onWin(WinEvent e) {
		for (int i = 0; i < 50; i++) {
			turnRight(30);
			turnLeft(30);
		}
	}
			
}
