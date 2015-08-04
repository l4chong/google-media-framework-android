/**
 Copyright 2014 Google Inc. All rights reserved.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.google.android.libraries.mediaframework.exoplayerextensions;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.extractor.mp3.Mp3Extractor;
import com.google.android.exoplayer.extractor.mp4.Mp4Extractor;

/**
 * Generate a renderer builder appropriate for rendering a video.
 */
public class RendererBuilderFactory {

    public static AudioCapabilities mAudioCapabilities;

    public static void setAudioCapabilities(AudioCapabilities audioCapabilities){
        mAudioCapabilities = audioCapabilities;
    }
  /**
   * Create a renderer builder which can build the given video.
   * @param ctx The context (ex {@link android.app.Activity} in whicb the video has been created.
   * @param video The video which will be played.
   */
  public static ExoplayerWrapper.RendererBuilder createRendererBuilder(Context ctx,
                                                                       Video video) {
      AudioCapabilities audioCapabilities;
    switch (video.getVideoType()) {
      case HLS:
//        return new HlsRendererBuilder(ctx, ExoplayerUtil.getUserAgent(ctx),
//                                      video.getUrl(),
//                                      video.getContentId());

        return new HlsRendererBuilder(ctx, ExoplayerUtil.getUserAgent(ctx), video.getUrl(), mAudioCapabilities);
//      case DASH:
//        return new DashRendererBuilder(ctx,
//                                       ExoplayerUtil.getUserAgent(ctx),
//                                       video.getUrl(),
//                                       video.getContentId(),
//                                       new WidevineTestMediaDrmCallback(video.getContentId()),
//                                       null); // TODO: Pass in DebugTextView here.
      case MP4:
        // TODO: DebugTextView.
        return new ExtractorRendererBuilder(ExoplayerUtil.getUserAgent(ctx), Uri.parse(video.getUrl()),
                        null, new Mp4Extractor(), ctx);
    case MP3:
        // TODO: DebugTextView.
        return new ExtractorRendererBuilder(ExoplayerUtil.getUserAgent(ctx), Uri.parse(video.getUrl()),
                null, new Mp3Extractor(), ctx);
      default:
        return null;
    }
  }
}
