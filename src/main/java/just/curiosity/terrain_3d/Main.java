package just.curiosity.terrain_3d;

import just.curiosity.renderer_3d.core.Renderer3D;
import just.curiosity.terrain_3d.core.PerlinNoise2D;
import just.curiosity.terrain_3d.core.TerrainGenerator;

public class Main {
  private static final Renderer3D renderer;
  private static final TerrainGenerator terrainGenerator;

  static {
    renderer = new Renderer3D(1000, 700);
    terrainGenerator = new TerrainGenerator(3.6, 3.6, 200);
  }

  public static void main(String[] args) {
    // when you press "R"
    renderer.onReload(() -> {
      PerlinNoise2D.genAux();
      renderer.setPolygons(terrainGenerator.generate());
    });

    renderer.setPolygons(terrainGenerator.generate());
    renderer.start();
  }
}