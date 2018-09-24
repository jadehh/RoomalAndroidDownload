package cn.sddman.download.common;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.coorchice.library.SuperTextView;
import com.irozon.sneaker.Sneaker;

import net.steamcrafted.loadtoast.LoadToast;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import cn.sddman.download.R;

public class BaseActivity extends AppCompatActivity {
    private TextView topBarTitle=null;
    private SuperTextView closeView=null;
    protected LoadToast lt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
//        lt = new LoadToast(this);
//        lt.setTranslationY(200);
//        lt.setTextColor(Color.WHITE).setBackgroundColor(getResources().getColor(R.color.colorMain)).setProgressColor(Color.WHITE);
    }

    protected void setTopBarTitle(int title) {
        if(topBarTitle==null) {
            topBarTitle = findViewById(R.id.topBarTitle);
        }
        topBarTitle.setText(title);
    }
    protected void setTopBarTitle(String title) {
        if(topBarTitle==null) {
            topBarTitle = findViewById(R.id.topBarTitle);
        }
        topBarTitle.setText(title);
    }

    protected void hideCloseView(){
        if(closeView==null){
            closeView=findViewById(R.id.close_view);
        }
        closeView.setVisibility(View.GONE);
    }

    @Event(value = R.id.close_view)
    private void closeView(View view) {
        finish();
    }
}
