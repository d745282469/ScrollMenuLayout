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
    private FrameLayout container_item, container_menu_right;
    private Context context;

    private float lastX, moveX;

    public ScrollMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setHorizontalScrollBarEnabled(false);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ScrollMenuLayout);

        this.context = context;
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        container_item = new FrameLayout(context);
        container_menu_right = new FrameLayout(context);

        //如果在xml文件中设置了布局id，则直接加载
        int item_layout_id = array.getResourceId(R.styleable.ScrollMenuLayout_itemLayout, -1);
        if (item_layout_id != -1) {
            container_item.addView(LayoutInflater.from(context).inflate(item_layout_id, null));
        }
        int menu_layout_right_id = array.getResourceId(R.styleable.ScrollMenuLayout_rightMenuLayout, -1);
        if (menu_layout_right_id != -1) {
            container_menu_right.addView(LayoutInflater.from(context).inflate(menu_layout_right_id, null));
        }

        //组装
        linearLayout.addView(container_item);
        linearLayout.addView(container_menu_right);
        addView(linearLayout);

        array.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //将Item的宽度设为父容器的宽度，用于将侧滑菜单顶出视野
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
                    //意图：收起
                    if (Math.abs(moveX) >= container_menu_right.getWidth() / 2) {
                        //滑动距离大于一半，收起
                        closeRightMenu();
                    } else {
                        //展开
                        expandRightMenu();
                    }
                } else if (moveX < 0) {
                    //意图展开
                    if (Math.abs(moveX) >= container_menu_right.getWidth() / 2) {
                        //展开
                        expandRightMenu();
                    } else {
                        //收起
                        closeRightMenu();
                    }
                }
                moveX = 0;
                return true;
        }
        return super.onTouchEvent(ev);
    }
}
