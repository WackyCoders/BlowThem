package items;

import poor2D.Operations;
import poor2D.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 7/15/14.
 */
public class BulletCore {
    static {
        bulletArray = new BulletCore[20];
    }

    public static BulletCore[] bulletArray;
    private Vector bulletPosition;
    private Vector bulletTarget;
    private int indexInList;

    public BulletCore(Vector position, Vector target) {
        bulletPosition = (Vector) position.clone();
        bulletTarget = (Vector) target.clone();

        for (int i = 0; i < 10; ++i) {
            if (bulletArray[i] == null) {
                bulletArray[i] = this;
                indexInList = i;
                break;
            }
            assert i != 9;
        }
    }

    private void destroy() {
        bulletArray[indexInList] = null;
    }

    public float getX() {
        return bulletPosition.get(0);
    }

    public float getY() {
        return bulletPosition.get(1);
    }

    private void bulletStep(float multiplier) {
        bulletPosition = Operations.add(bulletPosition, Operations.multiply(multiplier, bulletTarget));
    }

    public static void commonBulletStep(float multiplier) {
        for (BulletCore element : bulletArray) {
            if (element != null) {
                if (element.getX() < 200 || element.getY() < 200)
                    element.bulletStep(multiplier);
                else
                    element.destroy();
            }
        }
    }

    @Override
    public String toString(){
        return "Bullet" + indexInList + ": Position: " + bulletPosition + "; Target: " + bulletTarget + ";";
    }
}
