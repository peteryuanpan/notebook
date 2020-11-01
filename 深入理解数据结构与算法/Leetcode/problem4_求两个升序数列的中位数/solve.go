func findMedianSortedArrays(nums1 []int, nums2 []int) float64 {
    n1 := len(nums1)
    n2 := len(nums2)
    if n1 == 0 {
        return findMedianSortedArrays(nums2, nums1)
    }
    if n2 == 0 {
        if n1 % 2 == 1 {
            return float64(nums1[n1/2])
        } else {
            return float64(nums1[n1/2-1] + nums1[n1/2]) / 2
        }
    }
    if (n1 + n2) % 2 == 1 {
        return cal(nums1, nums2, (n1 + n2) / 2  + 1)
    } else {
        ans1 := cal(nums1, nums2, (n1 + n2) / 2)
        ans2 := cal(nums1, nums2, (n1 + n2) / 2 + 1)
        return float64(ans1 + ans2) / 2
    }
}
func cal(nums1 []int, nums2 []int, tnum int) float64 {
    n1 := len(nums1)
    n2 := len(nums2)
    l := 0
    r := n1 - 1
    for {
        if l == r {
            break
        }
        m := (l + r) / 2
        index1 := m
        index2 := tnum - (index1 + 1) - 1
        if index2 < 0 {
            r = m
        } else if index2 >= n2 {
            l = m + 1
        } else if nums1[index1] < nums2[index2] {
            l = m + 1
        } else {
            r = m
        }
    }
    index1 := l
    index2 := tnum - (index1 + 1) - 1
    if index2 >= 0 && index2 < n2 && nums1[index1] < nums2[index2] {
        return cal(nums2, nums1, tnum)
    }
    if index2 + 1 >= 0 && index2 + 1 < n2 && nums1[index1] > nums2[index2+1] {
        return cal(nums2, nums1, tnum)
    }
    return float64(nums1[index1])
}
