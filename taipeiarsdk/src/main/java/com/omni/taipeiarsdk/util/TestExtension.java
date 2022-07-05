package com.omni.taipeiarsdk.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.android.volley.VolleyError;
import com.omni.taipeiarsdk.ArchitectViewExtensionActivity;
import com.omni.taipeiarsdk.R;
import com.omni.taipeiarsdk.TaipeiArSDKActivity;
import com.omni.taipeiarsdk.model.GetWtcFeedback;
import com.omni.taipeiarsdk.model.OmniEvent;
import com.omni.taipeiarsdk.model.mission.MissionCompleteFeedback;
import com.omni.taipeiarsdk.network.NetworkManager;
import com.omni.taipeiarsdk.network.TpeArApi;
import com.omni.taipeiarsdk.pano.ArPattensFeedback;
import com.omni.taipeiarsdk.tool.DialogTools;
import com.wikitude.architect.ArchitectJavaScriptInterfaceListener;
import com.wikitude.architect.ArchitectView;
import com.wikitude.common.permission.PermissionManager;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;

import static com.omni.taipeiarsdk.TaipeiArSDKActivity.isMission;
import static com.omni.taipeiarsdk.TaipeiArSDKActivity.missionId;
import static com.omni.taipeiarsdk.TaipeiArSDKActivity.ng_id;
import static com.omni.taipeiarsdk.TaipeiArSDKActivity.ng_title;
import static com.omni.taipeiarsdk.TaipeiArSDKActivity.userId;
import static com.wikitude.architect.ArchitectView.CaptureScreenCallback.CAPTURE_MODE_CAM;

public class TestExtension extends ArchitectViewExtension implements ArchitectJavaScriptInterfaceListener {

    public static String recognizedPattenId;
    public static String isRightTarget = "false";
    private final PermissionManager permissionManager = ArchitectView.getPermissionManager();
    public static String interactive_text, interactive_url;
    public static int pos = 0;

    private final PermissionManager.PermissionManagerCallback permissionManagerCallback = new PermissionManager.PermissionManagerCallback() {
        @Override
        public void permissionsGranted(int requestCode) {
            saveScreenCaptureToExternalStorage(screenCapture);
        }

        @Override
        public void permissionsDenied(@NonNull final String[] deniedPermissions) {
            Toast.makeText(activity, activity.getString(R.string.permissions_denied) + Arrays.toString(deniedPermissions), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void showPermissionRationale(final int requestCode, @NonNull String[] strings) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle(R.string.permission_rationale_title);
            alertBuilder.setMessage(activity.getString(R.string.permission_rationale_text) + Manifest.permission.WRITE_EXTERNAL_STORAGE);
            alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    permissionManager.positiveRationaleResult(requestCode, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
                }
            });

            AlertDialog alert = alertBuilder.create();
            alert.show();
        }
    };

    private Bitmap screenCapture;

    public TestExtension(Activity activity, ArchitectView architectView) {
        super(activity, architectView);
    }


    @Override
    public void onCreate() {
        /*
         * The ArchitectJavaScriptInterfaceListener has to be added to the Architect view after ArchitectView.onCreate.
         * There may be more than one ArchitectJavaScriptInterfaceListener.
         */
        architectView.addArchitectJavaScriptInterfaceListener(this);
        makeWtcFileRequest();
    }

    @Override
    public void onDestroy() {
        // The ArchitectJavaScriptInterfaceListener has to be removed from the Architect view before ArchitectView.onDestroy.
        architectView.removeArchitectJavaScriptInterfaceListener(this);
    }

    @Override
    public void onJSONObjectReceived(JSONObject jsonObject) {
        try {
            switch (jsonObject.getString("action")) {
                case "return_target_id":
                    recognizedPattenId = jsonObject.getString("id");
                    makePattenContentReq();

                    Log.e("LOG", "onJSONObjectReceived: " + recognizedPattenId);
                    break;

                case "capture_screen":
                    /*
                     * ArchitectView.captureScreen has two different modes:
                     *  - CAPTURE_MODE_CAM_AND_WEBVIEW which will capture the camera and web-view on top of it.
                     *  - CAPTURE_MODE_CAM which will capture ONLY the camera and its content (AR.Drawables).
                     *
                     * onScreenCaptured will be called once the ArchitectView has processed the screen capturing and will
                     * provide a Bitmap containing the screenshot.
                     */
                    architectView.captureScreen(CAPTURE_MODE_CAM, new ArchitectView.CaptureScreenCallback() {
                        @Override
                        public void onScreenCaptured(final Bitmap screenCapture) {
                            TestExtension.this.screenCapture = screenCapture;
                            permissionManager.checkPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123, permissionManagerCallback);
                        }
                    });
                    break;
            }
        } catch (JSONException e) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "error_parsing_json", Toast.LENGTH_LONG).show();
                }
            });
            e.printStackTrace();
        }
        //architectView.callJavascript("World.getCallback('"+  "http://vrar360.dev.utobonus.com/upload_model/dfcb26da713f.wt3" +"');");
    }

    private void makeWtcFileRequest() {
        TpeArApi.getInstance().getPattenFile(activity,
                new NetworkManager.NetworkManagerListener<GetWtcFeedback>() {
                    @Override
                    public void onSucceed(GetWtcFeedback feedback) {
                        String pattenUrl = feedback.getData().getWtc()[0];
                        String Img = TaipeiArSDKActivity.ar_info_Img;
                        architectView.callJavascript("World.callPattenRecognizeStart('" + pattenUrl + "','" + Img + "');");

//                        Log.e("LOG", "makeWtcFileRequest " + TaipeiArSDKActivity.active_method);
//                        if (TaipeiArSDKActivity.active_method != null &&
//                                TaipeiArSDKActivity.active_method.equals("1")) {
//                            EventBus.getDefault().post(new OmniEvent(OmniEvent.TYPE_SHOW_AR_MODEL, ""));
//                        }
                    }

                    @Override
                    public void onFail(VolleyError error, boolean shouldRetry) {
                        DialogTools.getInstance().showErrorMessage(activity, "API Error", "get news data error");
                    }
                });
    }

    private void makePattenContentReq() {
        Log.d("LOG", "makePattenContentReq: " + recognizedPattenId + "," + userId);
        TpeArApi.getInstance().getPatternsContent(activity,
                recognizedPattenId,
                "1",
                new NetworkManager.NetworkManagerListener<ArPattensFeedback>() {
                    @Override
                    public void onSucceed(ArPattensFeedback feedback) {
                        if (feedback.getData().length == 0)
                            return;

                        Log.i("LOG", "onSucceed: " + feedback.getData()[pos].getContent_type());
                        if (feedback.getData().length > 1) {
                            EventBus.getDefault().post(new OmniEvent(OmniEvent.TYPE_SHOW_AR_PATTERNS, feedback));
                        } else {
                            isRightTarget = "true";

                            interactive_text = feedback.getData()[pos].getInteractive_text();
                            interactive_url = feedback.getData()[pos].getInteractive_url();
                            EventBus.getDefault().post(new OmniEvent(OmniEvent.TYPE_USER_AR_INTERACTIVE_TEXT, ""));

                            String contentType = feedback.getData()[pos].getContent_type();
                            String content = feedback.getData()[pos].getContent();
                            String angle = "270";
                            String view_size = "";
                            if (feedback.getData()[pos].getSize() != null)
                                view_size = feedback.getData()[pos].getSize();
                            switch (view_size) {
                                case "S":
                                    view_size = "0.09";
                                    break;
                                case "N":
                                    view_size = "0.135";
                                    break;
                                case "B":
                                    view_size = "0.18";
                                    break;
                                default:
                                    view_size = "0.135";
                                    break;
                            }
                            String isTransparent = feedback.getData()[pos].getIsTransparent();
                            String isCoverImage = feedback.getData()[pos].getCover_idnetify_image();

                            if (contentType != null) {
                                ArchitectViewExtensionActivity.pattern_content_name = feedback.getData()[pos].getName();
                                ArchitectViewExtensionActivity.support_pattern_id = feedback.getData()[pos].getId();
                                if (contentType.equals("video")) {
                                    Toast.makeText(activity, R.string.right_target_video_hint, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(activity, R.string.right_target_hint, Toast.LENGTH_SHORT).show();
                                }
                                switch (contentType) {
                                    case "3d_model":
                                        architectView.callJavascript("World.callback3DModel('" + content + "','" + angle + "','" + isRightTarget + "','" + view_size + "');");
                                        break;
                                    case "image":
                                        architectView.callJavascript("World.callbackImage('" + content + "','" + isRightTarget + "');");
                                        break;
                                    case "video":
                                        architectView.callJavascript("World.callbackVideo('" + content + "','" + TestExtension.isRightTarget + "','" + isTransparent + "','" + isCoverImage + "');");
                                        break;

                                    case "text":
                                        String textImgUrl = feedback.getData()[pos].getUrl_image();
                                        architectView.callJavascript("World.callbackText('" + textImgUrl + "','" + isRightTarget + "');");
                                        break;

                                }

                                if (isMission.equals("true")) {
//                                    showCompleteMessage();
                                    showHintMessage(String.format(activity.getString(R.string.break_through), ng_title),
                                            activity.getString(R.string.congratulation));
                                    TpeArApi.getInstance().getMissionComplete(activity,
                                            missionId, ng_id, userId,
                                            new NetworkManager.NetworkManagerListener<MissionCompleteFeedback>() {
                                                @Override
                                                public void onSucceed(MissionCompleteFeedback feedback) {
                                                    EventBus.getDefault().post(new OmniEvent(OmniEvent.TYPE_MISSION_COMPLETE, ""));
                                                }

                                                @Override
                                                public void onFail(VolleyError error, boolean shouldRetry) {

                                                }
                                            });
                                }
                            }
                        }
                    }

                    @Override
                    public void onFail(VolleyError error, boolean shouldRetry) {
                        //DialogTools.getInstance().showErrorMessage(activity, "API Error", "get news data error");
                    }

                });

    }

    public void showHintMessage(String title, String content) {
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_mission_hint, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity).setView(view);
        final AlertDialog hintDialog = builder.create();
        hintDialog.setCancelable(false);
        hintDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ((TextView) view.findViewById(R.id.dialog_mission_hint_title)).setText(title);
        ((TextView) view.findViewById(R.id.dialog_mission_hint_content)).setText(content);
        view.findViewById(R.id.dialog_mission_hint_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hintDialog.dismiss();
            }
        });
        hintDialog.show();
    }

    public void showCompleteMessage() {
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_mission_complete, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity).setView(view);
        final AlertDialog completeDialog = builder.create();
        completeDialog.setCancelable(false);
        completeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        view.findViewById(R.id.dialog_mission_complete_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                completeDialog.dismiss();
            }
        });
        completeDialog.show();
    }

    /**
     * This method will store the screenshot in a file and will create an intent to share it.
     *
     * @param screenCapture Bitmap from onScreenCaptured
     */
    private void saveScreenCaptureToExternalStorage(Bitmap screenCapture) {
        if (screenCapture != null) {
            // store screenCapture into external cache directory
            final File screenCaptureFile = new File(Environment.getExternalStorageDirectory().toString(), "screenCapture_" + System.currentTimeMillis() + ".jpg");
            // 1. Save bitmap to file & compress to jpeg. You may use PNG too
            try {
                File filePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "TaipeiAR");
                filePath.mkdirs();
                final FileOutputStream out;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ContentResolver resolver = activity.getApplicationContext().getContentResolver();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "screenCapture_" + System.currentTimeMillis() + ".jpg");
                    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
                    contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/" + "TaipeiAR");
                    Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                    out = (FileOutputStream) resolver.openOutputStream(imageUri);

                } else {
                    out = new FileOutputStream(filePath + File.separator + "screenCapture_" + System.currentTimeMillis() + ".jpg");

                }
                screenCapture.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();

                // 2. create send intent
                final Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpg");


                /**
                 *
                 * Defined the provider in the AndroidManifest.xml
                 * */
//                Uri apkURI = FileProvider.getUriForFile(
//                        activity.getApplicationContext(),
//                        activity.getApplicationContext()
//                                .getPackageName() + ".provider", screenCaptureFile);
//
                /**
                 *
                 * Defined the provider in the AndroidManifest.xml
                 * */
                Uri apkURI = FileProvider.getUriForFile(activity.getApplicationContext(),
                        "com.taoyuan.vrar.provider", screenCaptureFile);


                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                share.putExtra(Intent.EXTRA_STREAM, apkURI);


                // 3. launch intent-chooser
                final String chooserTitle = "Share Snaphot";
                activity.startActivity(Intent.createChooser(share, chooserTitle));

            } catch (final Exception e) {
                // should not occur when all permissions are set
                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // show toast message in case something went wrong
                        Toast.makeText(activity, "unexpected error" + e, Toast.LENGTH_LONG).show();

                        Log.i("LOG", "run: " + e);


                    }
                });
            }
        }
    }


}
