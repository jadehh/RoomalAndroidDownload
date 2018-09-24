package cn.sddman.download.mvp.v;

import java.util.List;

import cn.sddman.download.mvp.e.TorrentInfoEntity;

public interface TorrentInfoView {
    void initTaskListView(List<TorrentInfoEntity> list);
    void itemClick(int index);
    void startTaskSuccess();
    void startTaskFail(String msg);
}
