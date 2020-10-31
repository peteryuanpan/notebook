/**
 * Definition for singly-linked list.
 * type ListNode struct {
 *     Val int
 *     Next *ListNode
 * }
 */
func merge2Lists(a *ListNode, b *ListNode) *ListNode {
    var c *ListNode = new(ListNode);
    var d *ListNode = c;
    for {
        if a == nil && b == nil {
            break
        }
        if b == nil || (a != nil && a.Val < b.Val) {
            c.Next = a
            c = c.Next
            a = a.Next
        } else {
            c.Next = b
            c = c.Next
            b = b.Next
        }
    }
    return d.Next;
}
func mergeKLists(lists []*ListNode) *ListNode {
    if len(lists) == 0 {
        return nil
    }
    if len(lists) == 1 {
        return lists[0]
    }
    var i int
    var merges []*ListNode
    for i = 0; i < len(lists); i += 2 {
        if i + 1 == len(lists) {
            merges = append(merges, lists[i])
        } else {
            var c *ListNode = merge2Lists(lists[i], lists[i+1])
            merges = append(merges, c)
        }
    }
    return mergeKLists(merges)
}
