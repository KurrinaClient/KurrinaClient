package cn.kuelcancel.kurrina.management.music.ytdlp;

import java.io.File;

import cn.kuelcancel.kurrina.Kurrina;
import cn.kuelcancel.kurrina.management.file.FileManager;

public class Ytdlp {

	public boolean download(String url) {
		
		try {
			
			FileManager fileManager = Kurrina.getInstance().getFileManager();
			File ytdlpFile = new File(fileManager.getExternalDir(), "ytdlp/ytdlp.exe");
			File ffmpegDir = new File(fileManager.getExternalDir(), "ffmpeg/bin");
			
			Runtime rt = Runtime.getRuntime();
			Process p = rt.exec('"' + ytdlpFile.getAbsolutePath() + '"' + 
					" -x --embed-thumbnail --audio-format mp3" + 
					" --ffmpeg-location " + '"' + ffmpegDir.getAbsolutePath() + '"' + " " +
					" -f bestaudio -o " + 
					'"' + fileManager.getMusicDir().getAbsolutePath() + "/" + "%(title)s.%(ext)s" + '"' + " " + 
					url);
			
			int exitCode = p.waitFor();
			
			if(exitCode != 0) {
				return false;
			}
			
			p.destroy();
			
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}