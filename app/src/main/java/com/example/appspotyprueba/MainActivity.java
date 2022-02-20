package com.example.appspotyprueba;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.PlayerApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

public class MainActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "cee8fefa95dd4a278d319f76877eba08";
    private static final String REDIRECT_URI = "com.example.appspotyprueba://callback";
    private SpotifyAppRemote mSpotifyAppRemote;

    private boolean isPlaying = false;

    ImageView buttonPlayPause;
    ImageView buttonNext;
    ImageView buttonPrevious;
    TextView cancionName;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonPlayPause = (ImageView) findViewById(R.id.bPlayPause);
        buttonNext = (ImageView) findViewById(R.id.nextButtom);
        buttonPrevious = (ImageView) findViewById(R.id.previousButtom);
        cancionName = (TextView) findViewById(R.id.textview_first);

        buttonPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPlaying) {
                    mSpotifyAppRemote.getPlayerApi().resume();
                    buttonPlayPause.setBackgroundResource(R.drawable.pause_music);
                    isPlaying = true;
                } else {
                    mSpotifyAppRemote.getPlayerApi().pause();
                    buttonPlayPause.setBackgroundResource(R.drawable.play_music);
                    isPlaying = false;
                }
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSpotifyAppRemote.getPlayerApi().skipNext();
            }
        });

        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSpotifyAppRemote.getPlayerApi().skipPrevious();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("MainActivity", "Connected! Yay!");

                        // Now you can start interacting with App Remote
                        connected();
                    }

                    public void onFailure(Throwable throwable) {
                        Log.e("MyActivity", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    private void connected(){
        // Play a playlist spotify:playlist:6IyPKJq69D0Um2AOwprVbn
        mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:6IyPKJq69D0Um2AOwprVbn");

        // Subscribe to PlayerState
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;
                    cancionName.setText(playerState.track.name);
                    if (track != null) {
                        Log.d("MainActivity", track.name + " by " + track.artist.name);
                    }
                    if(playerState.isPaused){
                        isPlaying = false;
                        buttonPlayPause.setBackgroundResource(R.drawable.play_music);
                    }else{
                        isPlaying = true;
                        buttonPlayPause.setBackgroundResource(R.drawable.pause_music);
                    }
                });
    }
}