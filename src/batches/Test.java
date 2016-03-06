package batches;

import memory.BestFit;
import memory.Pointer;

/**
 * @author nekosaur
 */
public class Test {


    public static void main(String[] args) {
        BestFit m = new BestFit(100); // Swap this for  your own implementation
        Pointer p1, p2, p3, p4, p5, p6;

        p1 = m.alloc(50);

        p2 = m.alloc(10);

        p3 = m.alloc(20);

        //m.release(p3);
        m.release(p2);

        m.compact();
        //m.printLayout();

    }
}
