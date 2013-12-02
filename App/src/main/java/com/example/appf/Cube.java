package com.example.appf;

import android.annotation.TargetApi;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Build;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

class Cube extends Shape{



    // Set color with red, green, blue and alpha (opacity) values
    @TargetApi(Build.VERSION_CODES.FROYO)
    public Cube() {

        drawOrder = new short[]{
                0, 1, 2, 0, 2, 3,
                4, 5, 1, 4, 1, 0,
                3,2,6,3,6,7,
                7,4,5,7,5,6,
                3,7,4,3,4,0,
                2,6,5,2,1,5
        };
        coords = new float[]{
                -0.5f,  0.5f, 0.50f,
                -0.5f, -0.5f, 0.50f,
                0.5f, -0.5f, 0.50f,
                0.5f,  0.5f, 0.50f,

                -0.5f,  0.5f, -0.50f,
                -0.5f, -0.5f, -0.50f,
                0.5f, -0.5f, -0.50f,
                0.5f,  0.5f, -0.50f };

        Matrix.setIdentityM(rotation, 0);
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                coords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(coords);
        vertexBuffer.position(0);
        colorCoords = new float[]{
                0.2f, 0.709803922f, 0.898039216f, 1.0f,
                0.2f, 0.709803922f, 0.898039216f, 1.0f,
                0.2f, 0.709803922f, 0.898039216f, 1.0f,
                0.2f, 0.709803922f, 0.898039216f, 1.0f,
                0.2f, 0.709803922f, 0.898039216f, 1.0f,
                0.2f, 0.709803922f, 0.898039216f, 1.0f
        };

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

