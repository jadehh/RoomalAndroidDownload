package cn.sddman.download.mvp.v;

import java.util.List;

import cn.sddman.download.mvp.e.DownloadTaskEntity;

public interface DownLoadSuccessView {
    void initTaskListView(List<DownloadTaskEntity> list);
    void deleTask(DownloadTaskEntity task);
    void openFile(DownloadTaskEntity task);
    void refreshData();
    void alert(String msg, int msgType);
}
