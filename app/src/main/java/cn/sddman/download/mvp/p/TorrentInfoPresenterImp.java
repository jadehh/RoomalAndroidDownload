package cn.sddman.download.mvp.p;

import com.xunlei.downloadlib.XLTaskHelper;
import com.xunlei.downloadlib.parameter.TorrentInfo;

import org.xutils.x;
import java.io.File;
import java.util.List;

import cn.sddman.download.R;
import cn.sddman.download.mvp.e.DownloadTaskEntity;
import cn.sddman.download.mvp.e.TorrentInfoEntity;
import cn.sddman.download.mvp.m.DownLoadModel;
import cn.sddman.download.mvp.m.DownLoadModelImp;
import cn.sddman.download.mvp.m.TaskModel;
import cn.sddman.download.mvp.m.TaskModelImp;
import cn.sddman.download.mvp.v.TorrentInfoView;

public class TorrentInfoPresenterImp implements TorrentInfoPresenter {
    private TorrentInfoView torrentInfoView;
    private String torrentPath;
    private TaskModel taskModel;
    private DownLoadModel downLoadModel;
    private DownloadTaskEntity task;
    private List<TorrentInfoEntity> list=null;
    public TorrentInfoPresenterImp(TorrentInfoView torrentInfoView,String torrentPath){
        this.torrentInfoView=torrentInfoView;
        this.torrentPath=torrentPath;
        taskModel=new TaskModelImp();
        downLoadModel=new DownLoadModelImp();
        list=downLoadModel.getTorrentInfo(torrentPath);
        torrentInfoView.initTaskListView(list);

    }

    @Override
    public void startTask(List<TorrentInfoEntity> checkList) {
        //String path=task.getLocalPath()+ File.separator+task.getmFileName();
        TorrentInfo torrentInfo= XLTaskHelper.instance(x.app().getApplicationContext()).getTorrentInfo(torrentPath);
        List<DownloadTaskEntity> tasks=taskModel.findTaskByHash(torrentInfo.mInfoHash);
        if(tasks!=null && tasks.size()>0){
            torrentInfoView.startTaskFail(x.app().getString(R.string.task_earlier_has));
        }else{
            downLoadModel.startTorrentTask(torrentPath);
            torrentInfoView.startTaskSuccess();
        }
    }
}
