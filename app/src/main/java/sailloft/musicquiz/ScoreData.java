package sailloft.musicquiz;

/**
 * Created by davidpos on 8/22/15.
 */
public class ScoreData {
    private String userName;
    private String score;
    private String playlistId;
    private String playlistName;
    private String playListIcon;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public String getPlayListIcon() {
        return playListIcon;
    }

    public void setPlayListIcon(String playListIcon) {
        this.playListIcon = playListIcon;
    }
}
