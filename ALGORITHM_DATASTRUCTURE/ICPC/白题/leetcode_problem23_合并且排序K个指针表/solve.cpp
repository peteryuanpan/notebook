/**
 * Definition for singly-linked list.
 * struct ListNode {
 *     int val;
 *     ListNode *next;
 *     ListNode(int x) : val(x), next(NULL) {}
 * };
 */
class Solution {
public:    
    ListNode * merge2Lists(ListNode * l1, ListNode * l2) {
        ListNode * head = NULL;
        ListNode * l3 = NULL;
        while (l1 != NULL || l2 != NULL) {
            if (l2 == NULL || (l1 != NULL && l1->val <= l2->val)) {
                if (l3 == NULL) {
                    l3 = l1;
                    head = l3;
                } else {
                    l3->next = l1;
                    l3 = l3->next;
                }
                l1 = l1->next;
            } else {
                if (l3 == NULL) {
                    l3 = l2;
                    head = l3;
                } else {
                    l3->next = l2;
                    l3 = l3->next;
                }
                l2 = l2->next;
            }
        }
        return head;
    }
    
    ListNode * mergeKLists(vector<ListNode*>& lists) {
        int len = lists.size();
        if (len == 0) return NULL;
        if (len == 1) return lists[0];
        sort(lists.begin(), lists.end());
        vector<ListNode*> harf_lists;
        for (int i = 0; i < len - 1; i += 2) {
            ListNode * m = merge2Lists(lists[i], lists[i+1]);
            harf_lists.push_back(m);
        }
        if (len % 2 == 1) harf_lists.push_back(lists[len-1]);
        return mergeKLists(harf_lists);
    }
};
