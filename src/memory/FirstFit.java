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
	private boolean isCompacted;
	private boolean secondTry;
	

	/**
	 * Initializes an instance of a first fit-based memory.
	 * 
	 * @param size The number of cells.
	 */
	public FirstFit(int size) {
		super(size);
		pointers = new LinkedList<Pointer>();
		pointersSize = new HashMap<Pointer, Integer>();
		isCompacted = false;
		secondTry = false;
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
		if(cells.length - counter + 1 > size){
			return addPointer(counter, size);
		}
		if(!secondTry && !isCompacted){
			secondTry = true;
			compact();
			Pointer  temp = alloc(size);
			secondTry = false;
			return temp;
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
		isCompacted = false;
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
		System.out.println("------------------------------------");

		printAlternateLayout();

		int counter = 0;
		for(Pointer p : pointers){
			if(counter < p.pointsAt()){
				System.out.println("" + counter + " - " + (p.pointsAt() - 1) + " Free");
			}
			System.out.println("" + p.pointsAt() + " - " + (p.pointsAt() + pointersSize.get(p) - 1) + " Allocated  (pointerSize is " + pointersSize.get(p) + ")");
			counter = p.pointsAt() + pointersSize.get(p);
		}
		if(counter < cells.length){
			System.out.println("" + counter + " - " + cells.length + " Free");
		}
	}



	private void printAlternateLayout() {

		String[][] output = generateEmptyMemory(this.cells.length);
		boolean indexAbove = true;
		boolean halfTone = false;
		int pointerIndex = 0;


		for (Pointer p : pointers) {
			int start = p.pointsAt() + 1;
			int end = start + pointersSize.get(p);

			/*
			output[1][start] = '#';
			output[2][start] = '|';
			output[3][start] = '#';

			output[1][end] = '#';
			output[2][end] = '|';
			output[3][end] = '#';
			*/


			for (int i = start; i < end; i++) {
				output[2][i] = halfTone ? "▓" : "█";
			}
			//output[2][end - 1] = "▌";
			//output[2][start] = "▐";
			//halfTone = !halfTone;

			printText(output, start, indexAbove, String.valueOf(start - 1));
			indexAbove = !indexAbove;
			printText(output, end - 1, indexAbove, String.valueOf(end - 2));
			indexAbove = !indexAbove;

		}

		for (int i = 0; i < output.length; i++) {
			for (int j = 0; j < output[i].length; j++)
				System.out.print(output[i][j]);
			System.out.println();
		}



	}
	
	/**
	 * Compacts the memory space.
	 */
	public void compact() {
		int counter = 0;
		for(Pointer p : pointers){
			p.pointAt(counter);
			counter = p.pointsAt() + pointersSize.get(p);
		}
		isCompacted = true;
	}
	
	/**
	 * Sorts the pointers in order of what memoryadress they are pointing to.
	 * (Ascending)
	 */
	private void sortPointers(){
		pointers.sort((p1, p2) -> { return p1.pointsAt() - p2.pointsAt(); });
	}
}
