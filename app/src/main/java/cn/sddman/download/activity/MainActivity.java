package cn.sddman.download.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.cocosw.bottomsheet.BottomSheet;
import com.ess.filepicker.FilePicker;
import com.ess.filepicker.model.EssFile;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

import cn.sddman.download.R;
import cn.sddman.download.common.BaseActivity;
import cn.sddman.download.common.Const;
import cn.sddman.download.util.Util;

@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {
    private BottomSheet.Builder bottomSheet=null;

    private static final int REQUEST_CODE_CHOOSE = 10086;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBottomMenu();
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

                                break;
                            case R.id.url:
                                Intent intent =new Intent(MainActivity.this,UrlDownLoadActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.bt:
                                FilePicker.from(MainActivity.this)
                                        .chooseForBrowser()
                                        .isSingle()
                                        //.setFileTypes("TORRENT")
                                        .requestCode(REQUEST_CODE_CHOOSE)
                                        .start();
                                break;

                        }
                    }
        });
    }
    @Event(value = R.id.add_download)
    private void searchClick(View view) {
        bottomSheet.show();
    }

    @Event(value = R.id.down_manage)
    private void downManageClick(View view) {
        Intent intent =new Intent(MainActivity.this,DownloadManagementActivity.class);
        startActivity(intent);
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
                Util.alert(MainActivity.this,"选择的文件不是种子文件", Const.ERROR_ALERT);
            }

        }
    }



}
