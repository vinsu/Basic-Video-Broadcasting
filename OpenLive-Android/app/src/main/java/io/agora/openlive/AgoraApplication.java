package io.agora.openlive;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;



import com.pubnub.api.PubNub;

import com.google.gson.JsonObject;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.objects_api.channel.PNChannelMetadataResult;
import com.pubnub.api.models.consumer.objects_api.membership.PNMembershipResult;
import com.pubnub.api.models.consumer.objects_api.uuid.PNUUIDMetadataResult;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.pubnub.api.models.consumer.pubsub.PNSignalResult;
import com.pubnub.api.models.consumer.pubsub.files.PNFileEventResult;
import com.pubnub.api.models.consumer.pubsub.message_actions.PNMessageActionResult;

import java.util.Arrays;


import io.agora.openlive.rtc.EngineConfig;
import io.agora.openlive.rtc.AgoraEventHandler;
import io.agora.openlive.rtc.EventHandler;
import io.agora.openlive.stats.StatsManager;
import io.agora.openlive.utils.FileUtil;
import io.agora.openlive.utils.PrefManager;
import io.agora.rtc.RtcEngine;
import io.agora.rtm.ChatManager;

public class AgoraApplication extends Application {
    private RtcEngine mRtcEngine;
    private EngineConfig mGlobalConfig = new EngineConfig();
    private AgoraEventHandler mHandler = new AgoraEventHandler();
    private StatsManager mStatsManager = new StatsManager();

    //pubnub
    private PubNub m_pnMgr;
    final private String clientUUID = java.util.UUID.randomUUID().toString();
    private String m_channel;
    private String m_user ;
    private static final String TAG = AgoraApplication.class.getSimpleName();
    //end


    private static AgoraApplication sInstance;
    private ChatManager mChatManager;


    public static AgoraApplication the() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            mRtcEngine = RtcEngine.create(getApplicationContext(), getString(R.string.private_app_id), mHandler);
            mRtcEngine.setLogFile(FileUtil.initializeLogFile(this));
        } catch (Exception e) {
            e.printStackTrace();
        }

        initConfig();

        //vin
        sInstance = this;

        mChatManager = new ChatManager(this);
        mChatManager.init();

        pnOnCreate();
    }

    public ChatManager getChatManager() {
        return mChatManager;
    }
    public PubNub getPubNubManager() {
        return m_pnMgr;
    }
    //vin , end

    private void initConfig() {
        SharedPreferences pref = PrefManager.getPreferences(getApplicationContext());
        mGlobalConfig.setVideoDimenIndex(pref.getInt(
                Constants.PREF_RESOLUTION_IDX, Constants.DEFAULT_PROFILE_IDX));

        boolean showStats = pref.getBoolean(Constants.PREF_ENABLE_STATS, false);
        mGlobalConfig.setIfShowVideoStats(showStats);
        mStatsManager.enableStats(showStats);

        mGlobalConfig.setMirrorLocalIndex(pref.getInt(Constants.PREF_MIRROR_LOCAL, 0));
        mGlobalConfig.setMirrorRemoteIndex(pref.getInt(Constants.PREF_MIRROR_REMOTE, 0));
        mGlobalConfig.setMirrorEncodeIndex(pref.getInt(Constants.PREF_MIRROR_ENCODE, 0));
    }

    public EngineConfig engineConfig() {
        return mGlobalConfig;
    }

    public RtcEngine rtcEngine() {
        return mRtcEngine;
    }

    public StatsManager statsManager() {
        return mStatsManager;
    }

    public void registerEventHandler(EventHandler handler) {
        mHandler.addHandler(handler);
    }

    public void removeEventHandler(EventHandler handler) {
        mHandler.removeHandler(handler);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        RtcEngine.destroy();
    }


    //PubNub
    private void updateChFUser() {
        m_channel = mGlobalConfig.getChannelName();
        m_user = mGlobalConfig.getmUserId();
    }
    private void pnOnCreate() {

        //setContentView(R.layout.activity_pubn);

        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setPublishKey("pub-c-56e137d8-e0ee-4f93-95fb-b15a5f082348");
        pnConfiguration.setSubscribeKey("sub-c-1c2f62c2-1344-11eb-ae19-92aa6521e721");
        pnConfiguration.setUuid(clientUUID);

        m_pnMgr = new PubNub(pnConfiguration);



    } /* pnOnCreate */

}