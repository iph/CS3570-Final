package com.example.appf;

import android.annotation.TargetApi;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Build;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

class Tetra {

    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
                    "uniform mat4 rotationMatrix;" +
                    "attribute vec4 vColor;" +
                    "attribute vec4 vPosition;" +
                    "varying vec4 fColor;" +
                    "void main() {" +
                    // the matrix must be included as a modifier of gl_Position
                    "  gl_Position = uMVPMatrix * rotationMatrix * vPosition ;" +
                    " fColor = vColor;"+
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec4 fColor;" +
                    "void main() {" +
                    "  gl_FragColor = fColor;" +
                    "}";

    private final FloatBuffer vertexBuffer;
    private final FloatBuffer colorBuffer;
    private final ShortBuffer drawListBuffer;
    private final int mProgram;
    private int mRotationHandle;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    private float[] rotation = new float[16];
    private float[] axis_degrees = new float[3];

    // number of coordinates per vertex in this array
    static final int RGB_PER_VERTEX = 4;
    static final int COORDS_PER_VERTEX = 3;
    static float squareCoords[] = {
            1.0f,  -.661f, 0.0f,
            -0.5f, -0.661f, 0.866f,
            -0.5f, -0.661f, -0.866f,
            0.0f,  0.661f, 0.0f
    };

    static float colorCoords[] = {
            1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f
    };
    private final short drawOrder[] = {
            0,3,1,
            2,1,3,
            3,2,0,
            0,1,2
    }; // order to draw vertices

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    private final int colorStride = RGB_PER_VERTEX * 4;
    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 0.2f, 0.709803922f, 0.898039216f, 1.0f };
    @TargetApi(Build.VERSION_CODES.FROYO)
    public Tetra() {
        Matrix.setIdentityM(rotation, 0);
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
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


