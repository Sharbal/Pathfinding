package backup_2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CaptureTheFlag extends Game{
	
	private boolean redWin, blueWin;
	
	private Color bgColor, lightRed, lightBlue;
	private final static Color RED_TRANSP = new Color(1f, 0f, 0f, 0.5f);
	private final static Color BLUE_TRANSP = new Color(0f, 0f, 1f, 0.5f);
	private BasicStroke stroke;
	private Font font;
	
	private Player player;
	private boolean moveLeft, moveRight, moveUp, moveDown;
	
	private ArrayList<AI> redTeam;
	private ArrayList<AI> blueTeam;
	
	private Entity redFlag, blueFlag;
	private ArrayList<Hazard> blueSafe;
	private ArrayList<Hazard> redSafe;
	private int redFlagTime, blueFlagTime;
	private static final int MAX_FLAG_TIME = 10000;
	
	private Entity redJail, blueJail;
	private float jailSize;
	
	public static void main(String[] args){
		new CaptureTheFlag().start();
	}
	
	@Override
	public void init(){
		super.init();
		setTitle("Pathfinding in Two Dimensions");
		
		redWin = blueWin = false;
		
		bgColor = new Color(48, 216, 98); //green
		lightRed = new Color(255, 158, 156);
		lightBlue = new Color(156, 159, 255);
		stroke = new BasicStroke(3f);
		font = new Font("res/consola.ttf", Font.PLAIN, 20);
		
		blueTeam = new ArrayList<AI>();
		redTeam = new ArrayList<AI>();
		
		blueSafe = new ArrayList<Hazard>();
		redSafe = new ArrayList<Hazard>();
		blueSafe.add(new Hazard(getWidth()/4, getHeight()/3, 50, 75));
		blueSafe.add(new Hazard(getWidth()/4, getHeight()*2/3, 50, 75));
		//blueSafe.add();
		redSafe.add(new Hazard(getWidth()*3/4, getHeight()/3, 50, 75));
		redSafe.add(new Hazard(getWidth()*3/4, getHeight()*2/3, 50, 75));
		//redSafe.add();
		
		
		player = new Player(getWidth()/4, getHeight()/8, 3, 10, 15);
		
		redTeam.add(new AI((float)(getWidth()*Math.random()*0.4), (float)(getHeight()*Math.random()), 1, 10, 25));
		redTeam.add(new AI((float)(getWidth()*Math.random()*0.4), (float)(getHeight()*Math.random()), 2, 10, 15));
		redTeam.add(new AI((float)(getWidth()*Math.random()*0.4), (float)(getHeight()*Math.random()), 2, 10, 15));		
		
		blueTeam.add(new AI((float)(getWidth()*(0.6+Math.random()*0.4)), (float)(getHeight()*Math.random()), 1, 10, 25));
		blueTeam.add(new AI((float)(getWidth()*(0.6+Math.random()*0.4)), (float)(getHeight()*Math.random()), 2, 10, 15));
		blueTeam.add(new AI((float)(getWidth()*(0.6+Math.random()*0.4)), (float)(getHeight()*Math.random()), 2, 10, 15));
		
		redFlag = new Entity(getWidth()/10, getHeight()/4);
		blueFlag = new Entity(getWidth()*9/10, getHeight()*3/4);
		for (AI red : redTeam) red.setGoal(blueFlag);
		for (AI blue : blueTeam) blue.setGoal(redFlag);
		redFlagTime = blueFlagTime =  0;
		
		for (AI red : redTeam) red.setStaticHazards(blueSafe);
		for (AI blue : blueTeam) blue.setStaticHazards(redSafe);
		
		jailSize = 100;
		redJail = new Entity(jailSize/2, getHeight() - jailSize/2);
		blueJail = new Entity(getWidth() - jailSize/2, jailSize/2);
		
		//team2.add(player);
		//for (AI e : team1) team2.add(e);
		
	}
	
	//temp location
	private void updatePlayer(){
		
	}
	
	private void updateRedTeam(){
		for(AI red : redTeam){
			
			if (red.isGoingHome()) red.runHome(getWidth()/2);
			else red.moveLinear();
			if (red.isInvincible()){
				if (red.x < getWidth()/2) red.setInvincible(false);
				else if (red.getGoal() == blueJail && red.distanceFrom(blueJail) < red.r){
					red.setInJail(true);
					red.setInvincible(false);
				}
			}
		
		//Enforce walls
			if (red.x < red.r) red.x = red.r;
			else if (red.x > getWidth() - red.r) red.x = getWidth() - red.r;
			if (red.y < red.r) red.y = red.r;
			else if (red.y > getHeight() - red.r) red.y = getHeight() - red.r;
			
			if (!red.inJail() && !red.isInvincible()){
			//Update home & hazard status
				if (red.x >= getWidth()/2 && red.isHome()){
					red.setHome(false);
					red.addHazards(blueTeam);
				}
				else if (red.x < getWidth()/2 && !red.isHome()){
					red.setHome(true);
					red.clearHazards();
				}
			//Update flag status
				if (red.distanceFrom(blueFlag) < red.r){
					if (!red.hasFlag()) red.setHasFlag(true);
					for (AI red2 : redTeam){
						if (red != red2 && red2.hasFlag()) red.setHasFlag(false);
					}
					if (red.hasFlag()){
						blueFlag.moveTo(red);
						blueFlagTime += getElapsedTime();
						if (blueFlagTime > MAX_FLAG_TIME){
							sendToJail(red, blueJail);
							if (red.hasFlag()){
								red.setHasFlag(false);
								blueFlagTime = 0;
							}
						}
					}
				}
				else if (red.hasFlag()){
					red.setHasFlag(false);
					blueFlagTime = 0;
				}
				if (!red.isInvincible()){
				//Update closest enemy
					if (red.getClosestEnemy() == null) red.setClosestEnemy(blueTeam.get(0)); //TODO case when closest enemy sent to jail OR just switch to other method below vvv
					for (AI blue : blueTeam){
						if (!blue.inJail() && !blue.isInvincible() &&
								red.distanceFrom(blue) < red.distanceFrom(red.getClosestEnemy())){
							red.setClosestEnemy(blue);
						}
					}
				//Update goal (Priority: Lowest to highest)
					red.setGoal(blueFlag);
					for (AI red2 : redTeam){
						if (red != red2 && red2.hasFlag()) red.setGoal(red); //TODO temp
					}
					for (AI red2 : redTeam){
						if (red2.inJail()){
							if (red.distanceFrom(blueFlag) > red.getClosestEnemy().distanceFrom(blueFlag)){
								red.setGoal(red2);
							}
						}
					}
					if (red.goalDistance() > red.getClosestEnemy().distanceFrom(red.getGoal())){
						Hazard closestToGoal = redSafe.get(0); //TODO refactor
						for (Hazard safe : redSafe){
							if (red.getGoal().distanceFrom(safe) < red.getGoal().distanceFrom(closestToGoal)){
								closestToGoal = safe;
							}
						}
						if (red.distanceFrom(closestToGoal) < red.getClosestEnemy().distanceFrom(closestToGoal)){
							red.setGoal(closestToGoal);
						}
						else{
							for (Hazard safe : redSafe){ //TODO old stuff, use if new is bad
								if (red.distanceFrom(safe) < red.goalDistance()){
									red.setGoal(safe);
								}
							}
						}
					}
					for (AI blue : blueTeam){
						if (!blue.isHome() && !blue.inJail() && !blue.isInvincible()
								&& red.distanceFrom(blue) < red.goalDistance()){
							boolean closest = true; //TODO refactor
							for (AI red2 : redTeam){
								if (!red2.isInvincible() && !red2.inJail() && red.distanceFrom(blue) > red2.distanceFrom(blue)){
									closest = false;
									break;
								}
							}
							if (closest) red.setGoal(blue);
						}
					}
					for (AI blue : blueTeam){
						if (blue.hasFlag()) red.setGoal(blue);
					}
					if (red.hasFlag()) red.setGoingHome(true);
					else if (red.isGoingHome()) red.setGoingHome(false);
				//Update jail status
					if (!red.isHome()){
						for (AI blue : blueTeam){
							if (red.distanceFrom(blue) < red.r + blue.r){
								sendToJail(red, blueJail);
								if (red.hasFlag()){
									red.setHasFlag(false);
									blueFlagTime = 0;
								}
								break;
							}
						}
					}
				//Check if made a rescue
					if (!red.isInvincible()){
						for (AI red2 : redTeam){
							if (red2 != red && red2.inJail() && red.distanceFrom(red2) < red.r + red2.r){
								red.setHasFlag(false);
								red.setInvincible(true);
								red.setGoal(redFlag); //TODO temp
								red2.setInJail(false);
								red2.setInvincible(true);
								red2.setGoal(redFlag); //TODO temp
								break;
							}
						}
					}
				}	
			}
		}
	}
	
	private void updateBlueTeam(){
		for(AI blue : blueTeam){	
			
			if (blue.isGoingHome()) blue.runHome(getWidth()/2);
			else blue.moveLinear();
			if (blue.isInvincible()){
				if (blue.x > getWidth()/2) blue.setInvincible(false);
				else if (blue.getGoal() == redJail && blue.distanceFrom(redJail) < blue.r){
					blue.setInJail(true);
					blue.setInvincible(false);
				}
			}
		
		//Enforce walls
			if (blue.x < blue.r) blue.x = blue.r;
			else if (blue.x > getWidth() - blue.r) blue.x = getWidth() - blue.r;
			if (blue.y < blue.r) blue.y = blue.r;
			else if (blue.y > getHeight() - blue.r) blue.y = getHeight() - blue.r;
			
			if (!blue.inJail() && !blue.isInvincible()){
			//Update home & hazard status
				if (blue.x <= getWidth()/2 && blue.isHome()){
					blue.setHome(false);
					blue.addHazards(redTeam);
				}
				else if (blue.x > getWidth()/2 && !blue.isHome()){
					blue.setHome(true);
					blue.clearHazards();
				}
			//Update flag status
				if (blue.distanceFrom(redFlag) < blue.r){
					if (!blue.hasFlag()) blue.setHasFlag(true);
					for (AI blue2 : blueTeam){
						if (blue != blue2 && blue2.hasFlag()) blue.setHasFlag(false);
					}
					if (blue.hasFlag()){
						redFlag.moveTo(blue);
						redFlagTime += getElapsedTime();
						if (redFlagTime > MAX_FLAG_TIME){
							sendToJail(blue, redJail);
							if (blue.hasFlag()){
								blue.setHasFlag(false);
								redFlagTime = 0;
							}
						}
					}
				}
				else if (blue.hasFlag()){
					blue.setHasFlag(false);
					redFlagTime = 0;
				}
				if (!blue.isInvincible()){
				//Update closest enemy
					if (blue.getClosestEnemy() == null) blue.setClosestEnemy(redTeam.get(0)); //TODO case when closest enemy sent to jail OR just switch to other method below vvv
					for (AI red : redTeam){
						if (!red.inJail() && !red.isInvincible() &&
								blue.distanceFrom(red) < blue.distanceFrom(blue.getClosestEnemy())){
							blue.setClosestEnemy(red);
						}
					}
				//Update goal (Priority: Lowest to highest)
					blue.setGoal(redFlag);
					for (AI blue2 : blueTeam){
						if (blue != blue2 && blue2.hasFlag()) blue.setGoal(blue); //TODO temp
					}
					for (AI blue2 : blueTeam){
						if (blue2.inJail()){
							if (blue.distanceFrom(redFlag) > blue.getClosestEnemy().distanceFrom(redFlag)){
								blue.setGoal(blue2);
							}
						}
					}
					if (blue.goalDistance() > blue.getClosestEnemy().distanceFrom(blue.getGoal())){
						Hazard closestToGoal = blueSafe.get(0); //TODO refactor
						for (Hazard safe : blueSafe){
							if (blue.getGoal().distanceFrom(safe) < blue.getGoal().distanceFrom(closestToGoal)){
								closestToGoal = safe;
							}
						}
						if (blue.distanceFrom(closestToGoal) < blue.getClosestEnemy().distanceFrom(closestToGoal)){
							blue.setGoal(closestToGoal);
						}
						else{
							for (Hazard safe : blueSafe){ //TODO old stuff, use if new is bad
								if (blue.distanceFrom(safe) < blue.goalDistance()){
									blue.setGoal(safe);
								}
							}
						}
					}
					for (AI red : redTeam){
						if (!red.isHome() && !red.inJail() && !red.isInvincible()
								&& blue.distanceFrom(blue) < blue.goalDistance()){
							boolean closest = true; //TODO refactor
							for (AI blue2 : blueTeam){
								if (!blue2.isInvincible() && !blue2.inJail() && blue.distanceFrom(red) > blue2.distanceFrom(red)){
									closest = false;
									break;
								}
							}
							if (closest) blue.setGoal(red);
						}
					}
					for (AI red : redTeam){
						if (red.hasFlag()) blue.setGoal(red);
					}
					if (blue.hasFlag()) blue.setGoingHome(true);
					else if (blue.isGoingHome()) blue.setGoingHome(false);
				//Update jail status
					if (!blue.isHome()){
						for (AI red : redTeam){
							if (blue.distanceFrom(red) < blue.r + red.r){
								sendToJail(blue, redJail);
								if (blue.hasFlag()){
									blue.setHasFlag(false);
									redFlagTime = 0;
								}
								break;
							}
						}
					}
				//Check if made a rescue
					if (!blue.isInvincible()){
						for (AI blue2 : blueTeam){
							if (blue2 != blue && blue2.inJail() && blue.distanceFrom(blue2) < blue.r + blue2.r){
								blue.setHasFlag(false);
								blue.setInvincible(true);
								blue.setGoal(blueFlag); //TODO temp
								blue2.setInJail(false);
								blue2.setInvincible(true);
								blue2.setGoal(blueFlag); //TODO temp
								break;
							}
						}
					}
				}
			}	
		}
	}
	
	private void sendToJail(AI ai, Entity jail){
		ai.setGoal(jail);
		ai.setInvincible(true);
		ai.setGoingHome(false);
	}
	
	@Override
	public void update(){
		super.update();
		
		if (!(blueWin || redWin)){
			if (moveLeft) player.moveLeft();
			if (moveRight) player.moveRight();
			if (moveUp) player.moveUp();
			if (moveDown) player.moveDown();
			
			updatePlayer();
			updateRedTeam();
			updateBlueTeam();
			
		//Check for a victory
			if (redFlag.x >= getWidth()/2) blueWin = true;
			else if (blueFlag.x <= getWidth()/2) redWin = true;
			else{
				blueWin = true;
				for (AI red : redTeam){
					if (!red.inJail()) blueWin = false;
				}
				redWin = true;
				for (AI blue : blueTeam){
					if (!blue.inJail()) redWin = false;
				}
			}
		}
		
	}
	
	@Override
	public void render(Graphics2D g){
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		//g.setColor(bgColor);
		//g.fillRect(getOrigin().x, getOrigin().y, getWidth(), getHeight());
		
		g.setColor(lightRed);
		g.fillRect(getOrigin().x, getOrigin().y, getWidth()/2, getHeight());
		g.setColor(lightBlue);
		g.fillRect(getOrigin().x + getWidth()/2, getOrigin().y, getWidth()/2, getHeight());
		
		g.setColor(Color.white);
		g.fillRect(getOrigin().x + getWidth()/2 - 2, getOrigin().y, 4, getHeight());
		
		g.setColor(Color.blue);
		g.setStroke(stroke);
		for (Hazard h : blueSafe){
			g.drawOval(getOrigin().x + (int)(h.x - h.r),
					getOrigin().y + (int)(h.y - h.r), 
					2*(int)h.r, 2*(int)h.r);
		}
		g.setColor(Color.red);
		for (Hazard h : redSafe){
			g.drawOval(getOrigin().x + (int)(h.x - h.r),
					getOrigin().y + (int)(h.y - h.r), 
					2*(int)h.r, 2*(int)h.r);
		}
		
		g.setColor(Color.yellow);
		g.drawRect((int)(stroke.getLineWidth() + getOrigin().x + redJail.x - jailSize/2),
				(int)(getOrigin().y + redJail.y - stroke.getLineWidth() - jailSize/2), 
				(int)jailSize, (int)jailSize);
		g.drawRect((int)(getOrigin().x + blueJail.x - stroke.getLineWidth() - jailSize/2),
				(int)(getOrigin().y + blueJail.y + stroke.getLineWidth() - jailSize/2),
				(int)jailSize, (int)jailSize);
		
		g.setColor(Color.red);
		for (AI ai : redTeam){
			if (ai.isInvincible()) g.setColor(RED_TRANSP);
			g.fillOval(getOrigin().x + (int)(ai.getX() - ai.r),
					getOrigin().y + (int)(ai.getY() - ai.r),
					2*(int)ai.r, 2*(int)ai.r);
			if (ai.isInvincible()) g.setColor(Color.red);
		}
		
		g.setColor(Color.blue);
		for (AI ai : blueTeam){
			if (ai.isInvincible()) g.setColor(BLUE_TRANSP);
			g.fillOval(getOrigin().x + (int)(ai.getX() - ai.r),
					getOrigin().y + (int)(ai.getY() - ai.r),
					2*(int)ai.r, 2*(int)ai.r);
			if (ai.isInvincible()) g.setColor(Color.blue);
		}
		
		/*g.setColor(Color.pink);
		g.fillOval(getOrigin().x + (int)(player.getX() - player.r),
				getOrigin().y + (int)(player.getY() - player.r),
				2*(int)player.r, 2*(int)player.r);*/
		
		g.setColor(Color.red);
		g.fillRect(getOrigin().x + (int)redFlag.x, getOrigin().y + (int)redFlag.y, 10, 10);
		g.setColor(Color.blue);
		g.fillRect(getOrigin().x + (int)blueFlag.x, getOrigin().y + (int)blueFlag.y, 10, 10);
		
		//Strings
		g.setFont(font);
		g.setColor(Color.red);
		g.drawString(String.valueOf(redFlagTime/1000.0), getOrigin().x + 10, getOrigin().y + 10 + 20); //TODO place properly
		g.setColor(Color.blue);
		g.drawString(String.valueOf(blueFlagTime/1000.0), getOrigin().x + 10, getOrigin().y + 10 + 50); //
		
		if (redWin || blueWin){
			g.setColor((redWin)? Color.red : Color.blue);
			g.drawString((redWin)? "RED WIN" : "BLUE WIN", getOrigin().x + getWidth()/2, getOrigin().y + getHeight()/2); //TODO place properly
		}
	}
	
	@Override
	public void click(MouseEvent e){
		//TODO
	}
	
	@Override
	public void unclick(MouseEvent e){
		//TODO
	}
	
	@Override
	public void drag(MouseEvent e){
		//TODO
	}
	
	@Override
	public void move(MouseEvent e) {
		//TODO
	}

	@Override
	public void type(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void press(KeyEvent e) {
		switch (e.getKeyCode()){
		case KeyEvent.VK_LEFT:
			moveLeft = true;
			break;
		case KeyEvent.VK_RIGHT:
			moveRight = true;
			break;
		case KeyEvent.VK_UP:
			moveUp = true;
			break;
		case KeyEvent.VK_DOWN:
			moveDown = true;
			break;
		}
	}

	@Override
	public void release(KeyEvent e) {
		switch (e.getKeyCode()){
		case KeyEvent.VK_LEFT:
			moveLeft = false;
			break;
		case KeyEvent.VK_RIGHT:
			moveRight = false;
			break;
		case KeyEvent.VK_UP:
			moveUp = false;
			break;
		case KeyEvent.VK_DOWN:
			moveDown = false;
			break;
		}
	}
	
	/*private float distance(float x1, float y1, float x2, float y2){
		return (float)Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}*/
	
	/*private BufferedImage readImage(String filepath){
		try {
			return ImageIO.read(new File(filepath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void addEnemy(){
		AI en = new AI(getWidth()/2, getHeight()/2, 2, 10, 20);
		team1.add(en);
		team2.add(en);
	}*/
	
	/*//TODO temp
	if (player.distanceFrom(flag2.x, flag2.y) < player.r){
		flag2.x = player.x;
		flag2.y = player.y;
		for (AI blue : team2) blue.setGoal(player); //TODO temp
		if (!player.hasFlag()) player.setHasFlag(true);
	}
	else if (player.hasFlag()) player.setHasFlag(false);
	if (player.x >= getWidth()/2){
		for (AI blue : team2){
			if (blue.distanceFrom(player.x, player.y) < blue.r + player.r){
				//TODO TEMP:
				//set red goal = jail, jail mode = true => no hazards or goals until out of jail
				player.x = getWidth()/8;
				player.y = getHeight()/2;
				blue.setGoal(flag1);
				for (Hazard safe : blueSafe){
					if (blue.distanceFrom(safe.x, safe.y) < blue.goalDistance()){
						blue.setGoal(safe);
					}
				}
			}
			else if (blue.distanceFrom(player.x, player.y) < blue.goalDistance()){
				blue.setGoal(player);
			}
		}
	}
	//end temp
	
	//OLD UPDATE
	for (AI red : team1){
		red.moveLinear();
		
		if (red.distanceFrom(flag2.x, flag2.y) < red.r){
			flag2.x = red.x;
			flag2.y = red.y;
			for (AI blue : team2) blue.setGoal(red); //TODO temp
			red.setGoal(flag1); //TODO should use slope goal or need an entity goal OR move away from all blues and towards left
			if (!red.hasFlag()) red.setHasFlag(true);
		}
		else if (red.hasFlag()){
			red.setGoal(flag2);
			for (Hazard safe : redSafe){
				if (red.distanceFrom(safe.x, safe.y) < red.goalDistance()){
					red.setGoal(safe);
				}
			}
			red.setHasFlag(false);
		}
		
		if (red.x >= getWidth()/2){
			if (red.isHome()){
				for (AI ai2 : team2) red.addHazard(ai2);
				red.setHome(false);
				red.setGoal(flag2); //TODO temp
				for (Hazard safe : redSafe){
					if (red.distanceFrom(safe.x, safe.y) < red.goalDistance()){
						red.setGoal(safe);
					}
				}
			}
			for (AI blue : team2){
				if (blue.distanceFrom(red.x, red.y) < blue.r + red.r){
					//TODO TEMP:
					//set red goal = jail, jail mode = true => no hazards or goals until out of jail
					red.x = getWidth()/8;
					red.y = getHeight()/2;
					if (!player.hasFlag()) blue.setGoal(flag1);
					red.setGoal(flag2);
					for (Hazard safe : redSafe){
						if (red.distanceFrom(safe.x, safe.y) < red.goalDistance()){
							red.setGoal(safe);
						}
					}
				}
				else if (blue.distanceFrom(red.x, red.y) < blue.goalDistance()){
					blue.setGoal(red);
				}
			}
		}
		else if (red.x < getWidth()/2 && !red.isHome()){
			red.setHome(true);
			red.clearHazards();
		}
	}
	
	for (AI blue : team2){
		blue.moveLinear();
		
		if (!player.hasFlag() && blue.distanceFrom(flag1.x, flag1.y) < blue.r){
			flag1.x = blue.x;
			flag1.y = blue.y;
			for (AI red : team1) red.setGoal(blue); //TODO temp
			blue.setGoal(flag2);
			if (!blue.hasFlag()) blue.setHasFlag(true);
		}
		else if (blue.hasFlag()){
			blue.setGoal(flag1);
			blue.setHasFlag(false);
		}
		
		if (blue.x <= getWidth()/2){
			if (blue.isHome()){
				for (AI ai2 : team1) blue.addHazard(ai2);
				blue.addHazard(player); //TODO temp
				blue.setHome(false);
				blue.setGoal(flag1); //TODO temp
			}
			for (AI red : team1){
				if (red.distanceFrom(blue.x, blue.y) < blue.r + red.r){
					//TODO TEMP:
					//set red goal = jail, jail mode = true => no hazards or goals until out of jail
					blue.x = getWidth()*7/8;
					blue.y = getHeight()/2;
					red.setGoal(flag2);
					blue.setGoal(flag1);
				}
				else if (red.distanceFrom(blue.x, blue.y) < red.goalDistance()){
					red.setGoal(blue);
				}
			}
			//FOR PLAYER TEMP:
			if (player.distanceFrom(blue.x, blue.y) < blue.r + player.r){
				//TODO TEMP:
				//set red goal = jail, jail mode = true => no hazards or goals until out of jail
				blue.x = getWidth()*7/8;
				blue.y = getHeight()/2;
				blue.setGoal(flag1);
			} //end
		}
		else if (blue.x > getWidth()/2 && !blue.isHome()){
			blue.setHome(true);
			blue.clearHazards();
		}
	}*/
	
}
