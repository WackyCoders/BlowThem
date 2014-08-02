package items;

import poor2D.Matrix;
import poor2D.Operations;
import poor2D.Vector;

import static items.Constants.*;

/**
 * Created by alex on 7/13/14.
 */

public class TankCore {

    private Vector position = new Vector(0.0f, 0.0f);
    private Vector target = HORIZONTAL_VECTOR;

    private float speedFactor = 0.01f;

    public TankCore() {
    }

    public TankCore(Vector position, Vector target) {
        this.position = position;
        this.target = target;
    }

    public void setPosition(Vector position) {
        this.position = position;
    }

    public void setTarget(Vector target) {
        this.target = target;
    }

    public Vector getPosition() {
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

    public void setSpeedFactor(float factor){
        speedFactor = factor;
    }

    public float getSpeedFactor(){
        return speedFactor;
    }
    /**
     * The angle is measured in radians
     */
    public void turn(float degree) {
        target = Operations.multiply(Matrix.initRotationTransformation(degree), HORIZONTAL_VECTOR);
    }

    public void step() {

        Vector result = Operations.add(position, Operations.multiply(speedFactor, target));

        boolean mostCommonFirst = position.get(0) < SCREEN_END - EPSILON;
        boolean mostCommonSecond = position.get(1) < SCREEN_END - EPSILON;
        boolean mostCommonThird = position.get(0) > SCREEN_START + EPSILON;
        boolean mostCommonFourth = position.get(1) > SCREEN_START + EPSILON;

        if (mostCommonFirst && mostCommonSecond && mostCommonThird && mostCommonFourth){
            position = result;
            return;
        }

        boolean commonFirst = position.get(0) < SCREEN_START + EPSILON;
        boolean commonSecond = position.get(1) < SCREEN_START + EPSILON;
        boolean commonThird = target.get(0) < SCREEN_START;
        boolean commonFourth = target.get(1) < SCREEN_START;


        if (commonFirst &&  commonSecond && commonThird && commonFourth)
            return;

        if (commonSecond && commonFourth) {
            if (mostCommonFirst || commonThird)
                position = new Vector(result.get(0), SCREEN_START);
            return;
        }
        boolean lessCommonFirst = target.get(0) > SCREEN_START;
        boolean lessCommonSecond = position.get(0) > SCREEN_END - EPSILON;

        if ( lessCommonSecond && lessCommonFirst) {
            if (mostCommonSecond || commonFourth)
                position = new Vector(SCREEN_END, result.get(1));
            return;
        }

        boolean leastCommonFirst = target.get(1) > SCREEN_START;
        boolean leastCommonSecond = position.get(1) > SCREEN_END - EPSILON;

        if (leastCommonSecond && leastCommonFirst) {
            if (mostCommonThird || lessCommonFirst)
                position = new Vector(result.get(0), SCREEN_END);
            return;
        }

        if (commonFirst && commonThird) {
            if (mostCommonFourth || leastCommonFirst)
                position = new Vector(SCREEN_START, result.get(1));
            return;
        }

        position = result;
    }

    @Override
    public String toString() {
        return "Tank: Position: " + position + "; Target: " + target + ";";
    }
}
