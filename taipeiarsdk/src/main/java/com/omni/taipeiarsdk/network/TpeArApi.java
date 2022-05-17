package com.omni.taipeiarsdk.network;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.android.volley.Request;
import com.omni.taipeiarsdk.model.GetWtcFeedback;
import com.omni.taipeiarsdk.model.UserImageFeedback;
import com.omni.taipeiarsdk.model.mission.MissionFeedback;
import com.omni.taipeiarsdk.model.mission.RewardFeedback;
import com.omni.taipeiarsdk.model.tpe_location.CategoryFeedback;
import com.omni.taipeiarsdk.model.tpe_location.IndexFeedback;
import com.omni.taipeiarsdk.model.tpe_location.ThemeFeedback;
import com.omni.taipeiarsdk.pano.ArPattenFeedback;
import com.omni.taipeiarsdk.pano.ArPattensFeedback;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public class TpeArApi {

    private final String TAG = getClass().getName();
    private final int TIMEOUT = 25000;

    private static TpeArApi mTpeArApi;

    public static TpeArApi getInstance() {
        if (mTpeArApi == null) {
            mTpeArApi = new TpeArApi();
        }
        return mTpeArApi;
    }

    interface TyService {
        @Multipart
        @POST("api/upload_userimage")
        Call<UserImageFeedback> uploadUserImage(@Part MultipartBody.Part user_image,
                                                @Part("timestamp") RequestBody timestamp,
                                                @Part("mac") RequestBody mac);
    }

    private TyService getTyService() {
        return NetworkManager.getInstance().getRetrofit().create(TyService.class);
    }

    public void getPattenFile(Context context, NetworkManager.NetworkManagerListener<GetWtcFeedback> listener) {
        String url = NetworkManager.TPE_DOMAIN_NAME + "api/get_wtc";
        Map<String, String> params = new HashMap<>();

        NetworkManager.getInstance().addJsonRequest(context, Request.Method.GET, url, params, GetWtcFeedback.class, TIMEOUT, listener);
    }

    public void getPatternsContent(Context context, String pattern_id, String user_id, NetworkManager.NetworkManagerListener<ArPattensFeedback> listener) {
        //DialogTools.getInstance().showProgress(context);

        String url = NetworkManager.TPE_DOMAIN_NAME + "api/get_patterns_content";
        Map<String, String> params = new HashMap<>();
        params.put("pattern_id", pattern_id);
        params.put("user_id", user_id);

        NetworkManager.getInstance().addJsonRequest(context, Request.Method.GET, url, params, ArPattensFeedback.class, TIMEOUT, listener);
    }

    public void uploadUserImage(Activity activity, String img_path, NetworkManager.NetworkManagerListener<UserImageFeedback> listener) {
        long currentTimestamp = System.currentTimeMillis() / 1000L;
        String mac = NetworkManager.getInstance().getMacStr(currentTimestamp);

        String mediaType = "image/jpg";
        if (img_path.contains(".png")) {
            mediaType = "image/png";
        }
        File file = new File(img_path);
        RequestBody requestFile = RequestBody.create(MediaType.parse(mediaType), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("user_image", file.getName(), requestFile);

        RequestBody currentTimestamp_rb = RequestBody.create(MediaType.parse("text/plain"), currentTimestamp + "");
        RequestBody mac_rb = RequestBody.create(MediaType.parse("text/plain"), mac);

        Call<UserImageFeedback> call;
        call = getTyService().uploadUserImage(body, currentTimestamp_rb, mac_rb);

        NetworkManager.getInstance().addPostRequest(activity, call, UserImageFeedback.class, listener);
    }

    public void getSpecificPoi(Context context, String type, Location mLastLocation, NetworkManager.NetworkManagerListener<IndexFeedback> listener) {
        String url = NetworkManager.TPE_DOMAIN_NAME + "api/get_index";
        Map<String, String> params = new HashMap<>();
        params.put("type", type);
        params.put("user_lat", String.valueOf(mLastLocation.getLatitude()));
        params.put("user_lng", String.valueOf(mLastLocation.getLongitude()));
        params.put("radius", "2");

        NetworkManager.getInstance().addJsonRequest(context, Request.Method.GET, url, params, IndexFeedback.class, TIMEOUT, listener);
    }

    public void getTheme(Context context, NetworkManager.NetworkManagerListener<ThemeFeedback> listener) {
        String url = NetworkManager.TPE_DOMAIN_NAME + "api/get_index";
        Map<String, String> params = new HashMap<>();
        params.put("type", "topic");

        NetworkManager.getInstance().addJsonRequest(context, Request.Method.GET, url, params, ThemeFeedback.class, TIMEOUT, listener);
    }

    public void getThemeCategory(Context context, NetworkManager.NetworkManagerListener<CategoryFeedback> listener) {
        String url = NetworkManager.TPE_DOMAIN_NAME + "api/get_topic_category";
        Map<String, String> params = new HashMap<>();

        NetworkManager.getInstance().addJsonRequest(context, Request.Method.GET, url, params, CategoryFeedback.class, TIMEOUT, listener);
    }

    public void getMission(Context context, String type, String u_id, NetworkManager.NetworkManagerListener<MissionFeedback> listener) {
        String url = NetworkManager.TPE_DOMAIN_NAME + "api/get_mission";
        Map<String, String> params = new HashMap<>();
        params.put("type", type);
        params.put("u_id", u_id);

        NetworkManager.getInstance().addJsonRequest(context, Request.Method.GET, url, params, MissionFeedback.class, TIMEOUT, listener);
    }

    public void getReward(Context context, String type, String u_id, NetworkManager.NetworkManagerListener<RewardFeedback> listener) {
        String url = NetworkManager.TPE_DOMAIN_NAME + "api/get_mission";
        Map<String, String> params = new HashMap<>();
        params.put("type", type);
        params.put("u_id", u_id);

        NetworkManager.getInstance().addJsonRequest(context, Request.Method.GET, url, params, RewardFeedback.class, TIMEOUT, listener);
    }

}
