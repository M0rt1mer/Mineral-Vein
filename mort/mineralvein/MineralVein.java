/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mort.mineralvein;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.Material;
import java.io.File;
import java.util.HashMap;
import org.bukkit.World; 
import org.bukkit.util.config.Configuration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.Chunk;
import org.bukkit.generator.BlockPopulator;
import java.util.HashSet;
import java.util.Random;
/**
 *
 * @author Martin
 */
public class MineralVein extends JavaPlugin{
    
    public static MineralVein plugin;
    public HashMap<World, OreVein[]> data = new HashMap<World, OreVein[]>();
    public OreVein[] def = null;
    public Configuration conf;
    private WorldList listener;
    public static HashSet<MVChunk> doneChunks = new HashSet<MVChunk>();
    public static HashSet<World> applyWorlds = new HashSet<World>();
    
    public MineralVein(){
        plugin = this;
    }
    
    @Override
    public void onEnable(){
        listener = new WorldList();
        getServer().getPluginManager().registerEvent(Type.WORLD_INIT, listener, Priority.Low, this);
        getServer().getPluginManager().registerEvent(Type.CHUNK_LOAD, listener, Priority.Monitor, plugin);
        
        getServer().getPluginCommand("mineralvein").setExecutor(this);
        
        conf = new Configuration( loadFile( "veins.yml" ) );
        conf.load();
    }
    
    @Override
    public void onDisable(){}
    
    public OreVein[] getWorldData(World w){
        if(data.containsKey(w))
            return data.get(w);
        else if ( conf.getKeys().contains(w.getName()) ){
            data.put(w, OreVein.loadConf(conf.getNodeList(w.getName(), null)) );
            return data.get(w);
        }
        else if( def!=null )
            return def;
        else if( conf.getKeys().contains("default") ){
            def = OreVein.loadConf(conf.getNodeList("default", null));
            return def;
        }
        return null;
    }
    
    
   public static Material readMaterial( org.bukkit.util.config.ConfigurationNode conf,
            String key, Material def ){
        String str = conf.getString( key );
        int mat = conf.getInt( key,-1);
        Material retMat = null;
        
        retMat = Material.getMaterial( mat );
        if( retMat == null )
            retMat = Material.getMaterial( str );
        if( retMat == null )
            retMat = def;
        
        return retMat;
    }
        
    public File loadFile( String filename ){
        File fl = new File( this.getDataFolder()+ File.separator + filename );
        //System.out.println("Attempting: "+"data/"+filename.replace('\\', '/'));
        if( fl.exists() )
            return fl;
        java.io.InputStream is = this.getClass().getClassLoader()
                .getResourceAsStream( "data/"+filename.replace('\\', '/') );

        fl.getParentFile().mkdirs();
        try{
            fl.createNewFile();
            java.io.FileOutputStream os = new java.io.FileOutputStream(fl);
            int data;
            while( (data = is.read() ) != -1 ){
                os.write(data);
            }
            is.close();
            os.flush();
            os.close();
        } catch (java.io.IOException ex){
            System.out.println("Error creating/extracting file "+filename);
            return null;
        }
        return fl;
    }
    
    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings){
        if(strings.length<2 || !strings[0].equals("apply") ){
            cs.sendMessage( "Usage:" + cmnd.getUsage() );
            return true;
        }
        World w = getServer().getWorld( strings[1] );
        if( w==null){
            cs.sendMessage("Given world not found");
            return true;
        }
        if(applyWorlds.contains(w)){
            cs.sendMessage("World repopulation disabled");
            applyWorlds.remove(w);
            return true;
        }
        
        int x = 0;
        int z = 0;
        if(strings.length>2)
            x = Integer.parseInt( strings[2] );
        if(strings.length>3)
            z = Integer.parseInt( strings[3] );
        
        VeinPopulator vein = null;
        for( BlockPopulator pop : w.getPopulators() ){
            if(pop instanceof VeinPopulator)
                vein = (VeinPopulator) pop;
        }
        if(vein==null)
            vein = new VeinPopulator();
        Random r = new Random();
        /*try{
            applyChunk( w, w.getChunkAt(x, z), new HashSet<MVChunk>(), vein, r );
        }catch (Exception e){e.printStackTrace();}*/
        for( Chunk ch : w.getLoadedChunks() ){
            vein.populate(w, r, ch);
            doneChunks.add(new MVChunk(ch));
        }
        applyWorlds.add(w);
        cs.sendMessage("World repopulation enabled");
        return true;
    }
    
    /*private void applyChunk( World w, Chunk ch, HashSet<MVChunk> done, BlockPopulator pop, Random r ){
        if(ch==null || done.contains( new MVChunk(ch) ) )
            return;
        boolean unload = false;
        if( w.isChunkLoaded(ch) ){
            if( !w.loadChunk(ch.getX(), ch.getZ(), false) )
                return;
            unload = true;
        }
        System.out.println(ch.getX()+"; "+ch.getZ() );
        pop.populate(w, r, ch);
        done.add( new MVChunk(ch) );
        if(unload)
            w.unloadChunk(ch.getX(), ch.getZ());
        applyChunk( w, w.getChunkAt(ch.getX()+16, ch.getZ()), done, pop, r );
        applyChunk( w, w.getChunkAt(ch.getX(), ch.getZ()+16), done, pop, r );
        applyChunk( w, w.getChunkAt(ch.getX()-16, ch.getZ()), done, pop, r );
        applyChunk( w, w.getChunkAt(ch.getX(), ch.getZ()-16), done, pop, r );
    }*/
    
}
