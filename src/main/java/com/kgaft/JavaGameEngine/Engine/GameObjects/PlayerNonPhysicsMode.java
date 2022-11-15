package com.kgaft.JavaGameEngine.Engine.GameObjects;

import com.kgaft.JavaGameEngine.Window.KeyBoardCallBack;
import com.kgaft.JavaGameEngine.Window.MouseMovementCallBack;
import com.kgaft.JavaGameEngine.Window.Window;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class PlayerNonPhysicsMode implements MouseMovementCallBack, KeyBoardCallBack {

    private float mouseXSensitivity = 10;
    private float mouseYSensitivity = 10;

    private float xMovementSensitivity = 1;
    private float yMovementSensitivity = 1;
    private float zMovementSensitivity = 1;

    private List<NonPhysicMoveAbleObject> dependentObjects = new ArrayList<>();


    @Override
    public int[] getKeyCodes() {
        return new int[]{GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_ESCAPE, GLFW.GLFW_KEY_W, GLFW.GLFW_KEY_A, GLFW.GLFW_KEY_S, GLFW.GLFW_KEY_D, GLFW.GLFW_KEY_SPACE, GLFW.GLFW_KEY_C};
    }

    @Override
    public void keyPressed(int keyCodeId) {
        Vector3f moveVector = new Vector3f(0, 0, 0);
        switch(keyCodeId){
            case GLFW.GLFW_KEY_ENTER:
                Window.getWindow().setCursorMode(Window.FIXED_HIDDEN_CURSOR_MODE);
                break;
            case GLFW.GLFW_KEY_ESCAPE:
                Window.getWindow().setCursorMode(Window.DYNAMIC_CURSOR);
                break;
            case GLFW.GLFW_KEY_W:
                moveVector.y+=1;
                break;
            case GLFW.GLFW_KEY_A:
                moveVector.x+=1;
                break;
            case GLFW.GLFW_KEY_S:
                moveVector.y-=1;
                break;
            case GLFW.GLFW_KEY_D:
                moveVector.x-=1;
                break;
            case GLFW.GLFW_KEY_SPACE:
                moveVector.z+=1;
                break;
            case GLFW.GLFW_KEY_C:
                moveVector.z-=1;
                break;
        }
        dependentObjects.forEach(nonPhysicMoveAbleObject -> {
            nonPhysicMoveAbleObject.move(moveVector.y*yMovementSensitivity, moveVector.x*xMovementSensitivity, moveVector.z*zMovementSensitivity);
        });
    }

    @Override
    public int getWorkMode() {
        return Window.FIXED_HIDDEN_CURSOR_MODE;
    }

    @Override
    public void mouseMoved(double x, double y) {
        dependentObjects.forEach(object->{
            object.rotate((float) y*-1*mouseYSensitivity, (float)x*-1*mouseXSensitivity);
        });
    }

    public void addDependentObject(NonPhysicMoveAbleObject object){
        dependentObjects.add(object);
    }

    public void removeDependentObject(NonPhysicMoveAbleObject object){
        dependentObjects.remove(object);
    }

    public float getMouseXSensitivity() {
        return mouseXSensitivity;
    }

    public void setMouseXSensitivity(float mouseXSensitivity) {
        this.mouseXSensitivity = mouseXSensitivity;
    }

    public float getMouseYSensitivity() {
        return mouseYSensitivity;
    }

    public void setMouseYSensitivity(float mouseYSensitivity) {
        this.mouseYSensitivity = mouseYSensitivity;
    }

    public float getxMovementSensitivity() {
        return xMovementSensitivity;
    }

    public void setxMovementSensitivity(float xMovementSensitivity) {
        this.xMovementSensitivity = xMovementSensitivity;
    }

    public float getyMovementSensitivity() {
        return yMovementSensitivity;
    }

    public void setyMovementSensitivity(float yMovementSensitivity) {
        this.yMovementSensitivity = yMovementSensitivity;
    }

    public float getzMovementSensitivity() {
        return zMovementSensitivity;
    }

    public void setzMovementSensitivity(float zMovementSensitivity) {
        this.zMovementSensitivity = zMovementSensitivity;
    }
}
