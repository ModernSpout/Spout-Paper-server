package org.fiddlemc.fiddle.impl.util.datastructure;

/**
 * Union-find is a data structure of a given number of nodes, where initially, all nodes form a group of themselves.
 * Then, there is a merge operation, that allows the groups that two nodes are in to be merged.
 * Additionally, there is a find operation, that finds the root of the group that a node belongs to.
 * This way, it can be easily checked whether two nodes are in the same group
 * by checking whether the roots of their groups are the same.
 *
 * <p>
 * Running time: O(n) to initialize, near-O(1) for {@link #find} and {@link #merge} (optimal).
 * </p>
 *
 * @author Martijn Muijsers
 */
public class UnionFind {

    private final int[] root, rank;

    public UnionFind(int size) {
        this.root = new int[size];
        this.rank = new int[size];
        for (int i = 1; i < size; i++) {
            this.root[i] = i;
        }
    }

    /**
     * @return The root of the group {@code x} belongs to.
     */
	public int find(int x) {
		if (this.root[x] != x) {
            this.root[x] = this.find(this.root[x]);
		}
		return this.root[x];
	}

    /**
     * Merges the groups that {@code x} and {@code y} belong to.
     */
	public void merge(int x, int y) {
		x = this.find(x);
		y = this.find(y);
		if (x == y) {
			return;
		}
		if (this.rank[x] < this.rank[y]) {
            this.root[x] = y;
		} else if (this.rank[x] >= this.rank[y]) {
            this.root[y] = x;
			if (this.rank[x] == this.rank[y]) {
                this.rank[x]++;
			}
		}
	}
	
}
