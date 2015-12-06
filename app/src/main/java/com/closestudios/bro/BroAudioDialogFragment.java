package com.closestudios.bro;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.closestudios.bro.networking.Bro;
import com.closestudios.bro.networking.BroMessage;
import com.closestudios.bro.networking.ServerApi;
import com.closestudios.bro.networking.ServerApiCalls;
import com.closestudios.bro.util.AudioCaptureCircleView;
import com.closestudios.bro.util.BroApplication;
import com.closestudios.bro.util.BroPreferences;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * Created by closestudios on 12/5/15.
 */
public class BroAudioDialogFragment  extends DialogFragment implements View.OnTouchListener {
    static String TAG = "BroAudioDialogFragment";

    static String arg0 = "bro_name";

    String broName;
    AudioCaptureCircleView audioCircle;
    private MediaRecorder mRecorder = null;
    private MediaPlayer   mPlayer = null;
    private Handler recordHandler = new Handler();
    boolean recording = false;
    boolean playing = false;
    long startRecordTime;
    long stopRecordTime;
    long startPlayingTime;
    long stopPlayingTime;

    final int MAX_LENGTH_SEC = 10;
    TextView tvSendMessage;

    public static BroAudioDialogFragment getInstance(String broName) {
        BroAudioDialogFragment broAudioDialogFragment = new BroAudioDialogFragment();
        Bundle data = new Bundle();
        data.putString(arg0, broName);
        broAudioDialogFragment.setArguments(data);
        return broAudioDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_bro_audio, null);

        broName = "";
        if (getArguments() != null && getArguments().containsKey(arg0)) {
            broName = getArguments().getString(arg0);
        }

        TextView tvCancel = (TextView)view.findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlaying();
                dismiss();
            }
        });

        tvSendMessage = (TextView)view.findViewById(R.id.tvSendMessage);
        tvSendMessage.setVisibility(View.GONE);
        tvSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlaying();
                try {
                    sendMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                dismiss();
            }
        });

        audioCircle = (AudioCaptureCircleView)view.findViewById(R.id.audioCircle);

        audioCircle.setOnTouchListener(this);

        recordHandler.postDelayed(recordRunnable, 10);

        builder.setView(view);
        return builder.create();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "Action Down!");
                startRecording();
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "Action Up!");
                stopRecording();
                return true;
        }

        return false;
    }

    Runnable recordRunnable = new Runnable() {
        @Override
        public void run() {

            if(getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (recording) {

                            // Update UI
                            audioCircle.setActive(true);
                            audioCircle.setPercentFilled((float) (System.currentTimeMillis() - startRecordTime) / (float) (1000 * MAX_LENGTH_SEC));

                            // Should we stop?
                            if (System.currentTimeMillis() - startRecordTime > 1000 * MAX_LENGTH_SEC) {
                                // Stop!
                                stopRecording();
                            }
                        }

                        if (playing) {

                            // Update UI
                            audioCircle.setActive(false);
                            audioCircle.setPercentFilled((float) (System.currentTimeMillis() - startPlayingTime) / (float) (stopRecordTime - startRecordTime));

                        }

                        recordHandler.postDelayed(recordRunnable, 10);
                    }
                });
            }

        }
    };

    private void startRecording() {
        stopPlaying();
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(getFileName());
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        mRecorder.start();

        startRecordTime = System.currentTimeMillis();
        recording = true;

    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        recording = false;
        stopRecordTime = System.currentTimeMillis();
        startPlaying();

        // Has Data
        tvSendMessage.setVisibility(View.VISIBLE);
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(getFileName());
            mPlayer.prepare();
            mPlayer.start();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlaying();
                    startPlaying();
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
        startPlayingTime = System.currentTimeMillis();
        playing = true;
    }

    private void stopPlaying() {
        if(mPlayer != null) {
            mPlayer.release();
        }
        mPlayer = null;
        stopPlayingTime = System.currentTimeMillis();
        playing = false;
    }

    private String getFileName() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS).getAbsolutePath() + "/tempSendFile.3gp";
    }

    private void sendMessage() throws IOException {

        byte[] audioBytes = readFile(new File(getFileName()));

        if ((getActivity() instanceof MainMenuActivity)) {
            // Send Bro
            ServerApi.getApi().createNewRequest().sendBroMessage(BroPreferences.getPrefs(getActivity()).getToken(),
                    broName, new BroMessage("Bro!", "You received a bro from " + BroPreferences.getPrefs(getActivity()).getBroName(), audioBytes, ".3gp"), (MainMenuActivity) getActivity());

            ((MainMenuActivity) getActivity()).showSpinner("Sending Bro", false);

        }
    }

    public static byte[] readFile(File file) throws IOException {
        // Open file
        RandomAccessFile f = new RandomAccessFile(file, "r");
        try {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength)
                throw new IOException("File size >= 2 GB");
            // Read file and return data
            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        } finally {
            f.close();
        }
    }


}
