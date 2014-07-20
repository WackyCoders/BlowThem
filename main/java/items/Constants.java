package items;

import poor2D.Vector;

/**
 * Created by alex on 7/17/14.
 */ 
class Constants {

    private Constants(){}

    public final static int BULLETS = 5;

    /*
    An abstract screen is of size 1.0x1.0
     */
    public final static float SCREEN_START = 0.0f;
    public final static float SCREEN_END = 1.0f;

    public final static Vector HORIZONTAL_VECTOR = new Vector(1.0f, 0.0f);
    public final static float EPSILON = 0.005f;


}
