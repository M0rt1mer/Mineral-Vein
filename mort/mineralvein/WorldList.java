/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mort.mineralvein;
import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.World.Environment;
import org.bukkit.generator.BlockPopulator;
/**
 *
 * @author Martin
 */
public class WorldList extends WorldListener {
         
    
    @Override
    public void onWorldInit(WorldInitEvent event) {
        if(event.getWorld().getEnvironment()==Environment.NORMAL)
            event.getWorld().getPopulators().add( new VeinPopulator() );
    }
    
    @Override
    public void onChunkLoad(ChunkLoadEvent evnt){
        if(evnt.isNewChunk()){ //will be populated in a moment
            MineralVein.doneChunks.add( new MVChunk(evnt.getChunk()) );
            return;
        }
        if(!MineralVein.applyWorlds.contains(evnt.getWorld()))
            return;
        
        MVChunk ch = new MVChunk( evnt.getChunk() );
        if( MineralVein.doneChunks.contains( ch ) )
            return;
        
        MineralVein.doneChunks.add(ch);
        
        VeinPopulator vein = null;
        for( BlockPopulator pop : evnt.getWorld().getPopulators() ){
            if(pop instanceof VeinPopulator)
                vein = (VeinPopulator) pop;
        }
        if(vein==null)
            vein = new VeinPopulator();
        
        vein.populate(evnt.getWorld(), new java.util.Random(), evnt.getChunk() );
    }
}
