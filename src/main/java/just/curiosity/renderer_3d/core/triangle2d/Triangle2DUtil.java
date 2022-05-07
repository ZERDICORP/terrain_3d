package just.curiosity.renderer_3d.core.triangle2d;

import just.curiosity.renderer_3d.core.point2d.Point2D;
import just.curiosity.renderer_3d.core.point2d.Point2DUtil;
import just.curiosity.renderer_3d.core.line2d.Line2DUtil;

public class Triangle2DUtil {
  public static boolean triangleVisibleOnScreen(Triangle2D t, int width, int height) {
    /*
     * If at least one of the vertices is in the rectangle, then
     * the triangle is visible on the screen.
     * */

    if (Point2DUtil.inRange(t.p1.x, t.p1.y, width, height) ||
      Point2DUtil.inRange(t.p2.x, t.p2.y, width, height) ||
      Point2DUtil.inRange(t.p3.x, t.p3.y, width, height)) {
      return true;
    }

    /*
     * Sides of the screen rectangle.
     * */

    final Point2D tL = new Point2D(0, 0);
    final Point2D tR = new Point2D(width, 0);
    final Point2D bL = new Point2D(0, height);
    final Point2D bR = new Point2D(width, height);

    /*
     * If one of the sides of the t intersects with any of
     * the sides of the screen rectangle, then the t is
     * visible on the screen.
     * */

    return (Line2DUtil.intersects(t.p3, t.p1, tL, tR) ||
      Line2DUtil.intersects(t.p3, t.p1, tR, bR) ||
      Line2DUtil.intersects(t.p3, t.p1, bR, bL) ||
      Line2DUtil.intersects(t.p3, t.p1, tL, bL))
      ||
      (Line2DUtil.intersects(t.p2, t.p3, tL, tR) ||
        Line2DUtil.intersects(t.p2, t.p3, tR, bR) ||
        Line2DUtil.intersects(t.p2, t.p3, bR, bL) ||
        Line2DUtil.intersects(t.p2, t.p3, tL, bL))
      ||
      (Line2DUtil.intersects(t.p1, t.p2, tL, tR) ||
        Line2DUtil.intersects(t.p1, t.p2, tR, bR) ||
        Line2DUtil.intersects(t.p1, t.p2, bR, bL) ||
        Line2DUtil.intersects(t.p1, t.p2, tL, bL));
  }
}
