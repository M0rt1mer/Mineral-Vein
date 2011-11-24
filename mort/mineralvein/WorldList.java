/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mort.mineralvein;
import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.World.Environment;
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
    
}