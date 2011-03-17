package com.helper;

import java.io.File;

import com.jogamp.opengl.util.texture.TextureData;

public class TDShell {
	
	private String path;
	private TextureData[] td;
	private File[] files;
	
	public TDShell(String path){
		this.path = path;
		td = null;
		files = null;
	}
	
	public String getPath(){
		return path;
	}
	
	public TextureData[] getTD(){
		return td;
	}
	
	public File[] getFiles(){
		return files;
	}
	
	public void setTD(TextureData[] td){
		this.td = td;
	}
	
	public void setFiles(File[] files){
		this.files = files;
	}
	
	

}
