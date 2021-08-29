import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.JFrame;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class GridDemoPanel extends JPanel implements MouseListener, KeyListener, Constants
{
	private int[][] grid;
	public GridDemoFrame myParent;
	private int numSnakes; //this is the mode - either 1 or SNAKE_NUM
	public int score;
	private int stepSize;
	private int [] presets = new int[3];
	private int generation;
	final private JFrame avFrame;
	//animation stepper globals
	private int animationStepper = 0;
	private final int printPerGen = 3;
	private boolean displayMode = false; //false = calculating, true = animating
	private int globalRepaintIndex = -1; //for the ann visualizer
	private int [] appleTracker; //holds current apple index of each displayed snake
	private int [] newArray; //holds the index list of viperPit that will be mutated in the next generation
	private int min;
	private ArrayList<int[]> masterAppleList;
	private ArrayList<Snakey> viperPit;

	public GridDemoPanel(GridDemoFrame parent)
	{
		super();
		numSnakes = 1000;
		boolean breaker = false;
		while (!breaker) {
			String stepSizeString = JOptionPane.showInputDialog(null,"How many steps should each snake take?");
			try {
				breaker = true;
				stepSize = Integer.valueOf(stepSizeString);}
			catch (NumberFormatException e) {
				breaker = false;
				JOptionPane.showConfirmDialog(null,"Please Select a real value", "Error", JOptionPane.DEFAULT_OPTION);}}
		breaker = false;
		while (!breaker) {
			String stepSizeString = JOptionPane.showInputDialog(null,"How many layers should the neural network have? (For speed pick an integer 1-5)");
			try {
				breaker = true;
				presets[0] = Integer.valueOf(stepSizeString);}
			catch (NumberFormatException e) {
				breaker = false;
				JOptionPane.showConfirmDialog(null,"Please Select a real value", "Error", JOptionPane.DEFAULT_OPTION);}
			if (breaker) {
				if (presets[0] < 1 || presets[0] > 5) {
					breaker = false;
					JOptionPane.showConfirmDialog(null,"Please Select a real value", "Error", JOptionPane.DEFAULT_OPTION);}}}
		breaker = false;
		while (!breaker) {
			String stepSizeString = JOptionPane.showInputDialog(null,"How many nodes per layer should the neural network have? (For speed pick an integer 1-8)");
			try {
				breaker = true;
				presets[1] = Integer.valueOf(stepSizeString);}
			catch (NumberFormatException e) {
				breaker = false;
				JOptionPane.showConfirmDialog(null,"Please Select a real value", "Error", JOptionPane.DEFAULT_OPTION);}
			if (breaker) {
				if (presets[1] < 1 || presets[1] > 8) {
					breaker = false;
					JOptionPane.showConfirmDialog(null,"Please Select a real value", "Error", JOptionPane.DEFAULT_OPTION);}}}

		//numSnakes = 10000;
		grid =  new int[NUM_ROWS][NUM_COLS];
		generation = 0;
		//inputs, layers,
		//mod2 //5 -> 6
		ANN.setDat(5,presets[0],presets[1]);//MUST be called before any snakey or ann calls - defines the size of the neural network
		Snakey.setMasterApples();

		//animation stepper global variable initializations
		appleTracker = new int[printPerGen];
		newArray = new int[numSnakes/10];
		min = numSnakes;

		viperPit = new ArrayList<Snakey>();
		for (int i = 0; i < numSnakes; i++) {
			viperPit.add(new Snakey(1));}
		avFrame = new JFrame("ANN");
		ANNVisualizer avPanel = new ANNVisualizer();
		avFrame.add(avPanel);
		avFrame.setSize(GRAPH_SIZE*(2+ANN.HIDDEN_LAYERS),Math.max(Math.max(ANN.NODES_PER_LAYER+1,ANN.INPUTS),3)*GRAPH_SIZE);
		avFrame.setVisible(true);
		avFrame.setLocation(900,100);
		setBackground(Color.BLACK);
		addMouseListener(this);
		parent.addKeyListener(this); // activate this if you wish to listen to the keyboard.
		myParent = parent;
	}	
	
	/**
	 * makes a new board with random colors, completely filled in, and resets the score to zero.
	 */
	//set up the display grid for all snakes simultaneously
	public void updateGrid() {
		grid =  new int[NUM_ROWS][NUM_COLS];
		for (int s = 0; s < printPerGen; s++) {
			if (viperPit.get(newArray[s]).isAlive()) {
				for (int i = 0; i < viperPit.get(newArray[s]).getSnake().size(); i++) {
					int x = viperPit.get(newArray[s]).getSnake().get(i)[0];
					int y = viperPit.get(newArray[s]).getSnake().get(i)[1];
					if (grid[x][y] == 0) {
						grid[x][y] = 1;}}
				int x = viperPit.get(newArray[s]).getAppleMovement().get(appleTracker[s])[0];
				int y = viperPit.get(newArray[s]).getAppleMovement().get(appleTracker[s])[1];
				if (grid[x][y] == 0) {
					grid[x][y] = -1;}}}}

	//updates the grid based on a single Snakey, often succeeded by a call of sense(Snakey snakey) that returns distances
	public void updateGrid(Snakey snakey) {
		grid =  new int[NUM_ROWS][NUM_COLS];
		for (int i = 0; i < snakey.getSnake().size(); i++) {
			grid[snakey.getSnake().get(i)[0]][snakey.getSnake().get(i)[1]] = 1;}
		grid[snakey.getApple()[0]][snakey.getApple()[1]] = -1;}

	//used in initializing viperPit
//	public void resetGame() {
//		score = 0;
//
//		for (int s = 0; s < numSnakes; s++) {
//			viperPit.get(s).revive();}}

	public void paintComponent(Graphics g) {
		//paints both boxes and seeing lines
		super.paintComponent(g);
			//draw the snake
			for (int r = 0; r < NUM_ROWS; r++) {
				for (int c = 0; c < NUM_COLS; c++) {
					if (grid[r][c] == 1) {
						g.setColor(Color.GREEN);
					} else if (grid[r][c] == -1) {
						g.setColor(Color.RED);
					} else {
						g.setColor(Color.BLACK);}
					g.fillRect(r * DENSITY, c * DENSITY, DENSITY, DENSITY);}}}
	
	/**
	 * the mouse listener has detected a click, and it has happened on the cell in theGrid at row, col
	 * @param row
	 * @param col
	 */
	public void userClickedCell(int row, int col) {}
	/**
	 * Here's an example of a simple dialog box with a message.
	 */
	public void makeGameOverDialog() {JOptionPane.showMessageDialog(this, "Game Over."); }
	
	//============================ Mouse Listener Overrides ==========================
	@Override
	// mouse was just released within about 1 pixel of where it was pressed.
	public void mouseClicked(MouseEvent e) {
		// mouse location is at e.getX() , e.getY().
		// if you wish to convert to the rows and columns, you can integer-divide by the cell size.
		int col = e.getX()/Cell.CELL_SIZE;
		int row = e.getY()/Cell.CELL_SIZE;
		userClickedCell(row,col);}

	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	// mouse just entered this window
	public void mouseEntered(MouseEvent e){}
	@Override
	// mouse just left this window
	public void mouseExited(MouseEvent e){}
	//============================ Key Listener Overrides ==========================
	@Override
	/**
	 * user just pressed and released a key. (May also be triggered by autorepeat, if key is held down?
	 * @param e
	 */
	public void keyTyped(KeyEvent e) {}
	@Override
	//not active in final program - remnant of when the program was a playable game of snake
	public void keyPressed(KeyEvent e)
	{
//			if (whichKey == 38 && viperPit.get(0).getHeading() != 3) { //up
//				viperPit.get(0).setHeading(1);
//			} else if (whichKey == 39 && viperPit.get(0).getHeading() != 4) { //right
//				viperPit.get(0).setHeading(2);
//			} else if (whichKey == 40 && viperPit.get(0).getHeading() != 1) { //down
//				viperPit.get(0).setHeading(3);
//			} else if (whichKey == 37 && viperPit.get(0).getHeading() != 2) { //left
//				viperPit.get(0).setHeading(4);
//			} else if (whichKey == 32) { // toggles between ai mode and manual single snake mode
//				if (numSnakes == SNAKE_NUM) {
//					numSnakes = 1;
//				} else {
//					numSnakes = SNAKE_NUM;}}
	}

	@Override
	public void keyReleased(KeyEvent e) {}
	// ============================= animation stuff ======================================
	/**
	 * if you wish to have animation, you need to call this method from the GridDemoFrame AFTER you set the window visibility.
	 */
	public void initiateAnimationLoop() {
		Thread aniThread = new Thread( new AnimationThread(0)); // the number here is the number of milliseconds between z.
		aniThread.start(); }
	
	/**
	 * Modify this method to do what you want to have happen periodically.
	 * This method will be called on a regular basis, determined by the delay set in the thread.
	 * Note: By default, this will NOT get called unless you uncomment the code in the GridDemoFrame's constructor
	 * that creates a thread.
	 *
	 */
	public void animationStep() {
			if (!displayMode) {
				//each snake, whole trial at a time
				for (int s = 0; s < numSnakes; s++) {
					Snakey sn = viperPit.get(s);
					for (int i = 0; i < stepSize; i++) {
						if (!sn.isAlive()) {
							break;}
						//1. Sense, propagate, adjust
						sn.getNet().buildIL(sense(sn), sn.adjustedApple());
						sn.getNet().propagate();
						sn.setNewHeading();
						sn.addHeadBit();
						//2. Cull
						int[] check = new int[]{sn.getSnake().get(0)[0] + sn.getHeadingVector()[0], sn.getSnake().get(0)[1] + sn.getHeadingVector()[1]};
						if (check[0] >= 0 && check[0] < NUM_COLS && check[1] >= 0 && check[1] < NUM_ROWS) {
							updateGrid(sn);
							if (grid[check[0]][check[1]] == 1) {
								sn.kill();
								break;
							}
						} else {
							sn.kill();
							break;
						}
						//check if everything goes in circles and kills the ones that do
						if (i > 5) {
							int index = i - 5;
							int firstIndex = sn.getHeadMovement().get(index);
							boolean killThis1 = true;
							boolean killThis2 = true;
							for (int j = 1; j < 5; j++) {
								if (firstIndex % 4 != (sn.getHeadMovement().get(j + index) + j) % 4) {
									killThis1 = false;
									break;
								}
							}
							for (int j = 1; j < 5; j++) {
								if (firstIndex % 4 != (4 + sn.getHeadMovement().get(j + index) - j) % 4) {
									killThis2 = false;
									break;
								}
							}
							if (killThis1 || killThis2) {
								sn.kill();
								break;
							}
						}
						//3. Move
						int[] h = new int[]{sn.getSnake().get(0)[0], sn.getSnake().get(0)[1]};
						int[] last = new int[]{sn.getSnake().get(sn.getSnake().size() - 1)[0], sn.getSnake().get(sn.getSnake().size() - 1)[1]};
						for (int j = 0; j < sn.getSnake().size(); j++) {
							if (j == 0) {
								sn.modSnakeValue(0, 0, sn.getHeadingVector()[0]);
								sn.modSnakeValue(0, 1, sn.getHeadingVector()[1]);
							} else {
								int[] h1 = new int[]{sn.getSnake().get(j)[0], sn.getSnake().get(j)[1]};
								sn.setSnakeValue(j, h);
								h = new int[]{h1[0], h1[1]};
							}
						}
						if (sn.getSnake().get(0)[0] == sn.getApple()[0] && sn.getSnake().get(0)[1] == sn.getApple()[1]) {
							sn.addSnakeBit(last);
							//						System.out.println("Apple Length and Score");
							//						System.out.println(sn.getAppleMovement().size() + "      " + sn.getScore());
							sn.increment();
							sn.resetApple();
						}
					}
				}
				//4.Sort
				System.out.println("EVOLVING: " + generation);
				generation++;
				//initial filled dummy array of newArray
				int high = 0;
				for (int i = 0; i < numSnakes; i++) {
					if (viperPit.get(i).getScore() > high) {
						high = viperPit.get(i).getScore();
					}
				}
				myParent.updateScore(high);
				for (int i = 0; i < numSnakes / 10; i++) {
					newArray[i] = i;
					if (viperPit.get(i).getScore() < min) {
						min = viperPit.get(i).getScore();
					}
				}
				//fills in the indices of newArray according to order
				for (int i = numSnakes / 10; i < numSnakes; i++) {
					if (viperPit.get(i).getScore() > min) {
						for (int j = 0; j < numSnakes / 10; j++) {//add new index to the array of top 10%
							if (viperPit.get(newArray[j]).getScore() < viperPit.get(i).getScore()) {
								newArray[j] = i;
								min = numSnakes; //make min really high and then lower it until it hits min in best list
								for (int k = 0; k < numSnakes / 10; k++) { //rebasing min
									if (viperPit.get(newArray[k]).getScore() < min) {
										min = viperPit.get(newArray[k]).getScore();
									}
								}
								break;
							}
						}
					}
				}
				//setup list of the apples to be printing as it cycles
				//set up the displaySnakes
				//Snakey.setMasterApples();
				for (int i = 0; i < printPerGen; i++) {
					int index = newArray[i];
					//System.out.println("Snake: " + i + "(Index: "+index+")");
					viperPit.get(index).revive();
					//				//this resets the snake, heading, and death status so it can be used in the animation
					//				System.out.println("Score: " + viperPit.get(index).getScore());
					//				System.out.println("Length: " + viperPit.get(index).getSnake().size());
					//				for (int j = 0; j < viperPit.get(index).getHeadMovement().size(); j++) {
					//					System.out.println("Headings: " + viperPit.get(index).getHeadMovement().get(j));
					//				}
					//				for (int j = 0; j < viperPit.get(index).getAppleMovement().size(); j++) {
					//					System.out.println("Apples: " + viperPit.get(index).getAppleMovement().get(j)[0] + "    " + viperPit.get(index).getAppleMovement().get(j)[1]);}
				}
				//shift to next mode
				displayMode = true;
				animationStepper = 0;
				appleTracker = new int[printPerGen];
			}
			if (displayMode) {
				//System.out.println("Animation Step: " + animationStepper);
				//			for (int i = 0; i < printPerGen; i++) {
				//				System.out.println(viperPit.get(newArray[i]).getHeadMovement().size());}
				//5. Display best
				for (int i = 0; i < printPerGen; i++) {
					int index = newArray[i];
					//must have a score, not be dead, and the animation has not passed it yet
					if (animationStepper == viperPit.get(index).getHeadMovement().size()) {
						viperPit.get(index).kill();
						continue;
					}
					if (viperPit.get(index).getScore() > 0 && viperPit.get(index).isAlive()) {
						viperPit.get(index).setApple(viperPit.get(index).getAppleMovement().get(appleTracker[i]));
						viperPit.get(index).setHeading(viperPit.get(index).getHeadMovement().get(animationStepper));
						//System.out.println("heading for snake " + i + "   " + viperPit.get(index).getHeading());
						int[] check = new int[]{viperPit.get(index).getSnake().get(0)[0] + viperPit.get(index).getHeadingVector()[0], viperPit.get(index).getSnake().get(0)[1] + viperPit.get(index).getHeadingVector()[1]};
						if (check[0] >= 0 && check[0] < NUM_COLS && check[1] >= 0 && check[1] < NUM_ROWS) {
							updateGrid(viperPit.get(index));
							if (grid[check[0]][check[1]] == 1) {
								viperPit.get(index).kill();
								continue;
							}
						} else {
							viperPit.get(index).kill();
							continue;
						}
						//3. Move the snakes
						int[] h = new int[]{viperPit.get(index).getSnake().get(0)[0], viperPit.get(index).getSnake().get(0)[1]};
						int[] last = new int[]{viperPit.get(index).getSnake().get(viperPit.get(index).getSnake().size() - 1)[0], viperPit.get(index).getSnake().get(viperPit.get(index).getSnake().size() - 1)[1]};
						for (int k = 0; k < viperPit.get(index).getSnake().size(); k++) {
							if (k == 0) {
								viperPit.get(index).modSnakeValue(0, 0, viperPit.get(index).getHeadingVector()[0]);
								viperPit.get(index).modSnakeValue(0, 1, viperPit.get(index).getHeadingVector()[1]);
							} else {
								int[] h1 = new int[]{viperPit.get(index).getSnake().get(k)[0], viperPit.get(index).getSnake().get(k)[1]};
								viperPit.get(index).setSnakeValue(k, h);
								h = new int[]{h1[0], h1[1]};
							}
						}
						if (viperPit.get(index).getSnake().get(0)[0] == viperPit.get(index).getApple()[0] && viperPit.get(index).getSnake().get(0)[1] == viperPit.get(index).getApple()[1]) {
							viperPit.get(index).addSnakeBit(last);
							//System.out.println("catch");
							appleTracker[i]++;
						}
					}
				}
				//			System.out.println("apple lengths");
				//			for (int i = 0; i < printPerGen; i++) {
				//				System.out.println(viperPit.get(newArray[i]).getAppleMovement().size());
				//			}
				globalRepaintIndex = newArray[0];
				avFrame.repaint();
				updateGrid();
				repaint();
				boolean displayBreaker = true;
				for (int i = 0; i < printPerGen; i++) {
					if (viperPit.get(newArray[i]).isAlive()) {
						displayBreaker = false;
						break;
					}
				}
				if (displayBreaker) {
					displayMode = false;
				}
				animationStepper++;


				//			System.out.println("snakes themselves");
				//			for (int i = 0; i < printPerGen; i++) {
				//				System.out.println(i + "    " + viperPit.get(newArray[i]).getSnake().size());
				//				for (int j = 0 ; j< viperPit.get(newArray[i]).getSnake().size(); j++) {
				//					System.out.println(i + "    " + j + "    "+ viperPit.get(newArray[i]).getSnake().get(j)[0] + " " + viperPit.get(newArray[i]).getSnake().get(j)[1]);}}

				//don't go to next animation loop if it needs to break out on the next animationStepper value


			}
			if (!displayMode) {
				//evolving section

				//add the best ten percent
				//6. Evolve
				ArrayList<Snakey> nextGen = new ArrayList<Snakey>();
				for (int i = 0; i < numSnakes / 10; i++) {
					//System.out.println(viperPit.get(newArray[i]).getScore());
					nextGen.add(new Snakey(viperPit.get(newArray[i]), generation));
				}
				int take = numSnakes / 10;
				//expand out to fill up numSnakes
				for (int i = 0; i < take; i++) {
					int news = 30;
					for (int j = 0; j < numSnakes / take - news; j++) {
						Snakey holdSnake = new Snakey(nextGen.get(i), generation);
						holdSnake.modify();
						nextGen.add(holdSnake);
					}
					for (int j = 0; j < news; j++) {
						nextGen.add(new Snakey(generation));
					}
				}
				viperPit = new ArrayList<Snakey>();
				for (int i = 0; i < numSnakes; i++) {
					viperPit.add(nextGen.get(i));
				}
			}
	}
	// ------------------------------- animation thread - internal class -------------------
	public class AnimationThread implements Runnable
	{
		long start;
		long timestep;
		public AnimationThread(long t) {
			timestep = t;
			start = System.currentTimeMillis();}
		@Override
		public void run() {
			long difference;
			while (true) {
				difference = System.currentTimeMillis() - start;
				if (difference >= timestep) {
					animationStep();
					start = System.currentTimeMillis();}
				try {Thread.sleep(100);}
				catch (InterruptedException iExp) {
					System.out.println(iExp.getMessage());
					break;}}}}

	//displays the full ANN connection and propagated value for a given Snakey with viperPit index as globalRepaintIndex
	public class ANNVisualizer extends JPanel
	{
		public ANNVisualizer()
		{super();}
		public void paintComponent(Graphics g)
		{
			if (globalRepaintIndex>-1 ){
				super.paintComponent(g);
				g.setColor(Color.BLACK);
				float[][] hl = viperPit.get(globalRepaintIndex).getNet().getHL();
				float[] ol = viperPit.get(globalRepaintIndex).getNet().getOL();
				int [] il = viperPit.get(globalRepaintIndex).getNet().getIL();
				//NEW PROPAGATING
				//draw lines of the connections
				for (int i = 0; i < ANN.NODES_PER_LAYER; i++) { //output
					for (int j = 0; j < ANN.INPUTS; j++) { //input
						g.drawLine(GRAPH_SIZE/2,GRAPH_SIZE/2+GRAPH_SIZE*j,(int)(1.5*GRAPH_SIZE),GRAPH_SIZE/2+GRAPH_SIZE*i);
						}}
				for (int i = 0; i < ANN.HIDDEN_LAYERS-1; i++) { //iterator
					for (int j = 0; j < ANN.NODES_PER_LAYER; j++) { //output
						for (int k = 0; k< ANN.NODES_PER_LAYER;k++) { //input
							g.drawLine((int)((1.5 + i)*GRAPH_SIZE),GRAPH_SIZE/2+GRAPH_SIZE*k,(int)((2.5 +i)*GRAPH_SIZE),GRAPH_SIZE/2+GRAPH_SIZE*j);
							}}}
				for (int i = 0; i < ANN.OUTPUTS; i++) { //output
					for (int j = 0; j< ANN.NODES_PER_LAYER;j++) { //input
						g.drawLine(this.getWidth()-GRAPH_SIZE/2,GRAPH_SIZE/2+GRAPH_SIZE*i,(int)((.5+ANN.HIDDEN_LAYERS)*GRAPH_SIZE),GRAPH_SIZE/2+j*GRAPH_SIZE);
					}}
				//draw filled ovals
				g.setColor(new Color(255,255,255));
				for (int i = 0; i < ANN.INPUTS; i++) {
					g.fillOval(GRAPH_SIZE / 4, GRAPH_SIZE / 4 + i * GRAPH_SIZE, GRAPH_SIZE / 2, GRAPH_SIZE / 2);}
				for (int i = 0; i < ANN.HIDDEN_LAYERS; i++) {
					for (int j = 0; j < ANN.NODES_PER_LAYER; j++) {
						g.fillOval((int)(GRAPH_SIZE*1.25+GRAPH_SIZE*i),GRAPH_SIZE*j + GRAPH_SIZE/4,GRAPH_SIZE/2,GRAPH_SIZE/2); }}
				for (int i = 0; i < 3; i++) {
					g.fillOval(this.getWidth()-(int)(GRAPH_SIZE*3/4), GRAPH_SIZE/4+ i*GRAPH_SIZE,GRAPH_SIZE/2,GRAPH_SIZE/2);}
				//draw borders and inside value text
				g.setColor(Color.BLACK);
				for (int i = 0; i < ANN.INPUTS; i++) {
					g.drawOval(GRAPH_SIZE/4, GRAPH_SIZE/4 + i*GRAPH_SIZE, GRAPH_SIZE/2,GRAPH_SIZE/2);
					g.drawString(String.valueOf(il[i]),GRAPH_SIZE/2, GRAPH_SIZE/2 + i*GRAPH_SIZE);}
				for (int i = 0; i < ANN.HIDDEN_LAYERS; i++) {
					for (int j = 0; j < ANN.NODES_PER_LAYER; j++) {
						g.drawOval((int)(GRAPH_SIZE*1.25+GRAPH_SIZE*i),GRAPH_SIZE*j + GRAPH_SIZE/4,GRAPH_SIZE/2,GRAPH_SIZE/2);
						g.drawString(String.valueOf(hl[i][j]),(int)(GRAPH_SIZE*3/2 +GRAPH_SIZE*i),(int)(GRAPH_SIZE*1/2 +GRAPH_SIZE*j));}}
				for (int i = 0; i < ANN.OUTPUTS; i++) {
					g.drawOval(this.getWidth()-(int)(GRAPH_SIZE*3/4), GRAPH_SIZE/4+ i*GRAPH_SIZE,GRAPH_SIZE/2,GRAPH_SIZE/2);
					g.drawString(String.valueOf(ol[i]),this.getWidth()-(int)(GRAPH_SIZE*1/2), GRAPH_SIZE/2+ i*GRAPH_SIZE);}}}}

	//0=N, 1 = E, 2 = S, 3 = W
	public int[] getHeadingVector(int h) {
		if (h == 1) {
			return new int[]{0,-1};}
		else if (h == 2) {
			return new int[]{1,0};}
		else if (h == 3) {
			return new int[]{0,1};}
		else if (h == 4){
			return new int[]{-1,0};}
		else {
			return new int[] {-10,10};}}

	//gives the distances in primary directions from a given Snakey through interacting with the global grid
	public int[] sense(Snakey snakey) {
		updateGrid(snakey);
		int [] dists = new int[4];
		int [] head = new int[] {snakey.getSnake().get(0)[0],snakey.getSnake().get(0)[1]};
		for (int i = 1; i <= 4; i++) {
			int j = 1;
				while (head[0]+j*getHeadingVector(i)[0] >= 0 && head[0]+j*getHeadingVector(i)[0] < NUM_COLS && head[1]+j*getHeadingVector(i)[1] >= 0 && head[1]+j*getHeadingVector(i)[1] < NUM_ROWS) {
					if (grid[head[0]+j*getHeadingVector(i)[0]][head[1]+j*getHeadingVector(i)[1]] <= 0) {
						j++;}
					else {break;}}
			dists[i-1] = j;}
		//return dists;
		///mod1
		int [] dists1 = new int[3];
		for (int i = snakey.getHeading()-1; i <= snakey.getHeading()+1; i++) {
			int j = i;
			if (j ==5) {j =1;}
			else if (j ==0) {j =4;}
			dists1[i-snakey.getHeading()+1] = dists[j-1];}
		return dists1;
	}



}
