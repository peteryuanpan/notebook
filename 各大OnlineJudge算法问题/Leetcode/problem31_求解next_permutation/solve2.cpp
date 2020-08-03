class Solution {
public:
    void nextPermutation(vector<int>& nums) {        
        bool hasNext = impl_next_permutation(nums);
        if (!hasNext) {
            sort(nums.begin(), nums.end());
        }
    }

    bool impl_next_permutation(vector<int>& nums) {
        for (int i = nums.size() - 2; i >= 0; i --) {
            if (nums[i] < nums[i+1]) {
                // 从右往左找到第一个大于nums[i[的数，与其交换
                int l = i + 1, r = nums.size() - 1;
                while (l < r) {
                    int mid = (l + r) / 2 + 1;
                    if (nums[mid] <= nums[i]) r = mid - 1;
                    else l = mid;
                }
                swap(nums[i], nums[l]);
                // 将 nums[i]（不包括）往后的数列 Reverse 一下
                l = i + 1, r = nums.size() - 1;
                while (l < r) {
                    swap(nums[l], nums[r]);
                    l ++, r --;
                }
                return true;
            }
        }
        return false;
    }
};
