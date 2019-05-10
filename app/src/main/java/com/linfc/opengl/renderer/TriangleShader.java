package com.linfc.opengl.renderer;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.linfc.opengl.util.BufferCreateUitl;
import com.linfc.opengl.util.ProgramCreateUtil;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * created by Linfc on 2019/5/9
 * 三角形,
 */
public class TriangleShader implements GLSurfaceView.Renderer {

    // 顶点着色器的脚本
    private static final String verticesShader
            = "attribute vec4 vPosition;            \n" // 顶点位置属性vPosition
            + "void main(){                         \n"
            + "   gl_Position = vPosition;          \n" // 确定顶点位置
            + "}";

    // 片元着色器的脚本
    private static final String fragmentShader
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
    private int mProgram;
    private int vPosition;
    private int uColor;
    private final FloatBuffer mVertices;

    public TriangleShader() {
        // 获取图形的顶点坐标
        mVertices = BufferCreateUitl.getFloatVertices(this.vertices);
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //初始化着色器 + 创建程序
        mProgram = ProgramCreateUtil.createProgram(verticesShader, fragmentShader);
        //获取着色器id 顶点着色器句柄vPosition   片元着色器vColor句柄
        vPosition = GLES20.glGetAttribLocation(mProgram, "vPosition");
        uColor = GLES20.glGetUniformLocation(mProgram, "uColor");

        // 设置clear color颜色RGBA(这里仅仅是设置清屏时GLES20.glClear()用的颜色值而不是执行清屏)
        GLES20.glClearColor(1.0f, 1f, 1f, 1.0f);//背景色白色


    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // 设置绘图的窗口(可以理解成在画布上划出一块区域来画图)
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // 清屏
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        // 使用某套shader程序
        GLES20.glUseProgram(mProgram);
        // 为画笔指定顶点位置数据(vPosition）
        GLES20.glVertexAttribPointer(vPosition, 3, GLES20.GL_FLOAT, false, 0, mVertices);
        // 允许顶点数组的句柄
        GLES20.glEnableVertexAttribArray(vPosition);

        // 设置属性uColor(颜色 索引,R,G,B,A) 有多个重载方法
        GLES20.glUniform4f(uColor, 0.0f, 0.0f, 1.0f, 1.0f);
        //设置颜色，依次为红绿蓝和透明通道 rgba
        //float color[] = {1.0f, 1.0f, 1.0f, 1.0f};
        //GLES20.glUniform4fv(uColor, 1, color, 0);
        // 绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertices.length / 3);

        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(vPosition);

    }


    /**
     * 参数解析
     * glVertexAttribPointer（属性索引,单顶点大小,数据类型,归一化,顶点间偏移量,顶点Buffer）
     * index
     *  指定要修改的顶点属性的索引值
     * size 看给的顶点坐标是几纬的 如果是（x,y） 传2 如果是（x,y,z）传3
     *   指定每个顶点属性的组件数量。必须为1、2、3或者4。初始值为4。（如position是由3个（x,y,z）组成，而颜色是4个（r,g,b,a））
     * type
     *  指定数组中每个组件的数据类型。可用的符号常量有GL_BYTE, GL_UNSIGNED_BYTE, GL_SHORT,GL_UNSIGNED_SHORT, GL_FIXED, 和 GL_FLOAT，初始值为GL_FLOAT。
     * normalized
     *  指定当被访问时，固定点数据值是否应该被归一化（GL_TRUE）或者直接转换为固定点值（GL_FALSE）。
     * stride
     *  指定连续顶点属性之间的偏移量。如果为0，那么顶点属性会被理解为：它们是紧密排列在一起的。初始值为0。
     * pointer
     *  指定第一个组件在数组的第一个顶点属性中的偏移量。该数组与GL_ARRAY_BUFFER绑定，储存于缓冲区中。初始值为0；
     *
     *
     *
     * glDrawArrays(绘制方式, 起始偏移, 顶点数量)
     *      mode，绘制方式，OpenGL2.0以后提供以下参数：GL_POINTS、GL_LINES、GL_LINE_LOOP、GL_LINE_STRIP、GL_TRIANGLES、GL_TRIANGLE_STRIP、GL_TRIANGLE_FAN。
     *      first，从数组缓存中的哪一位开始绘制，一般为0。
     *      count，数组中顶点的数量。
     */
}
