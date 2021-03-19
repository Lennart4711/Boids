import java.util.LinkedList;
import java.util.ListIterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InRageSelector implements Runnable {

    private Boid oneBoid;
    private LinkedList[][] boidGrid;

    public InRageSelector(Boid pBoid, LinkedList[][] pBoidGrid) {
        oneBoid = pBoid;
        boidGrid = pBoidGrid;
    }

    @Override
    public void run() {

        oneBoid.flock(inRange());
        oneBoid.keepInBound();
        oneBoid.limitSpeed();

        oneBoid.getBody().translateTo(oneBoid.getxPos(), oneBoid.getyPos());//moves the graphics object
        //b.getVec().translateTo(b.getxPos()+3.5, b.getyPos()+3.5, b.getxPos()+b.getdX()*6+3.5, b.getyPos()+b.getdY()*6+3.5); //moves velocity vector graphics object
        //updates the boids x,y based on the velocity
        oneBoid.setxPos(oneBoid.getxPos()+oneBoid.getdX());
        oneBoid.setyPos(oneBoid.getyPos()+oneBoid.getdY());
        Sky.taskCompleted();
    }

    private synchronized Boid[] inRange() {

        LinkedList<Boid> tmpBoids = new LinkedList<Boid>();
        ListIterator<Boid> listIterator;
        //get Grid coord
        int gridx = (int)(oneBoid.getxPos()/Boid.visibility);
        int gridy = (int)(oneBoid.getyPos()/Boid.visibility);

        long start = System.nanoTime();
        for (int dx = -1; dx<2; dx++) {
            for(int dy = -1; dy<2; dy++){
                try {
                    int relGridX = gridx - dx;
                    int relGridY = gridy - dy;

                    if (relGridX>=0 && relGridY>=0) {
                        listIterator = boidGrid[relGridX][relGridY].listIterator();

                        while (listIterator.hasNext()) {
                            tmpBoids.add(listIterator.next());
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
}
