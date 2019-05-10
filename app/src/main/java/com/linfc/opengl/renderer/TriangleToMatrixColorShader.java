package com.linfc.opengl.renderer;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.linfc.opengl.util.BufferCreateUitl;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * created by Linfc on 2019/5/
 * 基于矩阵得到三角形,彩色的
 */
public class TriangleToMatrixColorShader extends AbsRenderer {

    /**
     * 增加了一个aColor（顶点的颜色）作为输入量，传递给了vColor。
     */
    // 顶点着色器的脚本，带矩阵的带彩色的
    private final String verticesShader
            = "attribute vec4 vPosition;                        \n"// 顶点位置属性vPosition
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

    //顶点坐标 等腰三角形
    private float vertices[] = {
            0.0f, 0.5f, 0,
            -0.5f, -0.5f, 0,
            0.5f, -0.5f, 0,
    };


    //设置三个顶点颜色
    float color[] = {
            0.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f
    };

    //接收透视投影Matrix
    private float[] mFrustumMatrix = new float[16];
    //接收相机位置Matrix
    private float[] mCameraMatrix = new float[16];
    //转换过的矩阵数据
    private float[] mResultMatrix = new float[16];

    private int vPosition;
    private int mVMatrixHandler;
    private final FloatBuffer mVertices;
    private final FloatBuffer mColorvertices;
    private int mColorHandle;


    public TriangleToMatrixColorShader() {
        // 获取图形的顶点坐标
        mVertices = BufferCreateUitl.getFloatVertices(this.vertices);
        mColorvertices = BufferCreateUitl.getFloatVertices(color);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl,config);
        //获取着色器id 顶点着色器句柄vPosition   片元着色器uColor句柄, 矩阵变化vMatrix 句柄
        vPosition = GLES20.glGetAttribLocation(mProgram, "vPosition");  //GLES20.glGetAttribLocation()   获取attribute的值
        mVMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix");
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
        // 使用某套shader程序
        GLES20.glUseProgram(mProgram);
        //指定vMatrix的值
        GLES20.glUniformMatrix4fv(mVMatrixHandler, 1, false, mResultMatrix, 0);
        //启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(vPosition);
        //准备三角形的坐标数据
        GLES20.glVertexAttribPointer(vPosition, 3, GLES20.GL_FLOAT, false, 0, mVertices);

        //获取片元着色器的vColor成员的句柄
        mColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        //设置绘制三角形的颜色
        GLES20.glEnableVertexAttribArray(mColorHandle);
        GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false, 0, mColorvertices);
//        设置绘制三角形的颜色
//        GLES20.glUniform4f(uColor, 0.0f, 0.0f, 1.0f, 1.0f);
        //绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertices.length / 3);
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
