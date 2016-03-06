package memory;

import java.util.*;

/**
 * This memory model allocates memory cells based on the best-fit method.
 * 
 * @author "Johan Holmberg, Malmö university"
 * @since 1.0
 */
public class BestFit extends Memory {

	private class Block {
		private Pointer pointer;
		private int size;
		private boolean free = true;
		private Block next = null;
		private Block previous = null;

		public Block(Pointer pointer, int size) {
			this.pointer = pointer;
			this.size = size;
		}

		public String toString() {
			return String.format("%02d - %02d\t\t%s (size %d)", pointer.pointsAt(), pointer.pointsAt() + size - 1, free ? "Free" : "Allocated", size);
		}
	}

	private class BlockComparator implements Comparator<Block> {
		@Override
		public int compare(Block b1, Block b2) {
			int b1free = b1.free ? 1 : 0;
			int b2free = b2.free ? 1 : 0;

			if (b1.free && b2.free) {
				return (b1.size - b2.size);
			} else {
				return (b2free - b1free);
			}

		}
	}

	private final List<Block> blocks = new LinkedList<>();

	/**
	 * Initializes an instance of a best fit-based memory.
	 * 
	 * @param size The number of cells.
	 */
	public BestFit(int size) {
		super(size);

		blocks.add(new Block(new Pointer(this), size));
	}

	/**
	 * Allocates a number of memory cells. 
	 * 
	 * @param size the number of cells to allocate.
	 * @return The address of the first cell.
	 */
	@Override
	public Pointer alloc(int size) {
		if (size > this.cells.length)
			return null;

		System.out.println("Allocating " + size);

		Pointer p = null;

		for (Block block : blocks) {

			if (!block.free || block.size < size)
				continue;

			if (size == block.size) {
				block.free = false;
				p = block.pointer;
				break;
			}

			blocks.remove(block);

			int blockStart = block.pointer.pointsAt();
			p = new Pointer(blockStart, this);
			Block alloc = new Block(p, size);
			Block leftover = new Block(new Pointer(blockStart + size, this), block.size - size);

			if (block.previous != null)
				block.previous.next = alloc;
			if (block.next != null)
				block.next.previous = leftover;

			alloc.free = false;
			alloc.next = leftover;
			alloc.previous = block.previous;
			leftover.next = block.next;
			leftover.previous = alloc;
			blocks.add(alloc);
			blocks.add(leftover);

			break;

		}

		sort();

		printAlternateLayout();

		return p;
	}

	private void sort() {
		blocks.sort(new BlockComparator());
	}
	
	/**
	 * Releases a number of data cells
	 * 
	 * @param p The pointer to release.
	 */
	@Override
	public void release(Pointer p) {
		System.out.println("Releasing " + p.pointsAt());

		for (Block block : blocks) {
			if (!block.pointer.equals(p))
				continue;

			block.free = true;

			block = combine(block, block.next);
			if (block != null)
				combine(block.previous, block);


			break;
		}

		sort();

		printAlternateLayout();

	}

	private Block combine(Block b1, Block b2) {
		Block combined = null;
		if (b1.free && b2.free) {
			combined = new Block(b1.pointer, b1.size + b2.size);
			blocks.remove(b1);
			blocks.remove(b2);
			blocks.add(combined);

			b1.previous.next = combined;
			combined.previous = b1.previous;
			combined.next = b2.next;
		}

		return combined;
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
		blocks.sort((b1, b2) -> { return b1.pointer.pointsAt() - b2.pointer.pointsAt(); });

		for (Block block : blocks) {

			System.out.println(block);

		}

		blocks.sort(new BlockComparator());

		printAlternateLayout();
	}

	private void printAlternateLayout() {

		String[][] output = generateEmptyMemory(this.cells.length);
		boolean indexAbove = true;
		boolean halfTone = false;
		int pointerIndex = 0;

		for (Block block: blocks) {
			int start = block.pointer.pointsAt() + 1;
			int end = start + block.size;

			for (int i = start; i < end; i++) {
				output[2][i] = block.free ? "░" : "█";
			}

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

	public void compact() {
		System.out.println("Compacting...");

		int freeBlocks = (int)blocks.stream().filter(b -> b.free).count();

		blocks.sort((b1, b2) -> { return b1.pointer.pointsAt() - b2.pointer.pointsAt(); });

		for (int i = 0; i < blocks.size(); i++) {

			Block b = blocks.get(i);

			if (b.free && b.next != null) {
				if (!b.next.free)
					flip(b, b.next);
				else {
					combine(b, b.next);
					i--;
					blocks.sort((b1, b2) -> { return b1.pointer.pointsAt() - b2.pointer.pointsAt(); });
				}
			}

			printAlternateLayout();
		}

		sort();

	}

	private void flip(Block b1, Block b2) {
		int size = b1.size;
		b1.size = b2.size;
		b2.size = size;
		b2.pointer.pointAt(b1.pointer.pointsAt() + b1.size);
		b1.free = false;
		b2.free = true;
	}

}
