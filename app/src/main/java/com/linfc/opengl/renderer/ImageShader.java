package com.linfc.opengl.renderer;


import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.linfc.opengl.util.BufferCreateUitl;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * created by Linfc on 2019/5/10
 * 绘制显示图片
 */
public class ImageShader extends AbsRenderer {

    private String verticesShade =
            "attribute vec4 vPosition;\n" +
            "attribute vec2 vCoordinate;\n" +
            "uniform mat4 vMatrix;\n" +
            "varying vec2 aCoordinate;\n" +
            "void main(){\n" +
            "    gl_Position=vMatrix*vPosition;\n" +
            "    aCoordinate=vCoordinate;\n" +
            "}\n";
    private String fragmentShader =
            "precision mediump float;\n" +
            "uniform sampler2D vTexture;\n" +
            "varying vec2 aCoordinate;\n" +
            "void main(){\n" +
            "    gl_FragColor=texture2D(vTexture,aCoordinate);\n"+
            "}\n";


    //顶点坐标
    private final float[] sPos = {
            -1.0f, 1.0f,    //左上角
            -1.0f, -1.0f,   //左下角
            1.0f, 1.0f,     //右上角
            1.0f, -1.0f     //右下角
    };

    //纹理坐标
    private final float[] sCoord = {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
    };

    private Bitmap mBitmap;
    //接收正交投影Matrix
    private float[] mProjectMatrix = new float[16];
    //接收相机位置Matrix
    private float[] mCameraMatrix = new float[16];
    //转换过的矩阵数据
    private float[] mResultMatrix = new float[16];
    private int vCoordinate;
    private int mVMatrixHandler;
    private int vTexture;
    private int vPosition;
    private FloatBuffer mVertices;
    private FloatBuffer mTextureVertices;

    public ImageShader() {
        mVertices = BufferCreateUitl.getFloatVertices(sPos);
        mTextureVertices = BufferCreateUitl.getFloatVertices(sCoord);
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        //获取着色器id 顶点着色器句柄vPosition   片元着色器uColor句柄, 矩阵变化vMatrix 句柄
        vCoordinate = GLES20.glGetAttribLocation(mProgram, "vCoordinate");  //GLES20.glGetAttribLocation()   获取attribute的值
        vPosition = GLES20.glGetAttribLocation(mProgram, "vPosition");
        vTexture = GLES20.glGetUniformLocation(mProgram, "vTexture");       // GLES20.glGetUniformLocation() 获取uniform的值
        mVMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix");


    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        int w = mBitmap.getWidth();
        int h = mBitmap.getHeight();
        float imageWHRatio = w / (float) h;//图片款高比
        float viewWHRatio = width / (float) height;//绘制view宽高比
        if (width > height) {
            if (imageWHRatio > viewWHRatio) {
                Matrix.orthoM(mProjectMatrix, 0, -viewWHRatio * imageWHRatio, viewWHRatio * imageWHRatio, -1, 1, 3, 7);
            } else {
                Matrix.orthoM(mProjectMatrix, 0, -viewWHRatio / imageWHRatio, viewWHRatio / imageWHRatio, -1, 1, 3, 7);
            }
        } else {
            if (imageWHRatio > viewWHRatio) {
                Matrix.orthoM(mProjectMatrix, 0, -1, 1, -1 / viewWHRatio * imageWHRatio, 1 / viewWHRatio * imageWHRatio, 3, 7);
            } else {
                Matrix.orthoM(mProjectMatrix, 0, -1, 1, -imageWHRatio / viewWHRatio, imageWHRatio / viewWHRatio, 3, 7);
            }
        }
        //设置相机位置
        Matrix.setLookAtM(mCameraMatrix, 0, 0, 0, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mResultMatrix, 0, mProjectMatrix, 0, mCameraMatrix, 0);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);

        GLES20.glUniformMatrix4fv(mVMatrixHandler, 1, false, mResultMatrix, 0);
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glEnableVertexAttribArray(vCoordinate);
        GLES20.glUniform1i(vTexture, 0);
        createTexture();
        //传入顶点坐标
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, mVertices);
        //传入纹理坐标
        GLES20.glVertexAttribPointer(vCoordinate, 2, GLES20.GL_FLOAT, false, 0, mTextureVertices);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }


    private int createTexture() {
        int[] texture = new int[1];
        if (mBitmap != null && !mBitmap.isRecycled()) {
            //生成纹理
            GLES20.glGenTextures(1, texture, 0);
            //生成纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
            return texture[0];
        }
        return 0;
    }

    @Override
    String getVerticesShader() {
        return verticesShade;
    }

    @Override
    String getFragmentShader() {
        return fragmentShader;
    }
}
