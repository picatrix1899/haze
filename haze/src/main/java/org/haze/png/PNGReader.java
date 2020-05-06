package org.haze.png;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class PNGReader
{
	public Image read(String path) throws IOException
	{
		return read(ImageIO.read(new File(path)));
	}
	
	public Image read(File file) throws IOException
	{
		return read(ImageIO.read(file));
	}

	private Image read(BufferedImage image) throws IOException
	{	
		int[] pixels = new int[image.getWidth() * image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

		Image data = new Image();
		data.pixels = pixels;
		data.width = image.getWidth();
		data.height = image.getHeight();
		
		return data;
	}
}
