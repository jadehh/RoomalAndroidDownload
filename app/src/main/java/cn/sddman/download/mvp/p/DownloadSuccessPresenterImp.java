package cn.sddman.download.mvp.p;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.xunlei.downloadlib.XLTaskHelper;
import com.xunlei.downloadlib.parameter.TorrentInfo;
import com.xunlei.downloadlib.parameter.XLTaskInfo;

import org.xutils.x;

import java.io.File;
import java.util.List;

import cn.sddman.download.R;
import cn.sddman.download.common.Const;
import cn.sddman.download.mvp.e.DownloadTaskEntity;
import cn.sddman.download.mvp.m.DownLoadModel;
import cn.sddman.download.mvp.m.DownLoadModelImp;
import cn.sddman.download.mvp.m.TaskModel;
import cn.sddman.download.mvp.m.TaskModelImp;
import cn.sddman.download.mvp.v.DownLoadIngView;
import cn.sddman.download.mvp.v.DownLoadSuccessView;
import cn.sddman.download.util.FileTools;
import cn.sddman.download.util.Util;

public class DownloadSuccessPresenterImp implements DownloadSuccessPresenter {
    private DownLoadSuccessView downLoadSuccessView;
    private TaskModel taskModel;
    private DownLoadModel downLoadModel;
    private List<DownloadTaskEntity> list;


    public DownloadSuccessPresenterImp(DownLoadSuccessView downLoadSuccessView){
        this.downLoadSuccessView=downLoadSuccessView;
        taskModel=new TaskModelImp();
        downLoadModel=new DownLoadModelImp();
        list=taskModel.findSuccessTask();
        downLoadSuccessView.initTaskListView(list);
    }
    @Override
    public List<DownloadTaskEntity> getDownSuccessTaskList() {
        list=taskModel.findSuccessTask();
        return list;
    }



    @Override
    public void deleTask(DownloadTaskEntity task,Boolean deleFile) {
        Boolean b=downLoadModel.deleTask(task,deleFile);
        if(b) {
            downLoadSuccessView.refreshData();
            downLoadSuccessView.alert(x.app().getString(R.string.dele_success), Const.SUCCESS_ALERT);
        }else{
            downLoadSuccessView.alert(x.app().getString(R.string.dele_fail), Const.ERROR_ALERT);
        }
    }


}
