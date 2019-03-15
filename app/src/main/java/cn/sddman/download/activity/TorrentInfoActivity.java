package cn.sddman.download.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.coorchice.library.SuperTextView;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import cn.sddman.download.R;
import cn.sddman.download.adapter.TorrentInfoAdapter;
import cn.sddman.download.common.BaseActivity;
import cn.sddman.download.common.Const;
import cn.sddman.download.common.MessageEvent;
import cn.sddman.download.common.Msg;
import cn.sddman.download.listener.GetThumbnailsListener;
import cn.sddman.download.mvp.e.TorrentInfoEntity;
import cn.sddman.download.mvp.p.TorrentInfoPresenter;
import cn.sddman.download.mvp.p.TorrentInfoPresenterImp;
import cn.sddman.download.mvp.v.TorrentInfoView;
import cn.sddman.download.thread.GetTorrentVideoThumbnailsTask;
import cn.sddman.download.util.AlertUtil;
import cn.sddman.download.util.Util;

@ContentView(R.layout.activity_torrent_info)
public class TorrentInfoActivity extends BaseActivity implements TorrentInfoView{
    @ViewInject(R.id.recyclerview)
    private RecyclerView recyclerView;
    @ViewInject(R.id.right_view)
    private SuperTextView rightBtn;
    @ViewInject(R.id.start_download)
    private LinearLayout downLinearLayout;
    private List<TorrentInfoEntity> list;
    private List<TorrentInfoEntity> checkList=new ArrayList<>();
    private TorrentInfoAdapter torrentInfoAdapter;
    private TorrentInfoPresenter torrentInfoPresenter;
    private String torrentPath;
    private boolean isCheckAll=false;
    private boolean isDown=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTopBarTitle(R.string.bt_file_info);
        //rightBtn.setText(R.string.check_all);
        Intent getIntent = getIntent();
        torrentPath=getIntent.getStringExtra("torrentPath");
        isDown=getIntent.getBooleanExtra("isDown",false);
        if(isDown){
            downLinearLayout.setVisibility(View.VISIBLE);
        }
        torrentInfoPresenter=new TorrentInfoPresenterImp(this,torrentPath);
    }

    @Override
    public void initTaskListView(List<TorrentInfoEntity> list) {
        this.list=list;
        LinearLayoutManager manager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        torrentInfoAdapter=new TorrentInfoAdapter(this,this,this.list);
        recyclerView.setAdapter(torrentInfoAdapter);
        if(!isDown){
            AlertUtil.showLoading();
            new GetTorrentVideoThumbnailsTask(new GetThumbnailsListener() {
                @Override
                public void success(Bitmap bitmap) {
                    torrentInfoAdapter.notifyDataSetChanged();
                    AlertUtil.hideLoading();
                }
            }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,list);
        }
    }

    @Override
    public void itemClick(int index) {
//        TorrentInfoEntity torrent=list.get(index);
//        if(torrent.getCheck()){
//            torrent.setCheck(false);
//            isCheckAll=false;
//            rightBtn.setText(R.string.check_all);
//        }else{
//            torrent.setCheck(true);
//        }
//        torrentInfoAdapter.notifyDataSetChanged();
//        checkList();
//        setTopBarTitle(String.format(getString(R.string.check_count),list.size()+"",checkList.size()+""));

    }

    @Override
    public void startTaskSuccess() {
        EventBus.getDefault().postSticky(new MessageEvent(new Msg(Const.MESSAGE_TYPE_SWITCH_TAB, 0)));
        finish();
    }

    @Override
    public void startTaskFail(String msg) {
        Util.alert(this,msg, Const.ERROR_ALERT);
    }

    @Override
    public boolean getIsDown() {
        return isDown;
    }

    @Override
    public void playerViedo(TorrentInfoEntity te) {
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra("videoPath", te.getPath());
        startActivity(intent);
    }

    @Event(value = R.id.right_view)
    private void chheckAllClick(View view) {
//        for(TorrentInfoEntity torrent:list){
//            torrent.setCheck(!isCheckAll);
//        }
//        torrentInfoAdapter.notifyDataSetChanged();
//        if(isCheckAll){
//            isCheckAll=false;
//            rightBtn.setText(R.string.check_all);
//            setTopBarTitle(R.string.check_file);
//        }else{
//            isCheckAll=true;
//            rightBtn.setText(R.string.cancel_check_all);
//            setTopBarTitle(String.format(getString(R.string.check_count),list.size()+"",list.size()+""));
//        }
    }
    @Event(value = R.id.start_download)
    private void startDownClick(View view) {
        checkList();
        torrentInfoPresenter.startTask(checkList);
        //finish();
    }
    private void checkList(){
        checkList.clear();
        for(TorrentInfoEntity torrent:list){
            if(torrent.getCheck()){
                checkList.add(torrent);
            }
        }
    }

}
