package com.zom;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class TOAShamerTBI implements Transferable
{
	@NonNull
	private final BufferedImage image;

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException
	{
		if (flavor.equals(DataFlavor.imageFlavor))
		{
			return image;
		}
		else
		{
			throw new UnsupportedFlavorException(flavor);
		}
	}

	@Override
	public DataFlavor[] getTransferDataFlavors()
	{
		return new DataFlavor[]{DataFlavor.imageFlavor};
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor)
	{
		return flavor.equals(DataFlavor.imageFlavor);
	}
}