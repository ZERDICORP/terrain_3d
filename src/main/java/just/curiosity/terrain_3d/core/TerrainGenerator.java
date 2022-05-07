package just.curiosity.terrain_3d.core;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import just.curiosity.renderer_3d.core.point3d.Point3D;
import just.curiosity.renderer_3d.core.polygon3d.Polygon3D;
import just.curiosity.renderer_3d.core.polygon3d.Polygon3DUtil;
import just.curiosity.renderer_3d.core.vector3d.Vector3D;
import just.curiosity.terrain_3d.constants.Const;
import just.curiosity.terrain_3d.core.map.MapGenerator;
import just.curiosity.terrain_3d.core.map.object.Object;
import just.curiosity.terrain_3d.core.map.object.ObjectType;
import just.curiosity.terrain_3d.core.map.object.TreeObject;

public class TerrainGenerator {
  private final int resolution;
  private final double squareWidth;
  private final double squareHeight;
  private final double zStart;
  private final double yStart;
  private final MapGenerator mapGenerator;
  private final Map<ObjectType, Object> objects;

  {
    objects = new HashMap<>();
    objects.put(ObjectType.TREE, new TreeObject());
  }

  public TerrainGenerator(double width, double height, int resolution) {
    this.resolution = resolution;
    this.mapGenerator = new MapGenerator(resolution);

    squareWidth = width / resolution;
    squareHeight = height / resolution;
    zStart = -height / 2;
    yStart = -width / 2;
  }

  public List<Polygon3D> generate() {
    final double[] heightMap = createHeightMap(resolution);

    final List<Polygon3D> polygons = new ArrayList<>();
    final List<Point3D> points = new ArrayList<>();

    for (int i = 0; i < resolution; i++) {
      for (int j = 0; j < resolution; j++) {
        double heightValue = heightMap[i * resolution + j];
        int heightValueAsInt = (int) (heightValue * 10);

        // flatten all layers up to the beach layer
        double x = heightValueAsInt <= 0 // 0 - beach layer
          ? 0 : heightValue * Const.heightValueFactor;

        points.add(new Point3D(x,
          j * squareWidth + yStart,
          i * squareHeight + zStart));

        if (!(i > 0 && j > 0)) {
          continue;
        }

        Color color = heightColor(heightValueAsInt);

        polygons.add(new Polygon3D(color,
          points.get((i - 1) * resolution + (j - 1)),
          points.get(i * resolution + (j - 1)),
          points.get(i * resolution + j)));

        polygons.add(new Polygon3D(color,
          points.get((i - 1) * resolution + (j - 1)),
          points.get(i * resolution + j),
          points.get((i - 1) * resolution + j)));

        // create object by height
        ObjectType objectType = mapGenerator.getObjectByHeight(j, i, heightValueAsInt);
        if (objectType != ObjectType.EMPTY) {
          polygons.addAll(objects.get(objectType)
            .generate(x, yStart + j * squareWidth, zStart + i * squareWidth));
        }
      }
    }

    // rotate polygons for better view
    polygons.parallelStream()
      .forEach(p -> Polygon3DUtil.rotate(p, new Vector3D(45, -45, 0)));

    return polygons;
  }

  private double[] createFalloffMap(int size) {
    final double[] falloffMap = new double[size * size];
    final double cX = (double) size / 2;
    final double cY = (double) size / 2;
    final double radius = (double) size / 4;
    final double mod = PerlinNoise2D.gradientRange / radius;

    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        final double x = cX - j;
        final double y = cY - i;
        final double distToCenter = Math.sqrt(x * x + y * y);

        if (distToCenter >= radius) {
          falloffMap[i * size + j] = (distToCenter - radius) * mod;
        }
      }
    }

    return falloffMap;
  }

  private double[] createHeightMap(int size) {
    final double noiseSquareSize = (double) size / 3;
    final double[] falloffMap = createFalloffMap(size);
    final double[] heightMap = new double[size * size];

    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        double noiseValue = PerlinNoise2D.calc(
          (double) j / noiseSquareSize,
          (double) i / noiseSquareSize,
          20, 0.55f);

        heightMap[i * size + j] = noiseValue - falloffMap[i * size + j];
      }
    }

    return heightMap;
  }

  private Color heightColor(int h) {
    return switch (h) {
      case -10 -> new Color(0, 33, 86);
      case -9 -> new Color(0, 35, 91);
      case -8 -> new Color(1, 39, 101);
      case -7 -> new Color(1, 44, 114);
      case -6 -> new Color(1, 48, 126);
      case -5 -> new Color(1, 53, 140);
      case -4 -> new Color(2, 60, 155);
      case -3 -> new Color(1, 66, 171);
      case -2 -> new Color(2, 73, 189);
      case -1 -> new Color(1, 80, 210);
      case 0 -> new Color(248, 234, 30);
      case 1 -> new Color(21, 178, 0);
      case 2 -> new Color(19, 161, 0);
      case 3 -> new Color(17, 148, 0);
      case 4 -> new Color(16, 140, 0);
      case 5 -> new Color(13, 121, 0);
      case 6 -> new Color(12, 101, 0);
      case 7 -> new Color(101, 101, 101);
      case 8 -> new Color(114, 112, 112);
      case 9 -> new Color(131, 131, 131);
      case 10 -> new Color(154, 154, 154);
      default -> (h > 0 ?
        new Color(154, 154, 154) :
        new Color(0, 33, 86));
    };
  }
}
