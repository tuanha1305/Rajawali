package rajawali.animation;

import java.util.Timer;
import java.util.TimerTask;

import rajawali.ITransformable3D;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

public class Animation3D {
	public static final int INFINITE = -1;
	public static final int RESTART = 1;
	public static final int REVERSE = 2;
	
	protected long mDuration;
	protected Interpolator mInterpolator;
	protected int mRepeatCount;
	protected int mRepeatMode = RESTART;
	protected int mNumRepeats;
	protected int mDirection = 1;
	protected long mStartOffset;
	protected long mStartTime;
	protected long mDelay;
	protected long mUpdateRate = 1000/60;
	protected boolean mHasStarted;
	protected boolean mHasEnded;
	protected Animation3DListener mAnimationListener;
	protected Timer mTimer;
	protected ITransformable3D mTransformable3D;
	protected Animation3D mInstance;
	
	public Animation3D()
	{
		mInstance = this;
	}
	
	class UpdateTimeTask extends TimerTask {
		   public void run() {
		      long millis = System.currentTimeMillis() - mStartTime;
		      if(mDirection == -1) millis = mDuration - millis;
		      float interpolatedTime = mInterpolator.getInterpolation((float)millis / (float)mDuration);
		      
		      //Log.d(RajawaliRenderer.TAG, "interp " + interpolatedTime + ", " + (millis / mDuration));
		      applyTransformation(interpolatedTime);
		      if(mDirection == 1 && interpolatedTime >= 1 || mDirection == -1 && interpolatedTime <= 0)
		      {
		    	  if(mRepeatCount == mNumRepeats)
		    	  {
		    		  cancel();
		    		  if(mAnimationListener != null)  mAnimationListener.onAnimationEnd(mInstance);
		    	  }
		    	  else
		    	  {
		    		  if(mRepeatMode == REVERSE)
		    			  mDirection *= -1;
		    		  mStartTime = System.currentTimeMillis();
		    		  mNumRepeats++;
		    		  if(mAnimationListener != null)  mAnimationListener.onAnimationRepeat(mInstance);
		    	  }
		      }
		   }
		}
	
	public void cancel() {
		if(mTimer != null) {
			TimerManager.getInstance().killTimer(mTimer);
		}
	}
	
	public void reset() {
		mStartTime = System.currentTimeMillis();
		mNumRepeats = 0;
	}
	
	public void start() {
		if(mInterpolator == null)
			mInterpolator = new LinearInterpolator();
		reset();
		if(mTimer == null) mTimer = TimerManager.getInstance().createNewTimer();
		mTimer.scheduleAtFixedRate(new UpdateTimeTask(), mDelay, mUpdateRate);
		if(mAnimationListener != null)  mAnimationListener.onAnimationStart(this);
	}
	
	protected void applyTransformation(float interpolatedTime) {
		
	}
	
	public void setTransformable3D(ITransformable3D transformable3D) {
		mTransformable3D = transformable3D;
	}
	
	public void setAnimationListener(Animation3DListener animationListener) {
		mAnimationListener = animationListener;
	}
	
	public void setDuration(long duration) {
		mDuration = duration;
	}
	
	public long getDuration() {
		return mDuration;
	}
	
	public void setInterpolator(Interpolator interpolator) {
		mInterpolator = interpolator;
	}
	
	public Interpolator getInterpolator() {
		return mInterpolator;
	}
	
	public void setRepeatCount(int repeatCount) {
		mRepeatCount = repeatCount;
	}
	
	public int getRepeatCount() {
		return mRepeatCount;
	}
	
	public void setRepeatMode(int repeatMode) {
		mRepeatMode = repeatMode;
	}
	
	public int getRepeatMode() {
		return mRepeatMode;
	}

	public boolean isHasStarted() {
		return mHasStarted;
	}

	public void setHasStarted(boolean hasStarted) {
		this.mHasStarted = hasStarted;
	}

	public boolean isHasEnded() {
		return mHasEnded;
	}

	public void setHasEnded(boolean hasEnded) {
		this.mHasEnded = hasEnded;
	}

	public long getDelay() {
		return mDelay;
	}

	public void setDelay(long delay) {
		mDelay = delay;
	}

	public long getUpdateRate() {
		return mUpdateRate;
	}

	public void setUpdateRate(long updateRate) {
		this.mUpdateRate = updateRate;
	}
	
	public interface Animation3DListener {
		public void onAnimationEnd(Animation3D animation);
		public void onAnimationRepeat(Animation3D animation);
		public void onAnimationStart(Animation3D animation);
	}
}