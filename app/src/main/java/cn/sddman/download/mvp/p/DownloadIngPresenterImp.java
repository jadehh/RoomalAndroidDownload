package cn.sddman.download.mvp.p;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;

import com.xunlei.downloadlib.XLTaskHelper;
import com.xunlei.downloadlib.parameter.XLTaskInfo;

import org.greenrobot.eventbus.EventBus;
import org.xutils.x;

import java.io.File;
import java.util.List;

import cn.sddman.download.R;
import cn.sddman.download.common.MessageEvent;
import cn.sddman.download.common.Msg;
import cn.sddman.download.common.Const;
import cn.sddman.download.mvp.e.DownloadTaskEntity;
import cn.sddman.download.mvp.m.DownLoadModel;
import cn.sddman.download.mvp.m.DownLoadModelImp;
import cn.sddman.download.mvp.m.TaskModel;
import cn.sddman.download.mvp.m.TaskModelImp;
import cn.sddman.download.mvp.v.DownLoadIngView;
import cn.sddman.download.util.AppSettingUtil;
import cn.sddman.download.util.FileTools;
import cn.sddman.download.util.SystemConfig;
import cn.sddman.download.util.Util;

public class DownloadIngPresenterImp implements DownloadIngPresenter {
    private DownLoadIngView downLoadIngView;
    private TaskModel taskModel;
    private DownLoadModel downLoadModel;
    private List<DownloadTaskEntity> list;
    private Boolean isLoop=true;

    Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0) {
                List<DownloadTaskEntity> tasks=taskModel.findLoadingTask();
                if(tasks!=null) {
                    int netType=SystemConfig.getNetType();
                    if(!AppSettingUtil.getInstance().isDown()){
                        //downLoadIngView.alert("没有网络,下载暂停", Const.ERROR_ALERT);
                        for (DownloadTaskEntity task : tasks) {
                            if (task.getmTaskStatus() != Const.DOWNLOAD_STOP) {
                                stopTask(task);
                                task.setmTaskStatus(Const.DOWNLOAD_WAIT);
                                taskModel.upDataTask(task);
                            }
                        }
                    }else {
                        int downCount= AppSettingUtil.getInstance().getDownCount();
                        List<DownloadTaskEntity> downs=taskModel.findDowningTask();
                        int wait=downs==null?0:downs.size()-downCount;
                        for (DownloadTaskEntity task : tasks) {
                            if(wait>0){
                                if(task.getmTaskStatus()!=Const.DOWNLOAD_WAIT && task.getmTaskStatus()!=Const.DOWNLOAD_FAIL) {
                                    wait--;
                                    stopTask(task);
                                    task.setmTaskStatus(Const.DOWNLOAD_WAIT);
                                    taskModel.upDataTask(task);
                                    continue;
                                }
                            }
                            if (task.getmTaskStatus() != Const.DOWNLOAD_STOP && task.getmTaskStatus() != Const.DOWNLOAD_WAIT) {
                                XLTaskInfo taskInfo = XLTaskHelper.instance(x.app().getApplicationContext()).getTaskInfo(task.getTaskId());
                                task.setTaskId(taskInfo.mTaskId);
                                task.setmTaskStatus(taskInfo.mTaskStatus);
                                task.setmDCDNSpeed(taskInfo.mAdditionalResDCDNSpeed);
                                task.setmDownloadSpeed(taskInfo.mDownloadSpeed);
                                if (taskInfo.mTaskId != 0) {
                                    task.setmFileSize(taskInfo.mFileSize);
                                    task.setmDownloadSize(taskInfo.mDownloadSize);
                                }
                                taskModel.upDataTask(task);
//                                if (task.getThumbnail() == null) {
//                                    Bitmap bitmap = FileTools.getVideoThumbnail(task.getLocalPath() + File.separator + task.getmFileName(), 200, 200, MediaStore.Video.Thumbnails.MICRO_KIND);
//                                    if (bitmap != null) {
//                                        task.setThumbnail(bitmap);
//                                    }
//                                }
                                if (taskInfo.mTaskStatus == Const.DOWNLOAD_SUCCESS) {
                                    EventBus.getDefault().postSticky(new MessageEvent(new Msg(Const.MESSAGE_TYPE_REFRESH_DATA, task)));
                                    String suffix = task.getmFileName().substring(task.getmFileName().lastIndexOf(".") + 1).toUpperCase();
                                    if ("TORRENT".equals(suffix)) {
                                        downLoadIngView.openFile(task);
                                        //Boolean b = downLoadModel.startTorrentTask(task);
                                        //if (!b) downLoadIngView.alert(x.app().getString(R.string.bt_file_error), Const.ERROR_ALERT);
                                    }
                                }
                            }else{
                                if(wait<0 && task.getmTaskStatus()==Const.DOWNLOAD_WAIT){
                                    startTask(task);
                                }
                            }
                        }
                    }
                    downLoadIngView.refreshData(tasks);
                }
                if(isLoop) {
                    handler.sendMessageDelayed(handler.obtainMessage(0, null), 2000);
                }
            }
        }
    };
    public DownloadIngPresenterImp(DownLoadIngView downLoadIngView){
        this.downLoadIngView=downLoadIngView;
        taskModel=new TaskModelImp();
        downLoadModel=new DownLoadModelImp();
        list=taskModel.findLoadingTask();
        downLoadIngView.initTaskListView(list);
        refreshData();
    }
    @Override
    public List<DownloadTaskEntity> getDownloadingTaskList() {
        return list;
    }

    @Override
    public void startTask(DownloadTaskEntity task) {
        int netType=SystemConfig.getNetType();
        if(netType==Const.NET_TYPE_UNKNOW) {
            downLoadIngView.alert("没有网络,下载暂停", Const.ERROR_ALERT);
            return;
        }else if(!AppSettingUtil.getInstance().isMobileNetDown() && netType==Const.NET_TYPE_MOBILE){
            downLoadIngView.alert("设置不允许允许流量下载,请在设置里开启流量下载", Const.ERROR_ALERT);
            return;
        }
        int downCount= AppSettingUtil.getInstance().getDownCount();
        List<DownloadTaskEntity> downs=taskModel.findDowningTask();
        if(downCount<=downs.size()){
            task.setmTaskStatus(Const.DOWNLOAD_WAIT);
            taskModel.upDataTask(task);
            return;
        }
        //List<DownloadTaskEntity> tasks=taskModel.findLoadingTask();
        downLoadModel.startTask(task);
    }

    @Override
    public void stopTask(DownloadTaskEntity task) {
        downLoadModel.stopTask(task);
    }

    @Override
    public void deleTask(DownloadTaskEntity task,Boolean deleFile) {
        downLoadModel.deleTask(task,true,deleFile);
    }

    @Override
    public void refreshData() {
        handler.sendMessage(handler.obtainMessage(0,null));
    }

    @Override
    public void stopLoop() {
        isLoop=false;
    }

    @Override
    public void clearHandler() {
        handler.removeCallbacksAndMessages(null);
    }
}
