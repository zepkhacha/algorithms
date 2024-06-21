import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

import java.lang.Math;

public class PercolationStats {

    /**
     * The {@code PercolationStats} class creates a nxn @code Percolation
     * object many times. For each object, it opens a random site
     * until percolation is achieved.
     * It prints out the mean (average fraction of open sites at time of
     * percolation) and standard deviation thereof, in addition to the
     * low and high endpoints of the 95% confidence intervals.
     */

    private double mean;
    private double std;
    private double ciLow;
    private double ciHigh;

    public PercolationStats(int n, int trials) {

        if (n <=0 || trials <= 0) {
            throw new IllegalArgumentException();
        }

        // x is an array of fractional values representing no. of open sites we need at time of percolation
        double[] x = new double[trials];

        for (int trial =0; trial<trials; trial++){
            Percolation p = new Percolation(n);
            while (!p.percolates()){
                // pick a random site
                int randomRow = StdRandom.uniformInt(1,n+1);
                int randomCol = StdRandom.uniformInt(1,n+1);
                // open site
                p.open(randomRow,randomCol);
            }
            // when system percolates, update x[trial]
            x[trial] = (double) p.numberOfOpenSites() / (double) (n*n);
        }
        // update mean and std;
        mean = StdStats.mean(x);
        std = StdStats.stddev(x);
        // update ci interval
        ciLow = mean - (1.96*std)/Math.sqrt(trials);
        ciHigh = mean + (1.96*std)/Math.sqrt(trials);
    }

    // sample mean of percolation threshold
    public double mean() {
        return mean;
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return std;
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return ciLow;
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return ciHigh;
    }

    // test client
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int T = Integer.parseInt(args[1]);

        PercolationStats ps = new PercolationStats(n,T);
        StdOut.printf("mean                    = %f\n", ps.mean());
        StdOut.printf("stddev                  = %f\n", ps.stddev());
        StdOut.printf("95%% confidence interval = [%f,%f]\n", ps.confidenceLo(), ps.confidenceHi());

    }

}