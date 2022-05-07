package just.curiosity.terrain_3d.core.map.object;

import java.util.List;
import just.curiosity.renderer_3d.core.polygon3d.Polygon3D;

public abstract class Object {
  public abstract List<Polygon3D> generate(final double x, final double y, final double z);
}
