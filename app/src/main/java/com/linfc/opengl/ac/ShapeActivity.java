package com.linfc.opengl.ac;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.linfc.opengl.R;
import com.linfc.opengl.adapter.ShapeSelectAdapter;
import com.linfc.opengl.renderer.CubeToMatrixColorShader;
import com.linfc.opengl.renderer.ImageShader;
import com.linfc.opengl.renderer.OvilToMatrixShader;
import com.linfc.opengl.renderer.TriangleShader;
import com.linfc.opengl.renderer.TriangleToMatrixColorShader;
import com.linfc.opengl.renderer.TriangleToMatrixShader;

import java.util.ArrayList;
import java.util.List;

public class ShapeActivity extends AppCompatActivity {

    private List<ShapeData> mData;
    private GLSurfaceView mGlSurfaceView;

    public static void toStart(Context context) {
        Intent intent = new Intent(context, ShapeActivity.class);
        context.startActivity(intent);
    }

    /**
     * OpenGL中着色器shader就相当于画笔，而顶点vertices相当于图形
     */

    private FrameLayout mRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shape);
        initData();
        initView();
        initOpenGL(new TriangleShader());

    }

    private void initData() {
        mData = new ArrayList<>();
        mData.add(new ShapeData("三角形", TriangleShader.class));
        mData.add(new ShapeData("三角形带缩放", TriangleToMatrixShader.class));
        mData.add(new ShapeData("三角形带颜色", TriangleToMatrixColorShader.class));
        mData.add(new ShapeData("圆形", OvilToMatrixShader.class));
        mData.add(new ShapeData("正方形", CubeToMatrixColorShader.class));
        mData.add(new ShapeData("图片", ImageShader.class));

    }

    private void initView() {
        mRootView = findViewById(R.id.glSurfaceView);
        RecyclerView rvListView = findViewById(R.id.rvListView);
        GridLayoutManager manager = new GridLayoutManager(this, 4);
        rvListView.setLayoutManager(manager);
        rvListView.setAdapter(new ShapeSelectAdapter(new ShapeSelectAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(int position) {
                try {
                    GLSurfaceView.Renderer renderer = (GLSurfaceView.Renderer) mData.get(position).mClass.newInstance();
                    if (renderer instanceof ImageShader) {
                        ImageShader imageShader = (ImageShader) renderer;
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fengj);
                        imageShader.setBitmap(bitmap);
                        initOpenGL(imageShader);
                    } else {
                        initOpenGL(renderer);
                    }

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }, mData));

    }

    private void initOpenGL(GLSurfaceView.Renderer renderer) {
        mRootView.removeAllViews();
        mGlSurfaceView = new GLSurfaceView(this);
        /* 以下是重点 */
        // 设置OpenGL版本(一定要设置)
        mGlSurfaceView.setEGLContextClientVersion(2);
        // 设置渲染器(后面会着重讲这个渲染器的类)
        mGlSurfaceView.setRenderer(renderer);
        // 设置渲染模式为连续模式(会以60fps的速度刷新)
        mGlSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        /* 重点结束 */

        mRootView.addView(mGlSurfaceView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    //防止我们在切换程序时，OpenGL还在绘制图形导致程序崩溃
    @Override
    protected void onPause() {
        super.onPause();
        if (mGlSurfaceView != null) {
            mGlSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGlSurfaceView != null) {
            mGlSurfaceView.onResume();
        }
    }


    public class ShapeData {
        String title;
        Class<?> mClass;

        public ShapeData(String title, Class<?> clazz) {
            this.title = title;
            mClass = clazz;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Class<?> getClasss() {
            return mClass;
        }

        public void setClass(Class<?> aClass) {
            mClass = aClass;
        }
    }
}
