package cn.sddman.download.mvp.v;

import java.util.List;

import cn.sddman.download.mvp.e.DownloadTaskEntity;

public interface DownLoadIngView {
    void startTask(DownloadTaskEntity task);
    void sopTask(DownloadTaskEntity task);
    void openFile(DownloadTaskEntity task);
    void deleTask(DownloadTaskEntity task);
    void refreshData( List<DownloadTaskEntity> tasks);
    void alert(String msg,int msgType);
}
