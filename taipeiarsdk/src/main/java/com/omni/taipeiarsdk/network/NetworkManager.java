package com.omni.taipeiarsdk.network;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.omni.taipeiarsdk.R;
import com.omni.taipeiarsdk.model.CommonArrayResponse;
import com.omni.taipeiarsdk.model.CommonResponse;
import com.omni.taipeiarsdk.tool.AeSimpleSHA1;
import com.omni.taipeiarsdk.tool.DialogTools;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.omni.taipeiarsdk.tool.TaipeiArSDKText.LOG_TAG;

public class NetworkManager {

    public interface NetworkManagerListener<T> {
        void onSucceed(T object);

        void onFail(VolleyError error, boolean shouldRetry);
    }

    public static final String NETWORKMANAGER_TAG = NetworkManager.class.getSimpleName();
    public static final String ERROR_MESSAGE_API_TIME_OUT = "Call API timeout";

//    public static final String TY_DOMAIN_NAME = "http://vrar360.dev.utobonus.com/";
    public static final String TY_DOMAIN_NAME = "https://vr-api.tycg.gov.tw/";

    //    public static final String TY_WEB_DOMAIN = "http://vrar360.utobonus.com/";
    public static final String TY_WEB_DOMAIN = "https://vr.tycg.gov.tw/";

    public static final String API_RESULT_TRUE = "true";
    private final int DEFAULT_TIMEOUT = 30000;
    public static final int TIME_OUT = 120;

    private static NetworkManager mNetworkManager;
    private RequestQueue mRequestQueue;
    private Gson mGson;
    private Retrofit mRetrofit;

    public static NetworkManager getInstance() {
        if (mNetworkManager == null) {
            mNetworkManager = new NetworkManager();
        }
        return mNetworkManager;
    }

    public boolean isNetworkAvailable(Context context) {
        if (context != null) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            boolean isNetworkEnable = (manager != null &&
                    manager.getActiveNetworkInfo() != null &&
                    manager.getActiveNetworkInfo().isConnectedOrConnecting());

            return isNetworkEnable;
        } else {
            return false;
        }
    }

    public RequestQueue getRequestQueue(Context context) {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context);
        }
        return mRequestQueue;
    }

    public String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public Gson getGson() {
        if (mGson == null) {
            mGson = new Gson();
        }
        return mGson;
    }

    public void setNetworkImage(Context context, NetworkImageView networkImageView, String url) {

        LruImageCache lruImageCache = LruImageCache.getInstance();

        ImageLoader imageLoader = new ImageLoader(getRequestQueue(context), lruImageCache);

        networkImageView.setDefaultImageResId(R.mipmap.logo_icon_small);
        networkImageView.setErrorImageResId(R.mipmap.slime_dark);
        networkImageView.setImageUrl(url, imageLoader);
    }

    public void setNetworkImage(Context context, NetworkImageView networkImageView, String url, @DrawableRes int defaultIconResId) {

        LruImageCache lruImageCache = LruImageCache.getInstance();

        ImageLoader imageLoader = new ImageLoader(getRequestQueue(context), lruImageCache);

        networkImageView.setDefaultImageResId(defaultIconResId);
        networkImageView.setErrorImageResId(R.mipmap.slime_dark);
        networkImageView.setImageUrl(url, imageLoader);
    }

    public <T> void addToRequestQueue(Request<T> req, String tag, Context context) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? NETWORKMANAGER_TAG : tag);
        getRequestQueue(context).add(req);
    }

    public <T> void addToRequestQueue(Request<T> req, Context context) {
        req.setTag(NETWORKMANAGER_TAG);
        getRequestQueue(context).add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public String getMacStr(double currentTimestamp) {
        try {
            return AeSimpleSHA1.SHA1("urappapp://" + currentTimestamp);
        } catch (NoSuchAlgorithmException e) {
            Log.e("LOG", "NoSuchAlgorithmException cause : " + e.getCause());
            return "";
        } catch (UnsupportedEncodingException e) {
            Log.e("LOG", "UnsupportedEncodingException cause : " + e.getCause());
            return "";
        }
    }

    public <T> void addJsonRequest(Context context,
                                   int requestMethod,
                                   final String url,
                                   Map<String, String> params,
                                   final Class<T> responseClass,
                                   final NetworkManagerListener<T> listener) {

        addJsonRequest(context, requestMethod, url, params, responseClass, DEFAULT_TIMEOUT, listener);
    }

    public <T> void addJsonRequest(final Context context,
                                   int requestMethod,
                                   final String url,
                                   Map<String, String> params,
                                   final Class<T> responseClass,
                                   int timeoutMs,
                                   final NetworkManagerListener<T> listener) {

        if (!isNetworkAvailable(context)) {
            DialogTools.getInstance().dismissProgress(context);
            DialogTools.getInstance().showNoNetworkMessage(context);
            return;
        }

        String paramsString = "";
        if (params != null) {
            for (String key : params.keySet()) {
                if (!TextUtils.isEmpty(paramsString)) {
                    paramsString = paramsString + "&";
                }
                paramsString = paramsString + key + "=" + params.get(key);
            }
        }

//        double currentTimestamp = System.currentTimeMillis() / 1000.0f;
//        String mac = getMacStr(currentTimestamp);
//
//        if (!TextUtils.isEmpty(paramsString)) {
//            paramsString = paramsString + "&";
//        }
//        paramsString = paramsString + "timestamp=" + currentTimestamp + "&mac=" + mac;

        final String requestUrl = (TextUtils.isEmpty(paramsString)) ? url : url + "?" + paramsString;
        Log.e("LOG", "requestUrl : " + requestUrl);
        JsonObjectRequest request = new JsonObjectRequest(requestMethod,
                requestUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        T object = getGson().fromJson(response.toString(), responseClass);
                        listener.onSucceed(object);


                        Log.w("LOG", "Q!!!onResponse: " + response.toString());

                        DialogTools.getInstance().dismissProgress(context);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            Log.e("LOG", "Error NetworkResponse statusCode === " + error.networkResponse.statusCode);
                        } else {
                            if (error.getClass().equals(TimeoutError.class)) {
                                Log.e("LOG", "*** Error NetworkResponse Timeout, timeMs : " + error.getNetworkTimeMs());
                                error = new VolleyError("Call API timeout");
                            }
                        }
                        listener.onFail(error, (TextUtils.isEmpty(error.getMessage()) && error.getCause() == null && error.networkResponse == null));

                        DialogTools.getInstance().dismissProgress(context);
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                timeoutMs,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        addToRequestQueue(request, context.getClass().getSimpleName(), context);
    }


    public <T> void addJsonRequestwithSHA(final Context context,
                                          int requestMethod,
                                          final String url,
                                          Map<String, String> params,
                                          final Class<T> responseClass,
                                          int timeoutMs,
                                          final NetworkManagerListener<T> listener) {

        if (!isNetworkAvailable(context)) {
            DialogTools.getInstance().dismissProgress(context);
            DialogTools.getInstance().showNoNetworkMessage(context);
            return;
        }

        String paramsString = "";
        if (params != null) {
            for (String key : params.keySet()) {
                if (!TextUtils.isEmpty(paramsString)) {
                    paramsString = paramsString + "&";
                }
                paramsString = paramsString + key + "=" + params.get(key);
            }
        }

        double currentTimestamp = System.currentTimeMillis() / 1000.0f;
        String mac = getMacStr(currentTimestamp);

        if (!TextUtils.isEmpty(paramsString)) {
            paramsString = paramsString + "&";
        }
        paramsString = paramsString + "timestamp=" + currentTimestamp + "&mac=" + mac;

        final String requestUrl = (TextUtils.isEmpty(paramsString)) ? url : url + "?" + paramsString;
        Log.e("LOG", "requestUrl : " + requestUrl);


        JsonObjectRequest request = new JsonObjectRequest(requestMethod,
                requestUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        T object = getGson().fromJson(response.toString(), responseClass);
                        listener.onSucceed(object);


                        Log.e("LOG", " SHA onResponse: " + response.toString());

                        DialogTools.getInstance().dismissProgress(context);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            Log.e("LOG", "Error NetworkResponse statusCode === " + error.networkResponse.statusCode);
                        } else {
                            if (error.getClass().equals(TimeoutError.class)) {
                                Log.e("LOG", "*** Error NetworkResponse Timeout, timeMs : " + error.getNetworkTimeMs());
                                error = new VolleyError("Call API timeout");
                            }
                        }
                        listener.onFail(error, (TextUtils.isEmpty(error.getMessage()) && error.getCause() == null && error.networkResponse == null));

                        DialogTools.getInstance().dismissProgress(context);
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                timeoutMs,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        addToRequestQueue(request, context.getClass().getSimpleName(), context);
    }


    public <T> void addJsonRequestToCommonObj(Context context,
                                              int requestMethod,
                                              final String url,
                                              Map<String, String> params,
                                              final Class<T[]> responseClass,
                                              final NetworkManagerListener<T[]> listener) {

        addJsonRequestToCommonObj(context, requestMethod, url, params, responseClass, DEFAULT_TIMEOUT, listener);
    }

    public <T> void addJsonRequestToCommonObj(final Context context,
                                              int requestMethod,
                                              final String url,
                                              Map<String, String> params,
                                              final Class<T[]> responseClass,
                                              int timeoutMs,
                                              final NetworkManagerListener<T[]> listener) {

        if (!isNetworkAvailable(context)) {
            DialogTools.getInstance().dismissProgress(context);
            DialogTools.getInstance().showNoNetworkMessage(context);
            return;
        }

        String paramsString = "";
        if (params != null) {
            for (String key : params.keySet()) {
                if (!TextUtils.isEmpty(paramsString)) {
                    paramsString = paramsString + "&";
                }
                paramsString = paramsString + key + "=" + params.get(key);
            }
        }

        double currentTimestamp = System.currentTimeMillis() / 1000.0f;
        String mac = getMacStr(currentTimestamp);
        if (!TextUtils.isEmpty(paramsString)) {
            paramsString = paramsString + "&";
        }
        paramsString = paramsString + "timestamp=" + currentTimestamp + "&mac=" + mac;

        final String requestUrl = (TextUtils.isEmpty(paramsString)) ? url : url + "?" + paramsString;
        Log.e("LOG", "requestUrl : " + requestUrl);
        JsonObjectRequest request = new JsonObjectRequest(requestMethod,
                requestUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        CommonResponse commonResponse = getGson().fromJson(response.toString(), CommonResponse.class);
                        if (commonResponse.getResult().equals(API_RESULT_TRUE)) {
                            String json = new Gson().toJson(commonResponse.getData());
                            T[] data = getGson().fromJson(json, responseClass);

                            listener.onSucceed(data);

                            DialogTools.getInstance().dismissProgress(context);
                        } else {
                            DialogTools.getInstance().dismissProgress(context);
                            DialogTools.getInstance().showErrorMessage(context, R.string.error_dialog_title_text_api, commonResponse.getErrorMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            Log.e("LOG", "Error NetworkResponse statusCode === " + error.networkResponse.statusCode);
                        } else {
                            if (error.getClass().equals(TimeoutError.class)) {
                                Log.e("LOG", "*** Error NetworkResponse Timeout, timeMs : " + error.getNetworkTimeMs());
                                error = new VolleyError("Call API timeout");
                            }
                        }
                        listener.onFail(error, (TextUtils.isEmpty(error.getMessage()) && error.getCause() == null && error.networkResponse == null));

                        DialogTools.getInstance().dismissProgress(context);
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                timeoutMs,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        addToRequestQueue(request, context.getClass().getSimpleName(), context);
    }

    public <T> void addPostStringRequest(final Context context,
                                         final String url,
                                         final Map<String, String> params,
                                         final Class<T> responseClass,
                                         int timeoutMs,
                                         final NetworkManagerListener<T> listener) {
        if (!isNetworkAvailable(context)) {
            DialogTools.getInstance().dismissProgress(context);
            DialogTools.getInstance().showNoNetworkMessage(context);
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("ASD", "url : " + url + ", response : " + response);

                        T object = getGson().fromJson(response, responseClass);
                        listener.onSucceed(object);

                        DialogTools.getInstance().dismissProgress(context);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            Log.e("LOG", "Error NetworkResponse statusCode === " + error.networkResponse.statusCode);
                        } else {
                            if (error.getClass().equals(TimeoutError.class)) {
                                Log.e("LOG", "*** Error NetworkResponse Timeout, timeMs : " + error.getNetworkTimeMs());
                            }
                        }
                        listener.onFail(error, (TextUtils.isEmpty(error.getMessage()) && error.getCause() == null && error.networkResponse == null));

                        DialogTools.getInstance().dismissProgress(context);
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                double currentTimestamp = System.currentTimeMillis() / 1000.0f;
                String mac = getMacStr(currentTimestamp);
                params.put("timestamp", "" + currentTimestamp);
                params.put("mac", mac);

                Log.e("LOG", "params : " + params.toString());
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                timeoutMs,
                1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        addToRequestQueue(request, context.getClass().getSimpleName(), context);
    }

    public <T> void addPostStringRequestToCommonObj(final Context context,
                                                    final String url,
                                                    @NonNull final Map<String, String> params,
                                                    final Class<T[]> responseClass,
                                                    int timeoutMs,
                                                    final NetworkManagerListener<T[]> listener) {
        if (!isNetworkAvailable(context)) {
            DialogTools.getInstance().dismissProgress(context);
            DialogTools.getInstance().showNoNetworkMessage(context);
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        CommonResponse commonResponse = getGson().fromJson(response.toString(), CommonResponse.class);
                        if (commonResponse.getResult().equals(API_RESULT_TRUE)) {
                            String json = new Gson().toJson(commonResponse.getData());
                            T[] data = getGson().fromJson(json, responseClass);

                            listener.onSucceed(data);

                            DialogTools.getInstance().dismissProgress(context);
                        } else {
                            DialogTools.getInstance().dismissProgress(context);
                            DialogTools.getInstance().showErrorMessage(context, R.string.error_dialog_title_text_api, commonResponse.getErrorMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            Log.e("LOG", "Error NetworkResponse statusCode === " + error.networkResponse.statusCode);
                        } else {
                            if (error.getClass().equals(TimeoutError.class)) {
                                Log.e("LOG", "*** Error NetworkResponse Timeout, timeMs : " + error.getNetworkTimeMs());
                            }
                        }
                        listener.onFail(error, (TextUtils.isEmpty(error.getMessage()) && error.getCause() == null && error.networkResponse == null));

                        DialogTools.getInstance().dismissProgress(context);
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                double currentTimestamp = System.currentTimeMillis() / 1000.0f;
                String mac = getMacStr(currentTimestamp);
                params.put("timestamp", "" + currentTimestamp);
                params.put("mac", mac);
                Log.e("LOG", "params : " + params.toString());
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                timeoutMs,
                1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        addToRequestQueue(request, context.getClass().getSimpleName(), context);
    }


    public <T> void addPostStringRequestToCommonArrayObj(final Context context,
                                                         final String url,
                                                         @NonNull final Map<String, String> params,
                                                         final Class<T[]> responseClass,
                                                         int timeoutMs,
                                                         final NetworkManagerListener<T[]> listener) {
        if (!isNetworkAvailable(context)) {
            if (!url.contains("get_beacon")) {
                DialogTools.getInstance().dismissProgress(context);
                DialogTools.getInstance().showNoNetworkMessage(context);
            }
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        CommonArrayResponse commonArrayResponse = getGson().fromJson(response.toString(), CommonArrayResponse.class);
                        if (commonArrayResponse.getResult().equals(API_RESULT_TRUE)) {
                            String json = getGson().toJson(commonArrayResponse.getData());

                            T[] data = getGson().fromJson(json, responseClass);

                            listener.onSucceed(data);

                            DialogTools.getInstance().dismissProgress(context);
                        } else {
                            if (url.contains("api/check_login") || url.contains("api/logout")) {
                                DialogTools.getInstance().dismissProgress(context);

                                listener.onFail(new VolleyError(commonArrayResponse.getErrorMessage()), false);

                            } else {
                                DialogTools.getInstance().dismissProgress(context);
                                DialogTools.getInstance().showErrorMessage(context, R.string.error_dialog_title_text_api, commonArrayResponse.getErrorMessage() + "\n" + url);
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            Log.e("LOG", "Error NetworkResponse statusCode === " + error.networkResponse.statusCode);
                        } else {
                            if (error.getClass().equals(TimeoutError.class)) {
                                Log.e("LOG", "*** Error NetworkResponse Timeout, timeMs : " + error.getNetworkTimeMs());
                                error = new VolleyError(ERROR_MESSAGE_API_TIME_OUT);
                            }
                        }
                        listener.onFail(error, (TextUtils.isEmpty(error.getMessage()) && error.getCause() == null && error.networkResponse == null));

                        DialogTools.getInstance().dismissProgress(context);
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                long currentTimestamp = System.currentTimeMillis() / 1000L;
                String mac = getMacStr(currentTimestamp);
                params.put("timestamp", "" + currentTimestamp);
                params.put("mac", mac);
//                Log.e("LOG", "params : " + params.toString());
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                timeoutMs,
                1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        addToRequestQueue(request, context.getClass().getSimpleName(), context);
    }




    private boolean checkNetworkStatus(Context context) {
        if (!isNetworkAvailable(context)) {
            DialogTools.getInstance().dismissProgress(context);
            DialogTools.getInstance().showNoNetworkMessage(context);
            return false;
        }
        return true;
    }

    private void sendAPIFailMessage(Context context, String errorMsg, NetworkManagerListener listener) {
        listener.onFail(new VolleyError(errorMsg), false);
    }

    private void sendResponseFailMessage(Context context, retrofit2.Response response, NetworkManagerListener listener) {
        String errorMsg = response.message();
        listener.onFail(new VolleyError(errorMsg), false);
    }

    public Retrofit getRetrofit() {

        if (mRetrofit == null) {
            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .build();

            mRetrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(TY_DOMAIN_NAME)
                    .callbackExecutor(Executors.newSingleThreadExecutor())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return mRetrofit;
    }

    public <T> void addPostRequestToCommonArrayObj(final Activity activity,
                                                   Call<CommonArrayResponse> call,
                                                   final Class<T[]> responseClass,
                                                   final NetworkManagerListener<T[]> listener) {

        addPostRequestToCommonArrayObj(activity, true, call, responseClass, listener);
    }

    public <T> void addPostRequestToCommonArrayObj(final Activity activity,
                                                   final boolean shouldDismissProgress,
                                                   Call<CommonArrayResponse> call,
                                                   final Class<T[]> responseClass,
                                                   final NetworkManagerListener<T[]> listener) {

        if (!checkNetworkStatus(activity)) {
            return;
        }

        call.enqueue(new Callback<CommonArrayResponse>() {
            @Override
            public void onResponse(Call<CommonArrayResponse> call, final retrofit2.Response<CommonArrayResponse> response) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccessful()) {

                            if (response.body() == null) {
                                Log.e(LOG_TAG, "#1");
                                listener.onFail(new VolleyError(activity.getString(R.string.error_dialog_title_text_unknown)), false);
                            } else {
                                Log.e(LOG_TAG, "response.body().getResult() : " + response.body().getResult() +
                                        "\nerrorMsg : " + response.body().getErrorMessage());
                                if (response.body().getResult().equals(API_RESULT_TRUE)) {
                                    String json = getGson().toJson(response.body().getData());
                                    T[] data = getGson().fromJson(json, responseClass);

                                    listener.onSucceed(data);

                                } else {
                                    Log.e(LOG_TAG, "#2");
                                    sendAPIFailMessage(activity, response.body().getErrorMessage(), listener);
                                }
                            }

                        } else {
                            Log.e(LOG_TAG, "#3 response : " + response);
                            sendResponseFailMessage(activity, response, listener);
                        }

                        if (shouldDismissProgress) {
                            DialogTools.getInstance().dismissProgress(activity);
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<CommonArrayResponse> call, final Throwable t) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(LOG_TAG, "#4 localized msg : " + t.getLocalizedMessage() + ", msg : " + t.getMessage() + ", cause : " + t.getCause());
                        listener.onFail(new VolleyError(activity.getString(R.string.dialog_message_network_connect_not_good)), false);

                        if (shouldDismissProgress) {
                            DialogTools.getInstance().dismissProgress(activity);
                        }
                        Log.e(LOG_TAG, "#5");
                    }
                });
            }
        });
    }

    public <T> void addPostRequestToCommonObj(final Activity activity,
                                              Call<CommonResponse> call,
                                              final Class<T> responseClass,
                                              final NetworkManagerListener<T> listener) {

        if (!checkNetworkStatus(activity)) {
            return;
        }

        call.enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, final retrofit2.Response<CommonResponse> response) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccessful()) {
                            Log.e(LOG_TAG, "#11");
                            if (response.body() == null) {
                                listener.onFail(new VolleyError(activity.getString(R.string.error_dialog_title_text_unknown)), false);
                            } else {
                                Log.e(LOG_TAG, "response.body().getResult() : " + response.body().getResult() +
                                        "\nerrorMsg : " + response.body().getErrorMessage());
                                if (response.body().getResult().equals(API_RESULT_TRUE)) {
                                    if (response.body().getData().toString().length() != 0) {
                                        String json = getGson().toJson(response.body().getData());
                                        T data = getGson().fromJson(json, responseClass);

                                        listener.onSucceed(data);
                                    }

                                } else {
                                    sendAPIFailMessage(activity, response.body().getErrorMessage(), listener);
                                }
                            }

                        } else {
                            sendResponseFailMessage(activity, response, listener);
                        }

                        DialogTools.getInstance().dismissProgress(activity);
                    }
                });
            }

            @Override
            public void onFailure(Call<CommonResponse> call, final Throwable t) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(LOG_TAG, "localized msg : " + t.getLocalizedMessage() + ", msg : " + t.getMessage() + ", cause : " + t.getCause());
                        listener.onFail(new VolleyError(activity.getString(R.string.dialog_message_network_connect_not_good)), false);
                        DialogTools.getInstance().dismissProgress(activity);
                    }
                });
            }
        });
    }

    public <T> void addPostRequest(final Activity activity,
                                   Call<T> call,
                                   final Class<T> responseClass,
                                   final NetworkManagerListener<T> listener) {
        addPostRequest(activity, true, call, responseClass, listener);
    }

    public <T> void addPostRequest(final Activity activity,
                                   final boolean shouldDismissProgress,
                                   Call<T> call,
                                   final Class<T> responseClass,
                                   final NetworkManagerListener<T> listener) {

        if (!checkNetworkStatus(activity)) {
            return;
        }

        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, final retrofit2.Response<T> response) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(LOG_TAG,"#1");
                        if (response.isSuccessful()) {
                            Log.e(LOG_TAG,"#2");
                            if (response.body() == null) {
                                Log.e(LOG_TAG,"#3");
                                listener.onFail(new VolleyError(activity.getString(R.string.error_dialog_title_text_unknown)), false);
                            } else {
                                Log.e(LOG_TAG,"#4");
                                listener.onSucceed(response.body());
                            }

                        } else {
                            Log.e(LOG_TAG,"#5");
                            sendResponseFailMessage(activity, response, listener);
                        }

                        if (shouldDismissProgress) {
                            Log.e(LOG_TAG,"#6");
                            DialogTools.getInstance().dismissProgress(activity);
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<T> call, final Throwable t) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(LOG_TAG,"#7");
                        listener.onFail(new VolleyError(activity.getString(R.string.dialog_message_network_connect_not_good)), false);

                        if (shouldDismissProgress) {
                            Log.e(LOG_TAG,"#8");
                            DialogTools.getInstance().dismissProgress(activity);
                        }
                    }
                });
            }
        });
    }

}
