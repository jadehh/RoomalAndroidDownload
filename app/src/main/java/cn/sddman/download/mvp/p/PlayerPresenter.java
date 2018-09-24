package cn.sddman.download.mvp.p;

public interface PlayerPresenter {
    void setHistoryCurrentPlayTimeMs();
    void uaDataPlayerTime(int currentPlayTimeMs, int durationTimeMs);
}
