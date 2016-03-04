package memory;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * This memory model allocates memory cells based on the first-fit method. 
 * 
 * @author "Johan Holmberg, Malmö university"
 * @since 1.0
 */
public class FirstFit extends Memory {
	private LinkedList<Pointer> pointers;
	private HashMap<Pointer, Integer> pointersSize;

	/**
	 * Initializes an instance of a first fit-based memory.
	 * 
	 * @param size The number of cells.
	 */
	public FirstFit(int size) {
		super(size);
		pointers = new LinkedList<Pointer>();
		pointersSize = new HashMap<Pointer, Integer>();
	}

	/**
	 * Allocates a number of memory cells. 
	 * 
	 * @param size the number of cells to allocate.
	 * @return The address of the first cell.
	 */
	@Override
	public Pointer alloc(int size) {
		int counter = 0;
		for(Pointer p : pointers){
			if((p.pointsAt() - counter) > size){
				return addPointer(counter, size);
			}
			counter = p.pointsAt() + pointersSize.get(p);
		}
		if(cells.length - counter > size){
			return addPointer(counter, size);
		}
		return null;
	}
	
	private Pointer addPointer(int pointAt, int size) {
		Pointer pointer = new Pointer(this);
		pointer.pointAt(pointAt);
		pointers.add(pointer);
		pointersSize.put(pointer, size);
		sortPointers();
		return pointer;
	}

	/**
	 * Releases a number of data cells
	 * 
	 * @param p The pointer to release.
	 */
	@Override
	public void release(Pointer p) {
		pointersSize.remove(p);
		pointers.remove(p);
		sortPointers();
	}
	
	/**
	 * Prints a simple model of the memory. Example:
	 * 
	 * |    0 -  110 | Allocated
	 * |  111 -  150 | Free
	 * |  151 -  999 | Allocated
	 * | 1000 - 1024 | Free
	 */
	@Override
	public void printLayout() {
		//TODO: fungerar ej
		int counter = 0;
		for(Pointer p : pointers){
			if(counter != p.pointsAt()){
				System.out.println("" + counter + " - " + (p.pointsAt() - 1) + " Free");
			}
			System.out.println("" + p.pointsAt() + " - " + (p.pointsAt() + pointersSize.get(p)) + " Allocated");
			counter = p.pointsAt() + pointersSize.get(p) + 1;
		}
		if(counter < cells.length){
			System.out.println("" + counter + " - " + cells.length + " Free");
		}
	}
	
	/**
	 * Compacts the memory space.
	 */
	public void compact() {
		//TODO: fungerar ej tror jag? (alt. bara printLayout som inte fungerar
		int counter = 0;
		for(Pointer p : pointers){
			p.pointAt(counter);
			counter = p.pointsAt() + 1;
		}
	}
	
	/**
	 * Sorts the pointers in order of what memoryadress they are pointing to.
	 * (Ascending)
	 */
	private void sortPointers(){
		pointers.sort(new Comparator<Pointer>(){
			@Override
			public int compare(Pointer p1, Pointer p2) {
				return p1.pointsAt() - p2.pointsAt();
			}
			
		});
	}
}
