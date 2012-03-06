/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mort.mineralvein;
import org.bukkit.block.Block;
import org.bukkit.Material;
import java.util.StringTokenizer;
/**
 *
 * @author Martin
 */
public class MVMaterial {
    
    public int id;
    public byte data;
    public boolean ignoreData = false;
    
    public MVMaterial( String str ){
        try{
            Material mat = Material.valueOf( str.toUpperCase() );
            id = mat.getId();
            data = 0;
            ignoreData = true;
        }catch(Exception ef){
            try{
                id = Integer.parseInt(str);
                data = 0;
                ignoreData = true;
            }catch(Exception e){
                StringTokenizer tk = new StringTokenizer(str);
                try{
                    id = Integer.parseInt( tk.nextToken("-") );
                    data = Byte.parseByte( tk.nextToken("-") );
                }catch(Exception ex){
                    data = 0;
                    id=0;
                    System.out.println("MineralVein: Incorrect block ID format ("+str+")");
                }
            }
        }
    }
    
    public MVMaterial( Block bl ){
        id = bl.getTypeId();
        data = bl.getData();
    }
    
    public MVMaterial( Material mat ){
        id = mat.getId();
        data = 0;
        ignoreData = true;
    }
    
    @Override
    public int hashCode(){
        return id<<8 + ((data<0)?(data+256):data);
    }
    
    @Override
    public boolean equals( Object obj ){
        return (obj instanceof MVMaterial) && equalsMat( (MVMaterial)obj );
    }
    
    public boolean equalsMat( MVMaterial mat ){
        return (mat.ignoreData&&mat.id==id) || (ignoreData&&mat.id==id) || (mat.data==data && mat.id==id);
    }
}
