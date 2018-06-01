package fun.rubicon.entities;

import de.foryasee.httprequest.HttpRequestBuilder;
import de.foryasee.httprequest.RequestResponse;
import de.foryasee.httprequest.RequestType;
import fun.rubicon.RubiconBot;
import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Leon Kappes / Lee
 * @copyright RubiconBot Dev Team 2018
 * @License GPL-3.0 License <http://rubicon.fun/license>
 */
@Deprecated
public class YouTubeVideo {

    @Getter
    private String URL;
    @Getter
    private String title;
    @Getter
    private String thumbnail;
    @Getter
    private String creator;
    @Getter
    private String ID;
    @Getter
    private String description;

    public YouTubeVideo(String videoURL) {
        if (!videoURL.startsWith("https://youtu.be/"))
            this.URL = "https://youtu.be/" + videoURL.substring(videoURL.lastIndexOf("=") + 1);
        else this.URL = videoURL;
        fetchByURL();
        Logger logger = LoggerFactory.getLogger(YouTubeVideo.class);
        if (this.title == null)
            logger.error("YouTubeVideo Get-Request failed for an unexpected Reason. Please check if there is a google Token in the Config or contact Lee");
    }

    private void fetchByURL() {
        this.ID = this.URL.substring(this.URL.lastIndexOf("/") + 1);
        HttpRequestBuilder fetcher = new HttpRequestBuilder("https://www.googleapis.com/youtube/v3/videos", RequestType.GET)
                .addParameter("part", "snippet")
                .addParameter("id", this.ID)
                .addParameter("key", RubiconBot.getConfiguration().has("google_token") ? RubiconBot.getConfiguration().getString("google_token") : null);
        try {
            RequestResponse response = fetcher.sendRequest();
            JSONObject root = new JSONObject(response.getResponseMessage());
            JSONObject item = (JSONObject) ((JSONObject) ((JSONArray) root.get("items")).get(0)).get("snippet");

            this.description = (String) item.get("description");
            this.title = (String) item.get("title");
            this.thumbnail = (String) ((JSONObject) ((JSONObject) item.get("thumbnails")).get("maxres")).get("url");
            this.creator = (String) item.get("channelTitle");

        } catch (IOException ignored) {
            this.title = null;
        }
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
