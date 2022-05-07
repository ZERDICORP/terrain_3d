package just.curiosity.renderer_3d.core;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import just.curiosity.renderer_3d.constants.Const;
import just.curiosity.renderer_3d.core.lambda.OnReload;
import just.curiosity.renderer_3d.core.point2d.Point2DUtil;
import just.curiosity.renderer_3d.core.point3d.Point3D;
import just.curiosity.renderer_3d.core.point3d.Point3DUtil;
import just.curiosity.renderer_3d.core.polygon3d.Polygon3D;
import just.curiosity.renderer_3d.core.polygon3d.Polygon3DUtil;
import just.curiosity.renderer_3d.core.triangle2d.Triangle2D;
import just.curiosity.renderer_3d.core.triangle2d.Triangle2DUtil;
import just.curiosity.renderer_3d.core.vector3d.Vector3D;
import just.curiosity.renderer_3d.gui.Window;
import just.curiosity.renderer_3d.gui.interaction.Keyboard;
import just.curiosity.renderer_3d.gui.interaction.Mouse;

public final class Renderer3D {
  private final int width;
  private final int height;
  private final Window window;
  private final Mouse mouse;
  private final Keyboard keyboard;
  private final Point3D cameraPoint;
  private final Point3D originPoint;
  private final Vector3D lightVector;
  private final Vector3D rotateVector;
  private final double[] depthBuffer;
  private final int[] pixelBuffer;
  private List<Polygon3D> polygons;
  private double scale;
  private boolean isRunning;
  private OnReload onReload;

  {
    mouse = new Mouse();
    keyboard = new Keyboard();
    cameraPoint = new Point3D(200, 0, 0);
    originPoint = new Point3D(0, 0, 0);
    lightVector = new Vector3D(1, 1, 1);
    rotateVector = new Vector3D(0, 0, 0);
    polygons = new ArrayList<>();
    scale = 150;
    isRunning = false;
  }

  public Renderer3D(int width, int height) {
    this.width = width;
    this.height = height;

    depthBuffer = new double[width * height];

    BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    pixelBuffer = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer())
      .getData();

    window = new Window(bufferedImage, mouse, keyboard);
  }

  private void renderPolygon(Polygon3D polygon) {
    Polygon3DUtil.rotate(polygon, rotateVector);

    final Triangle2D triangle = new Triangle2D();

    Polygon3DUtil.convert(triangle, polygon, originPoint, width, height, scale);

    /*
     * Simple occlusion culling (if the polygon is behind
     * the screen, we don't render it).
     * */

    if (!Triangle2DUtil.triangleVisibleOnScreen(triangle, width, height)) {
      return;
    }

    final Color color = Polygon3DUtil.lightingColor(polygon.color, polygon.normalVector(), lightVector);

    final int xMin = (int) Math.ceil(Math.min(triangle.p1.x, Math.min(triangle.p2.x, triangle.p3.x)));
    final int yMin = (int) Math.ceil(Math.min(triangle.p1.y, Math.min(triangle.p2.y, triangle.p3.y)));
    final int xMax = (int) Math.ceil(Math.max(triangle.p1.x, Math.max(triangle.p2.x, triangle.p3.x)));
    final int yMax = (int) Math.ceil(Math.max(triangle.p1.y, Math.max(triangle.p2.y, triangle.p3.y)));

    /*
     * Drawing a scan line pixel by pixel.
     * */

    for (int y = yMin; y < yMax; y++) {
      for (int x = xMin; x < xMax; x++) {
        if (Point2DUtil.inRange(x, y, width, height) &&
          Point2DUtil.pointInTriangle(x, y, triangle.p1, triangle.p2, triangle.p3)) {
          double depth = Point2DUtil.averageDepth(x, y, triangle.p1, triangle.p2, triangle.p3);
          double dist = Point3DUtil.dist(new Point3D(depth, x, y), cameraPoint);

          if (dist <= 0) {
            continue;
          }

          int index = y * width + x;
          if (depthBuffer[index] == Const.DEFAULT_DEPTH || dist < depthBuffer[index]) {
            depthBuffer[index] = dist;
            pixelBuffer[index] = color.getRGB();
          }
        }
      }
    }
  }

  private void interactionControl() {
    if (keyboard.getCurrentReleasedKeyCode() == KeyEvent.VK_R) {
      onReload.exec();
    }

    rotateVector.clear();

    switch (keyboard.getCurrentKeyCode()) {
      case KeyEvent.VK_ESCAPE -> stop();

      case KeyEvent.VK_CONTROL -> {
        if (mouse.isDragged()) {
          originPoint.y += (double) mouse.getDiffX() * 0.01;
          originPoint.z += (double) -mouse.getDiffY() * 0.01;
        }

        if (mouse.getWheelRotation() == 1) {
          /*
           * Zoom out.
           * */

          scale /= Const.SCALE_FACTOR;
        } else if (mouse.getWheelRotation() == -1) {
          /*
           * Zoom in.
           * */

          scale *= Const.SCALE_FACTOR;
        }
      }

      case KeyEvent.VK_SHIFT -> {
        if (mouse.isDragged()) {
          rotateVector.x = mouse.getDiffX();
        }
      }

      default -> {
        if (mouse.isDragged()) {
          rotateVector.y = mouse.getDiffY();
          rotateVector.z = mouse.getDiffX();
        }
      }
    }

    mouse.reset();
    keyboard.reset();
  }

  private void updateAndDraw() {
    /*
     * Mouse and keyboard control.
     * */

    interactionControl();

    /*
     * Clearing buffers.
     * */

    Arrays.fill(depthBuffer, Const.DEFAULT_DEPTH);
    Arrays.fill(pixelBuffer, Const.BACKGROUND_COLOR_RGB);

    /*
     * Rendering.
     * */

    polygons.parallelStream().forEach(this::renderPolygon);

    /*
     * Drawing.
     * */

    window.draw();
  }

  public void start() {
    isRunning = true;

    long start = System.currentTimeMillis();
    int frames = 0;

    while (isRunning) {
      long end = System.currentTimeMillis();
      if (end - start >= 1000) {
        System.out.print("\rFPS: " + frames + "    ");
        frames = 0;
        start = end;
      }

      updateAndDraw();

      frames++;
    }

    window.dispose();
  }

  public void stop() {
    isRunning = false;
  }

  public void onReload(OnReload onReload) {
    this.onReload = onReload;
  }

  public void setPolygons(List<Polygon3D> polygons) {
    this.polygons = polygons;
  }

  public boolean isRunning() {
    return isRunning;
  }
}
