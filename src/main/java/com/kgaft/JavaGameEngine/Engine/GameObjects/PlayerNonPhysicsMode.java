package com.kgaft.JavaGameEngine.Engine.GameObjects;

import com.kgaft.JavaGameEngine.Window.KeyBoardCallBack;
import com.kgaft.JavaGameEngine.Window.MouseMovementCallBack;
import com.kgaft.JavaGameEngine.Window.Window;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class PlayerNonPhysicsMode implements MouseMovementCallBack, KeyBoardCallBack {
    private List<NonPhysicMoveAbleObject> dependentObjects = new ArrayList<>();


    @Override
    public int[] getKeyCodes() {
        return new int[]{GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_ESCAPE, GLFW.GLFW_KEY_W, GLFW.GLFW_KEY_A, GLFW.GLFW_KEY_S, GLFW.GLFW_KEY_D, GLFW.GLFW_KEY_SPACE, GLFW.GLFW_KEY_C};
    }

    @Override
    public void keyPressed(int keyCodeId) {
        switch(keyCodeId){
            case GLFW.GLFW_KEY_ENTER:
                Window.getWindow().setCursorMode(Window.FIXED_HIDDEN_CURSOR_MODE);
                break;
            case GLFW.GLFW_KEY_ESCAPE:
                Window.getWindow().setCursorMode(Window.DYNAMIC_CURSOR);
                break;
            case GLFW.GLFW_KEY_W:
                currentCamera.move(1, 0, 0);
                break;
            case GLFW.GLFW_KEY_A:
                currentCamera.move(0, -1, 0);
                break;
            case GLFW.GLFW_KEY_S:
                currentCamera.move(-1, 0, 0);
                break;
            case GLFW.GLFW_KEY_D:
                currentCamera.move(0, 1, 0);
                break;
            case GLFW.GLFW_KEY_SPACE:
                currentCamera.move(0, 0, 1);
                break;
            case GLFW.GLFW_KEY_C:
                currentCamera.move(0, 0, -1);
                break;

        }
    }

    @Override
    public int getWorkMode() {
        return Window.FIXED_HIDDEN_CURSOR_MODE;
    }

    @Override
    public void mouseMoved(double x, double y) {
        dependentObjects.forEach(object->{
            object.rotate((float) x*10, (float)y*10);
        });
    }

    public void addDependentObject(NonPhysicMoveAbleObject object){
        dependentObjects.add(object);
    }

    public void removeDependentObject(NonPhysicMoveAbleObject object){
        dependentObjects.remove(object);
    }



}
