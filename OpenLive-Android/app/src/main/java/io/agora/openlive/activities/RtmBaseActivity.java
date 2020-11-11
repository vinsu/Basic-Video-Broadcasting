package io.agora.openlive.activities;

import android.content.Intent;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import io.agora.openlive.R;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmClient;
import io.agora.utils.MessageUtil;


public abstract class RtmBaseActivity extends BaseActivity {

    private final String TAG = RtmBaseActivity.class.getSimpleName();


    private RtmClient mRtmClient;
    private boolean mIsInChat = false;

    /**
     * API CALL: login RTM server
     */
    protected void doLoginRTM(String userId) {
        mIsInChat = true;
        mRtmClient.login(null, userId, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                Log.i(TAG, "login success");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                Log.i(TAG, "login failed: " + errorInfo.getErrorCode());
                runOnUiThread(() -> {
                    //mLoginBtn.setEnabled(true);
                    mIsInChat = false;
                    showToast(getString(R.string.login_failed));
                });
            }
        });
    }

    /**
     * API CALL: logout from RTM server
     */
    protected void doLogoutRTM() {
        mRtmClient.logout(null);
        MessageUtil.cleanMessageListBeanList();
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

}
