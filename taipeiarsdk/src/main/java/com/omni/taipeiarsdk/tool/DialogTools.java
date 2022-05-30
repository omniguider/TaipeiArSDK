package com.omni.taipeiarsdk.tool;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentManager;

import com.omni.taipeiarsdk.R;

public class DialogTools {

    private boolean isTest = false;

    private static DialogTools mDialogTools;
    private ProgressDialog mProgressDialog;
    private AlertDialog mNoNetworkDialog;

    public static DialogTools getInstance() {
        if (mDialogTools == null) {
            mDialogTools = new DialogTools();
        }
        return mDialogTools;
    }

    boolean hintIsShown = false;

    public void showHintDialog(Activity activity, @StringRes int titleRes, @StringRes int contentRes) {
        if (activity == null) return;
        if (activity.isFinishing()) {
            return;
        }
        if (hintIsShown) return;
        showHintDialog(activity, activity.getString(titleRes), activity.getString(contentRes), null);
    }

    public void showHintDialog(Activity activity, @NonNull String title, @NonNull String hint) {
        if (activity == null) return;
        if (activity.isFinishing()) {
            return;
        }
        if (hintIsShown) return;
        showHintDialog(activity, title, hint, null);
    }

    public void showHintDialog(Activity activity, @StringRes int titleRes, @StringRes int contentRes, @Nullable final View.OnClickListener mOnClickListener) {
        if (activity == null) return;
        if (activity.isFinishing()) {
            return;
        }
        if (hintIsShown) return;
        showHintDialog(activity, activity.getString(titleRes), activity.getString(contentRes), mOnClickListener);
    }

    public void showHintDialog(Activity activity, @StringRes int contentRes) {
        if (activity == null) return;
        if (activity.isFinishing()) {
            return;
        }
        if (hintIsShown) return;
        showHintDialog(activity, activity.getString(contentRes), (View.OnClickListener) null);
    }

    public void showHintDialog(Activity activity, String contentRes) {
        if (activity == null) return;
        if (activity.isFinishing()) {
            return;
        }
        if (hintIsShown) return;
        showHintDialog(activity, contentRes, (View.OnClickListener) null);
    }

    public void showHintDialog(Activity activity, @StringRes int contentRes, @Nullable final View.OnClickListener mOnClickListener) {
        if (activity == null) return;
        if (activity.isFinishing()) {
            return;
        }
        if (hintIsShown) return;
        showHintDialog(activity, activity.getString(contentRes), mOnClickListener);
    }

    public void showHintDialog(Activity activity, @NonNull String hint, @Nullable final View.OnClickListener mOnClickListener) {
        if (activity == null) return;
        if (activity.isFinishing()) {
            return;
        }
        if (hintIsShown) return;
        hintIsShown = true;
        activity.runOnUiThread(() -> {
            AlertDialog.Builder adb = new AlertDialog.Builder(activity);
            final Dialog req = adb.setView(new View(activity)).create();

            req.show();
            //for make custom dialog with rounded corners in android
            //set the background of your dialog to transparent
            req.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            req.setContentView(R.layout.content_only_dialog);
            TextView contentTv = req.findViewById(R.id.content);
            contentTv.setText(hint);

            TextView okBtn = req.findViewById(R.id.ok);
            okBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    req.dismiss();
                    if (mOnClickListener != null)
                        mOnClickListener.onClick(v);
                }
            });
            req.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    hintIsShown = false;
                }
            });
            req.show();
        });
    }

    public void showHintDialog(@NonNull Activity activity, @NonNull String title, @NonNull String hint, @Nullable final View.OnClickListener mOnClickListener) {
        if (activity == null) return;
        if (activity.isFinishing()) {
            return;
        }
        if (hintIsShown) return;
        hintIsShown = true;
        activity.runOnUiThread(() -> {
            AlertDialog.Builder adb = new AlertDialog.Builder(activity);
            final Dialog req = adb.setView(new View(activity)).create();

            req.show();
            //for make custom dialog with rounded corners in android
            //set the background of your dialog to transparent
            req.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            req.setContentView(R.layout.title_content_dialog);
            TextView titleTv = req.findViewById(R.id.title);
            titleTv.setText(title);

            TextView contentTv = req.findViewById(R.id.content);
            contentTv.setText(hint);

            TextView okBtn = req.findViewById(R.id.ok);
            okBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    req.dismiss();
                    if (mOnClickListener != null)
                        mOnClickListener.onClick(v);
                }
            });
            req.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    hintIsShown = false;
                }
            });
            req.show();
        });
    }

    private AlertDialog createAlertDialog(Context context, String title, String message, int iconRes,
                                          String positiveBtnText, DialogInterface.OnClickListener positiveBtnClickListener,
                                          String negativeBtnText, DialogInterface.OnClickListener negativeBtnClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message);

        if (iconRes != -1) {
            builder.setIcon(iconRes);
        }

        if (!TextUtils.isEmpty(positiveBtnText) && positiveBtnClickListener != null) {
            builder.setPositiveButton(positiveBtnText, positiveBtnClickListener);
        }

        if (!TextUtils.isEmpty(negativeBtnText) && negativeBtnClickListener != null) {
            builder.setNegativeButton(negativeBtnText, negativeBtnClickListener);
        }

        return builder.create();
    }

//    public HomeDialogFragment showHomeFragment(FragmentManager manager) {
//        FragmentTransaction transaction = manager.beginTransaction();
//        transaction.addToBackStack(null);
//        HomeDialogFragment homeDialogFragment = HomeDialogFragment.getInstance(manager);
//        homeDialogFragment.show(transaction, HomeDialogFragment.TAG);
//
//        return homeDialogFragment;
//    }

    public void showSettingFragment(FragmentManager manager) {

    }

//    public void showProgress(Context context) {
//        if (mProgressDialog == null) {
//            mProgressDialog = new ProgressDialog(context);
//            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            mProgressDialog.setCanceledOnTouchOutside(false);
//            mProgressDialog.setCancelable(false);
//            mProgressDialog.setMessage(null);
//        }
//        if (context != null && !((Activity) context).isFinishing() && !mProgressDialog.isShowing()) {
//            mProgressDialog.show();
//        }
//    }
//
//
//
//
//    public void dismissProgress(Context context) {
//        if (context != null && !((Activity) context).isFinishing() && mProgressDialog != null && mProgressDialog.isShowing()) {
//            mProgressDialog.dismiss();
//            mProgressDialog = null;
//        }
//    }


    public void showProgress(Context context) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage(null);
        }
        if (context != null && !((Activity) context).isFinishing() && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    public void dismissProgress(Context context) {
        if (context != null && !((Activity) context).isFinishing() && mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public void showNoNetworkMessage(Context context) {
        if (mNoNetworkDialog == null && context != null) {
            mNoNetworkDialog = createAlertDialog(context, "目前您的設備沒有網路服務", "請確認網路服務狀況後再使用，謝謝。", -1,
                    "前往設定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent=new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                            context.startActivity(intent);
                            dialog.dismiss();
                        }
                    },
                    "離開", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        }
        if (context != null && !((Activity) context).isFinishing() && !mNoNetworkDialog.isShowing()) {
            mNoNetworkDialog.show();
        }
    }

    public void dismissNoNetworkMessage(Context context) {
        if (context != null && !((Activity) context).isFinishing() && mNoNetworkDialog != null && mNoNetworkDialog.isShowing()) {
            mNoNetworkDialog.dismiss();
            mNoNetworkDialog = null;
        }
    }

    public void showTestMessage(Context context, @StringRes int titleRes, String message) {
        if (isTest) {
            showErrorMessage(context, titleRes, message);
        }
    }

    public Dialog createErrorMessageDialog(Context context, @StringRes int titleRes, String message) {
        if (context != null && !((Activity) context).isFinishing()) {
            return createAlertDialog(context, context.getResources().getString(titleRes), message, -1,
                    context.getResources().getString(R.string.dialog_button_ok_text), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    },
                    null, null);
        }
        return null;
    }

    public void showErrorMessage(Context context, @StringRes int titleRes, @StringRes int messageRes) {
        showErrorMessage(context, context.getResources().getString(titleRes), context.getResources().getString(messageRes), null);
    }

    public void showErrorMessage(Context context, @StringRes int titleRes, String message) {
        showErrorMessage(context, context.getResources().getString(titleRes), message, null);
    }

    public void showErrorMessage(Context context, String title, String message) {
        showErrorMessage(context, title, message, null);
    }

    public void showErrorMessage(Context context, @StringRes int titleRes, String message, @Nullable DialogInterface.OnDismissListener dismissListener) {
        showErrorMessage(context, context.getResources().getString(titleRes), message, -1, dismissListener);
    }

    public void showErrorMessage(Context context, String title, String message, @Nullable DialogInterface.OnDismissListener dismissListener) {
        showErrorMessage(context, title, message, -1, dismissListener);
    }

    public void showErrorMessage(Context context, String title, String message, int iconRes, @Nullable DialogInterface.OnDismissListener dismissListener) {
        if (context != null && !((Activity) context).isFinishing()) {
            Dialog dialog = createAlertDialog(context, title, message, iconRes,
                    context.getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    },
                    null, null);

            if (dismissListener != null) {
                dialog.setOnDismissListener(dismissListener);
            }

            dialog.show();
        }
    }

}
