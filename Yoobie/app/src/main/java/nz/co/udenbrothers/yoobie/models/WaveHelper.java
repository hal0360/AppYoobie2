package nz.co.udenbrothers.yoobie.models;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import java.util.ArrayList;
import java.util.List;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class WaveHelper {

    private WaveView mWaveView;
    private AnimatorSet mAnimatorSet;

    public WaveHelper(WaveView waveView) {
        mWaveView = waveView;
        mWaveView.setAmplitudeRatio(0.04f);
        mWaveView.setWaveLengthRatio(1.3f);
        mWaveView.setShapeType(WaveView.ShapeType.SQUARE);
        mWaveView.setShowWave(true);
    }

    public void endit(){
        mAnimatorSet.end();
    }

    public void paraAnimation(boolean rev){
        List<Animator> animators = new ArrayList<>();
        ObjectAnimator waveShiftAnim;
        if(rev){
            waveShiftAnim = ObjectAnimator.ofFloat(mWaveView, "waveShiftRatio", 0.4f, -0.9f);
        }
        else {
            waveShiftAnim = ObjectAnimator.ofFloat(mWaveView, "waveShiftRatio",  -0.9f, 0.4f);
        }
        waveShiftAnim.setRepeatCount(1);
        waveShiftAnim.setDuration(300);
        waveShiftAnim.setInterpolator(new LinearInterpolator());
        animators.add(waveShiftAnim);
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(animators);
        mAnimatorSet.start();
    }

    public void initAnimation(float down, float up) {
        List<Animator> animators = new ArrayList<>();
        ObjectAnimator waveShiftAnim;
        if(down > up){
             waveShiftAnim = ObjectAnimator.ofFloat(mWaveView, "waveShiftRatio", 0.4f, -0.9f);
        }
        else {
             waveShiftAnim = ObjectAnimator.ofFloat(mWaveView, "waveShiftRatio",  -0.9f, 0.4f);
        }
        waveShiftAnim.setRepeatCount(1);
        waveShiftAnim.setDuration(500);
        waveShiftAnim.setInterpolator(new LinearInterpolator());
        animators.add(waveShiftAnim);
        ObjectAnimator waterLevelAnim = ObjectAnimator.ofFloat(mWaveView, "waterLevelRatio", down, up);
        waterLevelAnim.setDuration(1000);
        waterLevelAnim.setInterpolator(new DecelerateInterpolator());
        animators.add(waterLevelAnim);
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(animators);
        mAnimatorSet.start();
    }
}
