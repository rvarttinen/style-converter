/*-
 *  
 * style-converter
 *  
 * Copyright (C) 2025 Autocorrect Design HB
 * Copyright (c) 2023 Rob Camick - the HSL Color portion
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
package se.autocorrect.styleconverter.util;


import java.awt.Color;

/**
 * Collected utilities for handling conversion of RGB-values, including the
 * alpha-channel (opacity).
 * 
 * @see <a href="https://en.wikipedia.org/wiki/RGBA_color_model">Wiki article
 *      RGBA color model</a>
 */
public final class RGBUtils {
	
	/**
	 * The length of an formatted hex RGB string without opacity (alpha) channel present. 
	 */
	public static final int FORMATTED_HEX_RGBSTR_LENGTH = 7;

	private RGBUtils() {
	}

	/**
	 * Convert an Eclipse {@code RGB} instance with the given red, green and blue
	 * values to an corresponding integer using an alpha value as integer (0 to
	 * 255).
	 * 
	 * @param alpha    the opacity value (255 max opacity)
	 * @param colorRGB the color represented as a {@code RGB} instance
	 * @return the RGBA value as an integer
	 */
	public static int rgbaToInt(int alpha, RGB colorRGB) {
		return rgbaToInt(alpha, colorRGB.getRed(), colorRGB.getGreen(), colorRGB.getBlue());
	}

	/**
	 * Convert an Eclipse {@code RGB} instance with the given red, green and blue
	 * values to an corresponding integer using an alpha value as float (0.0 to
	 * 1.0).
	 * 
	 * @param alpha    the alpha parameter, a number between 0.0 (fully transparent)
	 *                 and 1.0 (fully opaque).
	 * @param colorRGB the color represented as a {@code RGB} instance
	 * @return the RGBA value as an integer
	 */
	public static int rgbaToInt(float alpha, RGB colorRGB) {

		int a = (int) Math.floor(alpha >= 1.0 ? 255 : alpha * 256.0);

		return rgbaToInt(a, colorRGB);
	}

	/**
	 * In a 32-bit integer representation of an RGB color, each color component is
	 * typically allocated 8 bits. The highest 8 bits are often used for the alpha
	 * channel (representing transparency), followed by red, green, and blue. The
	 * structure looks like this:
	 * <ul>
	 * <li>Bits 24-31: Alpha (A)</li>
	 * <li>Bits 16-23: Red (R)</li>
	 * <li>Bits 8-15: Green (G)</li>
	 * <li>Bits 0-7: Blue (B)</li>
	 * </ul>
	 * 
	 * @param alpha the opacity value (255 max opacity)
	 * @param red   the red component
	 * @param green the green component
	 * @param blue  the blue component
	 * @return the RGBA value as an integer
	 */
	public static int rgbaToInt(int alpha, int red, int green, int blue) {

		alpha = clamp(alpha, 0, 255);
		red = clamp(red, 0, 255);
		green = clamp(green, 0, 255);
		blue = clamp(blue, 0, 255);

		return (alpha << 24) | (red << 16) | (green << 8) | blue;
	}

	/**
	 * In a 32-bit integer representation of an RGB color, each color component is
	 * typically allocated 8 bits. The highest 8 bits are often used for the alpha
	 * channel (representing transparency), followed by red, green, and blue. The
	 * structure looks like this:
	 * <ul>
	 * <li>Bits 24-31: Alpha (A)</li>
	 * <li>Bits 16-23: Red (R)</li>
	 * <li>Bits 8-15: Green (G)</li>
	 * <li>Bits 0-7: Blue (B)</li>
	 * </ul>
	 * 
	 * @param alpha the opacity value (255 max opacity)
	 * @param red   the red component
	 * @param green the green component
	 * @param blue  the blue component
	 * @return the RGBA value as an hex string
	 */
	public static String rgbaToHexString(int alpha, int red, int green, int blue) {
		return String.format("#%02x%02x%02x%02x", alpha, red, green, blue);
	}

	/**
	 * In a 32-bit integer representation of an RGB color, each color component is
	 * typically allocated 8 bits. The structure looks like this:
	 * <ul>
	 * <li>Bits 16-23: Red (R)</li>
	 * <li>Bits 8-15: Green (G)</li>
	 * <li>Bits 0-7: Blue (B)</li>
	 * </ul>
	 * 
	 * @param red   the red component
	 * @param green the green component
	 * @param blue  the blue component
	 * @return the RGB value as an hex string
	 */
	public static String rgbToHexString(int red, int green, int blue) {
		return String.format("#%02x%02x%02x", red, green, blue);
	}

	/**
	 * In a 32-bit integer representation of an RGB color, each color component is
	 * typically allocated 8 bits. The highest 8 bits are often used for the alpha
	 * channel (representing transparency), followed by red, green, and blue. The
	 * structure looks like this:
	 * <ul>
	 * <li>Bits 24-31: Alpha (A)</li>
	 * <li>Bits 16-23: Red (R)</li>
	 * <li>Bits 8-15: Green (G)</li>
	 * <li>Bits 0-7: Blue (B)</li>
	 * </ul>
	 * 
	 * @param alpha    the opacity value (255 max opacity)
	 * @param colorRGB
	 * @return the RGBA value as an hex string
	 */
	public static String rgbaToHexString(int alpha, RGB colorRGB) {
		return rgbaToHexString(alpha, colorRGB.getRed(), colorRGB.getGreen(), colorRGB.getBlue());
	}

	/**
	 * In a 32-bit integer representation of an RGB color, each color component is
	 * typically allocated 8 bits. The highest 8 bits are often used for the alpha
	 * channel (representing transparency), followed by red, green, and blue. The
	 * structure looks like this:
	 * <ul>
	 * <li>Bits 24-31: Alpha (A)</li>
	 * <li>Bits 16-23: Red (R)</li>
	 * <li>Bits 8-15: Green (G)</li>
	 * <li>Bits 0-7: Blue (B)</li>
	 * </ul>
	 * 
	 * @param alpha    the opacity value (1.0f max opacity)
	 * @param colorRGB
	 * @return the RGBA value as an hex string
	 */
	public static String rgbaToHexString(float alpha, RGB colorRGB) {

		int a = (int) Math.floor(alpha >= 1.0 ? 255 : alpha * 256.0);

		return rgbaToHexString(a, colorRGB.getRed(), colorRGB.getGreen(), colorRGB.getBlue());
	}

	/**
	 * Convert a HSL color indication using individual HSL values to RGB.
	 * 
	 * @param h the Hue value in degrees between 0 - 360
	 * @param s the Saturation percentage between 0 - 100
	 * @param l the Luminance percentage between 0 - 100
	 * @return the HSL values converted to RGBA as an RGBA hex string
	 */
	public static String hslToRgbHexString(Float h, Float s, Float l) {

		HSLColor hsl = new HSLColor(h, s, l);
		Color rgb = hsl.getRGB();

		return String.format("#%02x%02x%02x", rgb.getRed(), rgb.getGreen(), rgb.getBlue());
	}

	/**
	 * Convert a HSLA color indication using individual HSL values to ARGB.
	 *
	 * @param h     the Hue value in degrees between 0 - 360
	 * @param s     the Saturation percentage between 0 - 100
	 * @param l     the Luminance percentage between 0 - 100
	 * @param alpha the alpha value between 0 - 1
	 * @return the HSLA values converted to RGBA as an RGBA hex string
	 */
	public static String hslaToRgbaHexString(float h, float s, float l, float alpha) {

		// https://tips4java.wordpress.com/2009/07/05/hsl-color/
		HSLColor hsl = new HSLColor(h, s, l, alpha);
		Color rgb = hsl.getRGB();

		return rgbaToHexString(alpha, new RGB(rgb.getRed(), rgb.getGreen(), rgb.getBlue()));
	}
	
	/**
	 * Parse an hex rgb string to an {@code RGB} instance.
	 * 
	 * @param rgbStr the string to parse
	 * @return an {@code RGB} instance
	 * @throws IllegalArgumentException if the input string is {@code null} or of
	 *                                  incorrect length
	 * @see FORMATTED_HEX_RGBSTR_LENGTH
	 */
	public static RGB parse(String rgbStr) {
		
		if(rgbStr == null || rgbStr.length() != FORMATTED_HEX_RGBSTR_LENGTH) {
			throw new IllegalArgumentException("Incorrrect RGB Hex string: " + rgbStr);
		}

		int r = Integer.valueOf(rgbStr.substring(1, 3), 16);
		int g = Integer.valueOf(rgbStr.substring(3, 5), 16);
		int b = Integer.valueOf(rgbStr.substring(5, 7), 16);

		return new RGB(r, g, b);
	}

	/**
	 * Clamp a value between a minimum and maximum.
	 * 
	 * @param value the value in question
	 * @param min   the minimum allowed
	 * @param max   the maximum allowed
	 * @return the clamped value, i.e the value itself if not exceeding the min and
	 *         max
	 */
	private static int clamp(int value, int min, int max) {
		return Math.max(min, Math.min(max, value));
	}
	
	/**
	 * A minimal implementation of HSL color space for use when converting to RGB. 
	 */
	static class HSLColor {

		private Color rgb;
		private float[] hsl;
		private float alpha;

		/**
		 * Create a HSLColor object using individual HSL values and a default alpha
		 * value of 1.0.
		 *
		 * @param h is the Hue value in degrees between 0 - 360
		 * @param s is the Saturation percentage between 0 - 100
		 * @param l is the Luminance percentage between 0 - 100
		 */
		HSLColor(float h, float s, float l) {
			this(h, s, l, 1.0f);
		}

		/**
		 * Create a HSLColor object using individual HSL values.
		 *
		 * @param h     the Hue value in degrees between 0 - 360
		 * @param s     the Saturation percentage between 0 - 100
		 * @param l     the Lumanance percentage between 0 - 100
		 * @param alpha the alpha value between 0 - 1
		 */
		HSLColor(float h, float s, float l, float alpha) {
			hsl = new float[] { h, s, l };
			this.alpha = alpha;
			rgb = toRGB(hsl, alpha);
		}

		/**
		 * Get the RGB Color object represented by this HDLColor.
		 *
		 * @return the RGB Color object.
		 */
		Color getRGB() {
			return rgb;
		}

		/**
		 * Convert HSL values to a RGB Color. H (Hue) is specified as degrees in the
		 * range 0 - 360. S (Saturation) is specified as a percentage in the range 1 -
		 * 100. L (Luminance) is specified as a percentage in the range 1 - 100.
		 *
		 * @param hsl   an array containing the 3 HSL values
		 * @param alpha the alpha value between 0 - 1
		 *
		 * @returns the RGB Color object
		 */
		static Color toRGB(float[] hsl, float alpha) {
			return toRGB(hsl[0], hsl[1], hsl[2], alpha);
		}

		/**
		 * Convert HSL values to a RGB Color.
		 *
		 * @param h     Hue is specified as degrees in the range 0 - 360.
		 * @param s     Saturation is specified as a percentage in the range 1 - 100.
		 * @param l     Luminance is specified as a percentage in the range 1 - 100.
		 * @param alpha the alpha value between 0 - 1
		 *
		 * @returns the RGB Color object
		 */
		static Color toRGB(float h, float s, float l, float alpha) {

			if (s < 0.0f || s > 100.0f) {
				throw new IllegalArgumentException("Color parameter outside of expected range - Saturation");
			}

			if (l < 0.0f || l > 100.0f) {
				throw new IllegalArgumentException("Color parameter outside of expected range - Luminance");
			}

			if (alpha < 0.0f || alpha > 1.0f) {
				throw new IllegalArgumentException("Color parameter outside of expected range - Alpha");
			}

			// Formula needs all values between 0 - 1.

			h = h % 360.0f;
			h /= 360f;
			s /= 100f;
			l /= 100f;

			float q = 0;

			if (l < 0.5) {
				q = l * (1 + s);
			}else {
				q = (l + s) - (s * l);
			}

			float p = 2 * l - q;

			float r = Math.max(0, hueToRGB(p, q, h + (1.0f / 3.0f)));
			float g = Math.max(0, hueToRGB(p, q, h));
			float b = Math.max(0, hueToRGB(p, q, h - (1.0f / 3.0f)));

			r = Math.min(r, 1.0f);
			g = Math.min(g, 1.0f);
			b = Math.min(b, 1.0f);

			return new Color(r, g, b, alpha);
		}

		private static float hueToRGB(float p, float q, float h) {

			if (h < 0)
				h += 1;

			if (h > 1)
				h -= 1;

			if (6 * h < 1) {
				return p + ((q - p) * 6 * h);
			}

			if (2 * h < 1) {
				return q;
			}

			if (3 * h < 2) {
				return p + ((q - p) * 6 * ((2.0f / 3.0f) - h));
			}

			return p;
		}
		
		@Override
		public String toString() {

			return "HSLColor[h=" + hsl[0] + ",s=" + hsl[1] + ",l=" + hsl[2] + ",alpha=" + alpha + "]";
		}
	}
}
