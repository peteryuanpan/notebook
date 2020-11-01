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
    y := findFirstLargerIndex(nums2, nums1[l])
    if l + 1 + y < target_n {
        return cal(nums2, nums1, target_n)
    }
    if l + 1 + y > target_n {
        d := l + 1 + y - target_n
        y = y - d
        return float64(nums2[y])
    }
    return float64(nums1[l])
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
