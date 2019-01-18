package vindicatedrt.com.myapplication.UI;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.View;

/**
 * UI类
 * 自动对焦TextureView类
 */
public class AutoFitTextureView extends TextureView {
    private int mRatioWidth = 0;
    private int mRatioHeight = 0;

    /**
     * 构造方法
     * @param context
     * @param attrs View声明的所有的属性
     */
    public AutoFitTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置宽高比方法
     * @param width
     * @param height
     */
    public void setAspectRatio(int width, int height) {
        mRatioWidth = width;
        mRatioHeight = height;
        requestLayout();//在view发生移动时,重新进行一次测量、布局、绘制
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height);
        } else {
            if (width < height * mRatioWidth / mRatioHeight) {
                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth);
            } else {
                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height);
            }
        }
    }
}
