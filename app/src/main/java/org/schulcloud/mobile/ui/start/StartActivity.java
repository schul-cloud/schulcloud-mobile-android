package org.schulcloud.mobile.ui.start;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.AwesomeTextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.ui.base.BaseActivity;
import org.schulcloud.mobile.ui.main.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StartActivity extends BaseActivity implements StartMvpView {

    @BindView(R.id.cloudy_icon)
    AwesomeTextView cloudIcon;
    @BindView(R.id.start_layout)
    RelativeLayout relativeLayout;
    @BindView(R.id.text_logo)
    TextView logoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);

        showDisplayAnimation();
    }

    /***** MVP View methods implementation *****/
    @Override
    public void goToMain() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            startActivity(new Intent(StartActivity.this, MainActivity.class));
            finish();
        }, 500);
    }

    @Override
    public void showDisplayAnimation() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        relativeLayout.clearAnimation();
        relativeLayout.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        anim.setAnimationListener(new Animation.AnimationListener() {
            boolean fired = false;

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (fired)
                    return;
                fired = true;

                Animation cloudAnim = AnimationUtils
                        .loadAnimation(getApplicationContext(), R.anim.pulse);
                cloudAnim.reset();
                cloudIcon.clearAnimation();
                cloudIcon.startAnimation(cloudAnim);
                goToMain();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        cloudIcon.setTextColor(ContextCompat.getColor(this, R.color.hpiRed));
        cloudIcon.clearAnimation();
        cloudIcon.startAnimation(anim);
    }
}
