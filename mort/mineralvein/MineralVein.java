/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mort.mineralvein;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.WorldInitEvent;
import java.util.HashMap;
import org.bukkit.World; 
import org.bukkit.configuration.Configuration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.generator.BlockPopulator;
import java.util.Random;
import org.bukkit.command.ConsoleCommandSender;
/**
 *
 * @author Martin
 */
public class MineralVein extends JavaPlugin{
    
    public static MineralVein plugin;
    public HashMap<World, OreVein[]> data = new HashMap<World, OreVein[]>();
    public OreVein[] def = null;
    public Configuration conf;
    private MVExecutor executor;
    public boolean debug;
    
    public MineralVein(){
        plugin = this;
    }
    
    @Override
    public void onEnable(){
        executor = new MVExecutor();
        getServer().getPluginManager().registerEvent( WorldInitEvent.class, new MVListener(), EventPriority.LOW, executor, this);

	getServer().getPluginCommand("mineralvein").setExecutor(this);
        
        conf = getConfig();
	conf.options().copyDefaults(true);
	saveConfig();

        debug = conf.getBoolean("debug", false);
        
    }
    
    @Override
    public void onDisable(){
    }
    
    public OreVein[] getWorldData(World w){
        if(data.containsKey(w))
            return data.get(w);
        else if ( conf.contains(w.getName()) ){
            data.put(w, OreVein.loadConf( conf.getMapList( w.getName() ) ) );
            return data.get(w);
        }
        else if( def!=null )
            return def;
        else if( conf.contains( "default" ) ){
            def = OreVein.loadConf( conf.getMapList("default") );
            return def;
        }
        return null;
    }
    
    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings){
        if( !(cs instanceof ConsoleCommandSender) )
	    return false;
	if(strings.length<2 || !strings[0].equals("apply") ){
            cs.sendMessage( "Usage:" + cmnd.getUsage() );
            return true;
        }
        World w = getServer().getWorld( strings[1] );
        int id = -1;
        if( w==null){
            java.util.List<World> list = getServer().getWorlds();
            try{
                id = Integer.parseInt(strings[1]);
            }catch(Exception e){
                cs.sendMessage("Given world not found. Try using world id: ");    
                for( int i = 0; i < list.size(); i++ ){
                    cs.sendMessage( i+". "+list.get(i).getName()+" ("+list.get(i).getEnvironment()+")" );
                }
                return true;
            }
            if( id < 0 || id >= list.size() ){
                cs.sendMessage( "No world at this ID." );
                return true;
            }
            w = list.get(id);
        }
        
        int x = 0;
        int z = 0;
        int width = 100;
        int height = 100;
        if(strings.length>2)
            x = Integer.parseInt( strings[2] );
        if(strings.length>3)
            z = Integer.parseInt( strings[3] );
        
        if(strings.length>4)
            width = Integer.parseInt( strings[4] );
        if(strings.length>5)
            height = Integer.parseInt( strings[4] );
        
        getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new WorldApplier(w,x,z,cs, width, height), 0, 1 );
        
        cs.sendMessage("Mineral Vein application started");
        return true;
    }
    
    protected static void applyChunkSimple( World w, int x, int z, BlockPopulator pop, Random r ){
        //boolean unload = false;
        if( !w.isChunkLoaded( x,z ) ){
            if( !w.loadChunk(x, z, false) )
                return;
            //unload = true;
        }
        
        pop.populate(w, r, w.getChunkAt(x, z) );
        /*try{
        if(unload)
            w.unloadChunkRequest(x,z);
        }catch (Exception e){}*/
    }
    
    private class WorldApplier implements Runnable{
        private World w;
        int x;
        int z;
        int width;
        int height;
        int progress;
        CommandSender report;
        VeinPopulator vein;
        Random rnd;
        public WorldApplier(World w, int x, int z, CommandSender cs, int width, int height){
            this.w = w;
            this.x = x;
            this.z = z;
            this.width = width;
            this.height = height;
            this.progress = -width;
            report = cs;
            rnd = new Random();
            for( BlockPopulator pop : w.getPopulators() ){
                if(pop instanceof VeinPopulator)
                    vein = (VeinPopulator) pop;
                }
            if(vein==null)
                vein = new VeinPopulator();
        }
        @Override
        public void run(){
            report.sendMessage( "Application on "+w.getName()+": "+(Runtime.getRuntime().freeMemory()/1000000)+"MB, "+(((progress/(double)width)+1)*50)+"%" );
            if( Runtime.getRuntime().freeMemory()<50000000 ){
                return;}
            for( int Z = z-width; Z<z+width;Z++ )
                    MineralVein.applyChunkSimple(w,x+progress,Z, vein, rnd );
            progress++;
            //report.sendMessage( "Application on "+w.getName()+": "+(Runtime.getRuntime().freeMemory()/1000000)+"MB, "+(progress/(double)width)+"%" );
            if(progress>width){
                report.sendMessage( "MineralVein applied to world "+w.getName()+"." );
                MineralVein.plugin.getServer().getScheduler().cancelTasks(MineralVein.plugin);
            }
        }
    }
    
}
