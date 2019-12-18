class Solution {
public:
	vector<vector<int>> threeSum(vector<int>& nums) {
		vector<vector<int>> ans;
		if(nums.size() <= 2) return ans;
		
		sort(nums.begin(),nums.end());
		
		for(int i = 0 ; i < nums.size();) {
			int left = i+1;
			int right = nums.size()-1;
			while(right > left) {
				if(nums[right] + nums[left] + nums[i] == 0) {
					vector<int> t;
					t.push_back(nums[right]);
					t.push_back(nums[left]);
					t.push_back(nums[i]);
					ans.push_back(t);
					
					//skip over duplicates		
					while(right > left && nums[right] == nums[right-1]) {
						right--;
					}
					while(right > left && nums[left] == nums[left+1]) { 
						left++;
					}
					
					right--;
					left++;
					
				} else if(nums[right] + nums[left] + nums[i] > 0) {
					right--;
				} else if (nums[right] + nums[left] + nums[i] < 0) {
					left++;
				}
			}
			
			//skip over duplicates
			while(i+1 < nums.size() && nums[i] == nums[i+1]) {
				i++;
			}
			i++;
		}
		
		return ans;
	}
};
