package cn.sddman.download.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.xunlei.downloadlib.XLTaskHelper;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cn.sddman.download.R;
import cn.sddman.download.common.BaseActivity;
import cn.sddman.download.common.Const;
import cn.sddman.download.mvp.p.UrlDownLoadPresenter;
import cn.sddman.download.mvp.p.UrlDownLoadPresenterImp;
import cn.sddman.download.mvp.v.UrlDownLoadView;
import cn.sddman.download.util.Util;

@ContentView(R.layout.activity_url_download)
public class UrlDownLoadActivity extends BaseActivity implements UrlDownLoadView{
    @ViewInject(R.id.url_input)
    private EditText urlInput;

    private UrlDownLoadPresenter urlDownLoadPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setTopBarTitle(R.string.new_download);
        XLTaskHelper.init(getApplicationContext());
        urlDownLoadPresenter=new UrlDownLoadPresenterImp(this);
    }

    @Event(value = R.id.start_download)
    private void startDownloadClick(View view) {
        urlDownLoadPresenter.startTask(urlInput.getText().toString().trim());
    }

    @Override
    public void addTaskSuccess() {
       // Intent intent =new Intent(UrlDownLoadActivity.this,DownloadManagementActivity.class);
       // startActivity(intent);
        finish();
    }

    @Override
    public void addTaskFail(String msg) {
        Util.alert(this,msg, Const.ERROR_ALERT);
    }
}
