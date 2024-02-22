/*
 * Copyright (c) 2020, Spedwards <https://github.com/Spedwards>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.zom.profiles.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTextField;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

public class Fields
{
	protected static final Dimension PREFERRED_SIZE = new Dimension(PluginPanel.PANEL_WIDTH - 20, 30);
	protected static final Dimension MINIMUM_SIZE = new Dimension(0, 30);
	protected static final Color FOREGROUND_COLOUR = ColorScheme.MEDIUM_GRAY_COLOR;
	protected static final Color FOREGROUND_HOVER_COLOUR = ColorScheme.DARK_GRAY_HOVER_COLOR;
	protected static final Color BACKGROUND_COLOUR = ColorScheme.DARKER_GRAY_COLOR;
	protected static final Color BACKGROUND_HOVER_COLOUR = ColorScheme.DARKER_GRAY_HOVER_COLOR;
	protected static final Color ACTIVE_COLOUR = ColorScheme.LIGHT_GRAY_COLOR;

	public static MouseAdapter getHoverAdapter(JTextField textField)
	{
		return new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				if (!textField.hasFocus())
				{
					textField.setForeground(FOREGROUND_HOVER_COLOUR);
				}
				textField.setBackground(BACKGROUND_HOVER_COLOUR);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				if (!textField.hasFocus())
				{
					textField.setForeground(FOREGROUND_COLOUR);
				}
				textField.setBackground(BACKGROUND_COLOUR);
			}
		};
	}
}
