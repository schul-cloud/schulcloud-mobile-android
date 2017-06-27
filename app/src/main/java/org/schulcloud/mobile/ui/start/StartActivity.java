package org.schulcloud.mobile.ui.start;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.plattysoft.leonids.ParticleSystem;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.ui.base.BaseActivity;
import org.schulcloud.mobile.ui.files.FileActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StartActivity extends BaseActivity implements StartMvpView {

    private static final int PARTICLE_NUM = 100;
    private static final int PARTICLE_DURATION = 1000;
    private static final float PARTICLE_RANGE_BEGIN = 0.1f;
    private static final float PARTICLE_RANGE_END = 0.35f;
    private static final Integer[] PARTICLE_DRAWABLES = new Integer[]{
            R.mipmap.graycloud,
            R.mipmap.redcloud,
            R.mipmap.yellowcloud,
            R.mipmap.orangecloud
    };

    @BindView(R.id.cloudy_icon)
    AwesomeTextView cloudIcon;
    @BindView(R.id.start_layout)
    RelativeLayout startLayout;
    @BindView(R.id.text_logo)
    TextView logoText;

    /**
     * Return an Intent to start this Activity.
     * triggerDataSyncOnCreate allows disabling the background sync service onCreate. Should
     * only be set to false during testing.
     */
    public static Intent getStartIntent(Context context, boolean triggerDataSyncOnCreate) {
        Intent intent = new Intent(context, FileActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);

        this.showDisplayAnimation();
    }

    /***** MVP View methods implementation *****/

    @Override
    public void goToMain() {
        Handler handler = new Handler();
        Context context = this;
        handler.postDelayed(() -> {
            Intent intent = new Intent(context, FileActivity.class);
            context.startActivity(intent);
            finish();
        }, 500);
    }

    @Override
    public void showDisplayAnimation() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        startLayout.clearAnimation();
        startLayout.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // particle effect
                showParticleAnimation();

                // pulse effect
                Animation cloudAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.pulse);
                cloudAnim.reset();
                cloudIcon.clearAnimation();
                cloudIcon.startAnimation(cloudAnim);
                StartActivity.this.goToMain();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        cloudIcon.setTextColor(ContextCompat.getColor(this, R.color.hpiRed));
        cloudIcon.clearAnimation();
        cloudIcon.startAnimation(anim);
    }

    @Override
    public void showParticleAnimation() {
        for (Integer particleDrawable : PARTICLE_DRAWABLES) {
            new ParticleSystem(this, PARTICLE_NUM, particleDrawable, PARTICLE_DURATION)
                    .setSpeedRange(PARTICLE_RANGE_BEGIN, PARTICLE_RANGE_END)
                    .oneShot(cloudIcon, PARTICLE_NUM);
        }
    }

    @Override
    public void goToSignIn() {

    }


}
