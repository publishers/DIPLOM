import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by sergey on 30.05.16.
 */
public class TestMathAdd {

    @After
    @Test
    public void testAdd(){
        System.out.println("testAdd");
        MathAdd md  = new MathAdd();
        assertEquals(10, md.add(6,4));
    }

    @Before
    @Test
    public void testMathInit(){
        System.out.println("testMathInit");
        int add = MathAdd.add(4,6);
        assertEquals(10, add);
    }
}
