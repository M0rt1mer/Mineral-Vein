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
import java.util.HashMap;
import org.bukkit.util.noise.NoiseGenerator;
import org.bukkit.util.noise.SimplexNoiseGenerator;
import org.bukkit.block.Biome;
/** 
 *
 * @author Martin
 */
public class VeinPopulator extends BlockPopulator{
    
    private HashMap<World,NoiseGenerator[]> noise = new HashMap<World,NoiseGenerator[]>();
    
    @Override
    public void populate( World w, Random r, Chunk ch ){
        int stoneID = Material.STONE.getId();
        OreVein[] ores = MineralVein.plugin.getWorldData(w);
        if( ores==null ) //no ores defined for this worlds
            return;
        NoiseGenerator[] noiseGen;
        if( !noise.containsKey(w) ){
            noiseGen = new NoiseGenerator[ores.length];
            for(int i=0;i<ores.length;i++){
                noiseGen[i] = new SimplexNoiseGenerator( w.getSeed() * ores[i].seed );
            }
            noise.put(w, noiseGen);
            }
        else
            noiseGen = noise.get(w);
        
        double roll, chance;
        double[] heightCache = new double[ores.length];
        double[] densCache = new double[ores.length];
        HashSet block = new HashSet();
        for(OreVein ore:ores){
            block.add(ore.block);
        }
        for(int x=0;x<16;x++)
            for(int z=0;z<16;z++){
                double exclusiveDens = 1;
                for(int i=0;i<ores.length;i++){
                    heightCache[i] = getVeinHeight( x+ch.getX()*16,z+ch.getZ()*16,ores[i],noiseGen[i], ores[i].heightLength );
                    boolean canSpawn = true;
                    if( ores[i].biomes!=null ){
                        canSpawn=false;
                        Biome bhere = ch.getBlock(x, 64, z).getBiome();
                        for( Biome b : ores[i].biomes )
                            if(bhere==b) canSpawn = true;
                    }
                    if(canSpawn)
                        densCache[i] = getVeinDensity( x+ch.getX()*16,z+ch.getZ()*16,ores[i],noiseGen[i], ores[i].densLength )*exclusiveDens;
                    else
                        densCache[i] = 0;
                    if(ores[i].exclusive)
                        exclusiveDens -= densCache[i];
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
                        chance = getOreChance(y,ores[i],w.getSeed(),heightCache[i],densCache[i] );
                        //if(x==0 && z==0 && ch.getX()==0 && ch.getZ()==0) System.out.println("Y: "+y+" "+ore+": "+chance);
                        if( roll < chance ){
                            w.getBlockAt(x+ch.getX()*16, y, z+ch.getZ()*16).setTypeId(ores[i].block, false);
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
    
    public double getOreChance( int y, OreVein ore, long seed, double veinHeight, double veinDensity ){
        //chance on exact same height - 50%
        double chance = Math.abs(y-veinHeight );
        if(chance>ore.maxSpan) return 0;
        else return Math.max( ((Math.cos( chance*Math.PI/ore.maxSpan ) +1)/2)*veinDensity, 0);
    }
    
    double getVeinHeight(double x, double z, OreVein ore, NoiseGenerator noise, double heightLength){
        return noise.noise(x/heightLength, z/heightLength)*ore.areaSpan + ore.areaHeight;
    }
    
    double getVeinDensity(double x, double z, OreVein ore, NoiseGenerator noise, double densLength){
        return (noise.noise(x/densLength, z/densLength)+ore.densBonus)*ore.density;
    }
    
}
