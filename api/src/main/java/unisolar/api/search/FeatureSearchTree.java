package unisolar.api.search;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class FeatureSearchTree {
    private Node root;

    // Classe para representar uma funcionalidade do sistema
    public static class Feature implements Comparable<Feature> {
        private final String name;
        private final String path;
        private final String description;
        private final String category;

        public Feature(String name, String path, String description, String category) {
            this.name = name;
            this.path = path;
            this.description = description;
            this.category = category;
        }

        @Override
        public int compareTo(Feature other) {
            return this.name.compareToIgnoreCase(other.name);
        }

        public String getName() { return name; }
        public String getPath() { return path; }
        public String getDescription() { return description; }
        public String getCategory() { return category; }

        @Override
        public String toString() {
            return String.format("📍 %s\n   %s\n   Categoria: %s\n   Caminho: %s",
                    name, description, category, path);
        }
    }

    // Nó da árvore AVL
    private class Node {
        Feature feature;
        Node left, right;
        int height;

        Node(Feature feature) {
            this.feature = feature;
            this.height = 1;
        }
    }

    // Obter altura do nó
    private int height(Node node) {
        return node == null ? 0 : node.height;
    }

    // Obter fator de balanceamento
    private int getBalance(Node node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    // Atualizar altura do nó
    private void updateHeight(Node node) {
        if (node != null) {
            node.height = Math.max(height(node.left), height(node.right)) + 1;
        }
    }

    // Rotação à direita
    private Node rightRotate(Node y) {
        Node x = y.left;
        Node T2 = x.right;

        x.right = y;
        y.left = T2;

        updateHeight(y);
        updateHeight(x);

        return x;
    }

    // Rotação à esquerda
    private Node leftRotate(Node x) {
        Node y = x.right;
        Node T2 = y.left;

        y.left = x;
        x.right = T2;

        updateHeight(x);
        updateHeight(y);

        return y;
    }

    // Inserir funcionalidade - O(log n)
    public void insert(Feature feature) {
        root = insertRec(root, feature);
    }

    private Node insertRec(Node node, Feature feature) {
        // Inserção BST padrão
        if (node == null) {
            return new Node(feature);
        }

        if (feature.compareTo(node.feature) < 0) {
            node.left = insertRec(node.left, feature);
        } else if (feature.compareTo(node.feature) > 0) {
            node.right = insertRec(node.right, feature);
        } else {
            return node; // Duplicata não permitida
        }

        // Atualizar altura
        updateHeight(node);

        // Balancear árvore
        int balance = getBalance(node);

        // Casos de balanceamento
        if (balance > 1) {
            if (feature.compareTo(node.left.feature) < 0) {
                return rightRotate(node);
            } else {
                node.left = leftRotate(node.left);
                return rightRotate(node);
            }
        }

        if (balance < -1) {
            if (feature.compareTo(node.right.feature) > 0) {
                return leftRotate(node);
            } else {
                node.right = rightRotate(node.right);
                return leftRotate(node);
            }
        }

        return node;
    }

    // Buscar funcionalidade - O(log n)
    public Feature search(String name) {
        Node result = searchRec(root, name);
        return result != null ? result.feature : null;
    }

    private Node searchRec(Node node, String name) {
        if (node == null || node.feature.getName().equalsIgnoreCase(name)) {
            return node;
        }

        if (name.compareToIgnoreCase(node.feature.getName()) < 0) {
            return searchRec(node.left, name);
        }

        return searchRec(node.right, name);
    }

    // Busca por prefixo - O(log n + k), onde k é o número de resultados
    public List<Feature> searchByPrefix(String prefix) {
        List<Feature> results = new ArrayList<>();
        searchByPrefixRec(root, prefix.toLowerCase(), results);
        return results;
    }

    private void searchByPrefixRec(Node node, String prefix, List<Feature> results) {
        if (node == null) {
            return;
        }

        String nodeName = node.feature.getName().toLowerCase();
        if (nodeName.startsWith(prefix)) {
            results.add(node.feature);
        }

        if (prefix.compareTo(nodeName) < 0) {
            searchByPrefixRec(node.left, prefix, results);
        }
        if (prefix.compareTo(nodeName) > 0 || nodeName.startsWith(prefix)) {
            searchByPrefixRec(node.right, prefix, results);
        }
    }
}