package cn.sddman.download.mvp.p;

import java.util.List;

import cn.sddman.download.mvp.e.DownloadTaskEntity;
import cn.sddman.download.mvp.e.TorrentInfoEntity;

public interface DownloadSuccessPresenter {
    List<DownloadTaskEntity> getDownSuccessTaskList();
    void  deleTask(DownloadTaskEntity task, Boolean deleFile);
}
