func toLeft(i int, nums []int) int {
    if i == 0 || nums[i-1] < nums[i] {
        return 1
    }
    return -1
}
func toRight(i int, nums []int) int {
    if i + 1 == len(nums) || nums[i] > nums[i+1] {
        return 1
    }
    return -1
}
func findPeakElement(nums []int) int {
    l := 0
    r := len(nums) - 1
    for {
        if l == r {
            break
        }
        m := (l + r) / 2
        tl := toLeft(m, nums)
        tr := toRight(m, nums)
        if tl > 0 && tr > 0 {
            return m
        }
        if tl > 0 && tr < 0 {
            l = m + 1
        } else {
            r = m
        }
    }
    return l
}
