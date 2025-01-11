package cn.kuelcancel.kurrina.management.profile.mainmenu;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cn.kuelcancel.kurrina.Kurrina;
import cn.kuelcancel.kurrina.management.file.FileManager;
import cn.kuelcancel.kurrina.management.language.TranslateText;
import cn.kuelcancel.kurrina.management.nanovg.NanoVGManager;
import cn.kuelcancel.kurrina.management.profile.mainmenu.impl.Background;
import cn.kuelcancel.kurrina.management.profile.mainmenu.impl.CustomBackground;
import cn.kuelcancel.kurrina.management.profile.mainmenu.impl.DefaultBackground;
import cn.kuelcancel.kurrina.utils.JsonUtils;
import cn.kuelcancel.kurrina.utils.file.FileUtils;
import net.minecraft.util.ResourceLocation;

public class BackgroundManager {

	private CopyOnWriteArrayList<Background> backgrounds = new CopyOnWriteArrayList<Background>();
	private CopyOnWriteArrayList<CustomBackground> removeBackgrounds = new CopyOnWriteArrayList<CustomBackground>();
	private Background currentBackground;
	
	public BackgroundManager() {
		
		FileManager fileManager = Kurrina.getInstance().getFileManager();
		File bgCacheDir = new File(fileManager.getCacheDir(), "background");
		File dataJson = new File(bgCacheDir, "Data.json");
		
		if(!bgCacheDir.exists()) {
			fileManager.createDir(bgCacheDir);
		}
		
		if(!dataJson.exists()) {
			fileManager.createFile(dataJson);
		}
		
		backgrounds.add(new DefaultBackground(0, TranslateText.FOX, new ResourceLocation("kurrina/mainmenu/background.png")));
		backgrounds.add(new DefaultBackground(1, TranslateText.NIGHT, new ResourceLocation("kurrina/mainmenu/background-night.png")));
		backgrounds.add(new DefaultBackground(2, TranslateText.DOLPHIN, new ResourceLocation("kurrina/mainmenu/background-dolphin.png")));
		backgrounds.add(new DefaultBackground(3, TranslateText.KEKE, new ResourceLocation("kurrina/mainmenu/background-keke.png")));
		backgrounds.add(new DefaultBackground(4, TranslateText.LUCA, new ResourceLocation("kurrina/mainmenu/background-lucaaus.png")));


		backgrounds.add(new DefaultBackground(999, TranslateText.ADD, null));
		
		ArrayList<String> removeImages = load();
		
		for(File f : bgCacheDir.listFiles()) {
			if(FileUtils.getExtension(f).equals("png")) {
				if(!removeImages.isEmpty() && removeImages.contains(f.getName())) {
					f.delete();
				}else {
					addCustomBackground(f);
				}
			}
		}
		
		currentBackground = getBackgroundById(0);
	}

	public ArrayList<String> load() {
		
		FileManager fileManager = Kurrina.getInstance().getFileManager();
		File bgCacheDir = new File(fileManager.getCacheDir(), "background");
		File dataJson = new File(bgCacheDir, "Data.json");
		ArrayList<String> output = new ArrayList<String>();
		
		try (FileReader reader = new FileReader(dataJson)) {
			
			Gson gson = new Gson();
			JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
			
			if(jsonObject != null) {
				
				JsonArray jsonArray = JsonUtils.getArrayProperty(jsonObject, "Remove Images");
				
				if(jsonArray != null) {
					
					Iterator<JsonElement> iterator = jsonArray.iterator();
					
					while(iterator.hasNext()) {
						
						JsonElement jsonElement = (JsonElement) iterator.next();
						JsonObject rJsonObject = gson.fromJson(jsonElement, JsonObject.class);
						
						output.add(JsonUtils.getStringProperty(rJsonObject, "Image", "null"));
					}
				}
			}
		} catch (Exception e) {}
		
		return output;
	}
	
	public void save() {
		
		FileManager fileManager = Kurrina.getInstance().getFileManager();
		File bgCacheDir = new File(fileManager.getCacheDir(), "background");
		File dataJson = new File(bgCacheDir, "Data.json");
		
		try(FileWriter writer = new FileWriter(dataJson)) {
			
			JsonObject jsonObject = new JsonObject();
			JsonArray jsonArray = new JsonArray();
			Gson gson = new Gson();
			
			for(CustomBackground bg : removeBackgrounds) {
				
				JsonObject innerJsonObject = new JsonObject();
				
				innerJsonObject.addProperty("Image", bg.getImage().getName());
				
				jsonArray.add(innerJsonObject);
			}
			
			jsonObject.add("Remove Images", jsonArray);
			
			gson.toJson(jsonObject, writer);
			
		} catch(Exception e) {}
	}
	
	public CopyOnWriteArrayList<Background> getBackgrounds() {
		return backgrounds;
	}
	
	public Background getBackgroundById(int id) {
		
		for(Background bg : backgrounds) {
			if(bg.getId() == id) {
				return bg;
			}
		}
		
		return getBackgroundById(0);
	}
	
	public int getMaxId() {
		
		int maxId = 0;
		
		for(Background bg : backgrounds) {
			if(bg.getId() != 999 && bg.getId() > maxId) {
				maxId = bg.getId();
			}
		}
		
		return maxId;
	}
	
	public void addCustomBackground(File image) {
		
		int maxId = getMaxId();
		int index = backgrounds.indexOf(getBackgroundById(999));
		
		backgrounds.add(index, new CustomBackground(maxId + 1, image.getName().replace(".png", ""), image));
	}
	
	public void removeCustomBackground(CustomBackground cusBackground) {
		
		NanoVGManager nvg = Kurrina.getInstance().getNanoVGManager();
		
		nvg.getAssetManager().removeImage(nvg.getContext(), cusBackground.getImage());
		backgrounds.remove(cusBackground);
		removeBackgrounds.add(cusBackground);
		
		save();
	}
	
	public Background getCurrentBackground() {
		return currentBackground;
	}

	public void setCurrentBackground(Background currentBackground) {
		this.currentBackground = currentBackground;
	}
}