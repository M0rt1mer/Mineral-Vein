/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mort.mineralvein;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.World.Environment;
/**
 *
 * @author Martin
 */
public class MVExecutor implements EventExecutor {

    @Override
    public void execute(Listener ll, Event evnt) throws EventException {
	
	if(evnt instanceof WorldInitEvent){
	    
	    WorldInitEvent event = (WorldInitEvent) evnt;
	    if(event.getWorld().getEnvironment()==Environment.NORMAL)
		event.getWorld().getPopulators().add( new VeinPopulator() );
	}
	
	
    }
    
}