class Solution {
    
    public int[] findBall(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;
        int[] ans = new int[n];
        for (int j = 0; j < n; j ++) {
            int x = 0, y = j;
            while (x < m) {
                if (y + 1 == n) {
                    if (y != 0 && grid[x][y] == -1 && grid[x][y-1] == -1) {
                        x ++;
                        y --;
                    } else {
                        break;
                    }
                    continue;
                }
                if (y == 0) {
                    if (y + 1 != n && grid[x][y] == 1 && grid[x][y+1] == 1) {
                        x ++;
                        y ++;
                    } else {
                        break;
                    }
                    continue;
                }
                if (grid[x][y] == 1 && grid[x][y+1] == 1) {
                    x ++;
                    y ++;
                } else if (grid[x][y] == 1 && grid[x][y+1] == -1) {
                    break;
                } else if (grid[x][y] == -1 && grid[x][y-1] == -1) {
                    x ++;
                    y --;
                } else if (grid[x][y] == -1 && grid[x][y-1] == 1) {
                    break;
                }
            }
            ans[j] = x == m ? y : -1;
        }
        return ans;
    }
}
