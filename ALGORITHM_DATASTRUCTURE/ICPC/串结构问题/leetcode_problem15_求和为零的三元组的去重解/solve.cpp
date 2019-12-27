class Solution {
public:
    vector<vector<int>> threeSum(vector<int>& nums) {
        vector<vector<int>> ans;
        
        sort(nums.begin(), nums.end());
        
        for (int i = 0; i < nums.size(); i ++) {
            if (i > 0 && nums[i] == nums[i-1]) continue;
            
            for (int j = i + 1; j < nums.size(); j ++) {
                if (j > i + 1 && nums[j] == nums[j-1]) continue;
                
                int l = j + 1;
                int r = nums.size() - 1;
                while (l < r) {
                    int mid = (l + r) / 2;
                    int sum = nums[i] + nums[j] + nums[mid];
                    if (sum == 0) {
                        l = r = mid;
                        break;
                    }
                    if (sum < 0) l = mid + 1;
                    else r = mid - 1;
                }
                
                if (l == r && l >= 0 && l < nums.size()) {
                    if (nums[i] + nums[j] + nums[l] == 0) {
                        vector<int> t;
                        t.push_back(nums[i]);
                        t.push_back(nums[j]);
                        t.push_back(nums[l]);
                        ans.push_back(t);
                    }
                }
            }
        }
        
        return ans;
    }
};
