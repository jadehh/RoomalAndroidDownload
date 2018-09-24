package cn.sddman.download.mvp.p;

import java.util.List;

import cn.sddman.download.mvp.e.TorrentInfoEntity;

public interface TorrentInfoPresenter {
    void startTask(List<TorrentInfoEntity> checkList);
}
