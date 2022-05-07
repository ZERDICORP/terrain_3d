package just.curiosity.terrain_3d.core.map.object;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import just.curiosity.renderer_3d.core.point3d.Point3D;
import just.curiosity.renderer_3d.core.polygon3d.Polygon3D;

public class TreeObject extends Object {
  private final double treeHeight;
  private final double treeWidthHalf;
  private final double trunkRootDepth;

  {
    treeHeight = 0.05;
    treeWidthHalf = 0.01 / 2;
    trunkRootDepth = treeHeight / 2;
  }

  @Override
  public List<Polygon3D> generate(double x, double y, double z) {
    final List<Polygon3D> polygons = new ArrayList<>();

    final int typeOfTree = new Random().nextInt(2);

    switch (typeOfTree) {
      // spruce
      case 0 -> {
        polygons.addAll(trunk(x - trunkRootDepth, y, z, treeHeight * 0.25 + trunkRootDepth));
        polygons.addAll(spruceCrown(x + treeHeight * 0.25, y, z, treeHeight * 0.75));
      }

      // broad-leaved tree
      case 1 -> {
        polygons.addAll(trunk(x - trunkRootDepth, y, z, treeHeight / 2 + trunkRootDepth));
        polygons.addAll(broadLeavedCrown(x + treeHeight / 2, y, z, treeHeight / 2));
      }
    }

    return polygons;
  }

  private List<Polygon3D> spruceCrown(double x, double y, double z, double height) {
    final List<Polygon3D> polygons = new ArrayList<>();
    final double width = 0.02;
    final double widthHalf = width / 2;
    final double cY = y - treeWidthHalf;
    final double cZ = z - treeWidthHalf;
    final int quality = 4;

    final Point3D crownTopCenter = new Point3D(x + height, cY, cZ);
    final Color color = new Color(11, 107, 0);
    final List<Point3D> points = new ArrayList<>();

    for (int i = 0; i < quality; i++) {
      final double alpha = 2 * Math.PI / quality * i;
      final double lY = cY + -Math.sin(alpha) * widthHalf;
      final double lZ = cZ + Math.cos(alpha) * widthHalf;

      points.add(new Point3D(x, lY, lZ));

      if (!(i > 0)) {
        continue;
      }

      if (i == quality - 1) {
        polygons.add(new Polygon3D(color,
          points.get(i),
          crownTopCenter,
          points.get(0)));
      }

      polygons.add(new Polygon3D(color,
        points.get(i - 1),
        crownTopCenter,
        points.get(i)));
    }

    return polygons;
  }

  private List<Polygon3D> broadLeavedCrown(double x, double y, double z, double height) {
    final List<Polygon3D> polygons = new ArrayList<>();
    final double width = 0.02;
    final double widthHalf = width / 2;
    final double cY = y - treeWidthHalf;
    final double cZ = z - treeWidthHalf;
    final int quality = 8;

    final Point3D crownTopCenter = new Point3D(x + height, cY, cZ);
    final Color color = new Color(21, 140, 0);
    final List<Point3D> points = new ArrayList<>();

    for (int i = 0; i < quality; i += 2) {
      final double alpha = 2 * Math.PI / quality * i;
      final double lY = cY + -Math.sin(alpha) * widthHalf;
      final double lZ = cZ + Math.cos(alpha) * widthHalf;

      points.add(new Point3D(x, lY, lZ));
      points.add(new Point3D(x + height, lY, lZ));

      if (!(i > 1)) {
        continue;
      }

      if (i == quality - 2) {
        polygons.add(new Polygon3D(color,
          points.get(i),
          points.get(i + 1),
          points.get(1)));

        polygons.add(new Polygon3D(color,
          points.get(i),
          points.get(1),
          points.get(0)));

        // cover the top of the tree crown
        polygons.add(new Polygon3D(color,
          points.get(i + 1),
          crownTopCenter,
          points.get(1)));
      }

      polygons.add(new Polygon3D(color,
        points.get(i - 2),
        points.get(i - 1),
        points.get(i + 1)));

      polygons.add(new Polygon3D(color,
        points.get(i - 2),
        points.get(i + 1),
        points.get(i)));

      // cover the top of the tree crown
      polygons.add(new Polygon3D(color,
        points.get(i - 1),
        crownTopCenter,
        points.get(i + 1)));
    }

    return polygons;
  }

  private List<Polygon3D> trunk(double x, double y, double z, double height) {
    final List<Polygon3D> polygons = new ArrayList<>();
    final double width = 0.005;
    final double widthHalf = width / 2;
    final double cY = y - treeWidthHalf;
    final double cZ = z - treeWidthHalf;
    final int quality = 10;

    final Color color = new Color(82, 71, 71);
    final List<Point3D> points = new ArrayList<>();

    for (int i = 0; i < quality; i += 2) {
      final double alpha = 2 * Math.PI / quality * i;
      final double lY = cY + -Math.sin(alpha) * widthHalf;
      final double lZ = cZ + Math.cos(alpha) * widthHalf;

      points.add(new Point3D(x, lY, lZ));
      points.add(new Point3D(x + height, lY, lZ));

      if (!(i > 1)) {
        continue;
      }

      if (i == quality - 2) {
        polygons.add(new Polygon3D(color,
          points.get(i),
          points.get(i + 1),
          points.get(1)));

        polygons.add(new Polygon3D(color,
          points.get(i),
          points.get(1),
          points.get(0)));
      }

      polygons.add(new Polygon3D(color,
        points.get(i - 2),
        points.get(i - 1),
        points.get(i + 1)));

      polygons.add(new Polygon3D(color,
        points.get(i - 2),
        points.get(i + 1),
        points.get(i)));
    }

    return polygons;
  }
}
