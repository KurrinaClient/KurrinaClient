package cn.kuelcancel.kurrina.utils.mouse;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cn.kuelcancel.kurrina.utils.animation.simple.SimpleAnimation;

public class Scroll {

	private float maxScroll = Float.MAX_VALUE, minScroll = 0, rawScroll;
	private SimpleAnimation scrollAnimation = new SimpleAnimation(0.0F);
	
	public void onScroll() {
		onScroll(2);
	}
	
	public void onAnimation() {
		onAnimation(14);
	}
	
	private void onScroll(int scrollSpeed) {
		
		int dWheel = Mouse.getDWheel();
		
		if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
	        rawScroll += dWheel / scrollSpeed;
		}else {
	        rawScroll += dWheel / (scrollSpeed * 2);
		}

        rawScroll = Math.max(Math.min(minScroll, rawScroll), -maxScroll);
	}
	
	private void onAnimation(int animationSpeed) {
		scrollAnimation.setAnimation(rawScroll, animationSpeed);
	}
	
	public void setMaxScroll(float scroll) {
		maxScroll = scroll;
	}
	
	public float getValue() {
        return scrollAnimation.getValue();
	}
	
	public void setScrollPosition(float scroll) {
		rawScroll = scroll;
		scrollAnimation.setValue(scroll);
	}

	public void reset() {
		rawScroll = minScroll;
	}
	
	public void resetAll() {
		rawScroll = minScroll;
		scrollAnimation.setValue(minScroll);
	}
}