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

    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
                    "uniform mat4 rotationMatrix;" +

                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    // the matrix must be included as a modifier of gl_Position
                    "  gl_Position = uMVPMatrix * rotationMatrix * vPosition ;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    private final FloatBuffer vertexBuffer;
    private final ShortBuffer drawListBuffer;


    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float squareCoords[] = {
            -0.5f,  0.5f, 0.50f,
            -0.5f, -0.5f, 0.50f,
            0.5f, -0.5f, 0.50f,
            0.5f,  0.5f, 0.50f,

           -0.5f,  0.5f, -0.50f,
           -0.5f, -0.5f, -0.50f,
            0.5f, -0.5f, -0.50f,
            0.5f,  0.5f, -0.50f };

    private final short drawOrder[] = {
            0, 1, 2, 0, 2, 3,
            4, 5, 1, 4, 1, 0,
            3,2,6,3,6,7,
            7,4,5,7,5,6,
            3,7,4,3,4,0,
            2,6,5,2,1,5
    }; // order to draw vertices

    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 0.2f, 0.709803922f, 0.898039216f, 1.0f };
    @TargetApi(Build.VERSION_CODES.FROYO)
    public Cube() {
        Matrix.setIdentityM(rotation, 0);
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        // prepare shaders and OpenGL program
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables
    }


    @TargetApi(Build.VERSION_CODES.FROYO)
    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL environment
        float [] rotation_x = new float[16];
        float [] rotation_y = new float[16];
        float [] rotation_z = new float[16];
        float [] scratch1 = new float[16];
        Matrix.setRotateM(rotation_x, 0, axis_degrees[0], 1.0f, 0, 0);
        Matrix.setRotateM(rotation_y, 0, axis_degrees[1], 0, 1, 0);
        Matrix.setRotateM(rotation_z, 0, axis_degrees[2], 0.0f, 0, 1);

        Matrix.multiplyMM(scratch1, 0, rotation_y, 0, rotation_x, 0);
        Matrix.multiplyMM(rotation, 0, rotation_z, 0, scratch1, 0);



        GLES20.glUseProgram(mProgram);
        mRotationHandle = GLES20.glGetUniformLocation(mProgram, "rotationMatrix");
        MyGLRenderer.checkGlError("glGetUniformLocation");
        GLES20.glUniformMatrix4fv(mRotationHandle, 1, false, rotation, 0);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        MyGLRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");

        // Draw the square
        GLES20.glDrawElements(GLES20.GL_LINE_LOOP, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }


}

