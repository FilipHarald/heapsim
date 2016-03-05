package memory;

/**
 * Memory is a slightly more advanced memory model, with support for object
 * allocation and deallocation. Basically, this is a heap in spe. Write and
 * read from the memory using {@link Pointer}.
 * 
 * @author "Johan Holmberg, Malmö university"
 * @since 1.0
 */
public abstract class Memory extends RawMemory {

	/**
	 * Initializes an instance of Memory.
	 * 
	 * @param size The number of cells.
	 */
	public Memory(int size) {
		super(size);
	}
	
	/**
	 * Allocates a number of memory cells. 
	 * 
	 * @param size the number of cells to allocate.
	 * @return The address of the first cell.
	 */
	public abstract Pointer alloc(int size);
	
	/**
	 * Releases a number of data cells
	 * 
	 * @param p The pointer to release.
	 */
	public abstract void release(Pointer p);

	/**
	 * Prints a simple model of the memory. Example:
	 *
	 * |    0 -  110 | Allocated
	 * |  111 -  150 | Free
	 * |  151 -  999 | Allocated
	 * | 1000 - 1024 | Free
	 */
	public abstract void printLayout();

	protected String[][] generateEmptyMemory(int size) {
		String[][] output = new String[5][size + 2];

		output[0][0] = " ";
		output[1][0] = "┌";
		output[2][0] = "│";
		output[3][0] = "└";
		output[4][0] = " ";

		output[0][size + 1] = " ";
		output[1][size + 1] = "┐";
		output[2][size + 1] = "│";
		output[3][size + 1] = "┘";
		output[4][size + 1] = " ";

		for (int i = 1; i < size + 1; i++) {

			output[0][i] = " ";
			output[1][i] = "─";
			output[2][i] = "░";
			output[3][i] = "─";
			output[4][i] = " ";

		}

		return output;
	}

	protected void printText(String[][] output, int index, boolean indexAbove, String text) {
		for (int i = 0; i < text.length(); i++) {
			int row = indexAbove ? 0 : 4;
			output[row][index + i] = String.valueOf(text.charAt(i));
		}
	}
}
