package cn.sddman.download.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.coorchice.library.SuperTextView;

import java.io.File;
import java.util.List;

import cn.sddman.download.R;
import cn.sddman.download.mvp.e.DownloadTaskEntity;
import cn.sddman.download.mvp.v.DownLoadSuccessView;
import cn.sddman.download.util.FileTools;

public class DownloadSuccessListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<DownloadTaskEntity> list;
    private Context context;
    private DownLoadSuccessView downLoadSuccessView;
    public DownloadSuccessListAdapter(Context context, DownLoadSuccessView downLoadSuccessView, List<DownloadTaskEntity> list){
        this.context=context;
        this.downLoadSuccessView=downLoadSuccessView;
        this.list=list;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_download_success, viewGroup, false);
        return new TaskHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final DownloadTaskEntity task=list.get(i);
        TaskHolder holder=(TaskHolder)viewHolder;
        holder.bind(task);
        holder.onClick();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class TaskHolder extends RecyclerView.ViewHolder{
        private DownloadTaskEntity task;
        private TextView fileNameText,downSize;
        private ImageView fileIcon,deleTask;
        private SuperTextView btnOpen,fileIsDele;

        public TaskHolder(View itemView) {
            super(itemView);
            fileNameText = (TextView) itemView.findViewById(R.id.file_name);
            downSize = (TextView) itemView.findViewById(R.id.down_size);
            fileIcon = (ImageView) itemView.findViewById(R.id.file_icon);
            deleTask = (ImageView) itemView.findViewById(R.id.dele_task);
            btnOpen = (SuperTextView)itemView.findViewById(R.id.btn_open);
            fileIsDele = (SuperTextView)itemView.findViewById(R.id.file_is_dele);
        }
        public void bind(DownloadTaskEntity task){
            this.task=task;
            fileNameText.setText(task.getmFileName());
            fileIcon.setImageDrawable(itemView.getResources().getDrawable(FileTools.getFileIcon(task.getmFileName())));
            if(task.getThumbnail()==null){
                Bitmap bitmap=FileTools.getVideoThumbnail(task.getLocalPath()+File.separator+task.getmFileName(),250,150, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                if(bitmap!=null){
                    fileIcon.setImageBitmap(bitmap);
                }
            }
            downSize.setText(FileTools.convertFileSize(task.getmDownloadSize()));
            if(FileTools.exists(task.getLocalPath()+ File.separator+task.getmFileName())){
                fileIsDele.setVisibility(View.GONE);
                btnOpen.setVisibility(View.VISIBLE);
                fileNameText.setTextColor(itemView.getResources().getColor(R.color.dimgray));
                downSize.setTextColor(itemView.getResources().getColor(R.color.gray_8f));
                if(FileTools.isVideoFile(task.getmFileName())){
                    btnOpen.setText(itemView.getResources().getString(R.string.play));
                }else{
                    btnOpen.setText(itemView.getResources().getString(R.string.open));
                    btnOpen.setVisibility(View.INVISIBLE);
                }
            }else{
                fileIsDele.setVisibility(View.VISIBLE);
                fileNameText.setTextColor(itemView.getResources().getColor(R.color.gray_cc));
                downSize.setTextColor(itemView.getResources().getColor(R.color.gray_cc));
                //btnOpen.setText("重新下载");
                btnOpen.setVisibility(View.INVISIBLE);
            }

        }

        public void onClick(){
            btnOpen.setOnClickListener(listener);
            deleTask.setOnClickListener(listener);
        }

        private View.OnClickListener listener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.btn_open:
                        downLoadSuccessView.openFile(task);
                        break;
                    case R.id.dele_task:
                        downLoadSuccessView.deleTask(task);
                        break;
                }
            }
        };
    }
}
