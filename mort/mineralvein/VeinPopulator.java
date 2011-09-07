/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mort.mineralvein;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.World;
import java.util.Random;
import org.bukkit.Material;
import org.bukkit.Chunk;
import java.util.HashSet;
/**
 *
 * @author Martin
 */
public class VeinPopulator extends BlockPopulator{
        //long[] count = new long[Ore.values().length];
       // int chunks=0;
    
    @Override
    public void populate( World w, Random r, Chunk ch ){
        int stoneID = Material.STONE.getId();
        OreVein[] ores = MineralVein.plugin.getWorldData(w);
        double roll, chance;
        double[] heightCache = new double[ores.length];
        double[] densCache = new double[ores.length];
        HashSet block = new HashSet();
        for(OreVein ore:ores){
            block.add(ore.block);
        }
        for(int x=0;x<16;x++)
            for(int z=0;z<16;z++){
                for(int i=0;i<ores.length;i++){
                    heightCache[i] = getVeinHeight( x+ch.getX()*16,z+ch.getZ()*16,ores[i],w.getSeed() );
                    densCache[i] = getVeinDensity( x+ch.getX()*16,z+ch.getZ()*16,ores[i],w.getSeed() );
                    //if(ch.getX()==0 && ch.getZ()==0 && z==0)
                    //    System.out.println("Height: "+heightCache[ore.ordinal()]+"\tdens: "+densCache[ore.ordinal()]);
                }
                for(int y=0;y<128;y++){
                    int blockType = w.getBlockTypeIdAt(x+ch.getX()*16,y,z+ch.getZ()*16);
                    if( blockType != stoneID ){
                            if( block.contains(blockType) )
                                w.getBlockAt(x+ch.getX()*16, y, z+ch.getZ()*16).setTypeId(stoneID, false);
                            else
                                continue;
                    }
                    roll = r.nextDouble();
                    for(int i=0;i<ores.length;i++){
                        chance = Math.max( getOreChance(x+ch.getX()*16,z+ch.getZ()*16,y,ores[i],w.getSeed(),heightCache[i],densCache[i] ), 0);
                        //if(x==0 && z==0 && ch.getX()==0 && ch.getZ()==0) System.out.println("Y: "+y+" "+ore+": "+chance);
                        if( roll < chance ){
                            w.getBlockAt(x+ch.getX()*16, y, z+ch.getZ()*16).setTypeId(ores[i].block, false);
                            //count[ore.ordinal()]++;
                            break;
                        }
                        else roll-= chance;
                    }
                }
            }
        //chunks++;
     //   for(Ore ore:ores)
     //       System.out.println( ore+": "+((double)count[ore.ordinal()])/chunks );
    }
    
    public double getOreChance( int x, int z, int y, OreVein ore, long seed, double veinHeight, double veinDensity ){
        //chance on exact same height - 50%
        double chance = Math.abs(y-veinHeight);
        if(chance>ore.maxSpan) return 0;
        else return Math.sqrt(Math.cos( chance*Math.PI/ore.maxSpan ) +1)/4*veinDensity;
    }
    
    public double getVeinHeight(int x, int z, OreVein ore, long seed){
        return getChance(seed+ore.offset, x, z)*ore.areaSpan*2+ore.areaHeight;
    }
    
    double getVeinDensity(int x, int z, OreVein ore, long seed){
        return (getChance(seed+ore.offset+654357387474L, x, z)+ore.densBonus)*ore.density;
    }
    
    //combines multiple noise functions, result should be -1 to 1
    public static double getChance( long seed,int x,int z){
        int[] width = {80,30,10};
        double[] height = {0.7,0.2,0.1};
        float result = 0;
        for(int i=0;i<width.length;i++){
            result += getNoise( seed,x,z,width[i] ) * height[i];
            }
        return result;
    }
    
    public static float interpolatedHeight( int x, float span, long randSeed ){
        float h1 = seededRandom( (int)Math.floor(x/span), randSeed );
        float h2 = seededRandom( (int)Math.floor(x/span) + 1,randSeed );
        float dist = ((x % span)+span)%span / span;
        dist = (float) ( 1-Math.cos(dist*Math.PI) )/2;
        return h1*(1-dist) + h2*dist;
    }
    
    //returns -1 to +1
    public static double getNoise( long seed, int x, int z, int spanWidth){
        return (interpolatedHeight( x,spanWidth,seed )
                *interpolatedHeight( z,spanWidth,seed+654384 )+
                interpolatedHeight( x+z,spanWidth,seed+584868 )
                *interpolatedHeight( z-x,spanWidth,seed+68445 )
                )/2;
    }
    
    private static Random rnd = new Random();
    
    public static float seededRandom( int x, long seed ){
        rnd.setSeed(seed + x*619);
        return rnd.nextFloat()*2-1;
    }
    
}
