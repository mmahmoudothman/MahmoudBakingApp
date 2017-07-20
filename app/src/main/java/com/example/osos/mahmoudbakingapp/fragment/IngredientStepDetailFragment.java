package com.example.osos.mahmoudbakingapp.fragment;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.osos.mahmoudbakingapp.R;
import com.example.osos.mahmoudbakingapp.model.Step;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

import static com.example.osos.mahmoudbakingapp.activity.StepActivity.PANES;
import static com.example.osos.mahmoudbakingapp.activity.StepActivity.POSITION;
import static com.example.osos.mahmoudbakingapp.adapter.StepAdapter.STEPS;

public class IngredientStepDetailFragment extends Fragment implements View.OnClickListener, ExoPlayer.EventListener {

    public static final String AUTOPLAY = "autoplay";
    public static final String CURRENT_WINDOW_INDEX = "current_window_index";
    public static final String PLAYBACK_POSITION = "playback_position";
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private boolean autoPlay = false;
    private int currentWindow;
    private long playbackPosition;
    private TrackSelector trackSelector;
    private SimpleExoPlayerView mPlayerView;
    private SimpleExoPlayer mExoPlayer;
    private TextView mDescription;
    private Button mPrevious;
    private Button mNext;
    private ArrayList<Step> mSteps;
    private int mIndex;
    private int mPosition;
    private boolean isTablet;


    //              constructor
    public IngredientStepDetailFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            mSteps = savedInstanceState.getParcelableArrayList(STEPS);
            isTablet = savedInstanceState.getBoolean(PANES);
            mPosition = savedInstanceState.getInt(POSITION);
            playbackPosition = savedInstanceState.getLong(PLAYBACK_POSITION, 0);
            currentWindow = savedInstanceState.getInt(CURRENT_WINDOW_INDEX, 0);
            autoPlay = savedInstanceState.getBoolean(AUTOPLAY, false);
        }

        View rootView = inflater.inflate(R.layout.fragment_step_detail, container, false);

        mDescription = (TextView) rootView.findViewById(R.id.tv_description);
        mPlayerView = (SimpleExoPlayerView) rootView.findViewById(R.id.sepv_step_video);
        mPrevious = (Button) rootView.findViewById(R.id.bt_previous);
        mNext = (Button) rootView.findViewById(R.id.bt_next);

        mPrevious.setOnClickListener(this);
        mNext.setOnClickListener(this);

        mPosition = getArguments().getInt(POSITION);
        mIndex = mPosition ;
        isTablet = getArguments().getBoolean(PANES);

        getRetainInstance();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isTablet) {
            showStepsForTab();
        } else {
            showStepsForPhone();
        }
    }



    public void showStepsForTab() {
        mPlayerView.setVisibility(View.VISIBLE);
        mDescription.setVisibility(View.VISIBLE);
        mSteps = getArguments().getParcelableArrayList(STEPS);
        assert mSteps != null;
        mDescription.setText(mSteps.get(mIndex).getDescription());
        playStepVideo(mIndex);
    }


    private void playStepVideo(int index) {
        mPlayerView.setVisibility(View.VISIBLE);
        mPlayerView.requestFocus();
        String videoUrl = mSteps.get(index).getVideoUrl();
        String thumbNailUrl = mSteps.get(index).getThumbnailUrl();
        if (!videoUrl.isEmpty()) {
            initializePlayer(Uri.parse(videoUrl));
        } else if (!thumbNailUrl.isEmpty()) {
            initializePlayer(Uri.parse(thumbNailUrl));
        } else {
            mPlayerView.setVisibility(View.GONE);
        }
    }

    void isLandscape() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            hideSystemUi();
    }

    private void showStepsForPhone() {
        showStepsForTab();
        isLandscape();
        mPrevious.setVisibility(View.VISIBLE);
        mNext.setVisibility(View.VISIBLE);
    }

    /**
     * Initialize ExoPlayer.
     *
     * @param mediaUri The URI of the sample to play.
     */
    private void initializePlayer(Uri mediaUri) {
        mExoPlayer = null;
        // Create an instance of the ExoPlayer.

        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(getActivity(),
                null, DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF);

        TrackSelection.Factory adaptiveTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);

        trackSelector = new DefaultTrackSelector(adaptiveTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);

        // Set the ExoPlayer.EventListener to this activity.
        mExoPlayer.addListener(this);

        mPlayerView.setPlayer(mExoPlayer);
        mExoPlayer.setPlayWhenReady(true);

        mExoPlayer.seekTo(currentWindow, playbackPosition);

        // Prepare the MediaSource.
        String userAgent = Util.getUserAgent(getActivity(), "Baking App");

        MediaSource mediaSource = new ExtractorMediaSource(mediaUri,
                new DefaultDataSourceFactory(getActivity(), BANDWIDTH_METER,
                        new DefaultHttpDataSourceFactory(userAgent, BANDWIDTH_METER)),
                new DefaultExtractorsFactory(), null, null);

        mExoPlayer.prepare(mediaSource);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_next:
                if (mIndex < mSteps.size() - 1) {
                    int index = ++mIndex;
                    mDescription.setText(mSteps.get(index).getDescription());
                    playStepVideo(index);
                } else {
                    Toast.makeText(getActivity(), R.string.end_of_steps, Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.bt_previous:
                if (mIndex > 0) {
                    int index = --mIndex;
                    mDescription.setText(mSteps.get(index).getDescription());
                    playStepVideo(index);
                } else {
                    Toast.makeText(getActivity(), R.string.start_of_steps, Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        View decorView = getActivity().getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    private void showSystemUI() {
        View decorView = getActivity().getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
//        playbackPosition=mExoPlayer.getCurrentPosition();
        if (mExoPlayer != null) {
            outState.putLong(PLAYBACK_POSITION, mExoPlayer.getCurrentPosition());
            outState.putInt(CURRENT_WINDOW_INDEX, currentWindow);
            outState.putBoolean(AUTOPLAY, autoPlay);
        }
        outState.putParcelableArrayList(STEPS, mSteps);
        outState.putBoolean(PANES, isTablet);
        outState.putInt(POSITION, mPosition);

    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
            Toast.makeText(getActivity(), "Playing", Toast.LENGTH_LONG).show();
        } else if ((playbackState == ExoPlayer.STATE_READY)) {

        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException e) {
        String errorString = null;
        if (e.type == ExoPlaybackException.TYPE_RENDERER) {
            Exception cause = e.getRendererException();
            if (cause instanceof MediaCodecRenderer.DecoderInitializationException) {
                // Special case for decoder initialization failures.
                MediaCodecRenderer.DecoderInitializationException decoderInitializationException =
                        (MediaCodecRenderer.DecoderInitializationException) cause;
                if (decoderInitializationException.decoderName == null) {
                    if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
                        errorString = getString(R.string.error_querying_decoders);
                    } else if (decoderInitializationException.secureDecoderRequired) {
                        errorString = getString(R.string.error_no_secure_decoder,
                                decoderInitializationException.mimeType);
                    } else {
                        errorString = getString(R.string.error_no_decoder,
                                decoderInitializationException.mimeType);
                    }
                } else {
                    errorString = getString(R.string.error_instantiating_decoder,
                            decoderInitializationException.decoderName);
                }
            }
        }
        if (errorString != null) {
            Toast.makeText(getActivity(), errorString, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPositionDiscontinuity() {
    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
    }


}
