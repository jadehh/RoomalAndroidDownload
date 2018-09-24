package cn.sddman.download.mvp.m;

import java.util.List;

import cn.sddman.download.mvp.e.AppSettingEntity;

public interface AppSettingModel {
    List<AppSettingEntity> findAllSetting();
    void saveOrUploadSteeing(AppSettingEntity setting);
    void setSavePath(String path);
    AppSettingEntity getSavePath();
    void setDownCount(String count);
    AppSettingEntity getDownCount();
    AppSettingEntity getMobileNet();
    void setMobileNet(String net);

}
