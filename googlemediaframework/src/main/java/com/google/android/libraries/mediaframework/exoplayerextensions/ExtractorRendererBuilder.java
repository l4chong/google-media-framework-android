/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.libraries.mediaframework.exoplayerextensions;

import android.content.Context;
import android.media.MediaCodec;
import android.net.Uri;
import android.widget.TextView;

import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.extractor.Extractor;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.text.TextTrackRenderer;
import com.google.android.exoplayer.text.tx3g.Tx3gParser;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.google.android.libraries.mediaframework.exoplayerextensions.ExoplayerWrapper.RendererBuilder;
import com.google.android.libraries.mediaframework.exoplayerextensions.ExoplayerWrapper.RendererBuilderCallback;

//import com.google.android.exoplayer.ExoPlay.player.DemoPlayer.RendererBuilder;
//import com.google.android.exoplayer.demo.player.DemoPlayer.RendererBuilderCallback;

/**
 * A {@link RendererBuilder} for streams that can be read using an {@link com.google.android.exoplayer.extractor.Extractor}.
 */
public class ExtractorRendererBuilder implements RendererBuilder {

  private static final int BUFFER_SIZE = 10 * 1024 * 1024;

    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 160;

  private final String userAgent;
  private final Uri uri;
  private final TextView debugTextView;
  private final Extractor extractor;
  private final Context mContext;

  public ExtractorRendererBuilder(String userAgent, Uri uri, TextView debugTextView,
      Extractor extractor, Context context) {
    this.mContext = context;
    this.userAgent = userAgent;
    this.uri = uri;
    this.debugTextView = debugTextView;
    this.extractor = extractor;
  }

//  @Override
//  public void buildRenderers(ExoplayerWrapper player, RendererBuilderCallback callback) {
//    // Build the video and audio renderers.
//    DataSource dataSource = new DefaultUriDataSource(mContext, userAgent);
//    ExtractorSampleSource sampleSource = new ExtractorSampleSource(uri, dataSource, extractor, 2,
//        BUFFER_SIZE);
//    MediaCodecVideoTrackRenderer videoRenderer = new MediaCodecVideoTrackRenderer(sampleSource,
//        null, true, MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT, 5000, null, player.getMainHandler(),
//        player, 50);
//    MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource,
//        null, true, player.getMainHandler(), player);
//
//    // Build the debug renderer.
////    TrackRenderer debugRenderer = debugTextView != null
////        ? new DebugTrackRenderer(debugTextView, player, videoRenderer) : null;
//
//    // Invoke the callback.
//    TrackRenderer[] renderers = new TrackRenderer[ExoplayerWrapper.RENDERER_COUNT];
//    renderers[ExoplayerWrapper.TYPE_VIDEO] = videoRenderer;
//    renderers[ExoplayerWrapper.TYPE_AUDIO] = audioRenderer;
////    renderers[ExoplayerWrapper.TYPE_DEBUG] = debugRenderer;
//    callback.onRenderers(null, null, renderers);
//  }

    @Override
    public void buildRenderers(ExoplayerWrapper player, RendererBuilderCallback callback) {
        Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);

        // Build the video and audio renderers.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter(player.getMainHandler(),
                null);
        DataSource dataSource = new DefaultUriDataSource(mContext, bandwidthMeter, userAgent);
        ExtractorSampleSource sampleSource = new ExtractorSampleSource(uri, dataSource, extractor,
                allocator, BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE);
        MediaCodecVideoTrackRenderer videoRenderer = new MediaCodecVideoTrackRenderer(sampleSource,
                null, true, MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT, 5000, null, player.getMainHandler(),
                player, 50);
        MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource,
                null, true, player.getMainHandler(), player);
        TrackRenderer textRenderer = new TextTrackRenderer(sampleSource, player,
                player.getMainHandler().getLooper(), new Tx3gParser());

        // Invoke the callback.
        TrackRenderer[] renderers = new TrackRenderer[ExoplayerWrapper.RENDERER_COUNT];
        renderers[ExoplayerWrapper.TYPE_VIDEO] = videoRenderer;
        renderers[ExoplayerWrapper.TYPE_AUDIO] = audioRenderer;
        renderers[ExoplayerWrapper.TYPE_TEXT] = textRenderer;
        callback.onRenderers(null, null, renderers, bandwidthMeter);
    }

}
