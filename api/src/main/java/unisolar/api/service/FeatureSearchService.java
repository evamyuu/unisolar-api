package unisolar.api.service;

import org.springframework.stereotype.Service;
import unisolar.api.search.FeatureSearchTree;

import java.util.ArrayList;
import java.util.List;

@Service
public class FeatureSearchService {
    private final FeatureSearchTree searchTree;

    public FeatureSearchService() {
        this.searchTree = new FeatureSearchTree();
        initializeFeatures();
    }

    private void initializeFeatures() {
        // Dashboard
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

        // Perfil
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

        // Perfil
        searchTree.insert(new FeatureSearchTree.Feature(
                "deletar_perfil",
                "Perfil > Deletar Perfil",
                "Deletar perfil",
                "Usuário"
        ));

        // Outros
        searchTree.insert(new FeatureSearchTree.Feature(
                "chat_ia",
                "Chat com SolarIA",
                "Conversar com assistente virtual",
                "Suporte"
        ));
    }

    public List<FeatureSearchTree.Feature> searchFeatures(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return searchTree.searchByPrefix(query.trim().toLowerCase());
    }
}