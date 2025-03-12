/*-
 *  
 * style-converter
 *  
 * Copyright (C) 2025 Autocorrect Design HB
 *  
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *  
 */
package se.autocorrect.styleconverter.internal.xml;


import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.oscim.theme.IRenderTheme.ThemeException;
import org.oscim.theme.ThemeFile;
import org.oscim.theme.XmlRenderThemeMenuCallback;
import org.oscim.theme.XmlThemeResourceProvider;

class ThemeFileImpl implements ThemeFile {

	private String xmlStr;

	ThemeFileImpl(String xmlStr) {
		this.xmlStr = xmlStr;
	}

	@Override
	public XmlRenderThemeMenuCallback getMenuCallback() {
		return null;
	}

	@Override
	public String getRelativePathPrefix() {
		return "";
	}

	@Override
	public InputStream getRenderThemeAsStream() throws ThemeException {
		return new ByteArrayInputStream(xmlStr.getBytes());
	}

	@Override
	public XmlThemeResourceProvider getResourceProvider() {
		return null;
	}

	@Override
	public boolean isMapsforgeTheme() {
		return false;
	}

	@Override
	public void setMapsforgeTheme(boolean mapsforgeTheme) {
	}

	@Override
	public void setMenuCallback(XmlRenderThemeMenuCallback menuCallback) {
	}

	@Override
	public void setResourceProvider(XmlThemeResourceProvider resourceProvider) {
	}
}
