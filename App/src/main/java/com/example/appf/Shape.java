package com.example.appf;

import android.opengl.Matrix;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by sean on 12/2/13.
 */
public class Shape {
    int mRotationHandle;
    int mPositionHandle;
    int mColorHandle;
    int mMVPMatrixHandle;

    FloatBuffer vertexBuffer;
    FloatBuffer colorBuffer;
    ShortBuffer drawListBuffer;
    int mProgram;


    // number of coordinates per vertex in this array
    static final int RGB_PER_VERTEX = 4;
    static final int COORDS_PER_VERTEX = 3;

    final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    final int colorStride = RGB_PER_VERTEX * 4;

    float[] rotation = new float[16];
    float[] axis_degrees = new float[3];

    public void rotate(float degrees, int axis_x, int axis_y, int axis_z){
        float[] scratch = new float[16];
        float[] old_rotate = rotation;
        float[] new_rotate = new float[16];
        Matrix.setRotateM(scratch, 0, degrees, axis_x, axis_y, axis_z);
        Matrix.multiplyMM(new_rotate, 0, old_rotate, 0, scratch, 0);
        rotation = new_rotate;

    }

    public void pure_rotate(float degrees, int axis_x, int axis_y, int axis_z){
        float[] scratch = new float[16];
        float[] new_rotate = new float[16];
        if(axis_x == 1){
            axis_degrees[0] = degrees;
        }
        else if(axis_y == 1){
            axis_degrees[1] = degrees;
        }
        else if(axis_z == 1){
            axis_degrees[2] = degrees;
        }
        //Matrix.setRotateM(scratch, 0, degrees, axis_x, axis_y, axis_z);
        //rotation = scratch;
    }

}
