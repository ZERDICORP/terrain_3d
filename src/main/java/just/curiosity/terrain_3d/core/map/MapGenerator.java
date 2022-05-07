package just.curiosity.terrain_3d.core.map;

import java.util.Arrays;
import java.util.Random;
import just.curiosity.renderer_3d.core.point2d.Point2DUtil;
import just.curiosity.terrain_3d.core.map.object.ObjectType;

public class MapGenerator {
  private final int fov;
  private final int mapSize;
  private final ObjectType[] map;

  {
    fov = 3;
  }

  public MapGenerator(int mapSize) {
    this.mapSize = mapSize;
    this.map = new ObjectType[mapSize * mapSize];

    Arrays.fill(map, ObjectType.EMPTY);
  }

  public ObjectType getObjectByHeight(int y, int z, int height) {
    // [1 - 6] - forest layer (tree objects)
    if (height >= 1 && height <= 6) {
      if (!isThereAnyMapObject(y, z) && probability(10)) {
        return (map[z * mapSize + y] = ObjectType.TREE);
      }
    }

    return ObjectType.EMPTY;
  }

  private boolean probability(int value) {
    return new Random().nextInt(100) < value;
  }

  private boolean isThereAnyMapObject(int y, int z) {
    for (int i = z - fov / 2; i < z + fov / 2; i++) {
      for (int j = y - fov / 2; j < y + fov / 2; j++) {
        if (Point2DUtil.inRange(i, j, mapSize, mapSize) && map[i * mapSize + j] != ObjectType.EMPTY) {
          return true;
        }
      }
    }

    return false;
  }
}
