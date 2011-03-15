package com.helper;

import java.io.File;
import java.util.List;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;


import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import com.physics.FileFinder;

public class TextureMap {
	
	private String REGEX = ".*\\.jpg|.*\\.JPG|.*\\.gif|.*\\.png|.*\\.GIF|.*\\.PNG|.*\\.bmp|.*\\.BMP";//|.*\\.flv|.*\\.mov|.*\\.pdf|.*\\.docx||.*\\.rtf");	
	
	private int CCIMG = 0;
	private int CCTXT = 1;
	private int CCQTE = 2;
	private int EMIMG = 3;
	private int EMTXT = 4;
	private int EMQTE = 5;
	private int GVIMG = 6;
	private int GVTXT = 7;
	private int GVQTE = 8;
	private int MMIMG = 9;
	private int MMTXT = 10;
	private int MMQTE = 11;
	
	private TextureData[] gvImages,gvTexts,gvQTE,emImages,emTexts,emQTE,mmImages,mmTexts,mmQTE, ccImages, ccTexts,ccQTE;
	
	public TextureMap(){
		
		gvImages = loadTextures("McLuhan/Global Village");
		gvTexts = loadTextures("McLuhan/Texts/Global Village");
		gvQTE = loadTextures("McLuhan/Quotes/Global Village");
		
		mmImages = loadTextures("McLuhan/The Medium is the Message");
		mmTexts = loadTextures("McLuhan/Texts/The Medium is the Message");
		mmQTE = loadTextures("McLuhan/Quotes/The Medium is the Message");
		
		emImages = loadTextures("McLuhan/Extensions of Man");
		emTexts = loadTextures("McLuhan/Texts/Extensions of Man");
		emQTE = loadTextures("McLuhan/Quotes/Extensions of Man");
		
		ccImages = loadTextures("McLuhan/City as Classroom");
		ccTexts = loadTextures("McLuhan/Texts/City as Classroom");
		ccQTE = loadTextures("McLuhan/Quotes/City as Classroom");
		
		
	}

	private TextureData[] loadTextures(String path) {
		File[] files;
		TextureData[] textures;
		TextureData tex = null;
		
		GLProfile.initSingleton(true);
		GLProfile glp = GLProfile.getDefault();
		
		List<File> filesAsList = FileFinder.findFiles(new File(path), REGEX);
		
		files = filesAsList.toArray(new File[filesAsList.size()]);
		
		textures = new TextureData[files.length];
		
		if(files.length==0){
			return null;
		}
		else
			for(int i=0;i<files.length;i++){
				try{
					tex = TextureIO.newTextureData(glp, files[i], true, files[i].getName());//)files[i], true);

				} catch(Exception e) {
					e.printStackTrace();
					System.exit(1);
				}
				textures[i] = tex;

			}
		
		return textures; 
	}


	public TextureData[] getMap(int opt){
		if(opt == CCIMG)
			return ccImages;
		else if(opt == CCTXT)
			return ccTexts;
		else if(opt == CCQTE)
			return ccQTE;
		else if(opt == EMIMG)
			return emImages;
		else if(opt == EMTXT)
			return emTexts;
		else if(opt == EMQTE)
			return emQTE;
		else if(opt == GVIMG)
			return gvImages;
		else if(opt == GVTXT)
			return gvTexts;
		else if(opt == GVQTE)
			return gvQTE;
		else if(opt == MMIMG)
			return mmImages;
		else if(opt == MMTXT)
			return mmTexts;
		else if(opt == MMQTE)
			return mmQTE;
		else
			return null;
	}


}
