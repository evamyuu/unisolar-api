package unisolar.api.search;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * FeatureSearchTree is a component that represents an AVL tree to manage and search system features.
 * The tree allows efficient insertion, searching, and prefix-based search operations on features.
 */
@Component
public class FeatureSearchTree {
    private Node root;  // Root of the AVL tree

    /**
     * The Feature class represents a system feature with relevant details such as name, path, description, and category.
     * This class implements Comparable to allow sorting and comparing features by name.
     */
    public static class Feature implements Comparable<Feature> {
        private final String name;        // Name of the feature
        private final String path;        // Path to the feature in the system
        private final String description; // Description of the feature
        private final String category;    // Category to which the feature belongs

        /**
         * Constructor for creating a Feature instance.
         *
         * @param name        The name of the feature.
         * @param path        The path to the feature.
         * @param description The description of the feature.
         * @param category    The category of the feature.
         */
        public Feature(String name, String path, String description, String category) {
            this.name = name;
            this.path = path;
            this.description = description;
            this.category = category;
        }

        @Override
        public int compareTo(Feature other) {
            return this.name.compareToIgnoreCase(other.name);  // Compare features by name
        }

        public String getName() { return name; }              // Getter for feature name
        public String getPath() { return path; }              // Getter for feature path
        public String getDescription() { return description; } // Getter for feature description
        public String getCategory() { return category; }      // Getter for feature category

        @Override
        public String toString() {
            // Return a string representation of the feature
            return String.format("📍 %s\n   %s\n   Category: %s\n   Path: %s",
                    name, description, category, path);
        }
    }

    /**
     * Node class represents a node in the AVL tree that stores a Feature and its left and right children.
     */
    private class Node {
        Feature feature;  // Feature stored in this node
        Node left, right; // Left and right children
        int height;       // Height of the node (used for balancing)

        Node(Feature feature) {
            this.feature = feature;
            this.height = 1;  // Initial height is 1
        }
    }

    /**
     * Returns the height of a node.
     *
     * @param node The node whose height is to be returned.
     * @return The height of the node.
     */
    private int height(Node node) {
        return node == null ? 0 : node.height;
    }

    /**
     * Returns the balance factor of a node, which is the difference in height between its left and right children.
     *
     * @param node The node whose balance factor is to be calculated.
     * @return The balance factor of the node.
     */
    private int getBalance(Node node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    /**
     * Updates the height of a node based on the heights of its children.
     *
     * @param node The node whose height is to be updated.
     */
    private void updateHeight(Node node) {
        if (node != null) {
            node.height = Math.max(height(node.left), height(node.right)) + 1;
        }
    }

    /**
     * Performs a right rotation on a node to balance the tree.
     *
     * @param y The node to rotate.
     * @return The new root of the rotated subtree.
     */
    private Node rightRotate(Node y) {
        Node x = y.left;
        Node T2 = x.right;

        x.right = y;
        y.left = T2;

        updateHeight(y);
        updateHeight(x);

        return x;
    }

    /**
     * Performs a left rotation on a node to balance the tree.
     *
     * @param x The node to rotate.
     * @return The new root of the rotated subtree.
     */
    private Node leftRotate(Node x) {
        Node y = x.right;
        Node T2 = y.left;

        y.left = x;
        x.right = T2;

        updateHeight(x);
        updateHeight(y);

        return y;
    }

    /**
     * Inserts a new feature into the AVL tree. This operation is performed in O(log n) time.
     *
     * @param feature The feature to insert.
     */
    public void insert(Feature feature) {
        root = insertRec(root, feature);
    }

    /**
     * Helper method to recursively insert a feature into the AVL tree.
     *
     * @param node The current node.
     * @param feature The feature to insert.
     * @return The updated node after insertion.
     */
    private Node insertRec(Node node, Feature feature) {
        // Standard BST insertion
        if (node == null) {
            return new Node(feature);  // Create a new node for the feature
        }

        if (feature.compareTo(node.feature) < 0) {
            node.left = insertRec(node.left, feature);  // Insert in the left subtree
        } else if (feature.compareTo(node.feature) > 0) {
            node.right = insertRec(node.right, feature); // Insert in the right subtree
        } else {
            return node;  // Duplicate feature, do not insert
        }

        // Update height of the current node
        updateHeight(node);

        // Balance the tree
        int balance = getBalance(node);

        // Balance the node if needed
        if (balance > 1) {
            if (feature.compareTo(node.left.feature) < 0) {
                return rightRotate(node);  // Right rotation
            } else {
                node.left = leftRotate(node.left);
                return rightRotate(node);  // Left-Right rotation
            }
        }

        if (balance < -1) {
            if (feature.compareTo(node.right.feature) > 0) {
                return leftRotate(node);  // Left rotation
            } else {
                node.right = rightRotate(node.right);
                return leftRotate(node);  // Right-Left rotation
            }
        }

        return node;
    }

    /**
     * Searches for a feature by its name. This operation is performed in O(log n) time.
     *
     * @param name The name of the feature to search for.
     * @return The feature if found, or null if not found.
     */
    public Feature search(String name) {
        Node result = searchRec(root, name);
        return result != null ? result.feature : null;
    }

    /**
     * Helper method to recursively search for a feature by name.
     *
     * @param node The current node.
     * @param name The name of the feature to search for.
     * @return The node containing the feature, or null if not found.
     */
    private Node searchRec(Node node, String name) {
        if (node == null || node.feature.getName().equalsIgnoreCase(name)) {
            return node;
        }

        if (name.compareToIgnoreCase(node.feature.getName()) < 0) {
            return searchRec(node.left, name);  // Search in the left subtree
        }

        return searchRec(node.right, name);  // Search in the right subtree
    }

    /**
     * Searches for features whose names start with the given prefix. This operation is performed in O(log n + k),
     * where k is the number of results.
     *
     * @param prefix The prefix to search for.
     * @return A list of features whose names start with the given prefix.
     */
    public List<Feature> searchByPrefix(String prefix) {
        List<Feature> results = new ArrayList<>();
        searchByPrefixRec(root, prefix.toLowerCase(), results);
        return results;
    }

    /**
     * Helper method to recursively search for features by prefix.
     *
     * @param node The current node.
     * @param prefix The prefix to search for.
     * @param results The list of matching features.
     */
    private void searchByPrefixRec(Node node, String prefix, List<Feature> results) {
        if (node == null) {
            return;
        }

        String nodeName = node.feature.getName().toLowerCase();
        if (nodeName.startsWith(prefix)) {
            results.add(node.feature);  // Add the feature to the results if it starts with the prefix
        }

        if (prefix.compareTo(nodeName) < 0) {
            searchByPrefixRec(node.left, prefix, results);  // Search in the left subtree
        }
        if (prefix.compareTo(nodeName) > 0 || nodeName.startsWith(prefix)) {
            searchByPrefixRec(node.right, prefix, results);  // Search in the right subtree
        }
    }
}
