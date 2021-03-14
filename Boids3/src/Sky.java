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
                ListIterator<Boid> listIterator = boidGrid[x][y].listIterator(boidGrid[x][y].size());
                while(listIterator.hasNext()) {
                    Boid tmpBoid = listIterator.previous();
                    double boidx = tmpBoid.getxPos();
                    double boidy = tmpBoid.getyPos();
                    if ((x == (int)(boidx/Boid.visibility)) && (y == (int)(boidy/Boid.visibility))) {
                        continue;
                    } else { //put into new gridelement
                        listIterator.remove();
                        boidGrid[(int)(boidx/Boid.visibility)][(int)(boidy/Boid.visibility)].add(tmpBoid);
                    }

                }

            }
        }

    }

    private Boid[] inRange(Boid pBoid) {

        ArrayList<Boid> tmpBoids = new ArrayList<>();
        //get Grid coord
        int gridx = (int)(pBoid.getxPos()/Boid.visibility);
        int gridy = (int)(pBoid.getyPos()/Boid.visibility);

        for (int dx = -1; dx<2; dx++) {
            for(int dy = -1; dy<2; dy++){
                try {
                    int relGridX = gridx - dx;
                    int relGridY = gridy - dy;
                    ListIterator<Boid> listIterator = boidGrid[relGridX][relGridY].listIterator(boidGrid[relGridX][relGridY].size());
                    while(listIterator.hasNext()) {
                        tmpBoids.add(listIterator.previous());
                    }
                } catch (ArrayIndexOutOfBoundsException aiobe) {
                    continue;
                    //System.out.println("raus");
                }
            }
        }
        return tmpBoids.toArray(new Boid[tmpBoids.size()]);
    }


    public void update() {
        for (Boid b:swarm) {

            //choose boids in grids in visibility


            b.flock(inRange(b));
            //b.flock(swarm);
            b.keepInBound();
            b.limitSpeed();
            // b.resetBounds();
            // b.flyToMagnet(ball);
            b.getBody().translateTo(b.getxPos(), b.getyPos());//moves the graphics object
            //b.getVec().translateTo(b.getxPos()+3.5, b.getyPos()+3.5, b.getxPos()+b.getdX()*6+3.5, b.getyPos()+b.getdY()*6+3.5); //moves velocity vector graphics object
            //updates the boids x,y based on the velocity
            b.setxPos(b.getxPos()+b.getdX());
            b.setyPos(b.getyPos()+b.getdY());
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
        Sky sky = new Sky(1200,2400, 1000);
        long lastTime;
        double avg = 0;
        int i = 1;

        while (true){
            lastTime = System.nanoTime();
            sky.update();
            avg += (1000000000.0 / (System.nanoTime() - lastTime));
            //System.out.println(avg/i);
            i++;
            System.out.println(swarm[123].getxPos());

        }
    }
}
