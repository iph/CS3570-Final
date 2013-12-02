package com.example.appf;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.annotation.TargetApi;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Build;
import android.util.Log;
@TargetApi(Build.VERSION_CODES.FROYO)
public class MyGLRenderer implements GLSurfaceView.Renderer {

    volatile Tetra mTetra;
    volatile Cube mCube;
    volatile Shader mShader;
    volatile Camera mCamera = new Camera();
    private float[] mMVPMatrix = new float[16];
    private final float[] mProjMatrix = new float[16];
    private float[] mVMatrix = new float[16];

    // Declare as volatile because we are updating it from another thread
    @TargetApi(Build.VERSION_CODES.FROYO)
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        mTetra = new Tetra();
        mCube = new Cube();
        mShader = new Shader();
        mCube.setProgram(mShader.mProgram);
        mTetra.setProgram(mShader.mProgram);
        mCube.translate(1.0f, 0.0f, 0.0f);
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    @Override
    public void onDrawFrame(GL10 unused) {
        float[] scratch = new float[16];
        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Set the camera position (View matrix)
        //Matrix.setLookAtM(mVMatrix, 0, 0, 0, -10, 0f, 0f, 0f , 0f, 1.0f, 0.0f);
        mVMatrix = mCamera.getViewMatrix();
        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);

        //mCamera.rotate(10, Camera.ROTATE_Z);
        // Draw square
        mCube.draw(mMVPMatrix);
        mTetra.draw(mMVPMatrix);

    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 1, 20000);

    }

}
