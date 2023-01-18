package com.kgaft.KGAFTEngine.Engine.Utils;

import com.bulletphysics.linearmath.Transform;
import org.joml.Matrix4f;


public class MatrixUtil {
    public static Matrix4f vecMathMatrixToJOMLMatrix(javax.vecmath.Matrix4f matrix4f){
        float[] tempResult = new float[4*4];
        new Transform(matrix4f).getOpenGLMatrix(tempResult);
        return new Matrix4f(tempResult[0], tempResult[1], tempResult[2], tempResult[3],tempResult[4],tempResult[5],tempResult[6],tempResult[7],tempResult[8],tempResult[9],tempResult[10],tempResult[11],tempResult[12],tempResult[13],tempResult[14],tempResult[15]);
    }

}
