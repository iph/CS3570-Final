package com.example.appf;


import android.annotation.TargetApi;
import android.opengl.Matrix;
import android.os.Build;

public class Camera{
    public static final int ROTATE_X = 1;
    public static final int ROTATE_Y = 2;
    public static final int ROTATE_Z = 4;

    boolean isOther;
    private float[] viewMatrix;

    private Vector3 m_position;
    private Vector3 m_nVector;
    private Vector3 m_uVector;
    private Vector3 m_vVector;


    public Camera(){
        isOther = false;
        m_position = Vector3.zero();
        m_nVector = Vector3.zero();
        m_uVector = Vector3.zero();
        m_vVector = Vector3.zero();

        m_position.set(0, 0, 10);
        m_nVector.set(0, 0, -10).normalize();
        m_vVector.set(0,1, 0).normalize();
        m_uVector = m_vVector.cross(m_nVector).normalize();


    }
    /**
     Returns the n vector of the camera.

     @return        ...think about it...
     */
    public Vector3 get_n()
    {
        return m_nVector;
    }

    /**
     Returns the position of the camera.

     @return        ...think about it...
     */
    public Vector3 get_position()
    {
        return m_position;
    }

    /**
     Returns the u vector of the camera.

     @return        ...think about it...
     */
    public Vector3 get_u()
    {
        return m_uVector;
    }

    /**
     Returns the v vector of the camera.

     @return        ...think about it...
     */
    public Vector3 get_v()
    {
        return m_vVector;
    }

    /**
     Moves the camera by the specified displacement in the n direction.

     @param delta        The displacement by which to move
     */
    public void move_n(double delta)
    {
        m_position = Vector3.scale_add(delta, m_nVector, m_position);
    }

    /**
     Moves the camera by the specified displacement in the u direction.

     @param delta        The displacement by which to move
     */
    public void move_u(double delta)
    {
        m_position = Vector3.scale_add(delta, m_uVector, m_position);
    }

    /**
     Moves the camera by the specified displacement in the v direction.

     @param delta        The displacement by which to move
     */
    public void move_v(double delta)
    {
        m_position = Vector3.scale_add(delta, m_vVector, m_position);
    }

    public void rotateX(double angle){
        rotate(m_uVector, angle);
    }

    public void rotateY(double angle){
        rotate(m_vVector, angle);
    }

    public void rotateZ(double angle){
        rotate(m_nVector, angle);
    }


    public void setViewMatrix(float[] v){
        isOther = true;
        viewMatrix = v;
    }
    public void rotate(Vector3 axis, double angle)
    {
        // Note: We try and optimise things a little by observing that there's no point rotating
        // an axis about itself and that generally when we rotate about an axis, we'll be passing
        // it in as the parameter axis, e.g. camera.rotate(camera.get_n(), Math.PI/2).
        if(axis != m_nVector) m_nVector = rotate_about_axis(m_nVector, angle, axis);
        if(axis != m_uVector) m_uVector = rotate_about_axis(m_uVector, angle, axis);
        if(axis != m_vVector) m_vVector = rotate_about_axis(m_vVector, angle, axis);
    }

    public static Vector3 rotate_about_axis(final Vector3 v, final double degreeAngle, final Vector3 axis)
    {
        // Check the preconditions.
        if(v == null || axis == null) throw new java.lang.Error();

        // Main algorithm
        double radianAngle = degreeAngle*Math.PI/180.0;
        double cosAngle = Math.cos(radianAngle), sinAngle = Math.sin(radianAngle);
        Vector3 aCROSSv = axis.cross(v);

        // ret = v cos radianAngle + (axis x v) sin radianAngle + axis(axis . v)(1 - cos radianAngle)
        // (See Mathematics for 3D Game Programming and Computer Graphics, P.62, for details of why this is (it's not very hard)).
        Vector3 ret = v.clone();
        ret.scale((float)cosAngle);
        ret = Vector3.scale_add(sinAngle, aCROSSv, ret);
        ret = Vector3.scale_add(axis.dot(v) * (1 - cosAngle), axis, ret);
        return ret;
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    public float[] getViewMatrix(){
        float[] mvMatrix = new float[16];
        if(!isOther){
            Matrix.setLookAtM(mvMatrix, 0, m_position.getX(), m_position.getY(), m_position.getZ(),
                m_position.getX() + m_nVector.getX(), m_position.getY() + m_nVector.getY(), m_position.getZ() + m_nVector.getZ(),
                m_vVector.getX(), m_vVector.getY(), m_vVector.getZ());
        }
        else{
            mvMatrix = viewMatrix;
        }
        return mvMatrix;
    }

}
