package com.base.library;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.Time;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class VoiceRecorder {
    MediaRecorder recorder;

    public static final int START = 1;
    public static final int VOICE = 10;
    public static final int STOP = 20;
    public static final int ERROR = 30;

    static final String PREFIX = "voice";
    static final String EXTENSION = ".amr";

    private boolean isRecording = false;
    private long startTime;
    private int maxTime;
    private String voiceFilePath = null;
    private RecorderCallback callback;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg == null) return;
            switch (msg.what) {
                case START:
                    if (callback != null) callback.callback(voiceFilePath, 0, 0, START);
                    break;
                case VOICE:
                    try {
                        V v = (V) msg.obj;
                        Long time = (new Date().getTime() - v.startTime) / 1000;

                        if (callback != null)
                            callback.callback(voiceFilePath, time, v.maxAmplitude, VOICE);
                        if (time >= maxTime) {
                            stopRecoding();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case STOP:
                    if (msg.obj != null) {
                        V v = (V) msg.obj;
                        Long time = (new Date().getTime() - v.startTime) / 1000;
                        LogX.d("voice", "voice recording finished. seconds:" + time);
                        if (callback != null) callback.callback(voiceFilePath, time, 0, STOP);
                    }
                    break;
                case ERROR:
                    if (callback != null) callback.callback(voiceFilePath, 0, 0, ERROR);
                    break;
            }

            // 切换msg切换图片
//            if (msg.what >= 0 && msg.what < micImages.length)
//                micImage.setImageDrawable(micImages[msg.what]);
//            Long time = (new Date().getTime() - ((Long) msg.obj)) / 1000;
//            if (time >= 60) {
//                isOK = true;
//                int length = stopRecoding();
//                if (length > 0) {
//                    if (recorderCallback != null) {
//                        recorderCallback.onVoiceRecordComplete(getVoiceFilePath(), length);
//                    }
//                }
//                discardRecording();
//            }
//            recording_time.setText(time + "/60 s");
        }
    };

    /**
     * start recording to the file
     */
    public String startRecording(Context appContext, int maxTime, RecorderCallback callback) {
        this.callback = callback;
        this.maxTime = maxTime;
        if (isRecording) {
            discardRecording();
        }
        voiceFilePath = null;
        try {
            // need to create recorder every time, otherwise, will got exception
            // from setOutputFile when try to reuse
            if (recorder != null) {
                recorder.release();
                recorder = null;
            }
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setAudioChannels(1); // MONO
            recorder.setAudioSamplingRate(8000); // 8000Hz
            recorder.setAudioEncodingBitRate(64); // seems if change this to
            voiceFilePath = getFileName();
            recorder.setOutputFile(voiceFilePath);
            recorder.prepare();
            isRecording = true;
            recorder.start();
            sendMessage(START, null);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("voice", "prepare() failed");
            discardRecording();
            sendMessage(ERROR, null);
            return voiceFilePath;
        }
        startTime = new Date().getTime();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (isRecording) {
                        V v = new V();
                        v.maxAmplitude = recorder.getMaxAmplitude() * 13 / 0x7FFF;
                        v.startTime = startTime;
                        sendMessage(VOICE, v);
                        SystemClock.sleep(100);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // from the crash report website, found one NPE crash from
                    // one android 4.0.4 htc phone
                    // maybe handler is null for some reason
                    Log.e("voice", e.toString());
                }
            }
        }).start();
        return voiceFilePath;
    }

    public static class V {
        public int maxAmplitude;
        public long startTime;
    }

    private void sendMessage(int what, Object obj) {
        if (handler == null) return;
        android.os.Message msg = new android.os.Message();
        msg.what = what;
        if (obj != null)
            msg.obj = obj;
        handler.sendMessage(msg);
    }

    private String getFileName() {
        String voiceFileName = getVoiceFileName(new Date().getTime() + "");
        voiceFilePath = Environment.getExternalStorageDirectory() + "/" + voiceFileName;
        File file = new File(voiceFilePath);
        return file.getAbsolutePath();
    }


    private void discardRecording() {
        if (recorder != null) {
            try {
                recorder.stop();
                recorder.release();
                recorder = null;
                File file = voiceFilePath == null ? null : new File(voiceFilePath);
                if (file != null && file.exists() && !file.isDirectory()) {
                    file.delete();
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
            isRecording = false;
        }
    }


    public String stopRecoding() {
        if (recorder != null) {
            isRecording = false;
            try {
                recorder.stop();
                recorder.release();
                recorder = null;
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
            File file = voiceFilePath == null ? null : new File(voiceFilePath);
            if (file == null || !file.exists() || !file.isFile()) {//文件不存在
                sendMessage(ERROR, null);
                return null;
            }
            if (file.length() <= 0) {//文件没有内容
                file.delete();
                sendMessage(ERROR, null);
                return null;
            }
//            int seconds = (int) (new Date().getTime() - startTime) / 1000;
            V v = new V();
            v.maxAmplitude = 0;
            v.startTime = startTime;
            sendMessage(STOP, v);
            return voiceFilePath;
        }
        return null;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (recorder != null) {
            recorder.release();
        }
    }

    private String getVoiceFileName(String uid) {
        Time now = new Time();
        now.setToNow();
        return uid + now.toString().substring(0, 15) + EXTENSION;
    }

    public boolean isRecording() {
        return isRecording;
    }


    public String getVoiceFilePath() {
        return voiceFilePath;
    }

    public interface RecorderCallback {
        public void callback(String path, long time, int volume, int status);
    }
}
