package net.sf.opendse.visualization;

import static java.lang.Math.max;
import static java.lang.Math.*;

import java.awt.Color;

public class Graphics {

	public static Color ALICEBLUE = new Color(0xF0F8FF);
	public static Color ANTIQUEWHITE = new Color(0xFAEBD7);
	public static Color AQUAMARINE = new Color(0x7FFFD4);
	public static Color AZURE = new Color(0xF0FFFF);
	public static Color BEIGE = new Color(0xF5F5DC);
	public static Color BISQUE = new Color(0xFFE4C4);
	public static Color BLACK = new Color(0x000000);
	public static Color BLANCHEDALMOND = new Color(0xFFEBCD);
	public static Color BLUE = new Color(0x0000FF);
	public static Color BLUEVIOLET = new Color(0x8A2BE2);
	public static Color BROWN = new Color(0xA52A2A);
	public static Color BURLYWOOD = new Color(0xDEB887);
	public static Color CADETBLUE = new Color(0x5F9EA0);
	public static Color CHARTREUSE = new Color(0x7FFF00);
	public static Color CHOCOLATE = new Color(0xD2691E);
	public static Color CORAL = new Color(0xFF7F50);
	public static Color CORNFLOWERBLUE = new Color(0x6495ED);
	public static Color CORNSILK = new Color(0xFFF8DC);
	public static Color CYAN = new Color(0x00FFFF);
	public static Color DARKGOLDENROD = new Color(0xB8860B);
	public static Color DARKGREEN = new Color(0x006400);
	public static Color DARKKHAKI = new Color(0xBDB76B);
	public static Color DARKOLIVEGREEN = new Color(0x556B2F);
	public static Color DARKORANGE = new Color(0xFF8C00);
	public static Color DARKORCHID = new Color(0x9932CC);
	public static Color DARKSALMON = new Color(0xE9967A);
	public static Color DARKSEAGREEN = new Color(0x8FBC8F);
	public static Color DARKSLATEBLUE = new Color(0x483D8B);
	public static Color DARKSLATEGRAY = new Color(0x2F4F4F);
	public static Color DARKTURQUOISE = new Color(0x00CED1);
	public static Color DARKVIOLET = new Color(0x9400D3);
	public static Color DEEPPINK = new Color(0xFF1493);
	public static Color DEEPSKYBLUE = new Color(0x00BFFF);
	public static Color DIMGRAY = new Color(0x696969);
	public static Color DODGERBLUE = new Color(0x1E90FF);
	public static Color FIREBRICK = new Color(0xB22222);
	public static Color FLORALWHITE = new Color(0xFFFAF0);
	public static Color FORESTGREEN = new Color(0x228B22);
	public static Color GAINSBORO = new Color(0xDCDCDC);
	public static Color GHOSTWHITE = new Color(0xF8F8FF);
	public static Color GOLD = new Color(0xFFD700);
	public static Color GOLDENROD = new Color(0xDAA520);
	public static Color GRAY = new Color(0x808080);
	public static Color GREEN = new Color(0x008000);
	public static Color GREENYELLOW = new Color(0xADFF2F);
	public static Color HONEYDEW = new Color(0xF0FFF0);
	public static Color HOTPINK = new Color(0xFF69B4);
	public static Color INDIANRED = new Color(0xCD5C5C);
	public static Color IVORY = new Color(0xFFFFF0);
	public static Color KHAKI = new Color(0xF0E68C);
	public static Color LAVENDER = new Color(0xE6E6FA);
	public static Color LAVENDERBLUSH = new Color(0xFFF0F5);
	public static Color LAWNGREEN = new Color(0x7CFC00);
	public static Color LEMONCHIFFON = new Color(0xFFFACD);
	public static Color LIGHTBLUE = new Color(0xADD8E6);
	public static Color LIGHTCORAL = new Color(0xF08080);
	public static Color LIGHTCYAN = new Color(0xE0FFFF);
	public static Color LIGHTGOLDENROD = new Color(0xEEDD82);
	public static Color LIGHTGOLDENRODYELLOW = new Color(0xFAFAD2);
	public static Color LIGHTGRAY = new Color(0xD3D3D3);
	public static Color LIGHTPINK = new Color(0xFFB6C1);
	public static Color LIGHTSALMON = new Color(0xFFA07A);
	public static Color LIGHTSEAGREEN = new Color(0x20B2AA);
	public static Color LIGHTSKYBLUE = new Color(0x87CEFA);
	public static Color LIGHTSLATE = new Color(0x8470FF);
	public static Color LIGHTSLATEGRAY = new Color(0x778899);
	public static Color LIGHTSTEELBLUE = new Color(0xB0C4DE);
	public static Color LIGHTYELLOW = new Color(0xFFFFE0);
	public static Color LIMEGREEN = new Color(0x32CD32);
	public static Color LINEN = new Color(0xFAF0E6);
	public static Color MAGENTA = new Color(0xFF00FF);
	public static Color MAROON = new Color(0xB03060);
	public static Color MEDIUMAQUAMARINE = new Color(0x66CDAA);
	public static Color MEDIUMBLUE = new Color(0x0000CD);
	public static Color MEDIUMORCHID = new Color(0xBA55D3);
	public static Color MEDIUMPURPLE = new Color(0x9370DB);
	public static Color MEDIUMSEAGREEN = new Color(0x3CB371);
	public static Color MEDIUMSLATEBLUE = new Color(0x7B68EE);
	public static Color MEDIUMSPRINGGREEN = new Color(0x00FA9A);
	public static Color MEDIUMTURQUOISE = new Color(0x48D1CC);
	public static Color MEDIUMVIOLET = new Color(0xC71585);
	public static Color MIDNIGHTBLUE = new Color(0x191970);
	public static Color MINTCREAM = new Color(0xF5FFFA);
	public static Color MISTYROSE = new Color(0xFFE4E1);
	public static Color MOCCASIN = new Color(0xFFE4B5);
	public static Color NAVAJOWHITE = new Color(0xFFDEAD);
	public static Color NAVY = new Color(0x000080);
	public static Color OLDLACE = new Color(0xFDF5E6);
	public static Color OLIVEDRAB = new Color(0x6B8E23);
	public static Color ORANGE = new Color(0xFFA500);
	public static Color ORANGERED = new Color(0xFF4500);
	public static Color ORCHID = new Color(0xDA70D6);
	public static Color PALEGOLDENROD = new Color(0xEEE8AA);
	public static Color PALEGREEN = new Color(0x98FB98);
	public static Color PALETURQUOISE = new Color(0xAFEEEE);
	public static Color PALEVIOLETRED = new Color(0xDB7093);
	public static Color PAPAYAWHIP = new Color(0xFFEFD5);
	public static Color PEACHPUFF = new Color(0xFFDAB9);
	public static Color PERU = new Color(0xCD853F);
	public static Color PINK = new Color(0xFFC0CB);
	public static Color PLUM = new Color(0xDDA0DD);
	public static Color POWDERBLUE = new Color(0xB0E0E6);
	public static Color PURPLE = new Color(0xA020F0);
	public static Color RED = new Color(0xFF0000);
	public static Color ROSYBROWN = new Color(0xBC8F8F);
	public static Color ROYALBLUE = new Color(0x4169E1);
	public static Color SADDLEBROWN = new Color(0x8B4513);
	public static Color SALMON = new Color(0xFA8072);
	public static Color SANDYBROWN = new Color(0xF4A460);
	public static Color SEAGREEN = new Color(0x2E8B57);
	public static Color SEASHELL = new Color(0xFFF5EE);
	public static Color SIENNA = new Color(0xA0522D);
	public static Color SKYBLUE = new Color(0x87CEEB);
	public static Color SLATEBLUE = new Color(0x6A5ACD);
	public static Color SLATEGRAY = new Color(0x708090);
	public static Color SNOW = new Color(0xFFFAFA);
	public static Color SPRINGGREEN = new Color(0x00FF7F);
	public static Color STEELBLUE = new Color(0x4682B4);
	public static Color TAN = new Color(0xD2B48C);
	public static Color THISTLE = new Color(0xD8BFD8);
	public static Color TOMATO = new Color(0xFF6347);
	public static Color TURQUOISE = new Color(0x40E0D0);
	public static Color VIOLET = new Color(0xEE82EE);
	public static Color VIOLETRED = new Color(0xD02090);
	public static Color WHEAT = new Color(0xF5DEB3);
	public static Color WHITE = new Color(0xFFFFFF);
	public static Color WHITESMOKE = new Color(0xF5F5F5);
	public static Color YELLOW = new Color(0xFFFF00);
	public static Color YELLOWGREEN = new Color(0x9ACD32);

	public static Color tone(Color color, double value) {
		double r = color.getRed();
		double g = color.getGreen();
		double b = color.getBlue();

		double scale = pow(2, -abs(value));

		r *= scale;
		g *= scale;
		b *= scale;

		if (value > 0) {
			double inv = (1 - scale) * 255;
			r += inv;
			g += inv;
			b += inv;
		}

		r = bounds(r, 0, 255);
		g = bounds(g, 0, 255);
		b = bounds(b, 0, 255);

		return new Color((int) r, (int) g, (int) b);
	}

	public static Color mix(Color... color) {
		int r = 0, g = 0, b = 0;
		for (Color c : color) {
			r += c.getRed();
			g += c.getGreen();
			b += c.getBlue();
		}
		r /= color.length;
		g /= color.length;
		b /= color.length;
		return new Color(r, g, b);
	}
	
	public static Color alpha(Color color, double alpha){
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(alpha*255));
	}

	private static double bounds(double value, double min, double max) {
		return max(min, min(value, max));
	}

}
