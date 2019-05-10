package com.linfc.opengl.renderer;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.linfc.opengl.util.BufferCreateUitl;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * created by Linfc on 2019/5/
 * 基于矩阵得到三角形
 */
public class TriangleToMatrixShader extends AbsRenderer {

    // 顶点着色器的脚本，带矩阵的
    private final String verticesShader
            = "attribute vec4 vPosition;                        \n" // 顶点位置属性vPosition
            + "uniform mat4 vMatrix;                            \n"//增加矩阵变化
            + "void main(){                                     \n"
            + "   gl_Position = gl_Position = vMatrix*vPosition;\n" // 确定顶点位置
            + "}";

    // 片元着色器的脚本
    private final String fragmentShader
            = "precision mediump float;         \n" // 声明float类型的精度为中等(精度越高越耗资源)
            + "uniform vec4 uColor;             \n" // uniform的属性uColor
            + "void main(){                     \n"
            + "   gl_FragColor = uColor;        \n" // 给此片元的填充色
            + "}";

    //顶点坐标 等腰三角形
    private float vertices[] = {
            0.0f, 0.5f, 0,
            -0.5f, -0.5f, 0,
            0.5f, -0.5f, 0,
    };

    //接收透视投影Matrix
    private float[] mFrustumMatrix = new float[16];
    //接收相机位置Matrix
    private float[] mCameraMatrix = new float[16];
    //转换过的矩阵数据
    private float[] mResultMatrix = new float[16];

    private int vPosition;
    private int uColor;
    private int mVMatrixHandler;
    private final FloatBuffer mVertices;


    public TriangleToMatrixShader() {
        // 获取图形的顶点坐标
        mVertices = BufferCreateUitl.getFloatVertices(this.vertices);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl,config);
        //获取着色器id 顶点着色器句柄vPosition   片元着色器uColor句柄, 矩阵变化vMatrix 句柄
        vPosition = GLES20.glGetAttribLocation(mProgram, "vPosition");  //GLES20.glGetAttribLocation()   获取attribute的值
        uColor = GLES20.glGetUniformLocation(mProgram, "uColor");       // GLES20.glGetUniformLocation() 获取uniform的值
        mVMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix");

        // 设置clear color颜色RGBA(这里仅仅是设置清屏时GLES20.glClear()用的颜色值而不是执行清屏)
        GLES20.glClearColor(1.0f, 1f, 1f, 1.0f);//背景色白色
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //计算宽高比
        float ratio = (float) width / height;
        //设置透视投影
        Matrix.frustumM(mFrustumMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        //设置相机位置
        Matrix.setLookAtM(mCameraMatrix, 0, 0, 0, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mResultMatrix, 0, mFrustumMatrix, 0, mCameraMatrix, 0);


    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        //指定vMatrix的值
        GLES20.glUniformMatrix4fv(mVMatrixHandler, 1, false, mResultMatrix, 0);
        //启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(vPosition);
        //准备三角形的坐标数据
        GLES20.glVertexAttribPointer(vPosition, 3, GLES20.GL_FLOAT, false, 0, mVertices);
        //设置绘制三角形的颜色
        GLES20.glUniform4f(uColor, 0.0f, 0.0f, 1.0f, 1.0f);
        //绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertices.length / 3);
        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(vPosition);
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
