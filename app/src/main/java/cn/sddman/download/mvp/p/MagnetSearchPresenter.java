package cn.sddman.download.mvp.p;

import cn.sddman.download.mvp.e.MagnetRule;

public interface MagnetSearchPresenter {
    void searchMagnet(MagnetRule rule, String keyword,String sort, int page );
}
