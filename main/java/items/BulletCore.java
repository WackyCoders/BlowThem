package items;

import poor2D.Operations;
import poor2D.Vector;

import static items.Constants.*;
/**
 * Created by alex on 7/15/14.
 */
public class BulletCore {

    private Vector position;
    private Vector target;

    private boolean isAlive = true;
    private float speedFactor = 0.03f;

    public BulletCore(Vector position, Vector target) {
        this.position = position;
        this.target = target;
    }

    public void setAlive(boolean value){
        isAlive = value;
    }

    public boolean getAlive(){
        return isAlive;
    }

    public void setSpeedFactor(float factor){
        speedFactor = factor;
    }

    public float getSpeedFactor(){
        return speedFactor;
    }
    public Vector getPosition(){
        return (Vector) position.clone();
    }

    public float getX(){
        return position.get(0);
    }

    public float getY(){
        return position.get(1);
    }

    public Vector getTarget() {
        return (Vector) target.clone();
    }

    public void step(Vector enemyTankPosition) {
        if (position.get(0) < SCREEN_END && position.get(1) < SCREEN_END && position.get(0) > SCREEN_START && position.get(1) > SCREEN_START){
            if (Math.sqrt(Math.pow(position.get(0) - enemyTankPosition.get(0), 2.0) + Math.pow(position.get(1) - enemyTankPosition.get(1), 2.0)) < TANK_RADIUS)
                isAlive = false;
            else
                position = Operations.add(position, Operations.multiply(speedFactor, target));
        }
        else isAlive = false;
    }

    @Override
    public String toString(){
        return "Bullet: Position: " + position + "; Target: " + target + ";";
    }
}
