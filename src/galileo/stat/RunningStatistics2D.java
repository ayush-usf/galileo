package galileo.stat;

/**
 * Expanding on the {@link RunningStatistics} class, this class supports
 * two dimensions (x and y), along with some extra features (simple linear
 * regression and calculating correlation).
 *
 * @author malensek
 */
public class RunningStatistics2D {

    private RunningStatistics xs;
    private RunningStatistics ys;

    private double SSxy;

    public RunningStatistics2D() {
        xs = new RunningStatistics();
        ys = new RunningStatistics();
    }

    /**
     * Adds several new (x, y) sample pairs to the 2D running statistics.
     *
     * @param samples Array of two-element arrays (representing x and y)
     */
    public void put(double[]... samples) {
        for (double[] sample : samples) {
            if (sample.length != 2) {
                throw new IllegalArgumentException("Input arrays must contain "
                        + "a single sample pair (x, y)");
            }
            put(sample[0], sample[1]);
        }
    }

    /**
     * Adds a new sample to the 2D running statistics.
     */
    public void put(double x, double y) {
        double n = (double) xs.numSamples();

        double dx = x - xs.mean();
        double dy = y - ys.mean();
        SSxy += dx * dy * n / (n + 1);

        xs.put(x);
        ys.put(y);
    }

    /**
     * @return Sum of the cross products (xy)
     */
    public double SSxy() {
        return SSxy;
    }

    /**
     * @return Sum of squared deviations from the mean of x
     */
    public double SSxx() {
        return xs.var() * (xs.numSamples() - 1.0);
    }

    /**
     * @return Sum of squared deviations from the mean of y
     */
    public double SSyy() {
        return ys.var() * (xs.numSamples() - 1.0);
    }

    /**
     * Calculate the Pearson product-moment correlation coefficient.
     *
     * @return PPMCC (Pearson's r)
     */
    public double R() {
        return Math.sqrt(R2());
    }

    /**
     * Calculate the coefficient of determination (R squared).
     *
     * @return coefficient of determination
     */
    public double R2() {
        double SSE = (SSyy() - SSxy() * SSxy() / SSxx());
        return (SSyy() - SSE) / SSyy();
    }

    /**
     * @return the slope of the regression line (beta).
     */
    public double slope() {
        return SSxy() / SSxx();
    }

    /**
     * @return the y-intercept of the regression line (alpha).
     */
    public double intercept() {
        return ys.mean() - slope() * xs.mean();
    }

    /**
     * Given a value of x, estimate the outcome of y using simple linear
     * regression.
     *
     * @return estimated value of y
     */
    public double predict(double x) {
        return intercept() + slope() * x;
    }

    /**
     * @return a copy of the running statistics instance for x.
     */
    public RunningStatistics xStatistics() {
        return new RunningStatistics(xs);
    }

    /**
     * @return a copy of the running statistics instance for y.
     */
    public RunningStatistics yStatistics() {
        return new RunningStatistics(ys);
    }
}
