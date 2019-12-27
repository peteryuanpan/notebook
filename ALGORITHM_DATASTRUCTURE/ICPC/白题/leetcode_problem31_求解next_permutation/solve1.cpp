class Solution {
public:
    void nextPermutation(vector<int>& nums) {
        bool hasNext = next_permutation(nums.begin(), nums.end());
        if (!hasNext) {
            sort(nums.begin(), nums.end());
        }
    }
};
