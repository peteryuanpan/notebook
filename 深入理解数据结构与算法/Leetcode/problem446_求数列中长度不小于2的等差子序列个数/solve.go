func numberOfArithmeticSlices(A []int) int {
    f := make([]map[int]int, len(A))
    for i := 0; i < len(A); i ++ {
        f[i] = make(map[int]int)
    }
    sum := 0
    for i := 0; i < len(A); i ++ {
        for j := 0; j < i; j ++ {
            d := A[j] - A[i]
            f[i][d] += f[j][d] + 1
            sum += f[j][d]
        }
    }
    return sum
}
