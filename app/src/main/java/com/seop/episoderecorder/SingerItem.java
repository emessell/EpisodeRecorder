package com.seop.episoderecorder;

public class SingerItem {

    String title,episode;
    String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public SingerItem() {
    }

    public SingerItem(String title, String episode) {
        this.title = title;
        this.episode = episode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEpisode() {
        return episode;
    }

    public void setEpisode(String episode) {
        this.episode = episode;
    }

}
