import java.util.ArrayList;

public class Sky{
    public static int width, height;

    private static Boid[] swarm;
    private Boid ball;

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
        fillNewGridWithSwarm(swarm);
    }

    private void fillNewGridWithSwarm (Boid[] pSwarm) {
        for (Boid b : pSwarm) {
            double x = b.getxPos();
            double y = b.getyPos();
            int gridX = (int)(x/Boid.visibility);
            int gridY = (int)(y/Boid.visibility);
            boidGrid[gridX][gridY].add(b);
        }
        System.out.println("New Swarm filled in Grid.");
    }

    public void update() {
        for (Boid b:swarm) {
            b.flock(swarm);
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
            System.out.println(avg/i);
            i++;
        }
    }
}
