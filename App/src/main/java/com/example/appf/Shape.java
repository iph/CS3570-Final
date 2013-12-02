package com.example.appf;

import android.annotation.TargetApi;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Build;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by sean on 12/2/13.
 */
public class Shape {
    int modelHandle;
    int mPositionHandle;
    int mColorHandle;
    int mMVPMatrixHandle;

    FloatBuffer vertexBuffer;
    FloatBuffer colorBuffer;
    ShortBuffer drawListBuffer;
    int mProgram;
    short drawOrder[];

    final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
                    "uniform mat4 modelMatrix;" +
                    "attribute vec4 vColor;" +
                    "attribute vec4 vPosition;" +
                    "varying vec4 fColor;" +
                    "void main() {" +
                    // the matrix must be included as a modifier of gl_Position
                    "  gl_Position = uMVPMatrix * modelMatrix * vPosition ;" +
                    " fColor = vColor;"+
                    "}";

    final String fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec4 fColor;" +
                    "void main() {" +
                    "  gl_FragColor = fColor;" +
                    "}";

    // number of coordinates per vertex in this array
    static final int RGB_PER_VERTEX = 4;
    static final int COORDS_PER_VERTEX = 3;

    final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    final int colorStride = RGB_PER_VERTEX * 4;

    float coords[];
    float colorCoords[];

    float[] rotation = new float[16];
    float[] model = new float[16];
    boolean isModel = false;

    float [] translate_amt = new float[3];
    float[] axis_degrees = new float[3];

    public void rotate(float degrees, int axis_x, int axis_y, int axis_z){
        float[] scratch = new float[16];
        float[] old_rotate = rotation;
        float[] new_rotate = new float[16];
        Matrix.setRotateM(scratch, 0, degrees, axis_x, axis_y, axis_z);
        Matrix.multiplyMM(new_rotate, 0, old_rotate, 0, scratch, 0);
        rotation = new_rotate;

    }

    public void set_model_matrix(float[] new_model){
        model = new_model;
    }

    public void translate(float x, float y, float z){
        translate_amt[0] = x;
        translate_amt[1] = y;
        translate_amt[2] = z;
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



    @TargetApi(Build.VERSION_CODES.FROYO)
    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL environment
        float [] rotation_x = new float[16];
        float [] rotation_y = new float[16];
        float [] rotation_z = new float[16];
        float [] scratch1 = new float[16];
        if(!isModel){
            Matrix.setRotateM(rotation_x, 0, axis_degrees[0], 1.0f, 0, 0);
            Matrix.setRotateM(rotation_y, 0, axis_degrees[1], 0, 1, 0);
            Matrix.setRotateM(rotation_z, 0, axis_degrees[2], 0.0f, 0, 1);

            Matrix.multiplyMM(scratch1, 0, rotation_y, 0, rotation_x, 0);
            Matrix.multiplyMM(rotation, 0, rotation_z, 0, scratch1, 0);
            Matrix.setIdentityM(scratch1, 0);
            Matrix.translateM(scratch1, 0, translate_amt[0], translate_amt[1], translate_amt[2]);
            Matrix.multiplyMM(model, 0, rotation, 0, scratch1, 0);
        }


        GLES20.glUseProgram(mProgram);
        modelHandle = GLES20.glGetUniformLocation(mProgram, "modelMatrix");
        Shader.checkGlError("glGetUniformLocation");
        GLES20.glUniformMatrix4fv(modelHandle, 1, false, model, 0);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mColorHandle = GLES20.glGetAttribLocation(mProgram, "vColor");
        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        GLES20.glEnableVertexAttribArray(mColorHandle);
        GLES20.glVertexAttribPointer(mColorHandle, RGB_PER_VERTEX, GLES20.GL_FLOAT, false, colorStride, colorBuffer);
        // get handle to fragment shader's vColor member
        //mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        //GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        Shader.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        Shader.checkGlError("glUniformMatrix4fv");

        // Draw the square
        GLES20.glDrawElements(GLES20.GL_LINE_LOOP, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }


    public void setProgram(int mProgram){
        this.mProgram = mProgram;
    }
}
