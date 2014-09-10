package com.example.lib;

import com.example.wifilock.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 鑷畾涔塻witch
 * 
 * @author fancyy
 * 
 */
public class SwitchView extends LinearLayout {
	private ImageView maskImage;              // 寮�叧閬洊鍥剧墖
	private boolean open;                     // 寮�叧褰撳墠鐘舵�
	private boolean isAninFinish = true;      // 鍔ㄧ敾鏄惁缁撴潫
	private float x;                          // 璁板綍ACTION_DOWN鏃跺�鐨勬í鍧愭爣
	private boolean isChangedByTouch = false; // 鏄惁鍦ㄤ竴娆′簨浠朵腑宸茬粡鍒囨崲杩囩姸鎬�
	private OnSwitchChangeListener switchChangeListener; // 鐩戞帶寮�叧鐘舵�

	public interface OnSwitchChangeListener {
		void onSwitchChanged(boolean open);
	}

	public SwitchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SwitchView(Context context) {
		super(context);
		init();
	}

	private void init() {
		setBackgroundResource(R.drawable.bg);
		maskImage = new ImageView(getContext());
		maskImage.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		maskImage.setImageResource(R.drawable.mask);
		addView(maskImage);
	}

	public boolean getSwitchStatus() {
		return open;
	}

	public void setSwitchStatus(boolean isOpen) {
		this.open = isOpen;
		if (isOpen) {
			setGravity(Gravity.RIGHT);
		} else {
			setGravity(Gravity.LEFT);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			x = event.getX();
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			if (event.getX() - x > 5 && !open) { // 鍚戝彸
				changeStatus();
			} else if (event.getX() - x < -5 && open) { // 鍚戝乏
				changeStatus();
			}
			break;
		}
		case MotionEvent.ACTION_UP: {
			if (Math.abs(event.getX() - x) <= 5) {
				changeStatus();
			}
			isChangedByTouch = false;
			break;
		}
		case MotionEvent.ACTION_CANCEL: {
			isChangedByTouch = false;
			break;
		}
		}
		return true;
	}
	
	private void changeStatus() {
		if (isAninFinish && !isChangedByTouch) {
			isChangedByTouch = true;
			open = !open;
			isAninFinish = false;
			if (switchChangeListener != null) {
				switchChangeListener.onSwitchChanged(open);
			}
			changeOpenStatusWithAnim(open);
		}
	}

	private void changeOpenStatusWithAnim(boolean open) {
		if (open) {
			// 宸﹀埌鍙�
			Animation leftToRight = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, 
					Animation.ABSOLUTE, getWidth() - maskImage.getWidth(), 
					Animation.RELATIVE_TO_SELF, 0, 
					Animation.RELATIVE_TO_SELF, 0);
			leftToRight.setDuration(300);
			leftToRight.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					maskImage.clearAnimation();
					setGravity(Gravity.RIGHT);
					isAninFinish = true;
				}
			});
			maskImage.startAnimation(leftToRight);
		} else {
			// 鍙冲埌宸�
			Animation rightToLeft = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
					Animation.ABSOLUTE, maskImage.getWidth() - getWidth(), 
					Animation.RELATIVE_TO_SELF, 0, 
					Animation.RELATIVE_TO_SELF, 0);
			rightToLeft.setDuration(300);
			rightToLeft.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					maskImage.clearAnimation();
					setGravity(Gravity.LEFT);
					isAninFinish = true;
				}
			});
			maskImage.startAnimation(rightToLeft);
		}
	}

	public OnSwitchChangeListener getSwitchChangeListener() {
		return switchChangeListener;
	}

	public void setOnSwitchChangeListener(OnSwitchChangeListener switchChangeListener) {
		this.switchChangeListener = switchChangeListener;
	}
	
}
