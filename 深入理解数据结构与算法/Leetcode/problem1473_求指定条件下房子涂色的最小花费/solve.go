const N = 25
const M = 105
var dp [M][N][M]int
func min(a int, b int) int {
    if b == -1 || (a != -1 && a < b) {
        return a
    }
    return b
}
func minCost(houses []int, cost [][]int, m int, n int, target int) int {
    for i := 0; i < M; i ++ {
        for j := 0; j < N; j ++ {
            for k := 0; k < M; k ++ {
                dp[i][j][k] = -1
            }
        }
    }

    if houses[0] == 0 {
        for j := 1; j <= n; j ++ {
            dp[0][j][1] = cost[0][j-1]
        }
    } else {
        dp[0][houses[0]][1] = 0
    }
    
    for i := 1; i < m; i ++ {
        for j0 := 1; j0 <= n; j0 ++ {
            for k := 1; k <= target; k ++ {
                if dp[i-1][j0][k] == -1 {
                    continue
                }
                for j1 := 1; j1 <= n; j1 ++ {
                    if houses[i] != 0 && j1 != houses[i] {
                        continue
                    }
                    var c int
                    if houses[i] == 0 {
                        c = cost[i][j1-1]
                    } else {
                        c = 0
                    }
                    if j0 == j1 {
                        dp[i][j1][k] = min(dp[i][j1][k], dp[i-1][j0][k] + c)
                    } else {
                        dp[i][j1][k+1] = min(dp[i][j1][k+1], dp[i-1][j0][k] + c)
                    }
                }
            }
        }
    }

    ans := -1
    for j := 1; j <= n; j ++ {
        ans = min(ans, dp[m-1][j][target])
    }
    return ans
}
