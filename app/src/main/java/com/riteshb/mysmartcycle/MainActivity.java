package com.riteshb.mysmartcycle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.riteshb.mysmartcycle.models.AltitudePoints;
import com.riteshb.mysmartcycle.models.TrackXml;
import com.riteshb.mysmartcycle.models.VideoPoints;
import com.riteshb.mysmartcycle.yotubekey.DeveloperKey;
import com.riteshb.mysmartcycle.yotubekey.YouTubeFailureRecoveryActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class MainActivity extends YouTubeFailureRecoveryActivity implements
        OnChartValueSelectedListener/*, YouTubePlayerSeekBarListener, YouTubePlayerListener */
        , YouTubePlayer.OnInitializedListener {

    private static final String PLAYBACK_TIME = "play_time";
    // Request code for require android READ_EXTERNAL_PERMISSION.
    private static final int REQUEST_CODE_READ_EXTERNAL_PERMISSION = 2;
    // Save local video file uri.
    private Uri videoFileUri = null;
    // Request code for user select video file.
    private static final int REQUEST_CODE_SELECT_VIDEO_FILE = 1;
    private String videoFileName;

    TrackXml trackXml_data;

    private LineChart chart;
    ArrayList<Entry> values = new ArrayList<>();
    int xypoints = 0;

    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
    ArrayList<Entry> valuesCurrent = new ArrayList<>();

    private static int INTERVAL = 1000; //1 sec
    private static int INTERVAL_SEEK = 100; //1 sec

    YouTubePlayerView youtube_plv;
    YouTubePlayer youtube_player;


    double videoTime_min = 0;
    double videoTime_max = 0;
    double distance_max = 0;
    double distance_min = 0;
    double runtime_dist = 0;//km
    double runtime_video_time = 0;
    double runtime_speed = 10;//init 10 kmph

    //    private MyPlaylistEventListener playlistEventListener;
    private MyPlayerStateChangeListener playerStateChangeListener;
    private MyPlaybackEventListener playbackEventListener;

    TextView tv_val_altitude, tv_val_distance, tv_val_vtime;
    private int progress;

    private final int[] colorsBg = new int[]{
            Color.rgb(137, 230, 81),
            Color.rgb(240, 240, 30),
            Color.rgb(89, 199, 250),
            Color.rgb(250, 104, 104)
    };
    private final int[] colors = new int[]{
            ColorTemplate.VORDIPLOM_COLORS[0],
            ColorTemplate.VORDIPLOM_COLORS[1],
            ColorTemplate.VORDIPLOM_COLORS[2]
    };

    Runnable mHandlerChartTask = new Runnable() {
        @Override
        public void run() {
            if (trackXml_data != null) {
                try {
                    AltitudePoints altitudePoints = trackXml_data.getAltitudePoints().get(xypoints);
                    tv_val_altitude.setText(String.format("%.2f", altitudePoints.getAlt())+" ft");
                    setchartcurrentData(altitudePoints, xypoints);
                    xypoints++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    };
    Handler mHandlerSeek = new Handler();

    Runnable mHandlerSeekTask = new Runnable() {
        @Override
        public void run() {
            if (trackXml_data != null) {
                runtime_dist += 0.35;
                runtime_video_time = videoTime_min
                        + ((runtime_dist - distance_min)
                        * (videoTime_max - videoTime_min)
                        / (distance_max - distance_min));
                try {
                    youtube_player.seekToMillis((int) runtime_video_time * 1000);
                    youtube_player.play();
                    Log.e("seektomillis", "runtime video time::" + String.valueOf(runtime_video_time * 1000));
                    Log.e("seektomillis", "runtime dist::" + String.valueOf(runtime_dist));
                    Log.e("seektomillis", "curr millis::" + String.valueOf(youtube_player.getCurrentTimeMillis()/1000));
                    mHandlerSeek.postDelayed(mHandlerSeekTask, INTERVAL_SEEK);
                    tv_val_distance.setText(String.format("%.2f", runtime_dist)+" m");
                    tv_val_vtime.setText(String.format("%.1f", runtime_video_time)+" seconds");
                    /*if (((youtube_player.getCurrentTimeMillis() / 1000) > Math.ceil(countries.getVideoPoints().get(xypoints).getVideoTime()))
                            && (Math.ceil(countries.getVideoPoints().get(xypoints).getDistance()) > Math.ceil(countries.getAltitudePoints().get(xypoints).getDistance()))) {
                            //update chart altitude
                        mHandlerChartTask.run();
                    }*/

                    if (Math.ceil(runtime_dist) > Math.ceil(trackXml_data.getAltitudePoints().get(xypoints).getDistance())){
                        //update chart
                        mHandlerChartTask.run();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    mHandlerSeek.removeCallbacks(mHandlerSeekTask);
                }
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_val_altitude = findViewById(R.id.tv_val_altitude);
        tv_val_distance = findViewById(R.id.tv_val_distance);
        tv_val_vtime = findViewById(R.id.tv_val_vtime);

        youtube_plv = findViewById(R.id.youtube_plv);
        youtube_plv.initialize(DeveloperKey.DEVELOPER_KEY, this);

//        playlistEventListener = new MyPlaylistEventListener();
        playerStateChangeListener = new MyPlayerStateChangeListener();
        playbackEventListener = new MyPlaybackEventListener();

        chart = findViewById(R.id.chart1);
        chart.setOnChartValueSelectedListener(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        parseXmlData();
        setupChart();
        setChartInitData();
        //find min max
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            distance_min = findMinVideoDistance(trackXml_data.getVideoPoints());
            distance_max = findMaxVideoDistance(trackXml_data.getVideoPoints());
            videoTime_min = findMinVideoTime(trackXml_data.getVideoPoints());
            videoTime_max = findMaxVideoTime(trackXml_data.getVideoPoints());

        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            youtube_player.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            mHandlerSeek.removeCallbacks(mHandlerSeekTask);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.youtube_plv);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        Log.e("YoutubePlayer", "init success::");
        this.youtube_player = youTubePlayer;
//        youTubePlayer.setPlaylistEventListener(playlistEventListener);
        this.youtube_player.setPlayerStateChangeListener(playerStateChangeListener);
        this.youtube_player.setPlaybackEventListener(playbackEventListener);
//        this.youtube_player.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);

        if (!wasRestored) {
//            youTubePlayer.cueVideo("wKJ9KzGQq0w");
            youTubePlayer.cueVideo("-xdB3TsG6X4");
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Log.e("YoutubePlayer", "init failure::" + youTubeInitializationResult.name());
    }


    private final class MyPlayerStateChangeListener implements YouTubePlayer.PlayerStateChangeListener {
        String playerState = "UNINITIALIZED";

        @Override
        public void onLoading() {
            playerState = "LOADING";
//            updateText();
            Log.e("YoutubePlayer", playerState);
        }

        @Override
        public void onLoaded(String videoId) {
            playerState = String.format("LOADED %s", videoId);
//            updateText();
            Log.e("YoutubePlayer", playerState);
            mHandlerSeekTask.run();
            youtube_player.play();
        }

        @Override
        public void onAdStarted() {
            playerState = "AD_STARTED";
//            updateText();

            Log.e("YoutubePlayer", playerState);
        }

        @Override
        public void onVideoStarted() {
            playerState = "VIDEO_STARTED";
//            updateText();
            //                if(state == PlayerConstants.PlayerState.PLAYING){
//                    mHandlerTask.run();
//                }else{
//                    mHandler.removeCallbacks(mHandlerTask);
//                }

            Log.e("YoutubePlayer", playerState);

        }

        @Override
        public void onVideoEnded() {
            playerState = "VIDEO_ENDED";
            try {
                mHandlerSeek.removeCallbacks(mHandlerSeekTask);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e("YoutubePlayer", playerState);
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason reason) {
            playerState = "ERROR (" + reason + ")";
            if (reason == YouTubePlayer.ErrorReason.UNEXPECTED_SERVICE_DISCONNECTION) {
                // When this error occurs the player is released and can no longer be used.
                youtube_player = null;
//                setControlsEnabled(false);
            }
            try {
                mHandlerSeek.removeCallbacks(mHandlerSeekTask);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e("YoutubePlayer", playerState);
        }

    }

    private final class MyPlaybackEventListener implements YouTubePlayer.PlaybackEventListener {
        String playbackState = "NOT_PLAYING";
        String bufferingState = "";

        @Override
        public void onPlaying() {
            playbackState = "PLAYING";
//            updateText();
            //                if(state == PlayerConstants.PlayerState.PLAYING){
//            try {
//                mHandlerTask.run();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//                }else{
//                    mHandler.removeCallbacks(mHandlerTask);
//                }

            Log.e("YoutubePlayer", "\tPLAYING " + getTimesText());
        }

        @Override
        public void onBuffering(boolean isBuffering) {
            bufferingState = isBuffering ? "(BUFFERING)" : "";
//            updateText();
            Log.e("YoutubePlayer", "\t\t" + (isBuffering ? "BUFFERING " : "NOT BUFFERING ") + getTimesText());
//            if(!isBuffering){
//                mHandlerSeekTask.run();
//            }else{
//                mHandlerSeek.removeCallbacks(mHandlerSeekTask);
//            }

        }

        @Override
        public void onStopped() {
            playbackState = "STOPPED";
            try {
                mHandlerSeek.removeCallbacks(mHandlerSeekTask);
            } catch (Exception e) {
                e.printStackTrace();
            }
//                }

            Log.e("YoutubePlayer", "\tSTOPPED");
        }

        @Override
        public void onPaused() {
            playbackState = "PAUSED";
            Log.e("YoutubePlayer", "\tPAUSED " + getTimesText());
        }

        @Override
        public void onSeekTo(int endPositionMillis) {
            Log.e("YoutubePlayer", String.format("\tSEEKTO: (%s/%s)",
                    formatTime(endPositionMillis),
                    formatTime(youtube_player.getDurationMillis())));
        }

    }


    private String formatTime(int millis) {
        int seconds = millis / 1000;
        int minutes = seconds / 60;
        int hours = minutes / 60;

        return (hours == 0 ? "" : hours + ":")
                + String.format("%02d:%02d", minutes % 60, seconds % 60);
    }

    private String getTimesText() {
        int currentTimeMillis = youtube_player.getCurrentTimeMillis();
        int durationMillis = youtube_player.getDurationMillis();
        return String.format("(%s/%s)", formatTime(currentTimeMillis), formatTime(durationMillis));
    }

    XmlPullParserFactory pullParserFactory;

    public void parseXmlData() {
        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = pullParserFactory.newPullParser();
            InputStream in_s = getApplicationContext().getAssets().open("track.xml");

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in_s, null);

            trackXml_data = parseXML(parser);

            Log.e("parseXml", "AltitudePoints::" + trackXml_data.getAltitudePoints().size() + " ::videoPoints::" + trackXml_data.getVideoPoints().size());

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

    }

    private TrackXml parseXML(XmlPullParser parser) throws XmlPullParserException, IOException {

        int eventType = parser.getEventType();
        TrackXml country = null;
        List<AltitudePoints> altipoints = null;
        AltitudePoints altitudePoint = null;
        List<VideoPoints> videoPoints = null;
        VideoPoints videoPoint = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name;
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    country = new TrackXml();
                    altipoints = new ArrayList<>();
                    videoPoints = new ArrayList<>();
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name.equals("AltitudePoint")) {
                        altitudePoint = new AltitudePoints();
                        altitudePoint.setKey(Boolean.parseBoolean(parser.getAttributeValue(null, "Key")));
                        altitudePoint.setLockDest(Boolean.parseBoolean(parser.getAttributeValue(null, "LockDest")));
                        altitudePoint.setLockAlt(Boolean.parseBoolean(parser.getAttributeValue(null, "LockAlt")));
                        altitudePoint.setAlt(Double.parseDouble(parser.getAttributeValue(null, "Alt")));
                        altitudePoint.setLng(Double.parseDouble(parser.getAttributeValue(null, "Lng")));
                        altitudePoint.setLat(Double.parseDouble(parser.getAttributeValue(null, "Lat")));
                        altitudePoint.setDistance(Double.parseDouble(parser.getAttributeValue(null, "Distance")));


                    } else if (name.equals("VideoPoint")) {
                        videoPoint = new VideoPoints();
                        videoPoint.setLng(Double.parseDouble(parser.getAttributeValue(null, "Lng")));
                        videoPoint.setLat(Double.parseDouble(parser.getAttributeValue(null, "Lat")));
                        videoPoint.setDistance(Double.parseDouble(parser.getAttributeValue(null, "Distance")));
                        videoPoint.setVideoTime(Double.parseDouble(parser.getAttributeValue(null, "VideoTime")));

                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("AltitudePoint") && altitudePoint != null) {
                        altipoints.add(altitudePoint);
                    } else if (name.equalsIgnoreCase("VideoPoint") && videoPoint != null) {
                        videoPoints.add(videoPoint);
                    }
            }
            eventType = parser.next();
        }
        country.setAltitudePoints(altipoints);
        country.setVideoPoints(videoPoints);
        return country;

    }

    private void setupChart() {
        chart.setDrawGridBackground(false);
        chart.getDescription().setEnabled(false);
        chart.setDrawBorders(false);
        chart.setBackgroundColor(colorsBg[2]);
        chart.getAxisLeft().setEnabled(false);
        chart.getAxisRight().setDrawAxisLine(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getXAxis().setDrawAxisLine(false);
        chart.getXAxis().setDrawGridLines(false);

// enable touch gestures
        chart.setTouchEnabled(true);

// enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

// if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);

//        seekBarX.setProgress(20);
//        seekBarY.setProgress(100);

        XAxis x = chart.getXAxis();
        x.setEnabled(false);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        youtube_plv.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void setchartcurrentData(AltitudePoints altitudePoints, int xypoints) {
        valuesCurrent.add(new Entry(xypoints, (float) altitudePoints.getAlt()));
        chart.notifyDataSetChanged();
        chart.invalidate();

    }

    private void setChartInitData() {
        chart.resetTracking();
        progress = trackXml_data.getAltitudePoints().size();

//        for (int z = 0; z < 3; z++) {

        ArrayList<Entry> values = new ArrayList<>();

        for (int i = 0; i < progress; i++) {
            values.add(new Entry(i, (float) trackXml_data.getAltitudePoints().get(i).getAlt()));
        }

        LineDataSet d = new LineDataSet(values, "Route");
        d.setLineWidth(2.5f);
        d.setCircleRadius(0.3f);
        d.setDrawCircles(false);
//            d.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        d.setDrawValues(false);

        int color = colors[1 % colors.length];
        d.setColor(color);
        d.setCircleColor(color);
        dataSets.add(d);


//        }
        LineDataSet d1 = new LineDataSet(valuesCurrent, "Altitude");
        d1.setLineWidth(2.5f);
        d1.setCircleRadius(1.8f);
        d1.setDrawValues(false);

//        d.setValues(values);
        int color1 = colors[2 % colors.length];
        d1.setColor(color1);
        d1.setCircleColor(color1);
        dataSets.add(d1);


        LineData data = new LineData(dataSets);
        chart.setData(data);

        chart.invalidate();
    }

    // function return maximum value in an unsorted
    //  list in Java using Collection
    @RequiresApi(api = Build.VERSION_CODES.N)
    public double findMaxAlti(List<AltitudePoints> list) {

        // check list is empty or not
        if (list == null || list.size() == 0) {
            return Double.MIN_VALUE;
        }

        // return maximum value of the ArrayList
//        return Collections.max(list);
        // get user with minimum age

        AltitudePoints user1 = list.stream()
                .max(Comparator.comparingDouble(AltitudePoints::getAlt))
                .get();
        return user1.getAlt();

//        System.out.println("User with Minimum age : " + user1);


        // get user with maximum age

    }

    // function to find minimum value in an unsorted
    // list in Java using Collection
    @RequiresApi(api = Build.VERSION_CODES.N)
    public double findMinAlti(List<AltitudePoints> list) {

        // check list is empty or not
        if (list == null || list.size() == 0) {
            return Double.MAX_VALUE;
        }

        // return minimum value of the ArrayList
//        return Collections.min(list);
        AltitudePoints user1 = list.stream()
                .min(Comparator.comparingDouble(AltitudePoints::getAlt))
                .get();
        return user1.getAlt();
    }

    // function return maximum value in an unsorted
    //  list in Java using Collection
    @RequiresApi(api = Build.VERSION_CODES.N)
    public double findMaxVideoTime(List<VideoPoints> list) {

        // check list is empty or not
        if (list == null || list.size() == 0) {
            return Double.MIN_VALUE;
        }

        // return maximum value of the ArrayList
//        return Collections.max(list);
        // get user with minimum age

        VideoPoints user1 = list.stream()
                .max(Comparator.comparingDouble(VideoPoints::getVideoTime))
                .get();
        return user1.getVideoTime();

    }

    // function to find minimum value in an unsorted
    // list in Java using Collection
    @RequiresApi(api = Build.VERSION_CODES.N)
    public double findMinVideoTime(List<VideoPoints> list) {

        // check list is empty or not
        if (list == null || list.size() == 0) {
            return Double.MAX_VALUE;
        }

        // return minimum value of the ArrayList
//        return Collections.min(list);
        VideoPoints user1 = list.stream()
                .min(Comparator.comparingDouble(VideoPoints::getVideoTime))
                .get();
        return user1.getVideoTime();
    }

    // function return maximum value in an unsorted
    //  list in Java using Collection
    @RequiresApi(api = Build.VERSION_CODES.N)
    public double findMaxVideoDistance(List<VideoPoints> list) {

        // check list is empty or not
        if (list == null || list.size() == 0) {
            return Double.MIN_VALUE;
        }
        VideoPoints user1 = list.stream()
                .max(Comparator.comparingDouble(VideoPoints::getDistance))
                .get();
        return user1.getDistance();

    }

    // function to find minimum value in an unsorted
    // list in Java using Collection
    @RequiresApi(api = Build.VERSION_CODES.N)
    public double findMinVideoDistance(List<VideoPoints> list) {

        // check list is empty or not
        if (list == null || list.size() == 0) {
            return Double.MAX_VALUE;
        }

        VideoPoints user1 = list.stream()
                .min(Comparator.comparingDouble(VideoPoints::getDistance))
                .get();
        return user1.getDistance();
    }

}