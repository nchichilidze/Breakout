
/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

	/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

	/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

	/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

	/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

	/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

	/** Separation between bricks */
	private static final int BRICK_SEP = 4;

	/** Width of a brick */
	private static final int BRICK_WIDTH = (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

	/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

	/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

	/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

	/** Number of turns */

	private static final int NTURNS = 3;
	// private objects
	private GRect paddle;
	private GOval ball;
	// ball velocity
	private double vy = 3.0;
	private double vx;
	// random generator
	private RandomGenerator rgen = RandomGenerator.getInstance();
	// number of bricks
	private int bricknum = NBRICKS_PER_ROW * NBRICK_ROWS;
	// number of lives
	private int lives = NTURNS;
	// pause after every move
	private int DELAY = 10;

	/* Method: run() */
	/** Runs the Breakout program. */
	// lets the player play for the number of turns
	public void run() {
		buildTheWorld();
		for (int i = 0; i < NTURNS; i++) {
			play();
		}
	}

	// builds the world and adds bricks, the ball and the paddle.
	private void buildTheWorld() {
		buildBricks();
		buildBall();
		buildPaddle();
	}

	// builds bricks, coloring them accordingly
	private void buildBricks() {
		for (int a = 0; a < NBRICKS_PER_ROW; a++) {
			for (int i = 0; i < NBRICK_ROWS; i++) {
				double x = (WIDTH - BRICK_WIDTH * NBRICKS_PER_ROW - BRICK_SEP * (NBRICKS_PER_ROW - 1)) / 2
						+ BRICK_WIDTH * a + BRICK_SEP * a;
				double y = BRICK_Y_OFFSET + i * BRICK_HEIGHT + i * BRICK_SEP;
				GRect rect = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
				rect.setFilled(true);
				if (i == 0 || i == 1)
					rect.setColor(Color.RED);
				if (i == 2 || i == 3)
					rect.setColor(Color.ORANGE);
				if (i == 4 || i == 5)
					rect.setColor(Color.YELLOW);
				if (i == 6 || i == 7)
					rect.setColor(Color.GREEN);
				if (i == 8 || i == 9)
					rect.setColor(Color.cyan);
				add(rect);
			}
		}
	}

	// builds the ball and puts it in the center of the screen
	private void buildBall() {
		double x = WIDTH / 2 - BALL_RADIUS;
		double y = HEIGHT / 2 - BALL_RADIUS;
		ball = new GOval(x, y, 2 * BALL_RADIUS, 2 * BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
	}

	// drawing the paddle in the center of the screen
	public void buildPaddle() {
		double x = WIDTH / 2 - PADDLE_WIDTH / 2;
		double y = HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		paddle = new GRect(x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.black);
		add(paddle);
		addMouseListeners();
	}

	// moving the paddle along with the mouse, also making sure the paddle
	// doesn't go out of the window
	public void mouseMoved(MouseEvent e) {
		double y = HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		if (e.getX() + PADDLE_WIDTH < WIDTH && e.getX() > 0)
			paddle.setLocation(e.getX(), y);
	}

	/*
	 * lets the player play until the game ends the game ends A) when there are
	 * no more bricks left B) when there are no more lives left
	 */
	private void play() {
		generateVel(); // generates a random velocity and direction
		while (true) {
			moveBall();
			if (bricknum == 0) { // when the player removes all bricks
				remove(ball);
				win(); // prints the message for winning
				break; // ends the cycle
			}
			if (lives == 0) { // when the player runs out of lives
				remove(ball);
				lose(); // prints the message for losing
				break; // ends the cycle
			}
		}
	}

	// generates a random vx with a random direction
	private void generateVel() {
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5))
			vx = -vx;
	}

	//
	private void moveBall() {
		ball.move(vx, vy); // moves the ball
		pause(DELAY); // pauses so the ball doesn't move too fast
		// determine if the ball has collided with either the right or the left
		// wall
		// changes the direction if true.
		if (ball.getX() > WIDTH - 2 * BALL_RADIUS || ball.getX() < 0) {
			vx = -vx;
		}
		if (ball.getY() < 0) {
			vy = -vy;
		}
		// determines whether the ball has collided with the lower wall
		if (ball.getY() + 2 * BALL_RADIUS > HEIGHT) {
			remove(ball);
			if (lives > 0) { // checks whether the player still has any lives
								// left
				pause(100);
				add(ball, WIDTH / 2 - BALL_RADIUS, HEIGHT / 2 - BALL_RADIUS); // adds
																				// the
																				// ball
																				// again
																				// if
																				// true
				GLabel click = new GLabel("Click to continue", 160, 250); // tells
																			// the
																			// player
																			// to
																			// click
				add(click);
				waitForClick();
				remove(click);
				lives--;
			}
		}
		collideWBricks(); // collisions with bricks
		collideWPaddle(); // collisions with the paddle
	}

	// if the player wins, all objects get removed and the program prints a
	// special message.
	private void win() {
		removeAll();
		add(new GLabel("congratulations!", 160, 250));

	}

	// if the player loses, all objects get removed and the program prints a
	// special message.
	private void lose() {
		removeAll();
		add(new GLabel("game lost.", 160,250));
	}

	// checks the 4 "sides" of the ball , if either of them touch any bricks
	// the brick gets removed, vy direction changes, the brick counter decreases
	// by 1.
	private void collideWBricks() {
		if (getElementAt(ball.getX(), ball.getY()) != null // top left corner
				&& getElementAt(ball.getX(), ball.getY()) != paddle) {
			remove(getElementAt(ball.getX(), ball.getY()));
			vy = -vy; // changing the direction
			bricknum--; // decreasing the counter
		} else if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()) != null // top
																					// right
																					// corner
				&& getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()) != paddle) {
			remove(getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()));
			vy = -vy;
			bricknum--;
		} else if (getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS) != null // lower
																					// left
																					// corner
				&& getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS) != paddle) {
			remove(getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS));
			vy = -vy;
			bricknum--;
		} else if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS) != null // lower
																										// right
																										// corner
				&& getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS) != paddle) {
			remove(getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS));
			vy = -vy;
			bricknum--;
		}
	}

	
	
	

	// when hitting the paddle, the ball changes the direction when its coming
	// from above
	// also checking every side of the ball
	private void collideWPaddle() {
		if (getElementAt(ball.getX(), ball.getY()) == paddle) {
			if (vy > 0) vy = -vy;
		} else if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()) == paddle) {
			if (vy > 0) vy = -vy;
		} else if (getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS) == paddle) {
			if (vy > 0) vy = -vy;
		} else if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS) == paddle) {
			if (vy > 0) vy = -vy;
		} // for the ball not to get stuck on the paddle.
		if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS) == paddle
				&& ball.getY() + 2 * BALL_RADIUS > paddle.getY()) {
			vx = -vx;
		}
		if (getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS) == paddle
				&& ball.getY() + 2 * BALL_RADIUS > paddle.getY()) {
			vx = -vx;
		}
		
		}
	}


