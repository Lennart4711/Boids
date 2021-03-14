import java.util.ArrayList;
import java.util.ListIterator;

public class Sky{
    public static int width, height;

    private static Boid[] swarm;
    private Boid ball;
    private Boid[] subSwarm;

    private ArrayList[][] boidGrid;

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
        boidGrid = new ArrayList[width/Boid.visibility][height/Boid.visibility]; //One Grid is as large as the visibility

        for(int x = 0; x<boidGrid.length; x++) {
            for (int y=0; y<boidGrid[x].length; y++) {
                ArrayList<Boid> severalBoids = new ArrayList<Boid>(); //new List for each Grid field
                boidGrid[x][y] = severalBoids;
            }
        }
        System.out.println("Grid size is " + width/Boid.visibility + " x " + height/Boid.visibility);
        fillNewGridWithSwarm();
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
                            System.out.println(gridX + "; " + gridY);
                        }
                    }

                }

            }
        }

    }

    private Boid[] inRange(Boid pBoid) {

        ArrayList<Boid> tmpBoids = new ArrayList<Boid>();
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
                    listIterator = boidGrid[relGridX][relGridY].listIterator();
                    while(listIterator.hasNext()) {
                        tmpBoids.add(listIterator.next());
                    }
                } catch (ArrayIndexOutOfBoundsException aiobe) {
                    continue;
                    //System.out.println("raus");
                }
            }
        }
        System.out.println((System.nanoTime()-start+ "ns in range"));
        long start2 = System.nanoTime();
        Boid[] retB = tmpBoids.toArray(new Boid[tmpBoids.size()]);
        System.out.println(System.nanoTime()-start2+" array time");
        return retB;
    }


    public void update() {
        for (Boid b:swarm) {

            //choose boids in grids in visibility

            long start = System.nanoTime();
            //b.flock(inRange(b));
            b.flock(swarm);
            System.out.println(System.nanoTime()-start+" ns for flock \n");

            b.keepInBound();
            b.limitSpeed();
            // b.resetBounds();
            // b.flyToMagnet(ball);
            b.getBody().translateTo(b.getxPos(), b.getyPos());//moves the graphics object
            //b.getVec().translateTo(b.getxPos()+3.5, b.getyPos()+3.5, b.getxPos()+b.getdX()*6+3.5, b.getyPos()+b.getdY()*6+3.5); //moves velocity vector graphics object
            //updates the boids x,y based on the velocity
            b.setxPos(b.getxPos()+b.getdX());
            b.setyPos(b.getyPos()+b.getdY());
            
            //updateGrid();
        }
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
        Sky sky = new Sky(1600,2400, 3000);
        long lastTime;
        double avg = 0;
        int i = 1;

        while (true){
            lastTime = System.nanoTime();
            sky.update();
            avg += (1000000000.0 / (System.nanoTime() - lastTime));
            System.out.println(avg/i);
            i++;
        }
    }
}
