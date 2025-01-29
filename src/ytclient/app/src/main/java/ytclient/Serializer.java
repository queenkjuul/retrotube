package ytclient;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import org.schabi.newpipe.extractor.*;
import org.schabi.newpipe.extractor.channel.ChannelInfoItem;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.services.youtube.ItagItem;
import org.schabi.newpipe.extractor.services.youtube.extractors.YoutubeStreamExtractor;
import org.schabi.newpipe.extractor.stream.*;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Serializer {
    public static void mediaFormatToJsonObject(JsonObject object, MediaFormat format) {
        object.put("name", format.getName());
        object.put("suffix", format.getSuffix());
        object.put("mimeType", format.getMimeType());
    }

    public static void itagItemToJsonObject(JsonObject object, ItagItem itagItem) {
        object.put("bitrate", itagItem.getBitrate());
        object.put("codec", itagItem.getCodec());
        object.put("itagType", itagItem.itagType.name());
        object.put("approxDurationMs", itagItem.getApproxDurationMs());
        object.put("height", itagItem.getHeight());
        object.put("audioChannels", itagItem.getAudioChannels());
        object.put("audioTrackId", itagItem.getAudioTrackId());
        object.put("audioTrackName", itagItem.getAudioTrackName());
        object.put("audioTrackType", itagItem.getAudioTrackType());
        object.put("averageBitrate", itagItem.getAverageBitrate());
        object.put("contentLength", itagItem.getContentLength());
        object.put("fps", itagItem.getFps());
        object.put("height", itagItem.getHeight());
        object.put("width", itagItem.getWidth());
        object.put("initStart", itagItem.getInitStart());
        object.put("indexEnd", itagItem.getIndexEnd());
        object.put("initStart", itagItem.getInitStart());
        object.put("initEnd", itagItem.getInitEnd());
        object.put("targetDurationSec", itagItem.getTargetDurationSec());
        object.put("sampleRate", itagItem.getSampleRate());
        object.put("resolutionString", itagItem.getResolutionString());
        object.put("quality", itagItem.getQuality());

        JsonObject jsonMediaFormat = new JsonObject();
        MediaFormat mediaFormat = itagItem.getMediaFormat();
        mediaFormatToJsonObject(jsonMediaFormat, mediaFormat);
        object.put("mediaFormat", jsonMediaFormat);
    }

    public static void streamsToJsonArray(JsonArray array, List<? extends Stream> streams) {
        streams.forEach(stream -> {
            JsonObject jsonStream = new JsonObject();
            streamToJsonObject(jsonStream, stream);
            array.add(jsonStream);
        });
    }

    public static void streamToJsonObject(JsonObject object, Stream stream) {
        object.put("id", stream.getId());
        object.put("content", stream.getContent());
        object.put("isUrl", stream.isUrl());
        JsonObject jsonMediaFormat = new JsonObject();
        if (stream.getFormat() != null) {
            mediaFormatToJsonObject(jsonMediaFormat, stream.getFormat());
        }
        object.put("mediaFormat", jsonMediaFormat);
        object.put("deliveryMethod", stream.getDeliveryMethod().name());
        object.put("manifestUrl", stream.getManifestUrl());
        // TODO: DRY up with an interface?
        if (stream instanceof AudioStream) {
            AudioStream audioStream = (AudioStream) stream;
            audioStreamToJsonObject(object, audioStream);
        } else if (stream instanceof VideoStream) {
            VideoStream videoStream = (VideoStream) stream;
            videoStreamToJsonObject(object, videoStream);
        } else if (stream instanceof SubtitlesStream) {
            SubtitlesStream subtitlesStream = (SubtitlesStream) stream;
            subtitlesStreamToJsonObject(object, subtitlesStream);
        }
    }

    public static void audioStreamToJsonObject(JsonObject object, AudioStream audioStream) {
        object.put("bitrate", audioStream.getBitrate());
        object.put("codec", audioStream.getCodec());
        object.put("indexStart", audioStream.getIndexStart());
        object.put("indexEnd", audioStream.getIndexEnd());
        object.put("initStart", audioStream.getInitStart());
        object.put("initEnd", audioStream.getInitEnd());
        object.put("itag", audioStream.getItag());
        JsonObject jsonItagItem = new JsonObject();
        ItagItem itagItem = audioStream.getItagItem();
        if (itagItem != null) {
            itagItemToJsonObject(jsonItagItem, itagItem);
        }
        object.put("itagItem", jsonItagItem);
        // TODO: DRY up above with AVStream interface
        object.put("locale", audioStream.getAudioLocale());
        object.put("audioTrackId", audioStream.getAudioTrackId());
        object.put("audioTrackName", audioStream.getAudioTrackName());
        object.put("audioTrackType", audioStream.getAudioTrackType());
        object.put("averageBitrate", audioStream.getAverageBitrate());
    }

    private static void videoStreamToJsonObject(JsonObject object, VideoStream videoStream) {
        object.put("bitrate", videoStream.getBitrate());
        object.put("codec", videoStream.getCodec());
        object.put("indexStart", videoStream.getIndexStart());
        object.put("indexEnd", videoStream.getIndexEnd());
        object.put("initStart", videoStream.getInitStart());
        object.put("initEnd", videoStream.getInitEnd());
        object.put("itag", videoStream.getItag());
        JsonObject jsonItagItem = new JsonObject();
        ItagItem itagItem = videoStream.getItagItem();
        if (itagItem != null) {
            itagItemToJsonObject(jsonItagItem, itagItem);
        }
        object.put("itagItem", jsonItagItem);
        // TODO: DRY up above with AVStream interface
        object.put("height", videoStream.getHeight());
        object.put("width", videoStream.getWidth());
        object.put("fps", videoStream.getFps());
        object.put("quality", videoStream.getQuality());
        object.put("resolution", videoStream.getResolution());
        object.put("isVideoOnly", videoStream.isVideoOnly());
    }

    public static void imagesToJsonArray(JsonArray jsonArray, List<Image> images) {
        images.forEach(image -> {
            JsonObject jsonImage = new JsonObject();
            imageToJsonObject(jsonImage, image);
            jsonArray.add(jsonImage);
        });
    }

    public static void imageToJsonObject(JsonObject object, Image image) {
        object.put("url", image.getUrl());
        object.put("height", image.getHeight());
        object.put("width", image.getWidth());
        object.put("estimatedResolution", image.getEstimatedResolutionLevel().toString());
    }

    public static void infoItemsToJsonArray(JsonArray jsonArray, List<? extends InfoItem> items) {
        items.forEach(item -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.put("name", item.getName());
            jsonObject.put("url", item.getUrl());
            JsonArray thumbnails = new JsonArray();
            imagesToJsonArray(thumbnails, item.getThumbnails());
            jsonObject.put("thumbnails", thumbnails);
            if (item instanceof ChannelInfoItem) {
                ChannelInfoItem channelInfoItem = (ChannelInfoItem) item;
                jsonObject.put("type", "channel");
                jsonObject.put("description", channelInfoItem.getDescription());
                jsonObject.put("streamCount", channelInfoItem.getStreamCount());
                jsonObject.put("subscriberCount", channelInfoItem.getSubscriberCount());
                jsonObject.put("isVerified", channelInfoItem.isVerified());
            } else if (item instanceof StreamInfoItem) {
                StreamInfoItem streamInfoItem = (StreamInfoItem) item;
                jsonObject.put("type", "stream");
                List<Image> uploaderAvatars = streamInfoItem.getUploaderAvatars();
                JsonArray jsonAvatars = new JsonArray();
                imagesToJsonArray(jsonAvatars, uploaderAvatars);
                jsonObject.put("uploaderAvatars", jsonAvatars);
                jsonObject.put("shortDescription", streamInfoItem.getShortDescription());
                jsonObject.put("uploaderName", streamInfoItem.getUploaderName());
                jsonObject.put("uploaderUrl", streamInfoItem.getUploaderUrl());
                jsonObject.put("uploadDate", streamInfoItem.getTextualUploadDate());
                jsonObject.put("duration", streamInfoItem.getDuration());
                jsonObject.put("viewCount", streamInfoItem.getViewCount());
                jsonObject.put("isShortFormContent", streamInfoItem.isShortFormContent());
                jsonObject.put("isUploaderVerified", streamInfoItem.isUploaderVerified());
            }
            jsonArray.add(jsonObject);
        });
    }

    public static void streamExtractorToJsonObject(JsonObject object, StreamExtractor streamExtractor) throws ExtractionException, IOException {
        JsonArray jsonAudioStreams = new JsonArray();
        streamsToJsonArray(jsonAudioStreams, streamExtractor.getAudioStreams());
        object.put("audioStreams", jsonAudioStreams);
        object.put("description", streamExtractor.getDescription().getContent());
        object.put("ageLimit", streamExtractor.getAgeLimit());
        object.put("category", streamExtractor.getCategory());
        JsonArray jsonRelatedItems = new JsonArray();
        List<InfoItem> relatedItems = ((YoutubeStreamExtractor) streamExtractor).getRelatedItems().getItems();
        infoItemsToJsonArray(jsonRelatedItems, relatedItems);
        object.put("relatedItems", jsonRelatedItems);
        object.put("dashMpdUrl", streamExtractor.getDashMpdUrl());
        object.put("dislikeCount", streamExtractor.getDislikeCount());
        object.put("errorMessage", streamExtractor.getErrorMessage());

        JsonArray jsonFrames = new JsonArray();
        List<Frameset> frames = streamExtractor.getFrames();
        framesetsToJsonArray(jsonFrames, frames);
        object.put("frames", jsonFrames);

        object.put("hlsUrl", streamExtractor.getHlsUrl());
        object.put("host", streamExtractor.getHost());
        object.put("languageInfo", streamExtractor.getLanguageInfo());
        object.put("length", streamExtractor.getLength());
        object.put("licence", streamExtractor.getLicence());
        object.put("likeCount", streamExtractor.getLikeCount());
        object.put("metaInfo", streamExtractor.getMetaInfo());
        object.put("privacy", streamExtractor.getPrivacy().toString());
        object.put("streamSegments", streamExtractor.getStreamSegments());
        object.put("streamType", streamExtractor.getStreamType().name());

        JsonArray jsonSubChannelAvatars = new JsonArray();
        List<Image> subChannelAvatars = streamExtractor.getSubChannelAvatars();
        imagesToJsonArray(jsonSubChannelAvatars, subChannelAvatars);
        object.put("subChannelAvatars", jsonSubChannelAvatars);

        object.put("subChannelName", streamExtractor.getSubChannelName());
        object.put("subChannelUrl", streamExtractor.getSubChannelUrl());

        JsonArray jsonSubtitles = new JsonArray();
        List<SubtitlesStream> subtitles = streamExtractor.getSubtitles(MediaFormat.SRT);
        streamsToJsonArray(jsonSubtitles, subtitles);
        object.put("subtitles", jsonSubtitles);

        JsonArray jsonSubtitlesDefault = new JsonArray();
        List<SubtitlesStream> subtitlesDefault = streamExtractor.getSubtitlesDefault();
        streamsToJsonArray(jsonSubtitlesDefault, subtitlesDefault);
        object.put("subtitlesDefault", jsonSubtitlesDefault);

        object.put("supportInfo", streamExtractor.getSupportInfo());
        object.put("tags", streamExtractor.getTags());
        object.put("textualUploadDate", streamExtractor.getTextualUploadDate());
        object.put("timeStamp", streamExtractor.getTimeStamp());

        JsonArray jsonThumbnails = new JsonArray();
        List<Image> thumbnails = streamExtractor.getThumbnails();
        imagesToJsonArray(jsonThumbnails, thumbnails);
        object.put("thumbnails", jsonThumbnails);

        JsonArray jsonUploaderAvatars = new JsonArray();
        List<Image> uploaderAvatars = streamExtractor.getUploaderAvatars();
        imagesToJsonArray(jsonUploaderAvatars, uploaderAvatars);
        object.put("uploaderAvatars", jsonUploaderAvatars);

        object.put("uploaderName", streamExtractor.getUploaderName());
        object.put("uploaderUrl", streamExtractor.getUploaderUrl());
        object.put("uploaderSubscriberCount", streamExtractor.getUploaderSubscriberCount());

        JsonArray jsonVideoOnlyStreams = new JsonArray();
        List<VideoStream> videoOnlyStreams = streamExtractor.getVideoOnlyStreams();
        streamsToJsonArray(jsonVideoOnlyStreams, videoOnlyStreams);
        object.put("videoOnlyStreams", jsonVideoOnlyStreams);

        JsonArray jsonVideoStreams = new JsonArray();
        List<VideoStream> videoStreams = streamExtractor.getVideoStreams();
        streamsToJsonArray(jsonVideoStreams, videoStreams);
        object.put("videoStreams", jsonVideoStreams);

        object.put("viewCount", streamExtractor.getViewCount());
        object.put("isShortFormContent", streamExtractor.isShortFormContent());
        object.put("isUploaderVerified", streamExtractor.isUploaderVerified());
    }

    public static void subtitlesStreamToJsonObject(JsonObject jsonSubtitle, SubtitlesStream subtitlesStream) {
        JsonObject jsonItagItem = new JsonObject();
        if (subtitlesStream.getItagItem() != null) {
            itagItemToJsonObject(jsonItagItem, subtitlesStream.getItagItem());
        }
        jsonSubtitle.put("itagItem", jsonItagItem);
        jsonSubtitle.put("extension", subtitlesStream.getExtension());
        jsonSubtitle.put("displayLanguageName", subtitlesStream.getDisplayLanguageName());
        jsonSubtitle.put("languageTag", subtitlesStream.getLanguageTag());
        JsonObject jsonLocale = new JsonObject();
        Locale locale = subtitlesStream.getLocale();
        localeToJsonObject(jsonLocale, locale);
        jsonSubtitle.put("locale", jsonLocale);
        jsonSubtitle.put("isAutoGenerated", subtitlesStream.isAutoGenerated());
    }

    public static void localeToJsonObject(JsonObject object, Locale locale) {
        object.put("displayName", locale.getDisplayName());
        object.put("language", locale.getLanguage());
        object.put("displayLanguage", locale.getDisplayLanguage());
        object.put("country", locale.getCountry());
        object.put("displayCountry", locale.getDisplayCountry());
        object.put("variant", locale.getVariant());
        object.put("displayVariant", locale.getDisplayVariant());
        object.put("script", locale.getScript());
        object.put("displayScript", locale.getDisplayScript());
        object.put("ISO3Language", locale.getISO3Language());
        object.put("ISO3Country", locale.getISO3Country());
        object.put("extensionKeys", locale.getExtensionKeys());
        object.put("unicodeLocaleKeys", locale.getUnicodeLocaleKeys());
        object.put("unicodeLocaleAttributes", locale.getUnicodeLocaleAttributes());
        object.put("hasExtensions", locale.hasExtensions());
        object.put("toLanguageTag", locale.toLanguageTag());
    }

    public static void framesetsToJsonArray(JsonArray array, List<Frameset> framesets) {
        framesets.forEach(frameset -> {
            JsonObject jsonFrameset = new JsonObject();
            framesetToJsonObject(jsonFrameset, frameset);
        });
    }

    public static void framesetToJsonObject(JsonObject object, Frameset frameset) {
        object.put("durationPerFrame", frameset.getDurationPerFrame());
        object.put("frameHeight", frameset.getFrameHeight());
        object.put("urls", frameset.getUrls());
        object.put("framesPerPageX", frameset.getFramesPerPageX());
        object.put("framesPerPageY", frameset.getFramesPerPageY());
        object.put("frameHeight", frameset.getFrameHeight());
        object.put("frameWidth", frameset.getFrameWidth());
        object.put("totalCount", frameset.getTotalCount());
    }
}
