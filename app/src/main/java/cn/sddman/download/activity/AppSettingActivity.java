package cn.sddman.download.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.ess.filepicker.FilePicker;
import com.ess.filepicker.model.EssFile;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

import cn.sddman.download.R;
import cn.sddman.download.common.BaseActivity;
import cn.sddman.download.common.Const;
import cn.sddman.download.mvp.p.AppSettingPresenter;
import cn.sddman.download.mvp.p.AppSettingPresenterImp;
import cn.sddman.download.mvp.v.AppSettingView;

@ContentView(R.layout.activity_app_setting)
public class AppSettingActivity extends BaseActivity implements AppSettingView {
    @ViewInject(R.id.local_path_text)
    private TextView localPathText;
    @ViewInject(R.id.down_count_text)
    private TextView downCountText;
    @ViewInject(R.id.mobile_net)
    private Switch mobileNetSwitch;

    private AppSettingPresenter appSettingPresenter;
    final private static int REQUEST_CODE_CHOOSE=10086;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setTopBarTitle(getString(R.string.setting));
        appSettingPresenter=new AppSettingPresenterImp(this);
        initView();
    }
    private void initView(){
        mobileNetSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int net=b?Const.MOBILE_NET_OK:Const.MOBILE_NET_NOT;
                appSettingPresenter.setMobileNet(net+"");
            }
        });
    }
    @Override
    public void initSetting(String key, String value) {
        if(Const.SAVE_PATH_KEY.equals(key)){
            localPathText.setText(value);
        }else if(Const.DOWN_COUNT_KEY.equals(key)){
            downCountText.setText(value);
        }else if(Const.MOBILE_NET_KEY.equals(key)){
            Boolean check=value.equals(Const.MOBILE_NET_OK+"")?true:false;
            mobileNetSwitch.setChecked(check);
        }
    }


    @Event(value = R.id.set_local_path)
    private void setLocalPathClick(View view) {
        FilePicker.from(AppSettingActivity.this)
                .chooseForFloder()
                .isSingle()
                .setMaxCount(0)
                //.setFileTypes("TORRENT")
                .requestCode(REQUEST_CODE_CHOOSE)
                .start();
    }
    @Event(value = R.id.down_count_plus)
    private void downCountPlusClick(View view) {
        int count=Integer.valueOf(downCountText.getText().toString());
        if(count<Const.MAX_DOWN_COUNT){
            count++;
            downCountText.setText(count+"");
            appSettingPresenter.setDownCount(count+"");
        }
    }
    @Event(value = R.id.down_count_cut)
    private void downCountCutClick(View view) {
        int count=Integer.valueOf(downCountText.getText().toString());
        if(count>Const.MIN_DOWN_COUNT){
            count--;
            downCountText.setText(count+"");
            appSettingPresenter.setDownCount(count+"");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_CHOOSE) {
            ArrayList<EssFile> fileList = data.getParcelableArrayListExtra(com.ess.filepicker.util.Const.EXTRA_RESULT_SELECTION);
            localPathText.setText(fileList.get(0).getAbsolutePath());
            appSettingPresenter.setSavePath(fileList.get(0).getAbsolutePath());
        }
    }

}
