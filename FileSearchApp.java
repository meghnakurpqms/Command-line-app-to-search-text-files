package com.example.practice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;



public class FileSearchApp {

	String path;
	String regex;
	String zipFileName;
	Pattern pattern;
	List<File> zipFiles = new ArrayList<File>();
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FileSearchApp app = new FileSearchApp();
		switch(Math.min(args.length, 3))
		{
			case 0: System.out.println("USAGE : FileSearchApp path [regex] [zipfile]");return;
			case 3: app.setZipFileName(args[2]);
			case 1: app.setPath(args[0]);
			case 2: app.setRegex(args[1]);
			System.out.println("App.get path:"+app.getPath());
			
			try{
				app.walkDirectoryJava8(app.getPath());
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

	}
//	public void walkDirectoryJava6(String path)throws IOException
//	{
//		File dir = new File(path);
//		File[] files = dir.listFiles();
//		
//		for(File file: files)
//		{
//			if(file.isDirectory())
//			{
//				walkDirectoryJava6(file.getAbsolutePath());
//			}
//			else
//			{
//				processFile(file);
//			}
//		}
//	}
//	public void walkDirectoryJava7(String path) throws IOException {
//		Files.walkFileTree(Paths.get(path), new SimpleFileVisitor<Path>() {
//			@Override
//			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) 
//					throws IOException {
//				processFile(file.toFile());
//				return FileVisitResult.CONTINUE;
//			}
//		});
//	}
	
	public void walkDirectoryJava8(String path)throws IOException
	{
		Files.walk(Paths.get(path)).forEach(f->processFile(f.toFile()));
		zipFilesJava7();
	}
	
	
	public void zipFilesJava7() throws IOException {
		try (ZipOutputStream out = 
				new ZipOutputStream(new FileOutputStream(getZipFileName())) ) {
			File baseDir = new File(getPath());
			
			for (File file : zipFiles) {
				// fileName must be a relative path, not an absolute one.
				String fileName = getRelativeFilename(file, baseDir);
				
				ZipEntry zipEntry = new ZipEntry(fileName);
				zipEntry.setTime(file.lastModified());
				out.putNextEntry(zipEntry);
				
				Files.copy(file.toPath(), out);
				
				out.closeEntry();
			}
		}
	}
private String getRelativeFilename(File file, File baseDir) {
	// TODO Auto-generated method stub
	
	String fileName = file.getAbsolutePath().substring(
			baseDir.getAbsolutePath().length());
	
	// IMPORTANT: the ZipEntry file name must use "/", not "\".
	fileName = fileName.replace('\\', '/');
	
	while (fileName.startsWith("/")) {
		fileName = fileName.substring(1);
	}
	
	return fileName;
}
//	public void walkDirectory(String path)
//	{
//		System.out.println("IN walk directory with path:"+path);
//		searchfile(null);
//		addfiletozip(null);
//	}
	public void processFile(File file) {
		System.out.println("processFile: " + file);
		try {
			if(searchfile(file))
				addfiletozip(file);
		} catch (IOException|UncheckedIOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error processing file:" + file +"  error:"+e);
		}
	}
	
	public boolean searchfile(File file)throws IOException
	{
		System.out.println("In search file :" + file);
		return searchFileJava8(file);
	}
	public boolean searchFileJava8(File file) throws IOException {
		return Files.lines(file.toPath(), StandardCharsets.UTF_8)
			.anyMatch(t -> searchText(t));
	}
	private boolean searchText(String t) {
		System.out.println("check:"+this.getRegex());
		if(this.getRegex() == null)
			return true;
		System.out.println("out:"+this.pattern.matcher(t).matches());
		return this.pattern.matcher(t).matches();
	}

	public void addfiletozip(File file)
	{
		if(getZipFileName()!=null)
			zipFiles.add(file);
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.pattern = Pattern.compile(regex);
		this.regex = regex;
	}
	public String getZipFileName() {
		return zipFileName;
	}
	public void setZipFileName(String zipFileName) {
		this.zipFileName = zipFileName;
	}

}
