package cn.sddman.download.mvp.p;

public interface AppSettingPresenter {
    void initSetting();
    void setSavePath(String path);
    void setDownCount(String count);
    void setMobileNet(String net);
}
