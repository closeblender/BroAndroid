package com.closestudios.bro;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.media.AudioFormat;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.closestudios.bro.networking.Bro;
import com.closestudios.bro.networking.BroMessage;
import com.closestudios.bro.networking.ServerApi;
import com.closestudios.bro.networking.ServerApiCalls;
import com.closestudios.bro.util.BroApplication;
import com.closestudios.bro.util.BroHub;
import com.closestudios.bro.util.BroPreferences;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by closestudios on 12/1/15.
 */
public class BroActionDialogFragment extends DialogFragment {

    static String arg0 = "bro_name";

    String broName;

    public static BroActionDialogFragment getInstance(String broName) {
        BroActionDialogFragment broActionDialogFragment = new BroActionDialogFragment();
        Bundle data = new Bundle();
        data.putString(arg0, broName);
        broActionDialogFragment.setArguments(data);
        return broActionDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_bro_options, null);

        broName = "";
        if (getArguments() != null && getArguments().containsKey(arg0)) {
            broName = getArguments().getString(arg0);
        }


        TextView tvSendBro = (TextView)view.findViewById(R.id.tvSendBro);
        TextView tvSendCustomBro = (TextView)view.findViewById(R.id.tvSendCustomBro);

        TextView tvRemoveBro = (TextView)view.findViewById(R.id.tvRemoveBro);
        TextView tvBlockBro = (TextView)view.findViewById(R.id.tvBlockBro);

        TextView tvCancel = (TextView)view.findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        tvSendBro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((getActivity() instanceof MainMenuActivity)) {

                    // Send Bro
                    try {
                        ServerApi.getApi().createNewRequest().sendBroMessage(BroPreferences.getPrefs(getActivity()).getToken(),
                                broName, new BroMessage("Bro!", "You received a bro from " + BroPreferences.getPrefs(getActivity()).getBroName(), getAudioBytes(getActivity()), ".mp3"), (MainMenuActivity) getActivity());

                        ((MainMenuActivity) getActivity()).showSpinner("Sending Bro", false);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    dismiss();

                }
            }
        });

        tvSendCustomBro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BroAudioDialogFragment broDialog = BroAudioDialogFragment.getInstance(broName);
                broDialog.show(getFragmentManager(), "bro_audio");
                dismiss();

            }
        });

        tvBlockBro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((getActivity() instanceof MainMenuActivity)) {

                    final MainMenuActivity act = ((MainMenuActivity)getActivity());

                    // Send Bro
                    ServerApi.getApi().createNewRequest().blockBro(BroPreferences.getPrefs(getActivity()).getToken(), broName, new ServerApiCalls.BroCallback() {
                        @Override
                        public void onSuccess(Bro[] bros) {
                            BroApplication.getBroHub(BroPreferences.getPrefs(getActivity()).getToken()).onSuccess(bros);
                            act.onSuccess(bros);
                        }

                        @Override
                        public void onError(String error) {
                            BroApplication.getBroHub(BroPreferences.getPrefs(getActivity()).getToken()).onError(error);
                            act.onError(error);
                        }
                    });

                    dismiss();

                    act.showSpinner("Blocking Bro", false);

                }
            }
        });

        tvRemoveBro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((getActivity() instanceof MainMenuActivity)) {

                    final MainMenuActivity act = ((MainMenuActivity)getActivity());

                    // Send Bro
                    ServerApi.getApi().createNewRequest().removeBro(BroPreferences.getPrefs(getActivity()).getToken(), broName, new ServerApiCalls.BroCallback() {
                        @Override
                        public void onSuccess(Bro[] bros) {
                            BroApplication.getBroHub(BroPreferences.getPrefs(getActivity()).getToken()).onSuccess(bros);
                            act.onSuccess(bros);
                        }

                        @Override
                        public void onError(String error) {
                            BroApplication.getBroHub(BroPreferences.getPrefs(getActivity()).getToken()).onError(error);
                            act.onError(error);
                        }
                    });
                    dismiss();

                    act.showSpinner("Removing Bro", false);

                }
            }
        });



        builder.setView(view);
        return builder.create();
    }

    public byte[] getAudioBytes(Context context) throws IOException {
        return convertStreamToByteArray(context.getResources().openRawResource(R.raw.bro));
    }

    public static byte[] convertStreamToByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int i = Integer.MAX_VALUE;
        while ((i = is.read(buff, 0, buff.length)) > 0) {
            baos.write(buff, 0, i);
        }

        return baos.toByteArray(); // be sure to close InputStream in calling function
    }

}
