package cn.sddman.download.mvp.e;

import com.xunlei.downloadlib.parameter.TorrentFileInfo;

public class TorrentInfoEntity {
    private int mFileIndex;
    private String mFileName;
    private long mFileSize;
    private int mRealIndex;
    private String mSubPath;
    private String playUrl;
    private String hash;
    private Boolean isCheck=false;

    public int getmFileIndex() {
        return mFileIndex;
    }

    public void setmFileIndex(int mFileIndex) {
        this.mFileIndex = mFileIndex;
    }

    public String getmFileName() {
        return mFileName;
    }

    public void setmFileName(String mFileName) {
        this.mFileName = mFileName;
    }

    public long getmFileSize() {
        return mFileSize;
    }

    public void setmFileSize(long mFileSize) {
        this.mFileSize = mFileSize;
    }

    public int getmRealIndex() {
        return mRealIndex;
    }

    public void setmRealIndex(int mRealIndex) {
        this.mRealIndex = mRealIndex;
    }

    public String getmSubPath() {
        return mSubPath;
    }

    public void setmSubPath(String mSubPath) {
        this.mSubPath = mSubPath;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Boolean getCheck() {
        return isCheck;
    }

    public void setCheck(Boolean check) {
        isCheck = check;
    }
}
