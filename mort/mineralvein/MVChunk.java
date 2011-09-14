/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mort.mineralvein;
import org.bukkit.Chunk;
/**
 *
 * @author Martin
 */
public class MVChunk {
    public Chunk ch;
    public MVChunk(Chunk ch){
        this.ch = ch;
    }
    public int hashCode(){
        return ( (ch.getX()&0xFFFF) <<16) + (ch.getZ()&0xFFFF)+ch.getWorld().hashCode();
    }
}
