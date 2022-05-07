package just.curiosity.renderer_3d.core.polygon3d;

import just.curiosity.renderer_3d.constants.Const;
import just.curiosity.renderer_3d.core.point3d.Point3D;
import just.curiosity.renderer_3d.core.point3d.Point3DUtil;
import just.curiosity.renderer_3d.core.triangle2d.Triangle2D;
import just.curiosity.renderer_3d.core.vector3d.Vector3D;
import just.curiosity.renderer_3d.core.vector3d.Vector3DUtil;

import java.awt.*;

public final class Polygon3DUtil {
  public static void convert(Triangle2D triangle, Polygon3D polygon, Point3D originPoint, int width,
                             int height, double scale) {
    Point3DUtil.convert(triangle.p1, polygon.v1, originPoint, width, height, scale);
    Point3DUtil.convert(triangle.p2, polygon.v2, originPoint, width, height, scale);
    Point3DUtil.convert(triangle.p3, polygon.v3, originPoint, width, height, scale);
  }

  public static void rotate(Polygon3D polygon, Vector3D rotateVector) {
    Point3DUtil.rotate(polygon.v1, rotateVector);
    Point3DUtil.rotate(polygon.v2, rotateVector);
    Point3DUtil.rotate(polygon.v3, rotateVector);
  }

  public static Color lightingColor(Color color, Vector3D normalVector, Vector3D lightVector) {
    double dotProduct = Vector3DUtil.dotProduct(normalVector, lightVector);
    int lightRatio = (int) ((-1 - dotProduct) * Const.AMBIENT_LIGHT_COEFFICIENT);

    return new Color(
      correctRGBValue(color.getRed() + lightRatio),
      correctRGBValue(color.getGreen() + lightRatio),
      correctRGBValue(color.getBlue() + lightRatio));
  }

  public static int correctRGBValue(int value) {
    return Math.min(255, Math.max(0, value));
  }
}
