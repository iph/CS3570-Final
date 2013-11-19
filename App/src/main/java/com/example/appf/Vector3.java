package com.example.appf;

/**
 * Created by sean on 11/18/13.
 */
public class Vector3 {
    private final float[] arr = new float[3];
    public Vector3(float x, float y, float z){
        arr[0] = x;
        arr[1] = y;
        arr[2] = z;
    }

    public static Vector3 up(){
        return new Vector3(0, 1, 0);
    }

    public static Vector3 zero(){
        return new Vector3(0, 0, 0);
    }

    public Vector3 set(float x, float y, float z){
        arr[0] = x;
        arr[1] = y;
        arr[2] = z;
        return this;
    }

    public Vector3 add(Vector3 other){
        arr[0] += other.getX();
        arr[1] += other.getY();
        arr[2] += other.getZ();
        return this;
    }

    public Vector3 subtract(Vector3 other){
        arr[0] += other.getX();
        arr[1] += other.getY();
        arr[2] += other.getZ();
        return this;
    }

    public Vector3 clone(){
        return new Vector3(arr[0], arr[1], arr[2]);
    }

    public boolean equals(final Vector3 rhs)
    {
        return (arr[0] == rhs.getX()) && (arr[1] == rhs.getY()) && (arr[2] == rhs.getZ());
    }

    public Vector3 scale(float amt){
        arr[0] *= amt;
        arr[1] *= amt;
        arr[2] *= amt;
        return this;
    }
    public float dot(Vector3 other){
        return getX() * other.getX() + getY() * other.getY() + getZ() * other.getZ();
    }

    public Vector3 cross(Vector3 other){
        float x, y, z;
        x = this.getY() * other.getZ() - this.getZ() * other.getY();
        y = -(this.getX() * other.getZ() - this.getZ() * other.getX());
        z = this.getX() * other.getY() - this.getY() * this.getX();
        return new Vector3(x, y, z);


    }

    public Vector3 normalize(){
        float length = (float)Math.sqrt(arr[0]*arr[0] + arr[1]*arr[1] + arr[2]*arr[2]);
        arr[0] /= length;
        arr[1] /= length;
        arr[2] /= length;
        return this;
    }
    public static Vector3 scale_add(double factor, final Vector3 u, final Vector3 v)
    {
        return new Vector3((float)factor*u.getX() + v.getX(), (float)factor*u.getY() + v.getY(), (float)factor*u.getZ() + v.getZ());
    }

    public void multMat(float[] rotation){
        float[] new_arr = new float[4];

        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                new_arr[i] += arr[j] * rotation[i*4 + j];
            }
        }

        arr[0] = new_arr[0];
        arr[1] = new_arr[1];
        arr[2] = new_arr[2];
    }

    public float getX(){
        return arr[0];
    }

    public float getY(){
        return arr[1];
    }

    public float getZ(){
        return arr[2];
    }


    public void setX(float x){
        arr[0] = x;
    }

    public void setY(float y){
        arr[1] = y;
    }

    public void setZ(float z){
        arr[2] = z;
    }




}
