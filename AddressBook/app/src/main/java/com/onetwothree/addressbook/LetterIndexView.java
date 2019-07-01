package com.onetwothree.addressbook;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LetterIndexView extends LinearLayout {

    /**
     * 上下文环境
     */
    private Context context;
    /**
     * 字母控件
     */
    private TextView[] lettersTxt = new TextView[28];
    /**
     * 触碰字母索引接口
     */
    private OnTouchLetterIndex touchLetterIndex;
    public LetterIndexView(Context context) {
        super(context);
        this.context = context;
    }

    public LetterIndexView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    /**
     * 初始化控件.
     */
    public void init(OnTouchLetterIndex touchLetterIndex) {
        this.touchLetterIndex = touchLetterIndex;
        this.setBackgroundColor(getResources().getColor(R.color.transparent));
        this.setOrientation(LinearLayout.VERTICAL);
        this.setGravity(Gravity.CENTER);
        //创建字母控件实例
        for (int i = 0; i < 28; i++) {
            lettersTxt[i] = new TextView(context);
            lettersTxt[i].setGravity(Gravity.CENTER);
            char tab = (char) (i + 63);
            if (i == 0)
                lettersTxt[i].setText("!");
            else if (i == 1)
                lettersTxt[i].setText("#");
            else
                lettersTxt[i].setText("" + tab);
            lettersTxt[i].setPadding(0, 0,0, 0);
            lettersTxt[i].setBackgroundColor(0xFF0000);
            lettersTxt[i].setTextSize(10);
            lettersTxt[i].setTextColor(Color.BLACK);
            LayoutParams letterParam = new LayoutParams(LayoutParams.WRAP_CONTENT, 0);
            letterParam.weight = 1;
            lettersTxt[i].setLayoutParams(letterParam);
            this.addView(lettersTxt[i]);
        }
        
        this.setOnTouchListener(new OnTouchListener() {
        	//移动y轴的距离
            private int y;
            //控件的高度
            private int height;
            //按到了哪个字母
            private String tab;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    	//按下时改变背景和字体颜色
                        setTextColor(Color.WHITE);
                        LetterIndexView.this.setBackgroundColor(getResources().getColor(R.color.ff7c7c7c));
                    case MotionEvent.ACTION_MOVE:
                    	// 获取触发事件点的纵坐标
                        y = (int) event.getY(); 
                        height = LetterIndexView.this.getHeight();
                        int location = (int) (y / (height / 28) + 0.5f);
                        if (location == 0) {
                            tab = "!";
                        } else if (location == 1) {
                            tab = "#";
                        } else if (location > 0 && location <= 27) {
                            tab = String.valueOf((char) (location + 63));
                        }
                        if (LetterIndexView.this.touchLetterIndex!=null) {
                        	//调用接口
                        	LetterIndexView.this.touchLetterIndex.touchLetterWitch(tab);
						}
                        break;
                    case MotionEvent.ACTION_UP:
                        LetterIndexView.this.setBackgroundColor(getResources().getColor(R.color.transparent));
                        if (LetterIndexView.this.touchLetterIndex!=null) {
                        	//调用接口
                        	LetterIndexView.this.touchLetterIndex.touchFinish();
						}
                        setTextColor(Color.BLACK);
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 设置字体颜色
     */
    private void setTextColor(int color) {
        for (int i = 0; i < 28; i++) {
            lettersTxt[i].setTextColor(color);
        }
    }

    /**
     * 触碰字母索引接口
     */
    public interface OnTouchLetterIndex {

        /**
         * 触摸字母空间接口.
         */
        void touchLetterWitch(String letter);

        /**
         * 结束查询
         */
        void touchFinish();

    }


}
