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
package se.autocorrect.styleconverter.json;


import java.net.URL;

public class JsonStyle {

	private String id;

	private int version;
	private String name;

	private float[] center;
	private float zoom;
	private int bearing;
	private int pitch;

	private JsonStyleMetaData metadata;
	private JsonStyleSources sources;

	private URL sprite;
	private URL glyphs;

	private JsonStyleLayerData[] layers;

	public String getId() {
		return id;
	}

	public int getVersion() {
		return version;
	}

	public String getName() {
		return name;
	}

	public float[] getCenter() {
		return center;
	}

	public float getZoom() {
		return zoom;
	}

	public int getBearing() {
		return bearing;
	}

	public int getPitch() {
		return pitch;
	}

	public JsonStyleMetaData getMetadata() {
		return metadata;
	}

	public JsonStyleSources getSources() {
		return sources;
	}

	public URL getGlyphs() {
		return glyphs;
	}

	public URL getSprite() {
		return sprite;
	}

	public JsonStyleLayerData[] getLayers() {
		return layers;
	}
}
