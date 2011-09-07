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
/**
 *
 * @author Martin
 */
public class MineralVein extends JavaPlugin{
    
    public static MineralVein plugin;
    public HashMap<World, OreVein[]> data = new HashMap<World, OreVein[]>();
    public OreVein[] def;
    public Configuration conf;
    
    public MineralVein(){
        plugin = this;
    }
    
    @Override
    public void onEnable(){
        getServer().getPluginManager().registerEvent(Type.WORLD_INIT, new WorldList(), Priority.Low, this);
        
        conf = new Configuration( loadFile( "veins.yml" ) );
        conf.load();        
    }
    
    @Override
    public void onDisable(){}
    
    public OreVein[] getWorldData(World w){
        if(data.containsKey(w))
            return data.get(w);
        else if ( conf.getNodeList(w.getName(), null)!=null ){
            data.put(w, OreVein.loadConf(conf.getNodeList(w.getName(), null)) );
            return data.get(w);
        }
        else if( def!=null )
            return def;
        else if( conf.getNodeList("default", null)!=null ){
            def = OreVein.loadConf(conf.getNodeList(w.getName(), null));
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
}
