package com.riteshb.mysmartcycle.ui.home;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;


import com.riteshb.mysmartcycle.MainActivity;
import com.riteshb.mysmartcycle.R;

import static android.app.Activity.RESULT_OK;

//import at.huber.youtubeExtractor.VideoMeta;
//import at.huber.youtubeExtractor.YouTubeExtractor;
//import at.huber.youtubeExtractor.YtFile;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    private static final int REQUEST_CODE_READ_EXTERNAL_PERMISSION = 2;
    private static final int REQUEST_CODE_SELECT_VIDEO_FILE = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        AppCompatImageView iv1 = root.findViewById(R.id.iv1);
        AppCompatImageView iv2 = root.findViewById(R.id.iv2);
        iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check whether user has granted read external storage permission to this activity.
//                int readExternalStoragePermission = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);

                // If not grant then require read external storage permission.
//                if(readExternalStoragePermission != PackageManager.PERMISSION_GRANTED)
//                {
//                    String requirePermission[] = {Manifest.permission.READ_EXTERNAL_STORAGE};
//                    ActivityCompat.requestPermissions(getActivity(), requirePermission, REQUEST_CODE_READ_EXTERNAL_PERMISSION);
//                }else {
//                    selectVideoFile();
//                }

                //call youtube url
//                extractYoutubeUrl();
//                getYoutubeUrl();
                Intent intent = new Intent(getActivity(), MainActivity.class);
//                intent.putExtra("videouri", sparseArray.get(17).getUrl());
//                Log.e("EXO", sparseArray.get(17).getUrl());
                startActivity(intent);

            }
        });
        iv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
// Check whether user has granted read external storage permission to this activity.
//                int readExternalStoragePermission = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
//
//                // If not grant then require read external storage permission.
//                if(readExternalStoragePermission != PackageManager.PERMISSION_GRANTED)
//                {
//                    String requirePermission[] = {Manifest.permission.READ_EXTERNAL_STORAGE};
//                    ActivityCompat.requestPermissions(getActivity(), requirePermission, REQUEST_CODE_READ_EXTERNAL_PERMISSION);
//                }else {
//                    selectVideoFile();
//                }

                //call youtube url
//                extractYoutubeUrl();
//                getYoutubeUrl();
                Intent intent = new Intent(getActivity(), MainActivity.class);
//                intent.putExtra("videouri", sparseArray.get(17).getUrl());
//                Log.e("EXO", sparseArray.get(17).getUrl());
                startActivity(intent);

            }
        });

        return root;
    }
    private void selectVideoFile()
    {
        // Create an intent with action ACTION_GET_CONTENT.
        Intent selectVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
        // Show video in the content browser.
        // Set selectVideoIntent.setType("*/*") to select all data
        // Intent for this action must set content type, otherwise you will encounter below exception : android.content.ActivityNotFoundException: No Activity found to handle Intent { act=android.intent.action.GET_CONTENT }
        selectVideoIntent.setType("video/*");

        // Start android get content activity ( this is a android os built-in activity.) .
        startActivityForResult(selectVideoIntent, REQUEST_CODE_SELECT_VIDEO_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_VIDEO_FILE) {
            // If the request is success.
            if (resultCode == RESULT_OK) {
                // To make example simple and clear, we only choose video file from local file,
                // this is easy to get video file real local path.
                // If you want to get video file real local path from a video content provider
                // Please read another article.
                Uri videoFileUri = data.getData();
                Log.e("Videos", "FileURI::"+videoFileUri);
                String videoFileName = videoFileUri.getLastPathSegment();
                Log.e("Videos", "Filename::"+videoFileName);

                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("videouri", videoFileUri.toString());
                startActivity(intent);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==REQUEST_CODE_READ_EXTERNAL_PERMISSION)
        {
            if(grantResults.length > 0)
            {
                int grantResult = grantResults[0];
                if(grantResult == PackageManager.PERMISSION_GRANTED)
                {
                    // If user grant the permission then open browser let user select audio file.
                    selectVideoFile();
                }else
                {
                    Toast.makeText(getActivity().getApplicationContext(), "You denied read external storage permission.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /*private void extractYoutubeUrl() {
        @SuppressLint("StaticFieldLeak") YouTubeExtractor mExtractor = new YouTubeExtractor(getActivity()) {
            @Override
            protected void onExtractionComplete(SparseArray<YtFile> sparseArray, VideoMeta videoMeta) {
                if (sparseArray != null) {
//                    playVideo(sparseArray.get(17).getUrl());
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.putExtra("videouri", sparseArray.get(17).getUrl());
                    Log.e("EXO", sparseArray.get(17).getUrl());
                    startActivity(intent);
                }
            }
        };
        mExtractor.extract("https://www.youtube.com/watch?v=-xdB3TsG6X4", true, true);
    }
    private void getYoutubeUrl(){
//        String youTubeVideoID = "v1uyQZNg2vE";
        String youTubeVideoID = "-xdB3TsG6X4";

        YouTubeVideoInfoRetriever retriever = new YouTubeVideoInfoRetriever();

        try
        {
            retriever.retrieve(youTubeVideoID);
            System.out.println(retriever.getInfo(YouTubeVideoInfoRetriever.KEY_DASH_VIDEO));
            Log.e("EXO", "YOUtube::"+retriever.getInfo(YouTubeVideoInfoRetriever.KEY_DASH_VIDEO));
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.e("EXO", "YOUtube::"+e.getMessage());
        }
    }*/

}