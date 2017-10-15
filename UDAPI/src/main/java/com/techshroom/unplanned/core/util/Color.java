package com.techshroom.unplanned.core.util;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collection;

import com.flowpowered.math.vector.Vector4d;
import com.flowpowered.math.vector.Vector4f;
import com.flowpowered.math.vector.Vector4i;
import com.google.auto.value.AutoValue;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Doubles;

@AutoValue
public abstract class Color {

    private static final String HEX2D = "%02x";

    private static final Multimap<Integer, Color> STATIC_COLORS = HashMultimap.create();

    public static final Color BLACK = addColor(createDirectOpaque(0, 0, 0));

    public static final Color RED = addColor(createDirectOpaque(0xFF, 0, 0));

    public static final Color GREEN = addColor(createDirectOpaque(0, 0xFF, 0));

    public static final Color BLUE = addColor(createDirectOpaque(0, 0, 0xFF));

    public static final Color LIGHT_GRAY = addColor(createDirectOpaque(0xC0, 0xC0, 0xC0));

    public static final Color GRAY = addColor(createDirectOpaque(0x80, 0x80, 0x80));

    public static final Color WHITE = addColor(createDirectOpaque(0xFF, 0xFF, 0xFF));

    public static final Color TRANSPARENT = addColor(createDirect(0, 0, 0, 0));

    private static int hash(int red, int green, int blue, int alpha) {
        int h = 1;
        h *= 1000003;
        h ^= red;
        h *= 1000003;
        h ^= green;
        h *= 1000003;
        h ^= blue;
        h *= 1000003;
        h ^= alpha;
        return h;
    }

    private static Color addColor(Color color) {
        // color.hashCode uses static hash function
        int key = color.hashCode();
        STATIC_COLORS.put(key, color);
        return color;
    }

    private static Color filterStatic(int red, int green, int blue, int alpha) {
        int key = hash(red, green, blue, alpha);
        Collection<Color> choices = STATIC_COLORS.get(key);
        for (Color color : choices) {
            if (color.getRed() == red &&
                    color.getGreen() == green &&
                    color.getBlue() == blue &&
                    color.getAlpha() == alpha) {
                return color;
            }
        }
        return createDirect(red, green, blue, alpha);
    }

    @SuppressWarnings("fallthrough")
    public static Color fromString(String color) {
        String c;
        if (color.startsWith("#")) {
            c = color.substring(1);
        } else {
            c = color;
        }
        int r;
        int g;
        int b;
        int a = 255;
        try {
            switch (c.length()) {
                case 4:
                    // RGBA, RGB handled below
                    a = Integer.parseInt(c.substring(3, 4), 16) * 0x11;
                case 3:
                    // RGB
                    r = Integer.parseInt(c.substring(0, 1), 16) * 0x11;
                    g = Integer.parseInt(c.substring(1, 2), 16) * 0x11;
                    b = Integer.parseInt(c.substring(2, 3), 16) * 0x11;
                    break;
                case 8:
                    // RRGGBBAA, RRGGBB handled below
                    a = Integer.parseInt(c.substring(6, 8), 16);
                case 6:
                    // RRGGBB
                    r = Integer.parseInt(c.substring(0, 2), 16);
                    g = Integer.parseInt(c.substring(2, 4), 16);
                    b = Integer.parseInt(c.substring(4, 6), 16);
                    break;
                default:
                    throw new IllegalArgumentException("Not a hex color: " + color);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Not a hex color: " + color, e);
        }
        return fromInt(r, g, b, a);
    }

    public static Color fromInt(int red, int green, int blue, int alpha) {
        return filterStatic(red, green, blue, alpha);
    }

    public static Color fromFloat(float red, float green, float blue, float alpha) {
        return fromInt(toInt(red), toInt(green), toInt(blue), toInt(alpha));
    }

    public static Color fromHsl(Vector4d hsl) {
        double h = hsl.getX();
        double s = hsl.getY();
        double l = hsl.getZ();
        double a = hsl.getW();
        double r;
        double g;
        double b;

        if (s == 0) {
            r = g = b = l; // achromatic
        } else {
            double q = l <= 0.5 ? l * (1 + s) : l + s - l * s;
            double p = 2 * l - q;
            r = hue2rgb(p, q, h + 1 / 3d);
            g = hue2rgb(p, q, h);
            b = hue2rgb(p, q, h - 1 / 3d);
        }
        return fromInt(toInt(r), toInt(g), toInt(b), toInt(a));
    }

    private static double hue2rgb(double p, double q, double t) {
        if (t < 0) {
            t += 1;
        }
        if (t > 1) {
            t -= 1;
        }
        if (t < 1 / 6d) {
            return p + (q - p) * 6 * t;
        }
        if (t < 1 / 2d) {
            return q;
        }
        if (t < 2 / 3d) {
            return p + (q - p) * (2 / 3d - t) * 6;
        }
        return p;
    }

    private static int toInt(double component) {
        return (int) (component * 0xFF);
    }

    private static Color createDirectOpaque(int red, int green, int blue) {
        return createDirect(red, green, blue, 0xFF);
    }

    private static Color createDirect(int red, int green, int blue, int alpha) {
        checkArgument(0 <= red && red <= 0xFF, "red out of bounds");
        checkArgument(0 <= green && green <= 0xFF, "green out of bounds");
        checkArgument(0 <= blue && blue <= 0xFF, "blue out of bounds");
        checkArgument(0 <= alpha && alpha <= 0xFF, "alpha out of bounds");
        return new AutoValue_Color(red, green, blue, alpha);
    }

    Color() {
    }

    public abstract int getRed();

    public abstract int getGreen();

    public abstract int getBlue();

    public abstract int getAlpha();

    /**
     * @return an integer with the colors order (least significant to most)
     *         ABGR.
     */
    public final int asABGRInt() {
        int red = getRed();
        int green = getGreen() << 8;
        int blue = getBlue() << 16;
        int alpha = getAlpha() << 24;
        return red | green | blue | alpha;
    }

    public final Vector4i asVector4i() {
        return new Vector4i(getRed(), getGreen(), getBlue(), getAlpha());
    }

    public final Vector4f asVector4f() {
        return asVector4i().toFloat().div(255);
    }

    public final Color lighter() {
        Vector4d hsl = toHsl();
        hsl = hsl.mul(1, 1, 1.1, 1);
        // add small fraction if already zero
        if (hsl.getZ() == 0) {
            hsl = hsl.add(0, 0, 0.05, 0);
        }
        return fromHsl(hsl);
    }

    public final Color darker() {
        Vector4d hsl = toHsl();
        hsl = hsl.mul(1, 1, 0.90, 1);
        return fromHsl(hsl);
    }

    @Override
    public final int hashCode() {
        return hash(getRed(), getGreen(), getBlue(), getAlpha());
    }

    @Override
    public final String toString() {
        return String.format("#" + HEX2D + HEX2D + HEX2D + HEX2D, getRed(), getGreen(), getBlue(), getAlpha());
    }

    public final Vector4d toHsl() {
        double r = getRed() / 255d;
        double g = getGreen() / 255d;
        double b = getBlue() / 255d;
        double a = getAlpha() / 255d;

        double max = Doubles.max(r, g, b);
        double min = Doubles.min(r, g, b);
        double h;
        double s;
        double l = (max + min) / 2;

        if (max == min) {
            h = s = 0;
        } else {
            double d = max - min;
            s = l > 0.5 ? d / (2 - max - min) : d / (max + min);
            if (max == r) {
                h = (g - b) / d + (g < b ? 6 : 0);
            } else if (max == g) {
                h = (b - r) / d + 2;
            } else if (max == b) {
                h = (r - g) / d + 4;
            } else {
                throw new IllegalStateException();
            }
            h /= 6;
        }

        return Vector4d.from(h, s, l, a);
    }

}
