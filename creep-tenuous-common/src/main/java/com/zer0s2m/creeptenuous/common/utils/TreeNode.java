package com.zer0s2m.creeptenuous.common.utils;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class TreeNode<T> implements Iterable<TreeNode<T>> {
    private T data;

    private TreeNode<T> parent;

    private final List<TreeNode<T>> children;

    private final HashMap<String, String> meta = new HashMap<>();

    private final List<TreeNode<T>> elementsIndex;

    public TreeNode(T data) {
        this.data = data;
        this.children = new ArrayList<>();
        this.elementsIndex = new ArrayList<>();
        this.elementsIndex.add(this);
    }

    public TreeNode() {
        this.children = new ArrayList<>();
        this.elementsIndex = new ArrayList<>();
        this.elementsIndex.add(this);
    }

    public boolean isRoot() {
        return parent == null;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public TreeNode<T> addChild(T child) {
        TreeNode<T> childNode = new TreeNode<>(child);
        childNode.parent = this;
        this.children.add(childNode);
        this.registerChildForSearch(childNode);
        return childNode;
    }

    public int getLevel() {
        if (this.isRoot())
            return 0;
        else
            return parent.getLevel() + 1;
    }

    private void registerChildForSearch(TreeNode<T> node) {
        elementsIndex.add(node);
        if (parent != null) {
            parent.registerChildForSearch(node);
        }
    }

    public Optional<TreeNode<T>> findTreeNode(Comparable<T> cmp) {
        for (TreeNode<T> element : this.elementsIndex) {
            T elData = element.data;
            if (cmp.compareTo(elData) == 0) {
                return Optional.of(element);
            }
        }

        return Optional.empty();
    }

    @Override
    public String toString() {
        return data != null ? data.toString() : null;
    }

    @Override
    public @NotNull Iterator<TreeNode<T>> iterator() {
        return new TreeNodeIter<>(this);
    }

    public String putMeta(String key, String value) {
        return meta.put(key, value);
    }

    public List<String> putMeta(List<String> keys, List<String> values) {
        assert keys.size() == values.size();
        List<String> valuesFromMeta = new ArrayList<>();
        for (int i = 0; i < keys.size(); i++) {
            valuesFromMeta.add(putMeta(keys.get(i), values.get(i)));
        }
        return valuesFromMeta;
    }

    public HashMap<String, String> getMeta() {
        return meta;
    }

    public String getMetaValue(String value) {
        return meta.get(value);
    }

    public List<TreeNode<T>> getChildren() {
        return children;
    }

    public Optional<TreeNode<T>> findChildren(Comparable<T> cmp) {
        for (TreeNode<T> element : this.children) {
            T elData = element.data;
            if (cmp.compareTo(elData) == 0) {
                return Optional.of(element);
            }
        }

        return Optional.empty();
    }

}