package com.hht.filebrowser.files;

import java.util.List;

public class FileTextManager {
	private static FileTextManager mInstance;
	private List<FileText> mFileTexts;
	private FileTextManager() {
	}
	public static FileTextManager getInstance(){
		if(mInstance == null){
			mInstance = new FileTextManager();
		}
		return mInstance;
	}
	public void setFileTextList(List<FileText> list){
		mFileTexts = list;
	}
	public List<FileText> getFileTextList(){
		return mFileTexts;
	}
}
