import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

/**
 * The {@code Percolation} class is a data type to help us solve the
 * percolation problem. This is typically demonstrated with a
 * nxn grid where each unit can be open or closed. The system
 * 'percolates' when there are open sites that connect any
 * portion of the top row to the bottom row.
 * @author Zepyoor Khechadoorian
 */

public class Percolation {

    /**
     * The grid stores 0 (closed) or 1 (open) for each site.
     * 'size' is the user-input size n for a nxn grid.
     * This class uses the WeightedQuickUnionUF to track which
     * sites are connected to each other. There are two such
     * instances of this class: ufVT, which keeps track of the
     * grid with the addition of a 'virtual top', and ufVB which
     * does the same with a 'virtual bottom'. In each case,
     * the {@code WeightedQuickUnionUF} object tracks whether
     * a site is connected to the virtual top and bottom separately.
     * In doing so, we avoid 'backwash', which can occur if a system
     * percolates and also has open sites in the bottom row that
     * are not connected to the percolating group.
      */

    private boolean[][] grid;
    private int size;
    private WeightedQuickUnionUF ufVT;
    private WeightedQuickUnionUF ufVB;
    private int openSites = 0;
    private boolean percolationAchieved = false;

    /**
     * Test whether row and col are valid coordinates
     * for grid 'size' assuming 1-based indexing.
     * @return true or false.
     */

    private boolean isValidSite(int row, int col) {
        if (row < 1 || row > size || col < 1 || col > size ) {
            return false;
        }else {
            return true;
        }
    }

    /**
     * Converts 1-based coordinates (row, col) into a flat index
     * from 0 - (nxn) to refer to the correct index in the UF
     * objects, which are not 2D but 1D.
     */
    private int convertToIndex(int row, int col) {
        return (row-1)*size + (col-1);
    }

    /**
     * Helper function that first prints the grid to shell, and next prints the
     * ufVT in grid-formatting. The ufVT grid shows the root number
     * for each site in the grid. Sites are connected if they
     * share the same root. Used for debugging.
     */

    private void showMaterial() {
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                StdOut.printf("%3d ", grid[row][col] ? 1 : 0);
            }
            StdOut.println("");
        }
        StdOut.println("");
        StdOut.printf("virtual top: %3d\n", ufVT.find(size*size));
        for (int row =0; row<size; row++){
            for (int col=0; col<size; col++){
                StdOut.printf("%3d ", ufVT.find( convertToIndex( row+1, col+1 )));
            }
            StdOut.println("");
        }
    }

    /**
     * Constructor class to create nxn grid.
     * @param n user-defined grid size
     */

    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException();
        }
        grid = new boolean[n][n];
        size = n;
        ufVT = new WeightedQuickUnionUF(n*n+1);
        ufVB = new WeightedQuickUnionUF(n*n+1);
        openSites = 0;
    }

    /**
     * If given a valid coordinate pair (assuming 1-based indexing), this function
     * opens the corresponding site in the grid. If in the top row, it unifies this
     * site to the virtual top in ufVT. If in the bottom row, it unifies this site
     * to the virtual bottom in ufVB. If any neighbors are open, it unifies this
     * site to its neighbors in both ufVT and ufVB.
     * It checks if percolation is achieved by checking if the opened cell is
     * connected to both the virtual top and bottom, and if so, sets
     * percolationAchieved to be true.
     */

    public void open(int row, int col) {

        // if the site is already open, we don't need to do more
        if (isOpen(row,col)){
            return;
        }

        grid[row - 1][col - 1] = true;
        openSites++;

        int p = convertToIndex(row, col);

        // if in top row, connect to virtual top
        if (row==1){
            ufVT.union(p, size*size);
        }
        // if in bot row, connect to virtual bot
        if (row==size){
            ufVB.union(p,size*size);
        }

        // if any of the neighbors are also open, they should be merged via union
        if (row>1){
            if (isOpen(row-1,col)){
                int q = convertToIndex(row-1, col);
                ufVT.union(p, q);
                ufVB.union(p, q);
            }
        }
        if (row<size){
            if (isOpen(row+1,col)){
                int q = convertToIndex(row+1,col);
                ufVT.union(p,q);
                ufVB.union(p, q);
            }
        }
        if (col>1){
            if (isOpen(row,col-1)){
                int q = convertToIndex(row,col-1);
                ufVT.union(p,q);
                ufVB.union(p, q);
            }
        }
        if (col<size){
            if (isOpen(row,col+1)){
                int q = convertToIndex(row,col+1);
                ufVT.union(p,q);
                ufVB.union(p, q);
            }
        }

        // if connected to virtual top in ufVT
        // and also to virtual bottom in ufVB,
        // percolation is achieved
        if (ufVT.find(p)==ufVT.find(size*size) && ufVB.find(p)==ufVB.find(size*size)){
            percolationAchieved = true;
        }
    }

    /**
     * @return If a 1-based coordinate pair points to a valid site
     * within the nxn grid.
     */

    public boolean isOpen(int row, int col) {
        if (!isValidSite(row, col)) {
            throw new IllegalArgumentException();
        }
        return grid[row-1][col-1];
    }

    /**
     * @return If a 1-based coordinate pair points to a 'full' site,
     * i.e. a site that is both open and connected to the virtual top.
     */

    public boolean isFull(int row, int col) {
        if (!isValidSite(row, col)) {
            throw new IllegalArgumentException();
        }
        if (isOpen(row, col)) {
            int p = convertToIndex(row,col);
            if (ufVT.find(p)==ufVT.find(size*size)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return Number of open sites in the grid.
     */

    public int numberOfOpenSites() {
        return openSites;
    }

    /**
     * @return Whether or not the system percolates.
     */

    public boolean percolates() {
        // go through last row and look for full sites
        return percolationAchieved;
    }

    // test client (optional)
    public static void main(String[] args) {

        Percolation p = new Percolation(5);
        p.open(1,4);
        p.open(1,3);
        p.open(4,4);
        p.open(3,4);
        p.open(5,4);
        p.open(2,4);
        p.open(5,1);

        p.showMaterial();
        StdOut.printf("\n%d open sites\n", p.openSites);
        StdOut.printf("is full? %b\n", p.isFull(5,1));
        StdOut.printf("percolates? %b\n", p.percolates());

    }

}
