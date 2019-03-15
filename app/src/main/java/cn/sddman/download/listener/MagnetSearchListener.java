package cn.sddman.download.listener;

import java.util.List;

import cn.sddman.download.mvp.e.MagnetInfo;

public interface MagnetSearchListener {
    void success(List<MagnetInfo> info);
    void fail(String error);
}
