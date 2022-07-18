package com.omni.taipeiarsdk.network;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.android.volley.Request;
import com.omni.taipeiarsdk.model.CommonResponse;
import com.omni.taipeiarsdk.model.GetWtcFeedback;
import com.omni.taipeiarsdk.model.UserImageFeedback;
import com.omni.taipeiarsdk.model.geo_fence.GeoFenceResponse;
import com.omni.taipeiarsdk.model.mission.MissionCompleteFeedback;
import com.omni.taipeiarsdk.model.mission.MissionFeedback;
import com.omni.taipeiarsdk.model.mission.MissionGridFeedback;
import com.omni.taipeiarsdk.model.mission.MissionRewardFeedback;
import com.omni.taipeiarsdk.model.mission.RewardFeedback;
import com.omni.taipeiarsdk.model.tpe_location.CategoryFeedback;
import com.omni.taipeiarsdk.model.tpe_location.IndexFeedback;
import com.omni.taipeiarsdk.model.tpe_location.ThemeFeedback;
import com.omni.taipeiarsdk.pano.ArPattensFeedback;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
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

        @FormUrlEncoded
        @POST("api/get_geofence")
        Call<CommonResponse> getGeoFence(@Field("user_lat") double userLat,
                                         @Field("user_lng") double userLng,
                                         @Field("timestamp") String timestamp,
                                         @Field("mac") String mac);
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
        params.put("radius", "7");

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

    public void getMissionGrid(Context context, String m_id, String u_id, NetworkManager.NetworkManagerListener<MissionGridFeedback> listener) {
        String url = NetworkManager.TPE_DOMAIN_NAME + "api/get_nine_grid";
        Map<String, String> params = new HashMap<>();
        params.put("m_id", m_id);
        params.put("u_id", u_id);

        NetworkManager.getInstance().addJsonRequest(context, Request.Method.GET, url, params, MissionGridFeedback.class, TIMEOUT, listener);
    }

    public void getMissionComplete(Context context, String m_id, String ng_id, String u_id, NetworkManager.NetworkManagerListener<MissionCompleteFeedback> listener) {
        String url = NetworkManager.TPE_DOMAIN_NAME + "api/get_mission_complete";
        Map<String, String> params = new HashMap<>();
        params.put("m_id", m_id);
        params.put("ng_id", ng_id);
        params.put("u_id", u_id);

        NetworkManager.getInstance().addJsonRequest(context, Request.Method.GET, url, params, MissionCompleteFeedback.class, TIMEOUT, listener);
    }

    public void getMissionReward(Context context, String m_id, String u_id,
                                 String device_id, NetworkManager.NetworkManagerListener<MissionRewardFeedback> listener) {

        String url = NetworkManager.TPE_DOMAIN_NAME + "api/get_mission_reward";
        Map<String, String> params = new HashMap<>();
        params.put("m_id", m_id);
        params.put("u_id", u_id);
        params.put("device_id", device_id);

        NetworkManager.getInstance().addJsonRequest(context, Request.Method.GET, url, params, MissionRewardFeedback.class, TIMEOUT, listener);
    }

    public void getGeoFenceData(Activity activity, double userLat, double userLng,
                                NetworkManager.NetworkManagerListener<GeoFenceResponse> listener) {

        long currentTimestamp = System.currentTimeMillis() / 1000L;
        String mac = NetworkManager.getInstance().getMacStr(currentTimestamp);
        Call<CommonResponse> call = getTyService().getGeoFence(userLat, userLng,
                currentTimestamp + "",
                mac);
        Log.e("LOG","userLat"+userLat);
        Log.e("LOG","userLng"+userLng);
        NetworkManager.getInstance().addPostRequestToCommonObj(activity, call, GeoFenceResponse.class, listener);
    }
}
