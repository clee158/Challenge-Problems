/**
 * Created by Chaeyoon on 2/22/2017.
 */

class Test extends GroovyTestCase {

    Set<Integer> set = new RBTreeSet<Integer>();

    public void testAdd(){
        set.add(10);
        set.add(20);
        set.add(30);
        set.add(15);
        set.add(25);
        set.add(23);
        set.add(24);
        set.add(9);
        set.add(27);
        set.add(31);
        set.add(28);
        set.add(13);
        set.add(14);
        set.add(40);
        set.add(33);

        assertEquals(false, set.isEmpty());
        assertEquals(15, set.size());

        Iterator elems = set.iterator();
        int[] solution = [9, 10, 13, 14, 15, 20, 23, 24, 25, 27, 28, 30, 31, 33, 40];
        for(int i = 0; i < 15; i++){
            assertEquals(solution[i], elems.next());
        }
    }

    public void testRemoval(){
        set.remove(40); // no child removal
        set.remove(25); // two child (root) removal
        set.remove(14); // black node w/ two child removal
        set.remove(9);  // black leaf
        set.remove(20); // black interior node w/ 1 red & 1 black child
        set.remove(27); // black interior node w/ 1 red child

        assertEquals(9, set.size());

        Iterator elems = set.iterator();

        int[] solution = [10, 13, 15, 23, 24, 28, 30, 31, 33];
        for(int i = 0; i < 9; i++){
            assertEquals(solution[i], elems.next());
        }
    }

    public void testIterator(){
        Iterator elems = set.iterator();
        assertEquals(true, elems.hasNext());
        assertEquals(10, elems.next());
        elems.remove();

        int[] solution = [13, 15, 23, 24, 28, 30, 31, 33];
        for(int i = 0; i < 8; i++){
            assertEquals(solution[i], elems.next());
        }
    }
}
