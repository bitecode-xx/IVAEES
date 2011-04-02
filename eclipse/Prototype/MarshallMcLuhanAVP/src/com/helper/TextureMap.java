package com.helper;

import java.io.File;
import java.util.List;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;


import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import com.physics.FileFinder;

/**
 * 
 * Preloading class run during inital execution of the system to preload all of the 
 * Texture data for faster rendering during canvas creation. 
 * 
 * 
 * @author Jozef
 *
 */
public class TextureMap {
	
	//regex string for image search
	private String REGEX = ".*\\.jpg|.*\\.JPG|.*\\.gif|.*\\.png|.*\\.GIF|.*\\.PNG|.*\\.bmp|.*\\.BMP";	
	private String VREGEX = ".*\\.avi|.*\\.mov|.*\\.mpg";
	//vars for specifying which texture data set to grab
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
	
	
	private TDShell gvImages,gvTexts,gvQTE, gvVid,emImages,emTexts,emQTE,emVid,mmImages,mmTexts,mmQTE,mmVid, ccImages, ccTexts,ccQTE,ccVid;
	
	public TextureMap(){
		
		//grab all the texture files
		gvImages = loadTextures("McLuhan/Global Village");
		gvTexts = loadTextures("McLuhan/Texts/Global Village");
		gvQTE = loadTextures("McLuhan/Quotes/Global Village");
		gvVid = loadVids("McLuhan/Video/Global Village");
		
		mmImages = loadTextures("McLuhan/The Medium is the Message");
		mmTexts = loadTextures("McLuhan/Texts/The Medium is the Message");
		mmQTE = loadTextures("McLuhan/Quotes/The Medium is the Message");
		mmVid = loadVids("McLuhan/Video/The Medium is the Message");
		
		emImages = loadTextures("McLuhan/Extensions of Man");
		emTexts = loadTextures("McLuhan/Texts/Extensions of Man");
		emQTE = loadTextures("McLuhan/Quotes/Extensions of Man");
		emVid = loadVids("McLuhan/Video/Extensions of Man");
		
		ccImages = loadTextures("McLuhan/City as Classroom");
		ccTexts = loadTextures("McLuhan/Texts/City as Classroom");
		ccQTE = loadTextures("McLuhan/Quotes/City as Classroom");
		ccVid = loadVids("McLuhan/Video/City as Classroom");
		
		
	}

	private TDShell loadVids(String string) {
		TDShell temp = new TDShell(string);
		File[] files;
		
		List<File> filesAsList = FileFinder.findFiles(new File(string), VREGEX);
		
		files = filesAsList.toArray(new File[filesAsList.size()]);
		temp.setFiles(files);
		
		return temp; 
	}

	/*
	 * For the speicifed path traverse the folders recurrsivly on the path and retrieve all files matching the REGEX string. 
	 */
	private TDShell loadTextures(String path) {
		TDShell temp = new TDShell(path);
		File[] files;
		
		List<File> filesAsList = FileFinder.findFiles(new File(path), REGEX);
		
		files = filesAsList.toArray(new File[filesAsList.size()]);
		temp.setFiles(files);
		//textures = new TextureData[files.length];
		
		//temp.setTD(textures);
		
		return temp; 
	}
	
	
	private TextureData[] buildTD(String[] files){
		TextureData tex = null;
		TextureData[] textures = new TextureData[files.length];
		GLProfile glp = GLProfile.getDefault();
		// no files create empty set
		if(files.length==0){
			return new TextureData[0];
		}
		else{
			//inialize the texture data for each file
			for(int i=0;i<files.length;i++){
				try{
					tex = TextureIO.newTextureData(glp, new File(files[i]), false, files[i]);//)files[i], true);

				} catch(Exception e) {
					e.printStackTrace();
					System.exit(1);
				}
				textures[i] = tex;

			}
		}
		return textures;
	}
	

	/*
	 * Retrieve the specified texture data set
	 * 
	 */
	public TextureData[] getMap(int opt){
		if(opt == CCIMG){
			
			return buildTD(ccImages.getFiles());//ccImages.getTD();
		}
		else if(opt == CCTXT){
			return buildTD(ccTexts.getFiles());//ccTexts.getTD();
			}
		else if(opt == CCQTE){
			return buildTD(ccQTE.getFiles());//ccQTE.getTD();
			}
		else if(opt == EMIMG){
			return buildTD(emImages.getFiles());//emImages.getTD();
			}
		else if(opt == EMTXT){
			return buildTD(emTexts.getFiles());//emTexts.getTD();
			}
		else if(opt == EMQTE){
			return buildTD(emQTE.getFiles());//emQTE.getTD();
			}
		else if(opt == GVIMG){
			return buildTD(gvImages.getFiles());//gvImages.getTD();
			}
		else if(opt == GVTXT){
			return buildTD(gvTexts.getFiles());//gvTexts.getTD();
			}
		else if(opt == GVQTE){
			return buildTD(gvQTE.getFiles());//gvQTE.getTD();
			}
		else if(opt == MMIMG){
			return buildTD(mmImages.getFiles());//mmImages.getTD();
			}
		else if(opt == MMTXT){
			return buildTD(mmTexts.getFiles());//mmTexts.getTD();
			}
		else if(opt == MMQTE){
			return buildTD(mmQTE.getFiles());//mmQTE.getTD();
			}
		else
			return null;
	}
	
	/*
	 * Retrieve the specified file list
	 * 
	 */
	public String[] getFiles(int opt){
		if(opt == CCIMG)
			return ccImages.getFiles();
		else if(opt == CCTXT)
			return ccTexts.getFiles();
		else if(opt == CCQTE)
			return ccQTE.getFiles();
		else if(opt == EMIMG)
			return emImages.getFiles();
		else if(opt == EMTXT)
			return emTexts.getFiles();
		else if(opt == EMQTE)
			return emQTE.getFiles();
		else if(opt == GVIMG)
			return gvImages.getFiles();
		else if(opt == GVTXT)
			return gvTexts.getFiles();
		else if(opt == GVQTE)
			return gvQTE.getFiles();
		else if(opt == MMIMG)
			return mmImages.getFiles();
		else if(opt == MMTXT)
			return mmTexts.getFiles();
		else if(opt == MMQTE)
			return mmQTE.getFiles();
		else
			return null;
	}

	public String[] getVids(int opt){
		if(opt == 0)
			return ccVid.getFiles();
		else if(opt == 1)
			return emVid.getFiles();
		else if(opt == 2)
			return gvVid.getFiles();
		else if(opt == 3)
			return mmVid.getFiles();
		else
			return null;
	}



}
