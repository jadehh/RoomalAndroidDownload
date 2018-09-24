package cn.sddman.download.mvp.m;

import java.util.List;

import cn.sddman.download.mvp.e.PlayerVideoEntity;

public interface PlayerVideoModel {
    List<PlayerVideoEntity> findAllVideo();
    List<PlayerVideoEntity> findVideoByPath(String path);
    PlayerVideoEntity findVideoById(int id);
    PlayerVideoEntity saveOrUpdata(PlayerVideoEntity video);
    PlayerVideoEntity upDataVideo(PlayerVideoEntity video);
    Boolean deleVideo(PlayerVideoEntity video);
}
