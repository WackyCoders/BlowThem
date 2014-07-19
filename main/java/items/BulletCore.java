package items;

import poor2D.Operations;
import poor2D.Vector;

import java.util.ArrayList;
import java.util.List;
import static items.Constants.*;
/**
 * Created by alex on 7/15/14.
 */
public class BulletCore {

    

    static {
        bulletArray = new BulletCore[BULLETS];
    }

    public static BulletCore[] bulletArray;
    public Vector bulletPosition;
    public Vector bulletTarget;
    private int indexInList;

    private boolean flagOfFireRate = true;

    public void setFlagOfFireRate(boolean value){
        flagOfFireRate = value;
    }
    public boolean getFlagOfFireRate(){
        return flagOfFireRate;
    }

    public BulletCore(Vector position, Vector target) {
        bulletPosition = (Vector) position.clone();
        bulletTarget = (Vector) target.clone();

        for (int i = 0; i < bulletArray.length; ++i) {
            if (bulletArray[i] == null) {
                bulletArray[i] = this;
                indexInList = i;
                break;
            }
            assert i != bulletArray.length-1;
        }
    }

    private void destroy() {
        //bulletArray[indexInList] = null;
        flagOfFireRate = false;
    }

    public float getX() {
        return bulletPosition.get(0);
    }

    public float getY() {
        return bulletPosition.get(1);
    }

    public void bulletStep(float factorX, float factorY, float commonFactor) {
        if (getX() < SCREEN_SIZE && getY() < SCREEN_SIZE && getX() > 0 && getY() > 0)
            bulletPosition = Operations.add(bulletPosition, Operations.multiply(commonFactor, bulletTarget));//new Vector(bulletTarget.get(0) * factorY / factorX * commonFactor, bulletTarget.get(1) * commonFactor));
        else
            flagOfFireRate = false;
    }

    /*public static void commonBulletStep(float multiplier) {
        for (BulletCore element : bulletArray) {
            if (element != null) {
                if (element.getX() < SCREEN_SIZE && element.getY() < SCREEN_SIZE)
                    element.bulletStep(multiplier);
                else
                    element.destroy();
            }
        }
    }*/

    @Override
    public String toString(){
        return "Bullet" + indexInList + ": Position: " + bulletPosition + "; Target: " + bulletTarget + ";";
    }
}
