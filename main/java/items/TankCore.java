package items;

import android.util.Log;

import poor2D.Matrix;
import poor2D.Operations;
import poor2D.Vector; 

import java.util.List;
import static items.Constants.*;

/**
 * Created by alex on 7/13/14.
 */

public class TankCore {
    private Vector position = new Vector(0.0f, 0.0f);
    private Vector target = new Vector(1.0f, 0.0f);

    private Vector horizontal = new Vector(1.0f, 0.0f);


    public TankCore(){}

    public TankCore(Vector position, Vector target){
        this.position = position;
        this.target = target;
    }

    public void setPosition(Vector position){
        this.position = (Vector) position.clone();
    }

    public void setTarget(Vector target){
        this.target = (Vector) target.clone();
    }

    public Vector getPosition(){
        return (Vector) position.clone();
    }

    public Vector getTarget(){
        return (Vector) target.clone();
    }

    /**
     * The angle is measured in radians
     */
    public void turn(float degree){

        target = Operations.multiply(Matrix.initRotationTransformation(degree), horizontal);
    }

    public void setStartX(float startX) {
        Constants.startX = startX;
    }

    public void setStratY(float stratY) {
        Constants.startY = stratY;
    }

    public void step(float factorX, float factorY, float commonFactor){
        Vector result = Operations.add(position, /*new Vector(target.get(0) * factorY / factorX * commonFactor, target.get(1) * commonFactor));*/Operations.multiply(commonFactor, target));
        if (result.get(0) < SCREEN_SIZE && result.get(1) < SCREEN_SIZE && result.get(0) >= startX && result.get(1) >= startY){
            //Log.d(" SLDKJFVHEUBVISDHNJIN", Float.toString(result.get(0)) + " === " + Float.toString(result.get(1)));
            position = result;
        }
    }

    @Override
    public String toString(){
        return "Tank: Position: " + position + "; Target: " + target + ";";
    }
}
