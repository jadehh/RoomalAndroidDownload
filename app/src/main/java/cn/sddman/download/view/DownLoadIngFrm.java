package cn.sddman.download.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yarolegovich.lovelydialog.LovelyChoiceDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.sddman.download.R;
import cn.sddman.download.activity.PlayerActivity;
import cn.sddman.download.activity.TorrentInfoActivity;
import cn.sddman.download.adapter.DownloadingListAdapter;
import cn.sddman.download.mvp.e.DownloadTaskEntity;
import cn.sddman.download.mvp.p.DownloadIngPresenter;
import cn.sddman.download.mvp.p.DownloadIngPresenterImp;
import cn.sddman.download.mvp.v.DownLoadIngView;
import cn.sddman.download.util.FileTools;
import cn.sddman.download.util.Util;

public class DownLoadIngFrm extends Fragment implements DownLoadIngView{
    private RecyclerView recyclerView;
    private DownloadIngPresenter downloadIngPresenter;
    private DownloadingListAdapter downloadingListAdapter;
    private List<DownloadTaskEntity> list=null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frm_download_ing, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        downloadIngPresenter=new DownloadIngPresenterImp(this);

    }
    private void initView(){
        recyclerView=getView().findViewById(R.id.recyclerview);

    }

    @Override
    public void initTaskListView(List<DownloadTaskEntity> list) {
        if(list!=null && list.size()>0)
            this.list=list;
        else
            this.list=new ArrayList<>();
        LinearLayoutManager manager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        downloadingListAdapter=new DownloadingListAdapter(getContext(),this,this.list);
        recyclerView.setAdapter(downloadingListAdapter);
    }

    @Override
    public void startTask(DownloadTaskEntity task) {
        downloadIngPresenter.startTask(task);
    }

    @Override
    public void sopTask(DownloadTaskEntity task) {
        downloadIngPresenter.stopTask(task);
    }

    @Override
    public void openFile(DownloadTaskEntity task) {
        String suffix = task.getmFileName().substring(task.getmFileName().lastIndexOf(".") + 1).toUpperCase();
        if("TORRENT".equals(suffix)) {
            Intent intent = new Intent(getActivity(), TorrentInfoActivity.class);
            intent.putExtra("torrentPath", task.getLocalPath()+ File.separator+task.getmFileName());
            startActivity(intent);
        }else if(FileTools.isVideoFile(task.getmFileName())){
            Intent intent = new Intent(getActivity(), PlayerActivity.class);
            intent.putExtra("videoPath", task.getLocalPath()+File.separator+task.getmFileName());
            startActivity(intent);
        }
    }

    @Override
    public void deleTask(final DownloadTaskEntity task) {
            String[] items = new String[]{getContext().getString(R.string.dele_data_and_file)};
            new LovelyChoiceDialog(getContext())
                    .setTopColorRes(R.color.colorAccent)
                    .setTitle(R.string.determine_dele)
                    .setIcon(R.drawable.ic_error)
                    .setItemsMultiChoice(items, new LovelyChoiceDialog.OnItemsSelectedListener<String>() {
                        @Override
                        public void onItemsSelected(List<Integer> positions, List<String> items) {
                            Boolean deleFile=items.size()>0?true:false;
                            downloadIngPresenter.deleTask(task,deleFile);
                        }
                    }).show();
    }

    @Override
    public void refreshData(List<DownloadTaskEntity> tasks) {
        list.clear();
        list.addAll(tasks);
        downloadingListAdapter.notifyDataSetChanged();
    }

    @Override
    public void alert(String msg, int msgType) {
        Util.alert(this.getActivity(),msg,msgType);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        downloadIngPresenter.stopLoop();
        downloadIngPresenter.clearHandler();
        super.onDestroy();
    }
}
