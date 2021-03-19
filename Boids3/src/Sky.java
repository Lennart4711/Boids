import java.util.LinkedList;
import java.util.ListIterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class Sky{
    public static int width, height;

    private static Boid[] swarm;
    private Boid ball;
    private Boid[] subSwarm;

    private LinkedList[][] boidGrid;

    private ThreadPoolExecutor threads;
    private static long activeTasks;

    public Sky(int height, int width, int boids) {
        //for Canvas size
        this.width = width;
        this.height = height;
        swarm = new Boid[boids];

        //ball = new Boid(width, height);
        for(int i =0; i<swarm.length; i++) {
            swarm[i] = new Boid(width, height);
        }

        //init Grid
        boidGrid = new LinkedList[width/Boid.visibility][height/Boid.visibility]; //One Grid is as large as the visibility

        for(int x = 0; x<boidGrid.length; x++) {
            for (int y=0; y<boidGrid[x].length; y++) {
                LinkedList<Boid> severalBoids = new LinkedList<Boid>(); //new List for each Grid field
                boidGrid[x][y] = severalBoids;
            }
        }
        System.out.println("Grid size is " + width/Boid.visibility + " x " + height/Boid.visibility);
        fillNewGridWithSwarm();

        threads = (ThreadPoolExecutor) Executors.newFixedThreadPool(8);
        //threads = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        activeTasks = 0;

    }


    private void fillNewGridWithSwarm () {
        for (Boid b : swarm) {
            double x = b.getxPos();
            double y = b.getyPos();
            int gridX = (int)(x/Boid.visibility);
            int gridY = (int)(y/Boid.visibility);
            boidGrid[gridX][gridY].add(b);
        }
        System.out.println("New Swarm filled in Grid.");
    }

    private void updateGrid () {
        for(int x = 0; x<boidGrid.length; x++) {
            for (int y=0; y<boidGrid[x].length; y++) {
                ListIterator<Boid> listIterator = boidGrid[x][y].listIterator();
                while(listIterator.hasNext()) {
                    Boid tmpBoid = listIterator.next();
                    double boidx = tmpBoid.getxPos();
                    double boidy = tmpBoid.getyPos();
                    if ((x == (int)(boidx/Boid.visibility)) && (y == (int)(boidy/Boid.visibility))) {
                        continue;
                    } else { //put into new gridelement
                        listIterator.remove();
                        int gridX =(int)(boidx/Boid.visibility);
                        int gridY = (int)(boidy/Boid.visibility);
                        try {
                            boidGrid[gridX][gridY].add(tmpBoid);
                        }catch (ArrayIndexOutOfBoundsException indexOutOfBoundsException){
                           // System.out.println(gridX + "; " + gridY);
                        }
                    }
                }
            }
        }
    }

    private Boid[] inRange(Boid pBoid) {

        LinkedList<Boid> tmpBoids = new LinkedList<Boid>();
        ListIterator<Boid> listIterator;
        //get Grid coord
        int gridx = (int)(pBoid.getxPos()/Boid.visibility);
        int gridy = (int)(pBoid.getyPos()/Boid.visibility);

        long start = System.nanoTime();
        for (int dx = -1; dx<2; dx++) {
            for(int dy = -1; dy<2; dy++){
                try {
                    int relGridX = gridx - dx;
                    int relGridY = gridy - dy;

                    if (relGridX>=0 && relGridY>=0) {
                        listIterator = boidGrid[relGridX][relGridY].listIterator();

                        while (listIterator.hasNext()) {
                            tmpBoids.add((Boid)listIterator.next());
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException aiobe) {
                    //System.out.println("raus");
                    continue;
                }
            }
        }
        //System.out.println((System.nanoTime()-start+ "ns in range"));
        long start2 = System.nanoTime();
        Boid[] retB = tmpBoids.toArray(new Boid[tmpBoids.size()]);
        //System.out.println(System.nanoTime()-start2+" array time");
        return retB;
    }

    public static synchronized void taskCompleted() {
        activeTasks += 1;
    }

    public static synchronized long getTasksCompleted(){
        return activeTasks;
    }

    public void update () throws InterruptedException {


        for (Boid b:swarm) {

            //choose boids in grids in visibility

            //long start = System.nanoTime();
            InRageSelector irs = new InRageSelector(b, boidGrid);
            threads.execute(irs);
            //irs = null;
           // b.flock(inRange(b));
            //b.flock(swarm);
            //System.out.println(System.nanoTime()-start+" ns for flock \n");

            //b.keepInBound();
            //b.limitSpeed();
            // b.resetBounds();
            // b.flyToMagnet(ball);
            //b.getBody().translateTo(b.getxPos(), b.getyPos());//moves the graphics object
            //b.getVec().translateTo(b.getxPos()+3.5, b.getyPos()+3.5, b.getxPos()+b.getdX()*6+3.5, b.getyPos()+b.getdY()*6+3.5); //moves velocity vector graphics object
            //updates the boids x,y based on the velocity
            //b.setxPos(b.getxPos()+b.getdX());
           //b.setyPos(b.getyPos()+b.getdY());
        }

        while (getTasksCompleted() < swarm.length) {};
        activeTasks = 0;

        /*
        for (Boid b:swarm) {
            b.getBody().translateTo(b.getxPos(), b.getyPos());//moves the graphics object
            //b.getVec().translateTo(b.getxPos()+3.5, b.getyPos()+3.5, b.getxPos()+b.getdX()*6+3.5, b.getyPos()+b.getdY()*6+3.5); //moves velocity vector graphics object
            //updates the boids x,y based on the velocity
            b.setxPos(b.getxPos()+b.getdX());
            b.setyPos(b.getyPos()+b.getdY());
        }*/

        updateGrid();
        //calcLeader();
        Canvas.getInstance().repaint();
    }

    public void calcLeader() {
        ball.setxPos(ball.getxPos()+ball.getdX());
        ball.setyPos(ball.getyPos()+ball.getdY());
        ball.limitSpeed();
        ball.keepInBound();
        ball.getBody().translateTo(ball.getxPos(), ball.getyPos());
        ball.getVec().translateTo(ball.getxPos()+3.5, ball.getyPos()+3.5, ball.getxPos()+ball.getdX()*6+3.5, ball.getyPos()+ball.getdY()*6+3.5);
    }

    public static void screenshot(String filename){
        Canvas.getInstance().saveToDisk("pics/"+filename);
    }

    public static void main(String[] args) {
        Sky sky = new Sky(960,1600, 8000);
        long lastTime;
        double avg = 0;
        int i = 1;

        while (true){
            lastTime = System.nanoTime();
            try {sky.update();} catch (Exception e) {};
            avg += (1000000000.0 / (System.nanoTime() - lastTime));
            if (i%10==0)
				System.out.println(avg/i);
            i++;
        }
    }
}
