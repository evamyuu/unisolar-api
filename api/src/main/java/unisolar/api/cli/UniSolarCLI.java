package unisolar.api.cli;

import org.springframework.boot.CommandLineRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import unisolar.api.controller.UserController;
import unisolar.api.domain.dto.userDTO.*;
import unisolar.api.domain.entity.*;
import unisolar.api.search.FeatureSearchTree;
import unisolar.api.service.ChatbotService;
import unisolar.api.service.FeatureSearchService;
import unisolar.api.service.MaintenanceService;

import java.text.DecimalFormat;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class UniSolarCLI implements CommandLineRunner {
    private final Scanner scanner;
    private final AuthenticationManager authenticationManager;
    private final UserController userController;
    private final ChatbotService chatbotService;
    private Authentication currentAuthentication;
    private MaintenanceService maintenanceService;

    private FeatureSearchService featureSearchService;

    private final DecimalFormat df = new DecimalFormat("#,##0.00");
    private static Map<String, String> respostas;

    public UniSolarCLI(AuthenticationManager authenticationManager,
                       UserController userController,
                       ChatbotService chatbotService,
                       FeatureSearchService featureSearchService) {
        this.scanner = new Scanner(System.in);
        this.authenticationManager = authenticationManager;
        this.userController = userController;
        this.chatbotService = chatbotService;
        this.featureSearchService = featureSearchService;
    }

    @Override
    public void run(String... args) {
        boolean running = true;
        while (running) {
            if (currentAuthentication == null) {
                showLoginMenu();
            } else {
                running = showMainMenu();
            }
        }
        scanner.close();
        System.out.println("\n=====================================");
        System.out.println("    Obrigado por usar o UniSolar!    ");
        System.out.println("          Até logo! 👋               ");
        System.out.println("=====================================");
    }

    private void showLoginMenu() {
        System.out.println("\n=========== UniSolar 🌞 =============");
        System.out.println("      Energia que transforma vidas      ");
        System.out.println("=====================================");

        int choice = -1;
        while (choice != 1 && choice != 2) {
            System.out.println("1. Login 🔑");
            System.out.println("2. Cadastro 📝");
            System.out.print("Escolha uma opção: ");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // consume newline
            } else {
                scanner.nextLine(); // consume invalid input
                choice = -1; // invalid choice
            }

            if (choice == 1) {
                doLogin();
            } else if (choice == 2) {
                doRegister();
            } else {
                System.out.println("\n[Erro ❌] Opção inválida! Tente novamente.");
            }

        }
    }


    private boolean showMainMenu() {
        System.out.println("\n=========== Menu Principal ===========");
        System.out.println("1. Buscar 🔎");
        System.out.println("2. Dashboard 📊");
        System.out.println("3. Perfil do Usuário 👤");
        System.out.println("4. Chat com SolarIA 🤖");
        System.out.println("5. Alterar Senha 🔒");
        System.out.println("6. Logout 🚶‍♂️");
        System.out.print("Escolha uma opção: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        switch (choice) {
            case 1:
                showFeatureSearch();
                return true;
            case 2:
                showDashboard();
                return true;
            case 3:
                showUserProfile();
                return true;
            case 4:
                startChat();
                return true;
            case 5:
                changePassword();
                return true;
            case 6:
                currentAuthentication = null;
                System.out.println("\n[Sucesso ✅] Logout realizado com sucesso!");
                return false; // Termina o loop para forçar logout
            default:
                System.out.println("\n[Erro ❌] Opção inválida! Tente novamente.");
                return true;
        }
    }

    private void doLogin() {
        System.out.println("\n=========== Login 🔑 ===========");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Senha: ");
        String password = scanner.nextLine();

        try {
            var authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
            currentAuthentication = authenticationManager.authenticate(authenticationToken);
            System.out.println("\n[Sucesso ✅] Login realizado com sucesso!");
        } catch (Exception e) {
            System.out.println("\n[Erro ❌] Credenciais inválidas. Tente novamente.");
        }
    }

    private void doRegister() {
        System.out.println("\n=========== Cadastro 📝 ===========");
        System.out.print("Nome: ");
        String name = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Senha: ");
        String password = scanner.nextLine();

        try {
            UserCreateDTO newUser = new UserCreateDTO(username, password, name, email);
            ResponseEntity<UserDetailDTO> response = userController.createUser(newUser, UriComponentsBuilder.newInstance());
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("\n[Sucesso ✅] Cadastro realizado com sucesso! Por favor, faça login.");
            }
        } catch (Exception e) {
            System.out.println("\n[Erro ❌] Ocorreu um erro no cadastro: " + e.getMessage());
        }
    }

    private void showDashboard() {

        System.out.println("\n=========== Dashboard 📊 ===========");

        ResponseEntity<UserDetailDTO> userResponse = userController.getCurrentUser(currentAuthentication);
        if (userResponse.getStatusCode().is2xxSuccessful() && userResponse.getBody() != null) {
            UserDetailDTO user = userResponse.getBody();
            System.out.println("👋 Olá, " + user.name() + "!");

            // Buscar detalhes da instalação do usuário
            Installation installation = getInstallationDetails(user.id());

            if (installation != null) {
                int option;
                do {
                    System.out.println("\nMenu Principal");
                    System.out.println("═".repeat(45));
                    System.out.println("1️ - Status do Sistema 📡");
                    System.out.println("2️ - Economia 💰");
                    System.out.println("3️ - Previsão 🔮");
                    System.out.println("4️ - Manutenção 🛠️");
                    System.out.println("5️ - O Que a SolarIA Planejou para Você Hoje 🤖");
                    System.out.println("6️ - Dicas para Economia de Energia 🌱");
                    System.out.println("7️ - Voltar 🔙");
                    System.out.print("\nEscolha uma opção (1-7): ");

                    while (!scanner.hasNextInt()) {
                        System.out.println("⚠️  Entrada inválida. Por favor, insira um número entre 1 e 7.");
                        System.out.print("Escolha uma opção (1-7): ");
                        scanner.next();
                    }
                    option = scanner.nextInt();
                    scanner.nextLine();

                    System.out.println("\n" + "═".repeat(45));

                    switch (option) {
                        case 1 -> {
                            mostrarStatus(installation);
                        }
                        case 2 -> {
                            mostrarEconomia(installation);
                        }
                        case 3 -> {
                            mostrarPrevisaoEnergia(installation);
                        }
                        case 4 -> {
                            mostrarManutencao(installation);
                        }
                        case 5 -> {
                            mostrarDecisoesDaIA(installation);
                        }
                        case 6 -> {
                            mostrarDicas();
                        }
                        case 7 -> System.out.println("🔙 Voltando ao menu principal...");
                        default -> System.out.println("⚠️  Opção inválida. Tente novamente.");
                    }

                    if (option != 7) {
                        System.out.println("\nPressione ENTER para retornar ao menu...");
                        scanner.nextLine();
                    }

                } while (option != 7);
            } else {
                System.out.println("⚠️  Nenhuma instalação encontrada. Verifique suas configurações.");
            }
        } else {
            System.out.println("⚠️  Não foi possível recuperar os detalhes do usuário.");
        }

        System.out.println("\n✅ Saindo do Dashboard. Até logo!\n");
    }

    private void showFeatureSearch() {
        System.out.println("\n=========== Busca de Funcionalidades 🔍 ===========");
        System.out.println("Digite 'voltar' para retornar ao menu principal");

        while (true) {
            System.out.print("\nBuscar funcionalidade: ");
            String query = scanner.nextLine().trim();

            if (query.equalsIgnoreCase("voltar")) {
                break;
            }

            if (query.length() < 2) {
                System.out.println("\n⚠️  Digite pelo menos 2 caracteres para buscar");
                continue;
            }

            List<FeatureSearchTree.Feature> results = featureSearchService.searchFeatures(query);

            if (results.isEmpty()) {
                System.out.println("\n❌ Nenhuma funcionalidade encontrada para '" + query + "'");
                continue;
            }

            System.out.println("\n=== Funcionalidades Encontradas ===");
            results.forEach(feature -> {
                System.out.println("\n" + feature);
            });
        }
    }

    private void mostrarDecisoesDaIA(Installation installation) {
        if (installation != null) {
            System.out.println("\n=== O Que a SolarIA planejou para você hoje? ===");

            double solarGenerationMorning = 100.0 + (Math.random() * 50.0);  // Geração solar maior pela manhã
            double batteryChargeMorning = 100.0;  // A bateria carrega para 100% durante o dia
            double batteryUsageMorning = solarGenerationMorning * 0.5;  // Usa 50% da geração solar para carregar a bateria
            double solarGenerationAfternoon = 50.0 + (Math.random() * 30.0);  // Geração solar menor devido às nuvens
            double batteryConsumptionNight = 20.0 + (Math.random() * 10.0);  // A bateria é utilizada para carregar os aparelhos essenciais
            double batteryDischargeAmount = 0.2 + (Math.random() * 0.3) * 100;  // Descarregar a bateria durante horários de pico (tarifa mais alta)

            System.out.println("\n🌅 Manhã Ensolarada:");
            System.out.println("Geração Solar: " + df.format(solarGenerationMorning) + " kWh");
            System.out.println("Bateria Carregada: " + df.format(batteryUsageMorning) + " kWh");
            if (batteryChargeMorning > 90) {
                System.out.println("💡 Decisão da IA: Usar energia solar ao máximo pela manhã.");
            } else {
                System.out.println("💡 Decisão da IA: Priorizar o uso da bateria para garantir energia à noite.");
            }

            System.out.println("\n🌥️ Tarde Nublada:");
            System.out.println("Geração Solar: " + df.format(solarGenerationAfternoon) + " kWh");
            if (solarGenerationAfternoon < 60) {
                System.out.println("💡 Decisão da IA: Usar energia solar sem utilizar a bateria.");
            } else {
                System.out.println("💡 Decisão da IA: Reduzir o uso da bateria para preservar energia para a noite.");
            }

            System.out.println("\n🌙 Noite:");
            System.out.println("Consumo da Bateria: " + df.format(batteryConsumptionNight) + " kWh 🔋");
            if (batteryConsumptionNight > 20) {
                System.out.println("💡 Decisão da IA: Aumentar a utilização da bateria para garantir energia durante a noite.");
            } else {
                System.out.println("💡 Decisão da IA: Otimizar o consumo de energia para evitar descarregar a bateria demais.");
            }

            System.out.println("\n🌞 Dia Seguinte com Tarifas Mais Altas:");
            System.out.println("Descarregamento da Bateria: " + df.format(batteryDischargeAmount) + "%");
            double savingsFromBatteryDischarge = batteryDischargeAmount * 0.75;  // Suposição de economia de 75%
            System.out.println("💡 Decisão da IA: Descarregar mais bateria durante horários de pico para economizar com tarifas altas.");
            System.out.println("Economia Estimada: R$ " + df.format(savingsFromBatteryDischarge) + " 💰");
        }
    }

    private Installation getInstallationDetails(Long userId) {

        ResponseEntity<Installation> installationResponse = userController.getUserInstallation(userId);
        if (installationResponse.getStatusCode().is2xxSuccessful() && installationResponse.getBody() != null) {
            return installationResponse.getBody();
        } else {
            System.out.println("\n[Erro ❌] Não foi possível carregar os dados de instalação.");
            return null;
        }
    }

    private void mostrarStatus(Installation installation) {
        if (installation != null) {
            System.out.println("\n=== Status do Sistema ===");

            boolean usandoEnergiaSolar = installation.getSolarPanels().size() > 0; // Verifica se há painéis solares
            System.out.println("\nFonte de Energia Atual: " + (usandoEnergiaSolar ? "Solar ☀️" : "Rede Elétrica ⚡"));

            Battery battery = installation.getBattery();
            int batteryCharge = battery != null ? (int) battery.getCurrentCharge() : 0;
            String statusBateria = batteryCharge > 50 ? "Carregada 👍" : "Baixa ⚠️";
            String batteryStatus = battery != null ? battery.getHealth() : "Desconhecido ❓";

            System.out.println("\nBateria:");
            System.out.println("Nível: " + batteryCharge + "% " + gerarBarraProgresso(batteryCharge));
            System.out.println("Status: " + statusBateria + " (" + batteryStatus + ")");

            List<SolarPanel> panels = installation.getSolarPanels();
            String statusPaineis = panels.isEmpty() ? "Sem Painéis 🚫" : "Operacional 🌞";
            System.out.println("\nPainéis Solares:");
            for (SolarPanel panel : panels) {
                String panelStatus = panel.getStatus();
                System.out.println("Painel Solar " + panel.getId() + ": " + panelStatus);
            }
            System.out.println("Status: " + statusPaineis);
        }
    }

    private void mostrarEconomia(Installation installation) {
        if (installation != null) {
            System.out.println("\n=== Economia ===");

            double totalSolarConsumption = Math.random() * 50 + 20;  // Consumo solar entre 20 e 70 kWh
            double totalGridConsumption = Math.random() * 50 + 10;   // Consumo da rede entre 10 e 60 kWh
            double totalBatteryConsumption = Math.random() * 30 + 5;  // Consumo de bateria entre 5 e 35 kWh

            double totalConsumption = totalSolarConsumption + totalGridConsumption + totalBatteryConsumption;
            double economiaHoje = totalSolarConsumption * 0.25; // Exemplo de economia (ajuste conforme necessário)
            double economiaMes = totalSolarConsumption * 7.5;   // Exemplo de economia mensal (ajuste conforme necessário)
            double projecaoAnual = economiaMes * 12;             // Projeção anual

            System.out.println("Consumo Total: " + df.format(totalConsumption) + " kWh");
            System.out.println("Consumo Solar: " + df.format(totalSolarConsumption) + " kWh 🌞");
            System.out.println("Consumo da Rede: " + df.format(totalGridConsumption) + " kWh ⚡");
            System.out.println("Consumo da Bateria: " + df.format(totalBatteryConsumption) + " kWh 🔋");

            System.out.println("\nEconomia Hoje: R$ " + df.format(economiaHoje) + " 💰");
            System.out.println("Economia do Mês: R$ " + df.format(economiaMes) + " 💵");
            System.out.println("Projeção Anual: R$ " + df.format(projecaoAnual) + " 📊");
        }
    }

    private void showUserProfile() {
        ResponseEntity<UserDetailDTO> response = userController.getCurrentUser(currentAuthentication);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            UserDetailDTO user = response.getBody();
            System.out.println("\n=========== Perfil do Usuário 👤 ===========");
            System.out.println("Nome: " + user.name());
            System.out.println("Email: " + user.email());
            System.out.println("Username: " + user.username());

            System.out.println("\n1. Atualizar perfil ✏️");
            System.out.println("2. Deletar perfil 🗑️");
            System.out.println("3. Voltar 🔙");
            System.out.print("Escolha uma opção: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    updateProfile(user);
                    break;
                case 2:
                    deleteProfile(user);
                    break;
                case 3:
                    return;
                default:
                    System.out.println("\n[Erro ❌] Opção inválida! Tente novamente.");
            }
        }
    }

    private void updateProfile(UserDetailDTO currentUser) {
        System.out.println("\n=========== Atualizar Perfil ✏️ ===========");
        System.out.println("Deixe em branco para manter o valor atual");

        System.out.print("Novo nome (" + currentUser.name() + "): ");
        String name = scanner.nextLine();

        System.out.print("Novo email (" + currentUser.email() + "): ");
        String email = scanner.nextLine();

        System.out.print("Novo username (" + currentUser.username() + "): ");
        String username = scanner.nextLine();

        try {
            UserUpdateDTO updateData = new UserUpdateDTO(
                    currentUser.id(),
                    username.isEmpty() ? null : username,
                    name.isEmpty() ? null : name,
                    email.isEmpty() ? null : email
            );
            ResponseEntity response = userController.updateUser(updateData);
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("\n[Sucesso ✅] Perfil atualizado com sucesso!");
            }
        } catch (Exception e) {
            System.out.println("\n[Erro ❌] Ocorreu um erro ao atualizar perfil: " + e.getMessage());
        }
    }

    private void changePassword() {
        ResponseEntity<UserDetailDTO> response = userController.getCurrentUser(currentAuthentication);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            System.out.print("\nSenha atual: ");
            String oldPassword = scanner.nextLine();
            System.out.print("Nova senha: ");
            String newPassword = scanner.nextLine();

            try {
                ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO(oldPassword, newPassword);
                ResponseEntity<String> changeResponse = userController.changePassword(response.getBody().id(), changePasswordDTO);
                if (changeResponse.getStatusCode().is2xxSuccessful()) {
                    System.out.println("\n[Sucesso ✅] Senha alterada com sucesso!");
                    currentAuthentication = null; // Force re-login
                    System.out.println("Por favor, faça login novamente com sua nova senha.");
                } else {
                    System.out.println("\n[Erro ❌] Não foi possível alterar a senha: " + changeResponse.getBody());
                }
            } catch (Exception e) {
                System.out.println("\n[Erro ❌] Ocorreu um erro ao alterar senha: " + e.getMessage());
            }
        }
    }

    private void deleteProfile(UserDetailDTO currentUser) {
        System.out.println("\n=========== Deletar Perfil 🗑️ ===========");
        System.out.print("Tem certeza que deseja deletar seu perfil? (s/n): ");
        String confirmation = scanner.nextLine();

        if (confirmation.equalsIgnoreCase("s")) {
            try {
                ResponseEntity<String> response = userController.deactivateUser(currentUser.id());
                if (response.getStatusCode().is2xxSuccessful()) {
                    System.out.println("\n[Sucesso ✅] Perfil deletado com sucesso. Você será desconectado.");
                    currentAuthentication = null; // Forçar logout após exclusão do perfil
                } else {
                    System.out.println("\n[Erro ❌] Não foi possível deletar o perfil: " + response.getBody());
                }
            } catch (Exception e) {
                System.out.println("\n[Erro ❌] Ocorreu um erro ao deletar perfil: " + e.getMessage());
            }
        } else {
            System.out.println("\n[Info ℹ️] Ação cancelada pelo usuário.");
        }
    }

    private String gerarBarraProgresso(int porcentagem) {
        int barSize = 20;
        int preenchido = (int) ((porcentagem / 100.0) * barSize);
        StringBuilder barra = new StringBuilder("[");

        for (int i = 0; i < barSize; i++) {
            barra.append(i < preenchido ? "█" : "░");
        }
        barra.append("]");
        return barra.toString();
    }

    private void mostrarManutencao(Installation installation) {
        if (installation != null) {
            System.out.println("\n=== Manutenção do Sistema ===");

            Random rand = new Random();

            for (SolarPanel panel : installation.getSolarPanels()) {
                int panelAge = rand.nextInt(10) + 1;  // Idade do painel (em anos)
                double efficiency = rand.nextDouble() * 100;  // Eficiência atual do painel
                boolean needMaintenance = efficiency < 85 || panelAge > 5 || rand.nextDouble() < 0.2;  // Condições de manutenção

                System.out.println("Painel Solar " + panel.getId() + " (Idade: " + panelAge + " anos, Eficiência: " + df.format(efficiency) + "%):");
                if (needMaintenance) {
                    System.out.println("    ⚠️ Precisa de manutenção (Eficiência baixa ou desgaste excessivo).");
                } else {
                    System.out.println("    👍 Em bom estado.");
                }
            }

            // Manutenção das baterias
            Battery battery = installation.getBattery();
            if (battery != null) {
                int batteryAge = rand.nextInt(10) + 1;  // Idade da bateria (em anos)
                double batteryHealth = rand.nextDouble() * 100;  // Estado de saúde da bateria
                boolean needsMaintenance = batteryHealth < 75 || batteryAge > 5 || rand.nextDouble() < 0.15;  // Condições de manutenção

                System.out.println("\nBateria (Idade: " + batteryAge + " anos, Saúde: " + df.format(batteryHealth) + "%):");
                if (needsMaintenance) {
                    System.out.println("    ⚠️ A bateria precisa de manutenção (Desgaste ou saúde comprometida).");
                } else {
                    System.out.println("    👍 Bateria em bom estado.");
                }
            }
        }
    }


    private void mostrarDicas() {
        System.out.println("\n=== Dicas para Economia de Energia ===");
        System.out.println("1. Aproveite ao máximo a energia solar durante o dia.");
        System.out.println("2. Evite picos de consumo de energia, distribuindo o uso de aparelhos ao longo do dia.");
        System.out.println("3. Realize a manutenção regular dos seus painéis solares para garantir alta eficiência.");
        System.out.println("4. Considere melhorar a eficiência energética da sua casa com melhores eletrodomésticos.");
    }

    private void mostrarPrevisaoEnergia(Installation installation) {
        if (installation != null) {
            System.out.println("\n=== Previsão de Energia ===");

            Random rand = new Random();

            // Simulando a previsão de geração solar e consumo considerando variabilidade climática
            double geraSolarDia = rand.nextDouble() * 30 + 20;  // Geração solar por hora (kWh), com variação climática
            double consomeEnergia = rand.nextDouble() * 20 + 10;  // Consumo de energia por hora (kWh), variando de acordo com o uso

            // Estimando a produção e consumo para as próximas 24 horas
            double totalSolarGenerated = geraSolarDia * 24;  // Geração solar nas próximas 24 horas
            double totalEnergyConsumed = consomeEnergia * 24;  // Consumo de energia nas próximas 24 horas

            // Economia estimada usando energia solar
            double economiaEsperada = (totalSolarGenerated - totalEnergyConsumed) > 0
                    ? (totalSolarGenerated - totalEnergyConsumed)
                    : 0;  // Economia se houver geração suficiente

            System.out.println("Previsão de energia gerada nas próximas 24 horas: " + df.format(totalSolarGenerated) + " kWh");
            System.out.println("Previsão de energia consumida nas próximas 24 horas: " + df.format(totalEnergyConsumed) + " kWh");
            System.out.println("Economia esperada (se você usar energia solar ao invés de rede elétrica): " + df.format(economiaEsperada) + " kWh");

            // Condições climáticas e variabilidade
            String weatherCondition = rand.nextDouble() < 0.5 ? "Ensolarado ☀️" : "Nublado ☁️";
            System.out.println("Condição climática prevista: " + weatherCondition);

            if ("Nublado ☁️".equals(weatherCondition)) {
                System.out.println("    ⛅ Geração solar será reduzida em 30% devido ao clima nublado.");
                totalSolarGenerated *= 0.7;  // Ajusta a geração solar
            }
        }
    }

    private void initializeResponses() {
        respostas = new HashMap<>();
        ResponseEntity<UserDetailDTO> userResponse = userController.getCurrentUser(currentAuthentication);
        UserDetailDTO user = userResponse.getBody();

        // Boas-vindas e Apresentação
        respostas.put("olá", "Olá " + user.name() + "! 👋 Como posso ajudar você hoje? Estou aqui para responder suas dúvidas sobre energia solar e mostrar como você está economizando! ☀️");
        respostas.put("bom dia", "Bom dia " + user.name() + "! ☀️ O dia está perfeito para energia solar! Sua geração já está 15% acima da média. Posso ajudar com algo específico?");
        respostas.put("boa tarde", "Boa tarde " + user.name() + "! 🌤️ Seus painéis estão funcionando a todo vapor, já geraram 12.5 kWh hoje! Como posso ajudar?");
        respostas.put("boa noite", "Boa noite " + user.name() + "! 🌙 Sua bateria está com 90% de carga, perfeita para o consumo noturno. Precisa de alguma informação?");
        respostas.put("como funciona a solaria", "🤖 Eu sou a SolarIA, a inteligência artificial por trás do sistema de energia solar da UniSolar! \nEu trabalho de forma integrada para otimizar o uso da energia solar em sua residência. Aqui está como eu funciono:\n\n1. **Painéis Solares**: Eu monitoro os painéis solares instalados no seu telhado ou outro local estratégico, capturando a energia solar durante o dia.\n2. **Bateria de Carro Elétrico Reutilizada**: Eu também gerencio a bateria que armazena a energia solar gerada para uso posterior, como à noite ou em dias nublados.\n3. **Minha Inteligência Artificial**: Eu analiso os dados em tempo real, como previsão do tempo, tarifas de energia e o consumo diário, para otimizar a utilização da energia solar e das baterias.");

                // Status Atual e Monitoramento
        respostas.put("como está meu sistema agora", String.format("📊 Status atual (%s):\nGeração Solar: 2.8 kWh/h\nCarga da Bateria: 85%%\nConsumo Atual: 1.2 kWh\nEconomia Hoje: R$ 15,40",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))));
        respostas.put("qual minha economia hoje", "💰 Economia do dia:\nEconomia Atual: R$ 22,50\nPrevisão até final do dia: R$ 35,80\nVocê está 20% acima da meta diária! Continue assim! 🎯");
        respostas.put("mostre o status da bateria", "🔋 Status da Bateria:\nNível: 85%\nSaúde: 96%\nTemperatura: 25°C\nAutonomia: 6.5 horas\nPróxima recarga estimada: 22:30");
        respostas.put("como estão meus painéis", "☀️ Status dos Painéis:\nEficiência: 98%\nGeração Atual: 2.8 kWh\nLimpeza: Boa\nÚltima Manutenção: 15 dias atrás\nPróxima limpeza recomendada: 7 dias");

        // Análises e Previsões
        respostas.put("como foi meu consumo essa semana", "📈 Análise Semanal:\nConsumo Total: 125 kWh\nEconomia: R$ 180,50\nRedução vs semana anterior: 15%\nMelhor dia: Terça (32 kWh)\nDica: Seus horários de consumo estão ótimos! 🌟");
        respostas.put("previsão para amanhã", "🔮 Previsão para amanhã:\nClima: Ensolarado ☀️\nGeração Estimada: 18.5 kWh\nMelhores horários: 9h-15h\nEconomia Prevista: R$ 28,90\nDica: Aproveite para usar eletrodomésticos entre 10h-14h!");
        respostas.put("mostre minha meta mensal", "🎯 Acompanhamento de Meta:\nMeta: R$ 300,00\nEconomizado: R$ 220,50\nFaltam: R$ 79,50\nVocê está 5% acima do planejado! 🏆");
        respostas.put("compare com mês passado", "📊 Comparativo Mensal:\nConsumo Atual: -15%\nGeração Solar: +20%\nEconomia: +25%\nUso da Bateria: +10%\nVocê está melhorando a cada mês! 🌟");

        // Recomendações Personalizadas
        respostas.put("dicas de economia", "💡 Dicas Personalizadas:\n1. Use a máquina de lavar às 14h (pico solar)\n2. Configure o ar-condicionado para 23°C\n3. Carregue dispositivos durante o dia\nSeguindo essas dicas, você pode economizar + R$ 45,00 esse mês!");
        respostas.put("melhor horário eletrodomésticos", "⏰ Horários Recomendados Hoje:\n9h-11h: Máquina de Lavar\n13h-15h: Aspirador\n10h-16h: Ar Condicionado\n12h-14h: Forno Elétrico\nAproveite o pico de geração solar! ☀️");
        respostas.put("sugestão de uso da bateria", "🔋 Recomendação de Uso:\nUse a bateria: 18h-21h\nRecarregue: 23h-5h\nEconomia estimada: R$ 18,50\nSua bateria está otimizada para seu padrão de consumo! ⚡");
        respostas.put("dicas do dia", "🌟 Dicas de Hoje:\n1. Dia ensolarado: aproveite para lavar roupas\n2. Bateria está cheia: ideal para usar à noite\n3. Tarifa alta às 18h: use a bateria\nSiga as dicas e economize + R$ 12,00 hoje!");

        // Manutenção e Cuidados
        respostas.put("preciso de manutenção", "🔧 Análise de Manutenção:\nPainéis: OK (98% eficiência)\nBateria: OK (95% saúde)\nInversor: OK (97% eficiência)\nPróxima manutenção preventiva: 15 dias\nSeu sistema está em ótimo estado! ✨");
        respostas.put("quando limpar painéis", "🧹 Recomendação de Limpeza:\nÚltima limpeza: 12 dias atrás\nEficiência atual: 96%\nPrevisão de chuva: Em 3 dias\nSugestão: Aguarde a chuva para avaliar necessidade de limpeza 👍");
        respostas.put("relatório de eficiência", "📋 Relatório Completo:\nEficiência Geral: 95%\nPainéis: 96%\nBateria: 94%\nInversor: 98%\nSeu sistema está entre os 10% mais eficientes! 🏆");
        respostas.put("histórico de manutenção", "📚 Histórico de Manutenções:\nÚltima geral: 60 dias atrás\nÚltima limpeza: 12 dias\nPróxima prevista: 20 dias\nTodas manutenções em dia! ✅");

        // Economia e Sustentabilidade
        respostas.put("impacto ambiental", "🌱 Seu Impacto Ambiental:\nCO2 evitado: 180kg\nÁrvores equivalentes: 15\nEconomia de água: 1200L\nSua contribuição para o planeta é incrível! 🌍");
        respostas.put("benefícios ambientais", "🌿 Benefícios Ambientais:\nRedução de CO2: 180kg/mês\nEconomia de água: 1200L/mês\nEnergia limpa gerada: 450 kWh/mês\nVocê está fazendo a diferença! 💚");
        respostas.put("economia total", "💰 Economia Total:\nEste mês: R$ 280,50\nEste ano: R$ 2.850,00\nDesde a instalação: R$ 8.500,00\nRetorno do investimento: 45% concluído 📈");
        respostas.put("retorno financeiro", "💵 Análise de Retorno:\nInvestimento inicial: R$ 15.000\nEconomia total: R$ 8.500\nTempo restante: 2.5 anos\nSeu sistema está pagando-se mais rápido que o previsto! 🎉");

        // Ajuda e Suporte
        respostas.put("problemas comuns", "❓ Problemas Mais Comuns:\n1. Baixa geração: Verifique sombras/sujeira\n2. Bateria não carrega: Verificar conexões\n3. App não conecta: Reiniciar roteador\nPrecisa de ajuda com algum desses? 🔧");
        respostas.put("contato suporte", "📞 Canais de Suporte:\nWhatsApp: (11) 99999-9999\nEmail: suporte@unisolar.com\nHorário: 8h-20h\nTempos médios de resposta: 5 minutos 👨‍💻");
        respostas.put("agendamento técnico", "👨‍🔧 Agendamento Técnico:\nPróxima visita disponível: 3 dias\nDuração: 1-2 horas\nCusto: Dentro da garantia\nDeseja agendar uma visita?");

        // Manter algumas respostas originais importantes
        respostas.put("como economizar energia com o sistema solarIA", "Você pode economizar energia ajustando o uso de eletrodomésticos durante o dia, aproveitando a energia solar. O sistema também otimiza o uso da bateria para garantir que você use a energia armazenada quando for mais vantajoso. 💡");
        respostas.put("como o sistema decide quando usar a energia da bateria e quando usar a rede elétrica", "A IA avalia o consumo, a previsão do tempo e as tarifas de energia. Ela usa energia da bateria quando necessário, e opta pela rede elétrica em horários de tarifa mais baixa ou se a bateria estiver quase descarregada. 🤖");
        respostas.put("o que é net metering", "O Net Metering é um programa que permite que você envie a energia excedente gerada pelos seus painéis solares de volta para a rede elétrica, gerando créditos que podem ser usados posteriormente para reduzir sua conta de energia. 💚");
    }

    public void startChat() {
        initializeResponses();
        System.out.println("\n=========== Chat com SolarIA 🤖 ===========");
        System.out.println("SolarIA: ☀️ Olá! Sou a SolarIA, assistente virtual da Unisolar! Como posso ajudar? 💡");
        System.out.println("Digite 'sair' para voltar ao menu principal ou 'ajuda' para ver comandos disponíveis");

        while (true) {
            System.out.print("\nVocê: ");
            String question = scanner.nextLine().toLowerCase();

            if (question.equalsIgnoreCase("sair")) {
                break;
            }

            if (question.equalsIgnoreCase("ajuda")) {
                showHelp();
                continue;
            }

            String resposta = getResposta(question);
            if (resposta != null) {
                System.out.println("SolarIA: " + resposta);
            } else {
                System.out.println("SolarIA: Desculpe, não entendi. Você pode reformular a pergunta? Digite 'ajuda' para ver os comandos disponíveis. 🤔");
            }
        }
    }

    private void showHelp() {
        System.out.println("\nComandos disponíveis:");
        System.out.println("- 'status atual': Ver o estado atual do sistema");
        System.out.println("- 'economia': Ver sua economia atual");
        System.out.println("- 'bateria': Ver status da bateria");
        System.out.println("- 'previsão': Ver previsão para amanhã");
        System.out.println("- 'dicas': Receber dicas de economia");
        System.out.println("- 'manutenção': Ver status de manutenção");
        System.out.println("- 'suporte': Contatar suporte técnico");
        System.out.println("- 'sair': Voltar ao menu principal");
    }

    private static String getResposta(String input) {
        // Remove acentos e normaliza o texto
        String normalizedInput = normalizeText(input.toLowerCase());

        for (Map.Entry<String, String> entry : respostas.entrySet()) {
            // Remove acentos e normaliza as chaves de resposta também
            String normalizedKey = normalizeText(entry.getKey().toLowerCase());

            if (normalizedInput.contains(normalizedKey)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private static String normalizeText(String text) {
        // Remove acentos e normaliza caracteres especiais
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return normalized.replaceAll("[^\\p{ASCII}]", "");
    }
}