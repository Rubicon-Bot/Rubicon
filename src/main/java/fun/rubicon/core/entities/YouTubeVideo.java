package fun.rubicon.core.entities;

import de.foryasee.httprequest.HttpRequestBuilder;
import de.foryasee.httprequest.RequestResponse;
import de.foryasee.httprequest.RequestType;
import fun.rubicon.RubiconBot;
import fun.rubicon.util.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

/**
 * @author Leon Kappes / Lee
 * @copyright RubiconBot Dev Team 2018
 * @License GPL-3.0 License <http://rubicon.fun/license>
 */
public class YouTubeVideo {

    private String URL;
    private String title;
    private String thumbnail;
    private String creator;
    private String ID;
    private String description;

    public YouTubeVideo(String videoURL) {
        if (!videoURL.startsWith("https://youtu.be/"))
            this.URL = "https://youtu.be/" + videoURL.substring(videoURL.lastIndexOf("=") + 1);
        else this.URL = videoURL;
        fetchByURL();
        if (this.title == null)
            Logger.error("YouTubeVideo Get-Request failed for an unexpected Reason. Please check if there is a google Token in the Config or contact Lee");
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

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
