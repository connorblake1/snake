import java.util.ArrayList;

public class Snakey implements Constants {
    private int heading;
    private int[] apple;
    private ArrayList<int[]> snake;
    private ArrayList<Integer> headMovement;
    private ArrayList<int[]> appleMovement;
    private ANN net;
    private int generation;
    private boolean isAlive;
    private int score;
    static int snakeCap;
    static ArrayList<int[]> masterAppleList;
    public static void setMasterApples() {
        snakeCap = 50;
        masterAppleList = new ArrayList<int[]>();
        for (int i =0; i<snakeCap; i++) {
            masterAppleList.add(new int[] {(int)(2+Math.random()*(NUM_COLS-4)),(int)(2+Math.random()*(NUM_ROWS-4))});}}
    Snakey(int gen) {
        generation = gen;
        this.headMovement = new ArrayList<Integer>();
        this.appleMovement = new ArrayList<int[]>();
        headMovement.add(1);
        net = new ANN(gen);
        resetScore();
        revive();
}

    Snakey (Snakey snakey, int gen) {
        generation=gen;
        this.headMovement = new ArrayList<Integer>();
        this.appleMovement = new ArrayList<int[]>();
        this.net = new ANN(this.generation,snakey.getNet());
        resetScore();
        revive();
        }
    public void genBump() {
        generation++;
        net.genBump();}
    public void resetScore() {score = 0;}
    public void revive() {
        isAlive = true;
        heading = 1;
        this.snake = new ArrayList<int[]>();
        this.snake.add(new int[] {NUM_COLS/2,NUM_ROWS/2});
        resetApple(); //each new snake generated should start with a default apple
        }

    public void modify() {this.net.modify();}
    public int getHeading() {return heading;}
    public int getScore() {return score;}
    public void modSnakeValue(int index, int xoy, int val) {
        if (xoy ==0) {this.snake.set(index, new int[] {snake.get(index)[0]+val,snake.get(index)[1]});}
        else {this.snake.set(index, new int[] {snake.get(index)[0],snake.get(index)[1]+val});}}
    public void setSnakeValue(int index, int[] val) {this.snake.set(index, val);}
    public void addSnakeBit(int[] val) {this.snake.add(val);}
    public void addAppleBit() {this.appleMovement.add(apple);}
    public void addHeadBit() {this.headMovement.add(heading);}
    public void setHeading(int heading) {this.heading = heading;}
    public int[] getApple() {return apple;}
    public void setApple(int[] apple) {this.apple = apple;} //not used
    public ArrayList<int[]> getSnake() {return snake;}
    public ArrayList<Integer> getHeadMovement() {return headMovement;}
    public ArrayList<int[]> getAppleMovement() {return appleMovement;}
    public ANN getNet() {return net;}
    //public void setNet(ANN net) {this.net = net;} // not used
    public boolean isAlive() {return isAlive;}
    public void kill() {isAlive = false;}

    public int[] adjustedApple() {
        //return apple;
        //mod7
        int[]adjustedHeading=new int[2];
        if (heading % 2 ==0) { //left and right
            adjustedHeading[0] = -1*getHeadingVector()[0]*(apple[1]-snake.get(0)[1]);
            adjustedHeading[1] = getHeadingVector()[0]*(apple[0]-snake.get(0)[0]);}
        else { //up and down
            adjustedHeading[0] = apple[0]-snake.get(0)[0];
            adjustedHeading[1] = -1*getHeadingVector()[1]*(apple[1]-snake.get(0)[1]);}
        return adjustedHeading;
    }

    public int[] getHeadingVector() {
        if (heading == 1) {
            return new int[]{0,-1};}
        else if (heading == 2) {
            return new int[]{1,0};}
        else if (heading == 3) {
            return new int[]{0,1};}
        else {
            return new int[]{-1,0};}}

    public void resetApple() {
        apple = masterAppleList.get(score);
        //apple = new int[] {(int)(Math.random()*NUM_COLS),(int)(Math.random()*NUM_ROWS)};
        this.addAppleBit();}

    public void setNewHeading() {
        int newHeading = 0;
        float newWeight = -20000;
        //mod4
//        for (int i = 0; i <4; i++) {
//            if (net.getOL()[i] > newWeight) {
//                newWeight = net.getOL()[i];
//                newHeading=i+1;}}
//        if ((newHeading+4)%4 != (heading+2)%4) { //don't turn around
//            heading = newHeading;}
        for (int i = -1; i <= 1; i++) {
            if (net.getOL()[i+1] > newWeight) {
                newWeight = net.getOL()[i+1];
                newHeading=i;}}
        this.heading += newHeading;
        if (this.heading == 0) {this.heading=4;}
        else if (this.heading == 5) {
            this.heading=1;}
        }

    public void increment() {score++;}

}
