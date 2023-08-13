package com.zer0s2m.creeptenuous.common.utils;

import java.util.Iterator;

public class TreeNodeIter <T> implements Iterator<TreeNode<T>> {
    enum ProcessStages {
        PROCESS_PARENT,
        PROCESS_CHILD_CUR_NODE,
        PROCESS_CHILD_SUB_NODE
    }

    private final TreeNode<T> treeNode;

    private ProcessStages doNext;

    private TreeNode<T> next;

    private final Iterator<TreeNode<T>> childrenCurNodeIter;

    private Iterator<TreeNode<T>> childrenSubNodeIter;

    public TreeNodeIter(TreeNode<T> treeNode) {
        this.treeNode = treeNode;
        this.doNext = ProcessStages.PROCESS_PARENT;
        this.childrenCurNodeIter = treeNode.getChildren().iterator();
    }

    @Override
    public boolean hasNext() {

        if (this.doNext == ProcessStages.PROCESS_PARENT) {
            this.next = this.treeNode;
            this.doNext = ProcessStages.PROCESS_CHILD_CUR_NODE;
            return true;
        }

        if (this.doNext == ProcessStages.PROCESS_CHILD_CUR_NODE) {
            if (childrenCurNodeIter.hasNext()) {
                TreeNode<T> childDirect = childrenCurNodeIter.next();
                childrenSubNodeIter = childDirect.iterator();
                this.doNext = ProcessStages.PROCESS_CHILD_SUB_NODE;
                return hasNext();
            }

            else {
                this.doNext = null;
                return false;
            }
        }

        if (this.doNext == ProcessStages.PROCESS_CHILD_SUB_NODE) {
            if (childrenSubNodeIter.hasNext()) {
                this.next = childrenSubNodeIter.next();
                return true;
            }
            else {
                this.next = null;
                this.doNext = ProcessStages.PROCESS_CHILD_CUR_NODE;
                return hasNext();
            }
        }

        return false;
    }

    @Override
    public TreeNode<T> next() {
        return this.next;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
