package unisolar.api.service;

import org.springframework.stereotype.Service;
import unisolar.api.search.FeatureSearchTree;

import java.util.ArrayList;
import java.util.List;

/**
 * FeatureSearchService is a service responsible for initializing and searching system features within a FeatureSearchTree.
 * The service stores various features of the system and allows for querying them by their names or paths.
 */
@Service
public class FeatureSearchService {
    private final FeatureSearchTree searchTree;

    /**
     * Constructor for FeatureSearchService, initializing the FeatureSearchTree and inserting the system's features.
     */
    public FeatureSearchService() {
        this.searchTree = new FeatureSearchTree();
        initializeFeatures();
    }

    /**
     * Initializes system features by inserting predefined features into the FeatureSearchTree.
     */
    private void initializeFeatures() {
        // Dashboard features
        searchTree.insert(new FeatureSearchTree.Feature(
                "status_sistema",
                "Dashboard > Status do Sistema",
                "Visualizar status atual do sistema solar",
                "Monitoramento"
        ));

        searchTree.insert(new FeatureSearchTree.Feature(
                "economia",
                "Dashboard > Economia",
                "Visualizar economia e dados financeiros",
                "Financeiro"
        ));

        searchTree.insert(new FeatureSearchTree.Feature(
                "previsao",
                "Dashboard > Previsão",
                "Ver previsões de geração de energia",
                "Análise"
        ));

        // Profile features
        searchTree.insert(new FeatureSearchTree.Feature(
                "alterar_perfil",
                "Perfil > Atualizar Perfil",
                "Atualizar informações do perfil",
                "Usuário"
        ));

        searchTree.insert(new FeatureSearchTree.Feature(
                "alterar_senha",
                "Perfil > Alterar Senha",
                "Modificar senha de acesso",
                "Segurança"
        ));

        // Delete profile feature
        searchTree.insert(new FeatureSearchTree.Feature(
                "deletar_perfil",
                "Perfil > Deletar Perfil",
                "Deletar perfil",
                "Usuário"
        ));

        // Other features
        searchTree.insert(new FeatureSearchTree.Feature(
                "chat_ia",
                "Chat com SolarIA",
                "Conversar com assistente virtual",
                "Suporte"
        ));
    }

    /**
     * Searches for features in the FeatureSearchTree based on the provided query.
     * It returns a list of features whose names or paths start with the given query (prefix search).
     *
     * @param query The query string used for searching features.
     * @return A list of features matching the query prefix, or an empty list if no matches are found or the query is empty.
     */
    public List<FeatureSearchTree.Feature> searchFeatures(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return searchTree.searchByPrefix(query.trim().toLowerCase());
    }
}
