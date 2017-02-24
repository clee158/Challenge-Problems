import java.util.Iterator;
import java.util.Stack;

/**
 * Created by Chaeyoon on 2/15/2017.
 */
public class RBTreeSet<E extends Comparable<E>> implements Set<E>{
    private class Node{
        Comparable elem;
        Node left;
        Node right;
        Node parent;
        int color;      // 0 = black; 1 = red;

        // Default Empty Node
        Node(){
            this.elem = null;
            this.left = null;
            this.right = null;
            this.parent = null;
            this.color = 0;
        }

        // Black Node
        Node(Comparable elem){
           this.elem = elem;
           this.left = null;
           this.right = null;
           this.parent = null;
           this.color = 0;
        }

        // Red Interior Node
        Node(Comparable elem, Node left, Node right, Node parent){
            this.elem = elem;
            this.left = left;
            this.right = right;
            this.parent = parent;
            this.color = 1;
        }
    }
    public class RBIterator implements Iterator<E>{

        private Stack<Node> elements = new Stack<Node>();
        E elem;

        public RBIterator(){
            Node subroot = root;
            while(subroot != null){
                elements.push(subroot);
                subroot = subroot.left;
            }
        }

        public boolean hasNext() {
            return (!elements.isEmpty());
        }

        public E next() {
            Node subroot = elements.pop();
            E val = (E) subroot.elem;
            Node next = subroot.right;
            while(next != null){
                elements.push(next);
                next = next.left;
            }
            elem = val;
            return val;
        }

        public void remove() {
            RBTreeSet.this.remove(elem);
        }
    }

    public Node root;
    int size;

    RBTreeSet() {
        root = null;
        size = 0;
    }

    public boolean add(E e){
        return insert(e);
    }

    public boolean remove(E e){
        if(size <= 0)
            return false;
        Node subroot = findNode(root, e);
        if(subroot == null)
            return false;
        size--;
        return removal(subroot);
    }

    public boolean isEmpty(){
        return (root == null && size == 0);
    }

    public Iterator<E> iterator(){
        return new RBIterator();
    }

    // DO if you have time
    public int size(){
        return size;
    }

    boolean insert(E e){
        if(root == null){
            root = new Node(e);     // black root
            size++;
            return true;
        }
        Node subroot = root;
        while(true){
            if(subroot.elem == e)
                return false;
            else if(subroot.elem.compareTo(e) == 1){    // subroot.elem > e
                if(subroot.left == null){
                    subroot.left = new Node(e, null, null, subroot);
                    rebalanceInsert(subroot.left);
                    size++;
                    return true;
                }
                subroot = subroot.left;
            }
            else if(subroot.elem.compareTo(e) == -1){   // subroot.elem < e
                if(subroot.right == null){
                    subroot.right = new Node(e, null, null, subroot);
                    rebalanceInsert(subroot.right);
                    size++;
                    return true;
                }
                subroot = subroot.right;
            }
        }
    }

    void rebalanceInsert(Node subroot){

        // 1. If color of subroot's parent is not BLACK or subroot is NOT root
        if(subroot != root && subroot.parent.color == 1) { // changed from OR
            Node uncle = uncle(subroot);
            Node grandp = grandparent(subroot);
            if (uncle != null && uncle.color == 1) {
                // i. set uncle and parent as BLACK
                uncle.color = 0;
                subroot.parent.color = 0;
                // ii. set grandparent as RED
                grandp.color = 1;
                // iii. recurse on grandparent
                rebalanceInsert(grandp);
            } else if (grandp != null) {
                if (subroot.parent == grandp.left) {
                    if (subroot == subroot.parent.left) {     // Left Left
                        adjustRotateRight(grandp, true);
                    } else {                                  // Left Right
                        //grandp.left = rotateLeft(subroot.parent);
                        adjustRotateLeft(subroot.parent, true);
                        adjustRotateRight(grandp, true);
                    }
                } else {
                    if (subroot == subroot.parent.right) {    // Right Right
                        adjustRotateLeft(grandp, true);
                    } else {                                   // Right Left
                        //grandp.right = rotateRight(subroot.parent); // CHECK
                        adjustRotateRight(subroot.parent, true);
                        adjustRotateLeft(grandp, true);
                    }
                }
            }
        }
        root.color = 0;
    }

    public boolean removal(Node target){
        // Node to be removed
        int dummy = 0, left = 0;
        Node subroot;
        if(target.left == null || target.right == null)
            subroot = target;
        else
            subroot = rightMostChild(target.left);
        // Replacement node initialization
        Node replacement;
        if(subroot.left != null)
            replacement = subroot.left;
        else
            replacement = subroot.right;
        // Resetting target's parents to new replacement
        if(replacement != null)
            replacement.parent = subroot.parent;
        if(subroot.parent == null)
            root = replacement;
        else {
            if (subroot == subroot.parent.left) {
                subroot.parent.left = replacement;
                left = 1;
            }
            else {
                subroot.parent.right = replacement;
            }
        }
        // twoChild - copy over IOP's data
        if(subroot != target){
            target.elem = subroot.elem;
        }
        if(subroot.color == 0) {
            // Dummy node if replacment node is null
            if(replacement == null){
                replacement = new Node();
                replacement.parent = subroot.parent;
                if(left == 1)
                    subroot.parent.left = replacement;
                else
                    subroot.parent.right = replacement;
                dummy = 1;
            }
            rebalanceRemoval(replacement);
            // Cleanup dummy Node
            if(dummy == 1) {
                if(left == 1)
                    subroot.parent.left = null;
                else
                    subroot.parent.right = null;
            }
        }
        root.color = 0;
        return true;
    }

    void rebalanceRemoval(Node subroot){
        while(subroot != root && isBlack(subroot)){
            if(subroot == subroot.parent.left){
                Node sibling = subroot.parent.right;
                if(sibling.color == 1){
                    sibling.color = 0;
                    subroot.parent.color = 1;
                    adjustRotateLeft(subroot.parent, false);
                    sibling = subroot.parent.right;
                }
                if(isBlack(sibling.left) && isBlack(sibling.right)){
                    sibling.color = 1;
                    subroot = subroot.parent;
                }
                else{
                    if (isBlack(sibling.right)) {
                        if(sibling.left != null)
                            sibling.left.color = 0;
                        sibling.color = 1;
                        adjustRotateRight(sibling, false);
                        sibling = subroot.parent.right;
                    }
                    sibling.color = subroot.parent.color;
                    subroot.parent.color = 0;
                    if(sibling.right != null)
                        sibling.right.color = 0;
                    adjustRotateLeft(subroot.parent, false);
                    subroot = root;
                }
            }
            else {
                Node sibling = subroot.parent.left;
                if(sibling.color == 1){
                    sibling.color = 0;
                    subroot.parent.color = 1;
                    adjustRotateRight(subroot.parent, false);   // look at their imp of rotate
                    sibling = subroot.parent.left;
                }
                if(isBlack(sibling.right) && isBlack(sibling.left)){
                    sibling.color = 1;
                    subroot = subroot.parent;
                }
                else{
                    if (isBlack(sibling.left)) {
                        if(sibling.right != null)
                            sibling.right.color = 0;
                        sibling.color = 1;
                        adjustRotateLeft(sibling, false);
                        sibling = subroot.parent.left;
                    }
                    sibling.color = subroot.parent.color;
                    subroot.parent.color = 0;
                    if(sibling.left != null)
                        sibling.left.color = 0;
                    adjustRotateRight(subroot.parent, false);
                    subroot = root;
                }
            }
        }
        subroot.color = 0;
    }

    Node rotateLeft(Node parent, boolean swapColors){
        // rotation
        Node child = parent.right;
        parent.right = child.left;
        if(child.left != null)
            child.left.parent = parent;
        child.left = parent;
        // swap colors
        if(swapColors) {
            int temp = child.color;
            child.color = parent.color;
            parent.color = temp;
        }
        // update parent
        child.parent = parent.parent;
        parent.parent = child;
        return child;
    }

    Node rotateRight(Node parent, boolean swapColors){
        // rotation
        Node child = parent.left;
        parent.left = child.right;
        if(child.right != null)
            child.right.parent = parent;
        child.right = parent;
        // swap colors
        if(swapColors) {
            int temp = child.color;
            child.color = parent.color;
            parent.color = temp;
        }
        // update parent
        child.parent = parent.parent;
        parent.parent = child;
        return child;
    }

    void adjustRotateRight(Node grandp, boolean swapColors){
        // update root
        if(grandp == root)
            root = rotateRight(grandp, swapColors);
        // rotate & set new grandp's parent to grandp
        else {
            if (grandp == grandp.parent.left) {
                Node subroot = rotateRight(grandp, swapColors);
                subroot.parent.left = subroot;
            }
            else {
                Node subroot = rotateRight(grandp, swapColors);
                subroot.parent.right = subroot;
            }
        }
    }

    void adjustRotateLeft(Node grandp, boolean swapColors){
        // update root
        if(grandp == root)
            root = rotateLeft(grandp, swapColors);
        // rotate & set new grandp's parent to grandp
        else {
            if (grandp == grandp.parent.right) {
                Node subroot = rotateLeft(grandp, swapColors);
                subroot.parent.right = subroot;
            }
            else {
                Node subroot = rotateLeft(grandp, swapColors);
                subroot.parent.left = subroot;
            }
        }
    }

    Node grandparent(Node subroot){
        if((subroot != null) && (subroot.parent != null))
            return (subroot.parent).parent;
        else
            return null;
    }

    Node uncle(Node subroot){
        Node grandp = grandparent(subroot);
        if(grandp == null)
            return null;
        if(grandp.left == subroot.parent)
            return grandp.right;
        else
            return grandp.left;
    }

    Node rightMostChild(Node subroot){
        if(subroot.right == null)
            return subroot;
        return rightMostChild(subroot.right);
    }

    Node findNode(Node subroot, E e){
        if(subroot == null)
            return null;
        else if (subroot.elem == e)
            return subroot;
        else if(subroot.elem.compareTo(e) == 1) // subroot.elem > e
            return findNode(subroot.left, e);
        else
            return findNode(subroot.right, e);
    }

    boolean isBlack(Node subroot){
        return (subroot == null || subroot.color == 0);
    }
}