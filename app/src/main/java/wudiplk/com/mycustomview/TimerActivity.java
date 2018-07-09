package wudiplk.com.mycustomview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import wudiplk.com.mycustomview.custom_view.CountDownTimer;

public class TimerActivity extends AppCompatActivity {

    @BindView(R.id.cdvTimer)
    CountDownTimer cdvTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        ButterKnife.bind(this);
        // 自动开始
        cdvTimer.setOnFinishListener(new CountDownTimer.OnFinishListener() {
            @Override
            public void onFinish() {
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (cdvTimer != null) {
            cdvTimer.onStop();
        }
        super.onDestroy();
    }
}
