package cn.sddman.download.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.coorchice.library.SuperTextView;

import java.util.List;

import cn.sddman.download.R;
import cn.sddman.download.mvp.e.DownloadTaskEntity;
import cn.sddman.download.mvp.e.TorrentInfoEntity;
import cn.sddman.download.mvp.v.DownLoadSuccessView;
import cn.sddman.download.mvp.v.TorrentInfoView;
import cn.sddman.download.util.FileTools;

public class TorrentInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<TorrentInfoEntity> list;
    private Context context;
    private TorrentInfoView torrentInfoView;
    public TorrentInfoAdapter(Context context,TorrentInfoView torrentInfoView, List<TorrentInfoEntity> list){
        this.context=context;
        this.torrentInfoView=torrentInfoView;
        this.list=list;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_torrent_info, viewGroup, false);
        return new TaskHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        final TorrentInfoEntity task=list.get(i);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                torrentInfoView.itemClick(i);
            }
        });
        TaskHolder holder=(TaskHolder)viewHolder;
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class TaskHolder extends RecyclerView.ViewHolder{
        private TorrentInfoEntity task;
        private TextView fileNameText;
        private ImageView fileIcon,fileCheckBox;
        private SuperTextView fileType,fileSize;

        public TaskHolder(View itemView) {
            super(itemView);
            fileNameText = (TextView) itemView.findViewById(R.id.file_name);
            fileIcon = (ImageView) itemView.findViewById(R.id.file_icon);
            fileCheckBox = (ImageView) itemView.findViewById(R.id.file_check_box);
            fileSize = (SuperTextView)itemView.findViewById(R.id.file_size);
            fileType = (SuperTextView)itemView.findViewById(R.id.file_type);
        }
        public void bind(TorrentInfoEntity task){
            this.task=task;
            String suffix = task.getmFileName().substring(task.getmFileName().lastIndexOf(".") + 1);
            fileNameText.setText(task.getmFileName());
            fileIcon.setImageDrawable(itemView.getResources().getDrawable(FileTools.getFileIcon(task.getmFileName())));
            fileSize.setText(FileTools.convertFileSize(task.getmFileSize()));
            fileType.setText(suffix);
            if(task.getCheck()){
                fileCheckBox.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_check));
            }else{
                fileCheckBox.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_uncheck));
            }

        }

    }
}
