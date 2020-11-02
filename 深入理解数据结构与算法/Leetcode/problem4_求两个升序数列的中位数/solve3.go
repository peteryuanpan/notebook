func findMedianSortedArrays(nums1 []int, nums2 []int) float64 {
    n1 := len(nums1)
    n2 := len(nums2)
    if n1 == 0 {
        return findMedianSortedArrays(nums2, nums1)
    }
    if ((n1 + n2) % 2 == 1) {
        return cal(nums1, nums2, (n1 + n2) / 2 + 1)
    } else {
        ans1 := cal(nums1, nums2, (n1 + n2) / 2)
        ans2 := cal(nums1, nums2, (n1 + n2) / 2 + 1)
        return (ans1 + ans2) / 2
    }
}
func cal(nums1 []int, nums2 []int, target_n int) float64 {
    l := 0
    r := len(nums1) - 1
    for {
        if l == r {
            break
        }
        m := (l + r) / 2
        y := findFirstLargerIndex(nums2, nums1[m])
        if m + 1 + y == target_n {
            return float64(nums1[m])
        } else if m + 1 + y > target_n {
            r = m
        } else {
            l = m + 1
        }
    }
    type Pair struct {
        nums []int
        index int    
    }
    var pairs [2]Pair
    pairs[0] = Pair {
        nums: nums1,
        index: l,
    }
    pairs[1] = Pair {
        nums: nums2,
        index: findFirstLargerIndex(nums2, nums1[l]) - 1,
    }
    for {
        sum := 0
        for i := 0; i < len(pairs); i ++ {
            sum += pairs[i].index + 1
        }
        if sum == target_n {
            break
        }
        if sum > target_n {
            var max_pair *Pair = nil
            for i := 0; i < len(pairs); i ++ {
                if pairs[i].index >= 0 && pairs[i].index < len(pairs[i].nums) {
                    if max_pair == nil || pairs[i].nums[pairs[i].index] > max_pair.nums[max_pair.index] {
                        max_pair = &pairs[i]
                    }
                }
            }
            max_pair.index --
        } else {
            var min_pair *Pair = nil
            for i := 0; i < len(pairs); i ++ {
                if pairs[i].index + 1 >= 0 && pairs[i].index + 1 < len(pairs[i].nums) {
                    if min_pair == nil || pairs[i].nums[pairs[i].index+1] < min_pair.nums[min_pair.index+1] {
                        min_pair = &pairs[i]
                    }
                }
            }
            min_pair.index ++
        }
    }
    var max_pair *Pair = nil
    for i := 0; i < len(pairs); i ++ {
        if pairs[i].index >= 0 && pairs[i].index < len(pairs[i].nums) {
            if max_pair == nil || pairs[i].nums[pairs[i].index] > max_pair.nums[max_pair.index] {
                max_pair = &pairs[i]
            }
        }
    }
    return float64(max_pair.nums[max_pair.index])
}
func findFirstLargerIndex(nums []int, target_x int) int {
    l := 0
    r := len(nums)
    for {
        if l == r {
            break
        }
        m := (l + r) / 2
        if nums[m] <= target_x {
            l = m + 1
        } else {
            r = m
        }
    }
    return l
}
