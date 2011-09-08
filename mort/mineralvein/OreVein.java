/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mort.mineralvein;
import org.bukkit.util.config.ConfigurationNode;
import java.util.List;
import org.bukkit.Material;
/**
 *
 * @author Martin
 */  
public class OreVein {
        public int block;
        public int seed;
        public double density;
        public double maxSpan;
        public double densBonus;
        public double areaHeight;
        public double areaSpan;
        
        public static OreVein[] loadConf( List<ConfigurationNode> list ){
            OreVein[] ret = new OreVein[list.size()];
            for(int i=0;i<list.size();i++){
                ConfigurationNode nd = list.get(i);
                ret[i] = new OreVein();
                ret[i].block = MineralVein.readMaterial(nd, "block", Material.STONE).getId();
                ret[i].seed = nd.getInt("seed", 0);
                ret[i].density = nd.getDouble("density", 1);
                ret[i].maxSpan = nd.getDouble("thickness", 10);
                ret[i].densBonus = nd.getDouble("densityBonus", 0);
                ret[i].areaHeight = nd.getDouble("heightAvg", 32);
                ret[i].areaSpan = nd.getDouble("heightVar", 20);
            }
            
            return ret;
        }
        
}
