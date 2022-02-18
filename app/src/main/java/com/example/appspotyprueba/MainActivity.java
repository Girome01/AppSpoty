package com.example.appspotyprueba;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.renderscript.ScriptGroup;
import android.util.Log;
import android.widget.ImageView;

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

    //private boolean trackWasStarted = false;

    //ImageView buttonImage;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //buttonImage = (ImageView) findViewById(R.id.bottomPlayPause);
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
                        //mSpotifyAppRemote.getPlayerApi().subscribeToPlayerState().setEventCallback(MainActivity.this::handleTrackEnded);

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

    private void connected() {
        // Play a playlist 6IyPKJq69D0Um2AOwprVbn
        mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:6IyPKJq69D0Um2AOwprVbn");

        // Subscribe to PlayerState
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;
                    if (track != null) {
                        Log.d("MainActivity", track.name + " by " + track.artist.name);
                    }
                });
    }
/*
    private void handleTrackEnded(PlayerState playerState) {
        setTrackWasStarted(playerState);

        boolean isPaused = playerState.isPaused;
        long position = playerState.playbackPosition;
        boolean hasEnded = trackWasStarted && isPaused && position == 0L;

        if (hasEnded) {
            trackWasStarted = false;
            buttonImaage.setBackgroundResource(R.drawable.play_music);

        }
    }

    private void setTrackWasStarted(PlayerState playerState) {
        long position = playerState.playbackPosition;
        long duration = playerState.track.duration;
        boolean isPlaying = !playerState.isPaused;

        if (!trackWasStarted && position > 0 && duration > 0 && isPlaying) {
            trackWasStarted = true;
            buttonImaage.setBackgroundResource(R.drawable.pause_music);
        }
    }*/
}