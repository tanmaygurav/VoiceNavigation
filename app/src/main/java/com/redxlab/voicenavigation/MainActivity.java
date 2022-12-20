package com.redxlab.voicenavigation;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //Create placeholder for user's consent to record_audio permission.
    //This will be used in handling callback
    private final int MY_PERMISSIONS_RECORD_AUDIO = 0;
    private ImageView trigger;

    private SpeechRecognizer mSpeechRecognizer;
    private boolean mIsListening=false;
    private TextView mUserInfoText, mUserUtteranceOutput;

    ArrayList mCommandList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        trigger=findViewById(R.id.trigger_icon);
        mUserInfoText=findViewById(R.id.user_info_text);
        mUserUtteranceOutput=findViewById(R.id.user_utterance_output);

        trigger.setOnClickListener(view -> {
            // Handle audio sessions here
            if (mIsListening){
                handleSpeechEnd();
            }else{
                handleSpeechBegin();
            }
        });

        VerifyAudioPermissions();

        createSpeechRecognizer();

        initCommands();
    }

    private void initCommands() {
        mCommandList=new ArrayList();
        mCommandList.add("Cart");
        mCommandList.add("Cancel");
        mCommandList.add("Home");
        mCommandList.add("Search");
    }

    private void handleSpeechBegin() {
        // start audio session
        mUserInfoText.setText(R.string.listening);
        mIsListening= true;
        mSpeechRecognizer.startListening(createIntent());
    }

    private void handleSpeechEnd() {
        // end audio session
        mUserInfoText.setText(R.string.detected_speech);
        mIsListening= false;
        mSpeechRecognizer.cancel();
    }

    private Intent createIntent() {
        Intent i =new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"en-IN");
        return  i;
    }

    private void createSpeechRecognizer() {
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                // Called when recognition results are ready. This callback will be called when the
                // audio session has been completed and user utterance has been parsed.

                // This ArrayList contains the recognition results, if the list is non-empty,
                // handle the user utterance

                List matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if(matches!= null && matches.size() > 0 ){
                    // The results are added in decreasing order of confidence to the list
                    String command = (String) matches.get(0);
                    mUserUtteranceOutput.setText(command);
                    handleCommand(command);
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {
                // Called when partial recognition results are available, this callback will be
                // called each time a partial text result is ready while the user is speaking.

                List matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches!= null && matches.size() > 0 ){
                    // handle partial speech results
                    String partialText = (String) matches.get(0);
                    mUserUtteranceOutput.setText(partialText);
                }
            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });
    }

    private void handleCommand(String command) {
        // Function to handle user commands
        if (mCommandList.contains(command)){
            // Successful utterance, notify user
            Toast.makeText(this, "Executing: "+command, Toast.LENGTH_LONG).show();
        }else{
            // Unsuccessful utterance, show failure message on screen
            Toast.makeText(this, "Could not recognize command", Toast.LENGTH_LONG).show();
        }
    }

    private void VerifyAudioPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(this, "Please grant permissions to record audio", Toast.LENGTH_LONG).show();
                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);
            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);
            }
        }
        //If permission is granted, then go ahead recording audio
        else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if (requestCode == MY_PERMISSIONS_RECORD_AUDIO){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "You can now use voice commands!", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this, "Please provide microphone permission to use voice.", Toast.LENGTH_LONG).show();
            }
        }

//
//        switch (requestCode){
//            case MY_PERMISSIONS_RECORD_AUDIO:{
//                if(grantResults.length>0
//                && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    recordAudio();
//                }else{
//                    Toast.makeText(this, "Permissions Denied to record audio", Toast.LENGTH_LONG).show();
//                }
//                return;
//            }
//        }
    }
}