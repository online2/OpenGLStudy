package com.linfc.opengl.renderer;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.linfc.opengl.util.BufferCreateUitl;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * created by Linfc on 2019/5/
 * 基于矩阵得到正方形,彩色的
 * 使用索引法绘制glDrawElements（）
 */
public class CubeToMatrixColorShader extends AbsRenderer {

    /**
     * 增加了一个aColor（顶点的颜色）作为输入量，传递给了vColor。
     */
    // 顶点着色器的脚本，带矩阵的带彩色的
    private final String verticesShader
            = "attribute vec4 vPosition;                        \n" // 顶点位置属性vPosition
            + "uniform mat4 vMatrix;                            \n"//增加矩阵变化
            + "varying  vec4 vColor;                            \n"
            + "attribute vec4 aColor;                           \n"
            + "void main(){                                     \n"
            + "   gl_Position = gl_Position = vMatrix*vPosition;\n" // 确定顶点位置
            + "   vColor=aColor;                                \n"
            + "}";

    // 片元着色器的脚本
    private final String fragmentShader
            = "precision mediump float;         \n" // 声明float类型的精度为中等(精度越高越耗资源)
            + "varying vec4 vColor;             \n" // 这边该成varying 因为颜色是从顶点着色器传过来的
            + "void main(){                     \n"
            + "   gl_FragColor = vColor;        \n" // 给此片元的填充色
            + "}";


    //正方体八个顶点坐标
    final float cubePositions[] = {
            -1.0f,1.0f,1.0f,    //正面左上0
            -1.0f,-1.0f,1.0f,   //正面左下1
            1.0f,-1.0f,1.0f,    //正面右下2
            1.0f,1.0f,1.0f,     //正面右上3
            -1.0f,1.0f,-1.0f,    //反面左上4
            -1.0f,-1.0f,-1.0f,   //反面左下5
            1.0f,-1.0f,-1.0f,    //反面右下6
            1.0f,1.0f,-1.0f,     //反面右上7
    };
//    正面由032和021两个三角形形组成，其他面诸如此类拆分，得到索引数组：
    final short index[]={
            0,3,2,0,2,1,    //正面
            0,1,5,0,5,4,    //左面
            0,7,3,0,4,7,    //上面
            6,7,4,6,4,5,    //后面
            6,3,7,6,2,3,    //右面
            6,5,1,6,1,2     //下面
    };


    //八个顶点的颜色，与顶点坐标一一对应
    float color[] = {
            0f,1f,0f,1f,
            0f,1f,0f,1f,
            0f,1f,0f,1f,
            0f,1f,0f,1f,
            1f,0f,0f,1f,
            1f,0f,0f,1f,
            1f,0f,0f,1f,
            1f,0f,0f,1f,
    };

    //接收透视投影Matrix
    private float[] mFrustumMatrix = new float[16];
    //接收相机位置Matrix
    private float[] mCameraMatrix = new float[16];
    //转换过的矩阵数据
    private float[] mResultMatrix = new float[16];

    private int vPosition;
    private int mVMatrixHandler;
    private final FloatBuffer mCubeVertices;
    private final FloatBuffer mColorvertices;
    private final ShortBuffer mIndexvertices;
    private int mColorHandle;


    public CubeToMatrixColorShader() {
        // 获取图形的顶点坐标缓存数据
        mCubeVertices = BufferCreateUitl.getFloatVertices(cubePositions);
        mColorvertices = BufferCreateUitl.getFloatVertices(color);
        mIndexvertices = BufferCreateUitl.getShortVertices(index);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl,config);
        //开启深度测试
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        //获取着色器id 顶点着色器句柄vPosition   片元着色器uColor句柄, 矩阵变化vMatrix 句柄
        vPosition = GLES20.glGetAttribLocation(mProgram, "vPosition");  //GLES20.glGetAttribLocation()   获取attribute的值
        mVMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        //获取片元着色器的vColor成员的句柄
        mColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //计算宽高比
        float ratio=(float)width/height;
        //设置透视投影
        Matrix.frustumM(mFrustumMatrix, 0, -ratio, ratio, -1, 1, 3, 20);
        //设置相机位置
        Matrix.setLookAtM(mCameraMatrix, 0, 5.0f, 5.0f, 10.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mResultMatrix,0,mFrustumMatrix,0,mCameraMatrix,0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        //指定vMatrix的值
        GLES20.glUniformMatrix4fv(mVMatrixHandler, 1, false, mResultMatrix, 0);
        //启用正方形顶点的句柄
        GLES20.glEnableVertexAttribArray(vPosition);
        //准备正方形的坐标数据
        GLES20.glVertexAttribPointer(vPosition, 3, GLES20.GL_FLOAT, false, 0, mCubeVertices);
        //设置绘制正方形的颜色
        GLES20.glEnableVertexAttribArray(mColorHandle);
        GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false, 0, mColorvertices);
        //顶点绘制法
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, index.length, GLES20.GL_UNSIGNED_SHORT,mIndexvertices);
        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(vPosition);
        GLES20.glDisableVertexAttribArray(mColorHandle);

    }


    @Override
    String getVerticesShader() {
        return verticesShader;
    }

    @Override
    String getFragmentShader() {
        return fragmentShader;
    }
}
