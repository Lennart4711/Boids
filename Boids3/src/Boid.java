import java.awt.*;
import java.util.Random;

public class Boid {
    private Random random = new Random();
    private double xPos, yPos, dX, dY;
    private int canvasHeight, canvasWidth;
    private Ellipse body;
    private Line vec;

    private final int margin = 150;//distance to edge to steer away from it
    private final double marginFactor = 0.5;//how hard the Boid turns away from edge
    private final int visibility = 80;//visual range of any Boid
    private final double alignmentFactor = 0.05;//how hard the Boid steers into same direction as other
    private final double cohesionFactor = 0.007;//how hard the Boids steers towards the center of all Boids in range
    private final int speedLimit = 7;
    private final int minSpace = 15;//distance to other Boid before this steers away
    private final double avoidanceFactor = 0.08;// how hard the Boid steers away from others

    public Boid(int width, int height) {
        xPos = random.nextDouble() * width;
        yPos = random.nextDouble() * height;
        dX = random.nextDouble()*10-5;
        dY = random.nextDouble()*10-5;
        canvasHeight = height;
        canvasWidth = width;

        body = new Ellipse(this.xPos, this.yPos, 7,7);
        body.setColor(new Color(random.nextInt(255)/2, random.nextInt(255), random.nextInt(255)));
        body.fill();
        /*
        vec = new Line(this.xPos+3.5, this.yPos+3.5, this.xPos+dX*4+3.5, this.yPos+dY*4+3.5);
        vec.setColor(new Color(134, 101, 101));
        vec.draw();
         */
    }

    public void flock(Boid[] boids) {
        double avgDX = 0;
        double avgDY = 0;

        double centerX = 0;
        double centerY = 0;
        int neighbors = 0;

        double avoidX = 0;
        double avoidY = 0;

        for (Boid b: boids) {
            if (this.distanceTo(b) < visibility) {
                //gets average direction(alignment)
                avgDX += b.dX;
                avgDY += b.dY;

                //calculates center of mass (cohesion)
                centerX += b.getxPos();
                centerY += b.getyPos();
                neighbors++;

                //steers away from nearby boids (separation)
                if (this.distanceTo(b) < minSpace) {
                    avoidX += this.xPos - b.xPos;
                    avoidY += this.yPos - b.yPos;
                }
            }
        }

        avgDX /= neighbors;
        avgDY /= neighbors;

        centerX /= neighbors;
        centerY /= neighbors;

        this.dX += (centerX - this.xPos)* cohesionFactor + (avgDX-this.dX)* alignmentFactor + avoidX* avoidanceFactor;
        this.dY += (centerY - this.yPos)* cohesionFactor + (avgDY-this.dY)* alignmentFactor + avoidY* avoidanceFactor;
    }

    public double distanceTo(Boid b) {
        return Math.sqrt(
                Math.pow(this.xPos-b.xPos, 2) + Math.pow(this.yPos-b.yPos,2));
    }

    public void keepInBound() {
        if (this.xPos < margin) {//left
            this.dX += marginFactor;
        }else if (this.xPos > canvasWidth - margin) {//right
            this.dX -= marginFactor;
        }
        if (this.yPos < margin) {//top
            this.dY += marginFactor;
        }else if (this.yPos > canvasHeight - margin) {//bottom
            this.dY -= marginFactor;
        }
    }

    public void resetBounds() {
        if(this.xPos<0){
            this.xPos = 1000;
        }else if(this.xPos>=1000){
            this.xPos = 0;
        }
        if(this.yPos<0){
            this.yPos = 1000;
        }else if(this.yPos>=1000){
            this.yPos = 0;
        }
    }

    public void limitSpeed() {
        double speed = Math.sqrt(Math.pow(this.dX, 2) + Math.pow(this.dY,2));

        if(speed > speedLimit) {
            this.dX = (this.dX/speed)*speedLimit;
            this.dY = (this.dY/speed)*speedLimit;
        }
    }

    public void flyToMagnet(Boid ball){
        if(this.distanceTo(ball)<visibility) {
            this.dX += (ball.getxPos() - this.xPos) * cohesionFactor * 5;
            this.dY += (ball.getyPos() - this.yPos) * cohesionFactor * 5;
        }
    }

    public double getxPos() {
        return xPos;
    }

    public double getyPos() {
        return yPos;
    }

    public void setxPos(double xPos) {
        this.xPos = xPos;
    }

    public void setyPos(double yPos) {
        this.yPos = yPos;
    }

    public double getdX() {
        return dX;
    }

    public double getdY() {
        return dY;
    }

    public Line getVec() {
        return vec;
    }

    public Ellipse getBody() {
        return body;
    }
}
