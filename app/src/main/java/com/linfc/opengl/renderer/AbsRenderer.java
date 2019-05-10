package com.linfc.opengl.renderer;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.linfc.opengl.util.ProgramCreateUtil;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * created by Linfc on 2019/5/9
 *
 */
public abstract class AbsRenderer implements GLSurfaceView.Renderer {
    protected int mProgram;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //初始化着色器 + 创建程序
        mProgram = ProgramCreateUtil.createProgram(getVerticesShader(),getFragmentShader());
        // 设置clear color颜色RGBA(这里仅仅是设置清屏时GLES20.glClear()用的颜色值而不是执行清屏)
        GLES20.glClearColor(1.0f, 1f, 1f, 1.0f);//背景色白色
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // 清屏
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        // 使用某套shader程序
        GLES20.glUseProgram(mProgram);
    }


    /**
     * 顶点着色器脚本
     * @return
     */
    abstract String getVerticesShader();

    /**
     * 片源着色器脚本
     * @return
     */
    abstract String getFragmentShader();

}
