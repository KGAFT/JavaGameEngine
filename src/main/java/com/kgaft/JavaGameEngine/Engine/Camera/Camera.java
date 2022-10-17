package com.kgaft.JavaGameEngine.Engine.Camera;


import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class Camera {
    private Vector3f position;
    private Vector3f up;
    private Vector3f orientation;

    public Camera(){
        position = new Vector3f();
        up = new Vector3f(0, 1, 0);
        orientation = new Vector3f(0, 0, -1);
    }

    public void moveCamera(float forwardBackWardSpeed, float leftRightSpeed, float upDownSpeed){
        Vector3f orientation = this.orientation;
        Vector3f up = this.up;
        position.add(orientation.mul(forwardBackWardSpeed));
        Vector3f tempVector = new Vector3f();
        tempVector = tempVector.normalize(orientation.cross(up));
        position.add(tempVector.mul(leftRightSpeed*-1));
        position.add(up.mul(upDownSpeed));
    }

    public void rotateCamera(float xSpeed, float ySpeed){
        Vector3f orientation = this.orientation;
        Vector3f up = this.up;
        Vector3f tempVector = new Vector3f();
        tempVector = tempVector.normalize(orientation.cross(up));
        Quaternionf quaternionf = new Quaternionf();
        quaternionf.setAngleAxis(ySpeed, 0, 1, 0);
        Vector3f newOrientation = orientation.rotate(quaternionf, tempVector);
        if(Math.abs(newOrientation.angle(up))<=90){
            orientation = newOrientation;
            quaternionf = new Quaternionf();
            quaternionf.setAngleAxis(xSpeed, 1, 0, 0);
            this.orientation = orientation.rotate(quaternionf, up);
        }

    }
    public Matrix4f getCameraMatrix(float fov, float nearPlane, float farPlane, float aspectRation){
        Vector3f position = this.position;
        Matrix4f view = new Matrix4f().identity();
        Matrix4f projection = new Matrix4f().identity();

        view = view.lookAt(this.position, position.add(orientation), up);
        projection = projection.perspective((float) Math.toRadians(fov), aspectRation, nearPlane, farPlane);
        return projection.mul(view);
    }
}
