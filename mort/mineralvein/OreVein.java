/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mort.mineralvein;
import java.util.List;
import org.bukkit.configuration.MemoryConfiguration;
import java.util.Map;
import org.bukkit.block.Biome;
/**
 *
 * @author Martin
 */  
public class OreVein {
        public MVMaterial mat;
        public int seed;
        public double density;
        public double maxSpan;
        public double densBonus;
        public double areaHeight;
        public double areaSpan;
        public double heightLength;
        public double densLength;
        public boolean exclusive;
        public boolean addMode;
        public boolean heighRel;
        public Biome[] biomes;
        public Biome[] noBiomes;
        
        public static OreVein[] loadConf( List lst ){
            OreVein[] ret = new OreVein[lst.size()];
            for(int i=0;i<lst.size();i++){
		MemoryConfiguration nd = new MemoryConfiguration();
		nd.createSection( "sec", (Map<String,Object>) lst.get(i) );
                ret[i] = new OreVein();
                ret[i].mat = new MVMaterial( nd.getString("sec.block","0") );
                ret[i].seed = nd.getInt("sec.seed", 6516);
                ret[i].density = nd.getDouble("sec.density", 1);
                ret[i].maxSpan = nd.getDouble("sec.thickness", 5);
                ret[i].densBonus = nd.getDouble("sec.densityBonus", 0);
                ret[i].areaHeight = nd.getDouble("sec.heightAvg", 32);
                ret[i].areaSpan = nd.getDouble("sec.heightVar", 20);
                ret[i].heighRel = nd.getBoolean("sec.heighRel", false);
                ret[i].heightLength = nd.getDouble("sec.heightLength", 80);
                ret[i].densLength = nd.getDouble("sec.densLength", 80);
                ret[i].exclusive = nd.getBoolean("sec.exclusive", false);
                ret[i].addMode = nd.getString("sec.mode", "").equals("add");
                if( nd.contains("sec.biomes") ){
                    //System.out.println("LOADING BIOMES LIST"+nd.getProperty("biomes")+": "+nd.getStringList("biomes", null)+"; "+nd.getString("biomes"));
                    ret[i].biomes = convertStringList( nd.getStringList("sec.biomes") );}
                else ret[i].biomes = null;
                if( nd.contains("sec.exclude_biomes") ){
                    //System.out.println("LOADING BIOMES LIST"+nd.getProperty("biomes")+": "+nd.getStringList("biomes", null)+"; "+nd.getString("biomes"));
                    ret[i].noBiomes = convertStringList( nd.getStringList("sec.exclude_biomes") );}
                else ret[i].noBiomes = null;
                if(MineralVein.plugin.debug){
                    System.out.println( "LOADED ORE: "+ret[i].mat.id );
                }
            }
            
            return ret;
        }
        
        public static Biome[] convertStringList( List<String> list ){
            if( list.isEmpty() )
                return null;
            Biome[] ret = new Biome[list.size()];
            System.out.println("Size:"+list.size() );
            int i = 0;
            for(String str:list){
                try{
                    ret[i] = Biome.valueOf( str.toUpperCase() );
                    i++;
                }catch (Exception e){}
            }
            return ret;
        }
        
}
