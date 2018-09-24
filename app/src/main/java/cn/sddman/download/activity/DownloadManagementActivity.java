package cn.sddman.download.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.cocosw.bottomsheet.BottomSheet;
import com.coorchice.library.SuperTextView;
import com.ess.filepicker.FilePicker;
import com.ess.filepicker.model.EssFile;
import com.xunlei.downloadlib.android.XLUtil;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import cn.sddman.download.R;
import cn.sddman.download.common.BaseActivity;
import cn.sddman.download.common.Const;
import cn.sddman.download.common.CusAdapter;
import cn.sddman.download.common.MessageEvent;
import cn.sddman.download.common.Msg;
import cn.sddman.download.util.SystemConfig;
import cn.sddman.download.view.DownLoadIngFrm;
import cn.sddman.download.view.DownLoadSuccessFrm;
import cn.sddman.download.mvp.p.DownloadManagementPresenter;
import cn.sddman.download.mvp.p.DownloadManagementPresenterImp;
import cn.sddman.download.mvp.v.DownloadManagementView;
import cn.sddman.download.util.Util;

@ContentView(R.layout.activity_download_management)
public class DownloadManagementActivity extends BaseActivity implements DownloadManagementView{
    @ViewInject(R.id.viewPager)
    private ViewPager viewPager;
    @ViewInject(R.id.downloading)
    private TextView downloading;
    @ViewInject(R.id.downloadfinish)
    private TextView downloadfinish;
    @ViewInject(R.id.open_add_task_pop)
    private SuperTextView openAddTaskPopBtn;
    private List<Fragment> mFragments = new ArrayList<>();
    private  Intent intent=null;
    private BottomSheet.Builder bottomSheet=null;
    private static final int REQUEST_CODE_CHOOSE = 10086;
    private static final int REQUEST_CODE_SCAN = 10010;

    private DownloadManagementPresenter downloadManagementPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        downloadManagementPresenter=new DownloadManagementPresenterImp(this);
        initViewPage();
        initBottomMenu();
        int i= SystemConfig.getNetType();

    }

    private  void initViewPage(){
        DownLoadSuccessFrm downLoadSuccessFrm=new DownLoadSuccessFrm();
        DownLoadIngFrm downLoadIngFrm=new DownLoadIngFrm();
        mFragments.add(downLoadIngFrm);
        mFragments.add(downLoadSuccessFrm);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new CusAdapter(getSupportFragmentManager(),mFragments));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) { }
            @Override
            public void onPageSelected(int i) {
                changeTab(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {}
        });
    }

    private void changeTab(int index){
        if(index==0){
            downloading.setTextColor(getResources().getColor(R.color.white));
            downloadfinish.setTextColor(getResources().getColor(R.color.trwhite));
        }else{
            downloading.setTextColor(getResources().getColor(R.color.trwhite));
            downloadfinish.setTextColor(getResources().getColor(R.color.white));
        }
    }
    @Event(value = R.id.downloading)
    private void downloadingClick(View view) {
        viewPager.setCurrentItem(0);
    }
    @Event(value = R.id.downloadfinish)
    private void downloadfinishClick(View view) {
        viewPager.setCurrentItem(1);
    }
    @Event(value = R.id.open_add_task_pop)
    private void addTaskClick(View view) {
        bottomSheet.show();
    }
    @Event(value = R.id.open_setting)
    private void appSettingClick(View view) {
        intent =new Intent(DownloadManagementActivity.this,AppSettingActivity.class);
        startActivity(intent);
    }
    private void initBottomMenu(){
        bottomSheet=new BottomSheet.Builder(this)
                .title(R.string.new_download)
                .sheet(R.menu.down_source)
                .listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.qr:
                                intent = new Intent(DownloadManagementActivity.this, CaptureActivity.class);
                                startActivityForResult(intent, REQUEST_CODE_SCAN);
                                break;
                            case R.id.url:
                                intent =new Intent(DownloadManagementActivity.this,UrlDownLoadActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.bt:
                                FilePicker.from(DownloadManagementActivity.this)
                                        .chooseForBrowser()
                                        //.chooseForFloder()
                                        .isSingle()
                                        //.setMaxCount(0)
                                        //.setFileTypes("TORRENT")
                                        .requestCode(REQUEST_CODE_CHOOSE)
                                        .start();
                                break;

                        }
                    }
                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_CHOOSE) {
            ArrayList<EssFile> fileList = data.getParcelableArrayListExtra(com.ess.filepicker.util.Const.EXTRA_RESULT_SELECTION);
            String suffix = fileList.get(0).getName().substring(fileList.get(0).getName().lastIndexOf(".") + 1).toUpperCase();
            if("TORRENT".equals(suffix)) {
                Intent intent = new Intent(this, TorrentInfoActivity.class);
                intent.putExtra("torrentPath", fileList.get(0).getAbsolutePath());
                startActivity(intent);
            }else{
                Util.alert(DownloadManagementActivity.this,"选择的文件不是种子文件", Const.ERROR_ALERT);
            }

        }else  if (requestCode == REQUEST_CODE_SCAN) {
            final String content = data.getStringExtra(Constant.CODED_CONTENT);
            new LovelyStandardDialog(this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                    .setTopColorRes(R.color.colorMain)
                    .setIcon(R.drawable.ic_success)
                    .setButtonsColorRes(R.color.colorMain)
                    .setTitle("创建任务")
                    .setMessage(content)
                    .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            downloadManagementPresenter.startTask(content);
                        }
                    })
                    .show();
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN,sticky=true)
    public void onMessageEvent(MessageEvent event) {
        Msg msg=event.getMessage();
        if(msg.getType()== Const.MESSAGE_TYPE_SWITCH_TAB){
            int index=(int)msg.getObj();
            viewPager.setCurrentItem(index);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroy() {
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void addTaskSuccess() {
        // Intent intent =new Intent(UrlDownLoadActivity.this,DownloadManagementActivity.class);
        // startActivity(intent);
       // finish();
    }

    @Override
    public void addTaskFail(String msg) {
        Util.alert(this,msg, Const.ERROR_ALERT);
    }
}
