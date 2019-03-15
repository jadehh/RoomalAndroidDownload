package cn.sddman.download.mvp.p;

import org.xutils.common.util.KeyValue;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import cn.sddman.download.listener.MagnetSearchListener;
import cn.sddman.download.mvp.e.MagnetInfo;
import cn.sddman.download.mvp.e.MagnetRule;
import cn.sddman.download.mvp.e.MagnetSearchBean;
import cn.sddman.download.mvp.m.DownLoadModel;
import cn.sddman.download.mvp.m.DownLoadModelImp;
import cn.sddman.download.mvp.m.TaskModel;
import cn.sddman.download.mvp.m.TaskModelImp;
import cn.sddman.download.mvp.v.MagnetSearchView;
import cn.sddman.download.thread.MangetSearchTask;

public class MagnetSearchPresenterImp implements MagnetSearchPresenter {
    private MagnetSearchView magnetSearchView;
    private TaskModel taskModel;
    private DownLoadModel downLoadModel;
    public MagnetSearchPresenterImp(MagnetSearchView magnetSearchView){
        this.magnetSearchView=magnetSearchView;
        taskModel=new TaskModelImp();
        downLoadModel=new DownLoadModelImp();
    }

    @Override
    public void searchMagnet(MagnetRule rule, String keyword,String sort, int page) {
        MangetSearchTask mangetSearchTask=new MangetSearchTask(new MagnetSearchListener() {
            @Override
            public void success(List<MagnetInfo> info) {
                magnetSearchView.refreshData(info);
            }

            @Override
            public void fail(String error) {

            }
        });
        MagnetSearchBean bean=new MagnetSearchBean();
        bean.setKeyword(keyword);
        bean.setPage(page);
        bean.setRule(rule);
        bean.setSort(sort);
        mangetSearchTask.execute(bean);

    }
}
