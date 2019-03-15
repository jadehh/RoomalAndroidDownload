package cn.sddman.download.mvp.v;

import java.util.List;

import cn.sddman.download.mvp.e.MagnetInfo;

public interface MagnetSearchView {
    void refreshData(List<MagnetInfo> info);
    void moreOption(MagnetInfo magnet);
}
