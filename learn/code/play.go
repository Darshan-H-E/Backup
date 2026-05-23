package main



type TreeNode struct {
	Val int
	Left *TreeNode
	Right *TreeNode
}

func recoverTree(root *TreeNode) {
	var first, second, prev *TreeNode

	var inorder func(node *TreeNode)
	inorder = func (node *TreeNode) {
		if node == nil {
			return
		}

		inorder(node.Left)

		if prev != nil && prev.Val > node.Val {
			if first == nil {
				first = prev
			}
			second = node
		}
		prev = node
		inorder(node.Right)
	}
	inorder(root)

	if first != nil && second != nil {
	}
}


func main() {
}
















































































































