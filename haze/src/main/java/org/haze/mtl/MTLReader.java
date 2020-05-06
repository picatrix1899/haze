package org.haze.mtl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Optional;

public class MTLReader
{
	private static final String LK_NEW_MATERIAL = "newmtl";
	
	private static final String LK_MAP_DIFFUSE = "map_Kd";
	private static final String LK_MAP_BUMPMAP = "map_Bump";
	
	public MaterialList read(String path) throws IOException
	{
		Reader reader = new FileReader(path);
		
		MaterialList info = read(reader);
		
		reader.close();
		
		return info;
	}
	
	public MaterialList read(File file) throws IOException
	{
		Reader reader = new FileReader(file);
		
		MaterialList info = read(reader);
		
		reader.close();
		
		return info;
	}
	
	private MaterialList read(Reader inputReader) throws IOException
	{
		BufferedReader reader = new BufferedReader(inputReader);

		MaterialListRaw materials = new MaterialListRaw();
		MaterialRaw material = null;
		
		String line = "";
		while((line = reader.readLine()) != null)
		{
			line = line.trim();
			
			if(line.startsWith("#") || line == "")
				continue;
			
			if(line.startsWith(LK_NEW_MATERIAL))
			{
				String name = line.replaceFirst(LK_NEW_MATERIAL, "").trim();
				
				material = new MaterialRaw();
				materials.materials.put(name, material);
				
				continue;
			}
			
			if(line.startsWith(LK_MAP_DIFFUSE))
			{
				line = line.replaceFirst(LK_MAP_DIFFUSE, "").trim();
				String[] parts = line.split(" ");
				material.mapDiffuse = parts[parts.length -1];
				continue;
			}
			
			if(line.startsWith(LK_MAP_BUMPMAP))
			{
				line = line.replaceFirst(LK_MAP_BUMPMAP, "").trim();
				String[] parts = line.split(" ");
				material.mapNormal = parts[parts.length -1];
				continue;
			}
		}
		
		MaterialList out = process(materials);
		
		return out;
	}
	
	private MaterialList process(MaterialListRaw list) throws IOException
	{
		MaterialList out = new MaterialList();
		
		for(String key : list.materials.keySet())
		{
			MaterialRaw rawMaterial = list.materials.get(key);
			
			Material material = new Material();

			material.mapDiffuse = Optional.ofNullable(rawMaterial.mapDiffuse);
			
			material.mapNormal = Optional.ofNullable(rawMaterial.mapNormal);
			
			out.materials.put(key, material);
		}
		
		return out;
	}
}
