package com.dong.scrollmenulayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;


/**
 * 低入侵的侧滑菜单控件
 * 一个HorizontalScrollView中包含一个水平的LinearLayout
 * 水平LinearLayout中包含两个FrameLayout，其中第一个为item布局的容器，第二个为侧滑菜单的布局容器
 * @author pd
 * time     2019/4/16 11:13
 */
public class ScrollMenuLayout extends HorizontalScrollView {
    private static final String TAG = "MenuItem";
    private FrameLayout container_item;//用于包裹Item布局的容器
    private FrameLayout container_menu_right;//用于包裹侧滑菜单布局的容器

    private float lastX;//记录上一次触摸的x轴的值，也就是横向的位置
    private float moveX;//累计自手指按下→移动→抬起，这个过程中移动的距离

    public ScrollMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setHorizontalScrollBarEnabled(false);//隐藏滚动条

        //获取xml文件中的属性
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ScrollMenuLayout);

        //new一个水平的线性布局容器，用来水平放置两个FrameLayout
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        //将两个容器实例化
        container_item = new FrameLayout(context);
        container_menu_right = new FrameLayout(context);

        //侧滑按钮的高度应该为填满父容器
        container_menu_right.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

        //如果在xml文件中设置了布局id，则直接加载
        int item_layout_id = array.getResourceId(R.styleable.ScrollMenuLayout_itemLayout, -1);
        if (item_layout_id != -1) {
            //将Item布局文件加载到FrameLayout中，注意使用LayoutInflater时，第一个参数为布局文件id，第二个参数为布局容器
            //由于我使用了addView的方式，所以第二个参数要填Null，否则会出现Item已经有父容器的错误
            container_item.addView(LayoutInflater.from(context).inflate(item_layout_id, null));
        }
        int menu_layout_right_id = array.getResourceId(R.styleable.ScrollMenuLayout_rightMenuLayout, -1);
        if (menu_layout_right_id != -1) {
            //同上的逻辑
            container_menu_right.addView(LayoutInflater.from(context).inflate(menu_layout_right_id, null));
        }

        //组装
        linearLayout.addView(container_item);
        linearLayout.addView(container_menu_right);
        addView(linearLayout);

        array.recycle();//回收属性数组
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //将Item的宽度设为父容器的宽度，用于将侧滑菜单顶出视野
        //放在onDraw执行是为了保证能获取到父容器的宽度，这里的父容器指的就是在Adapter中
        //onCreateViewHolder方法的第二个参数ViewGroup
        ViewGroup.LayoutParams layoutParams = container_item.getLayoutParams();
        layoutParams.width = ((ViewGroup) getParent()).getWidth();
        container_item.setLayoutParams(layoutParams);
        super.onDraw(canvas);
    }

    /**
     * 设置item布局
     *
     * @param v item布局view
     */
    public void setItemView(View v) {
        container_item.removeAllViews();
        container_item.addView(v);
    }

    /**
     * 获取item布局view
     * 方便去做各种监听等等
     *
     * @return Null or View
     */
    public View getItemView() {
        if (container_item.getChildCount() > 0) {
            return container_item.getChildAt(0);
        }
        return null;
    }

    /**
     * 设置右边的菜单
     *
     * @param v 右边菜单布局View
     */
    public void setRightMenuView(View v) {
        container_menu_right.removeAllViews();
        container_menu_right.addView(v);
    }

    /**
     * 获取右边的菜单布局View
     *
     * @return Null or View
     */
    public View getRightMenuView() {
        if (container_menu_right.getChildCount() > 0) {
            return container_menu_right.getChildAt(0);
        }
        return null;
    }

    /**
     * 展开右边菜单
     */
    public void expandRightMenu() {
        arrowScroll(FOCUS_RIGHT);
    }

    /**
     * 收起右边菜单
     */
    public void closeRightMenu() {
        arrowScroll(FOCUS_LEFT);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = ev.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                //记录移动距离
                moveX += ev.getX() - lastX;
                lastX = ev.getX();
                Log.d(TAG, "menu width = " + container_menu_right.getWidth() + " moveX = " + moveX);
                break;
            case MotionEvent.ACTION_UP:
                if (moveX > 0) {
                    //意图：收起，手指从左向右滑动
                    if (Math.abs(moveX) >= container_menu_right.getWidth() / 2) {
                        //滑动距离大于一半，收起
                        closeRightMenu();
                    } else {
                        //展开
                        expandRightMenu();
                    }
                } else if (moveX < 0) {
                    //意图展开，手指从右向左滑动
                    if (Math.abs(moveX) >= container_menu_right.getWidth() / 2) {
                        //展开
                        expandRightMenu();
                    } else {
                        //收起
                        closeRightMenu();
                    }
                }
                moveX = 0;//重置
                return true;//消费该次事件，不再传递，解决滑动冲突
        }
        return super.onTouchEvent(ev);
    }
}
