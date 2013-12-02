package com.example.appf;

import android.annotation.TargetApi;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Build;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

class Tetra extends Shape{




    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 0.2f, 0.709803922f, 0.898039216f, 1.0f };
    @TargetApi(Build.VERSION_CODES.FROYO)
    public Tetra() {
        Matrix.setIdentityM(rotation, 0);
        drawOrder = new short[]{
                0,3,1,
                2,1,3,
                3,2,0,
                0,1,2
        };
        colorCoords = new float[]{
            1.0f, 0.0f, 0.0f, 1.0f,
                    0.0f, 1.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f, 1.0f,
                    1.0f, 1.0f, 0.0f, 1.0f
        };
        coords = new float[]{
                1.0f,  -.661f, 0.0f,
                -0.5f, -0.661f, 0.866f,
                -0.5f, -0.661f, -0.866f,
                0.0f,  0.661f, 0.0f
        };

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                coords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(coords);
        vertexBuffer.position(0);


        ByteBuffer cb = ByteBuffer.allocateDirect(colorCoords.length * 4);
        cb.order(ByteOrder.nativeOrder());
        colorBuffer = cb.asFloatBuffer();
        colorBuffer.put(colorCoords);
        colorBuffer.position(0);
        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

              // create OpenGL program executables
    }



}


