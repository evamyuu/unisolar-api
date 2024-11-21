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
import unisolar.api.infra.config.UserSeeder;
import unisolar.api.search.FeatureSearchTree;
import unisolar.api.service.ChatbotService;
import unisolar.api.service.FeatureSearchService;
import unisolar.api.service.MaintenanceService;

import java.text.DecimalFormat;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * This class represents the command-line interface (CLI) for the Unisolar system.
 *
 * It simulates a user-friendly command-line interface (CLI) for interacting with various features
 * of the Unisolar platform, designed for testing and demonstration purposes.
 *
 * <p>Main functionalities include:</p>
 * - User authentication (Login and Registration)
 * - Feature search
 * - Dashboard access
 * - User profile management
 * - Chat with SolarIA chatbot
 * - Password management
 * - Logout
 *
 * The CLI uses a scanner for user input and provides feedback via console messages.
 */
@Component
public class UnisolarCLI implements CommandLineRunner {

    private final Scanner scanner;
    private final AuthenticationManager authenticationManager;
    private final UserController userController;
    private final ChatbotService chatbotService;
    private final FeatureSearchService featureSearchService;
    private final UserSeeder userSeeder;
    private Authentication currentAuthentication;
    private MaintenanceService maintenanceService;

    private final DecimalFormat df = new DecimalFormat("#,##0.00");
    private static Map<String, String> respostas;

    /**
     * Constructor for initializing dependencies.
     *
     * @param authenticationManager the authentication manager for handling login
     * @param userController        the controller for managing user operations
     * @param chatbotService        the service providing chatbot functionalities
     * @param featureSearchService  the service enabling feature searches
     * @param userSeeder            the utility for seeding default users
     */
    public UnisolarCLI(AuthenticationManager authenticationManager,
                       UserController userController,
                       ChatbotService chatbotService,
                       FeatureSearchService featureSearchService,
                       UserSeeder userSeeder) {
        this.scanner = new Scanner(System.in);
        this.authenticationManager = authenticationManager;
        this.userController = userController;
        this.chatbotService = chatbotService;
        this.featureSearchService = featureSearchService;
        this.userSeeder = userSeeder;
    }

    /**
     * Main method executed when the application starts. It initializes the user seeding
     * process and continuously displays menus for user interaction.
     *
     * @param args command-line arguments passed to the application
     * @throws Exception if an error occurs during execution
     */
    @Override
    public void run(String... args) throws Exception {

        userSeeder.run();

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
        System.out.println("    Obrigado por usar o Unisolar!    ");
        System.out.println("          Até logo! 👋               ");
        System.out.println("=====================================");
    }

    /**
     * Displays the login menu, allowing users to log in or register.
     */
    private void showLoginMenu() {
        System.out.println("\n=========== Unisolar 🌞 =============");
        System.out.println("      Energia que transforma vidas      ");
        System.out.println("=====================================");

        int choice = -1;
        while (choice != 1 && choice != 2) {
            System.out.println("1. Login 🔑");
            System.out.println("2. Cadastro 📝");
            System.out.print("Escolha uma opção: ");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine();
            } else {
                scanner.nextLine();
                choice = -1;
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

    /**
     * Displays the main menu, providing access to various functionalities.
     *
     * @return true to keep the menu running, false to exit
     */
    private boolean showMainMenu() {
        System.out.println("\n=========== Home ===========");
        System.out.println("1. Buscar 🔎");
        System.out.println("2. Dashboard 📊");
        System.out.println("3. Perfil do Usuário 👤");
        System.out.println("4. Chat com SolarIA 🤖");
        System.out.println("5. Alterar Senha 🔒");
        System.out.println("6. Logout 🚶‍♂️");
        System.out.print("Escolha uma opção: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

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
                return false;
            default:
                System.out.println("\n[Erro ❌] Opção inválida! Tente novamente.");
                return true;
        }
    }

    /**
     * Handles the user login process, including authentication.
     */
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

    /**
     * Handles user registration by collecting user details and sending them to the controller.
     */
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

    /**
     * Displays the dashboard menu and various system statuses and features for the logged-in user.
     */
    private void showDashboard() {

        System.out.println("\n=========== Dashboard 📊 ===========");

        ResponseEntity<UserDetailDTO> userResponse = userController.getCurrentUser(currentAuthentication);
        if (userResponse.getStatusCode().is2xxSuccessful() && userResponse.getBody() != null) {
            UserDetailDTO user = userResponse.getBody();
            System.out.println("👋 Olá, " + user.name() + "!");

            Installation installation = getInstallationDetails(user.id());

            if (installation != null) {
                int option;
                do {
                    System.out.println("\n=========== Menu Principal ===========");
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

    /**
     * Displays a feature search interface, allowing users to search for functionalities.
     */
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

    /**
     * Handles displaying AI-driven energy optimization decisions based on solar panel
     * and battery performance for the current day and upcoming night.
     *
     * @param installation The installation details of the current user, including solar panels and battery information.
     */
    private void mostrarDecisoesDaIA(Installation installation) {
        if (installation != null) {
            System.out.println("\n=== O Que a SolarIA planejou para você hoje? ===");

            double solarGenerationMorning = 100.0 + (Math.random() * 50.0);
            double batteryChargeMorning = 100.0;
            double batteryUsageMorning = solarGenerationMorning * 0.5;
            double solarGenerationAfternoon = 50.0 + (Math.random() * 30.0);
            double batteryConsumptionNight = 20.0 + (Math.random() * 10.0);
            double batteryDischargeAmount = 0.2 + (Math.random() * 0.3) * 100;

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
            double savingsFromBatteryDischarge = batteryDischargeAmount * 0.75;
            System.out.println("💡 Decisão da IA: Descarregar mais bateria durante horários de pico para economizar com tarifas altas.");
            System.out.println("Economia Estimada: R$ " + df.format(savingsFromBatteryDischarge) + " 💰");
        }
    }

    /**
     * Retrieves installation details for a specific user based on their user ID.
     *
     * @param userId The unique identifier of the user.
     * @return The installation details, or null if the data could not be retrieved.
     */
    private Installation getInstallationDetails(Long userId) {

        ResponseEntity<Installation> installationResponse = userController.getUserInstallation(userId);
        if (installationResponse.getStatusCode().is2xxSuccessful() && installationResponse.getBody() != null) {
            return installationResponse.getBody();
        } else {
            System.out.println("\n[Erro ❌] Não foi possível carregar os dados de instalação.");
            return null;
        }
    }

    /**
     * Displays the current system status, including energy source, battery level, and solar panel performance.
     *
     * @param installation The installation details of the user.
     */
    private void mostrarStatus(Installation installation) {
        if (installation != null) {
            System.out.println("\n=== Status do Sistema ===");

            boolean usandoEnergiaSolar = installation.getSolarPanels().size() > 0;
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

    /**
     * Displays energy consumption data and calculates estimated savings for the user.
     *
     * @param installation The installation details of the user.
     */
    private void mostrarEconomia(Installation installation) {
        if (installation != null) {
            System.out.println("\n=== Economia ===");

            double totalSolarConsumption = Math.random() * 50 + 20;
            double totalGridConsumption = Math.random() * 50 + 10;
            double totalBatteryConsumption = Math.random() * 30 + 5;

            double totalConsumption = totalSolarConsumption + totalGridConsumption + totalBatteryConsumption;
            double economiaHoje = totalSolarConsumption * 0.25;
            double economiaMes = totalSolarConsumption * 7.5;
            double projecaoAnual = economiaMes * 12;

            System.out.println("Consumo Total: " + df.format(totalConsumption) + " kWh");
            System.out.println("Consumo Solar: " + df.format(totalSolarConsumption) + " kWh 🌞");
            System.out.println("Consumo da Rede: " + df.format(totalGridConsumption) + " kWh ⚡");
            System.out.println("Consumo da Bateria: " + df.format(totalBatteryConsumption) + " kWh 🔋");

            System.out.println("\nEconomia Hoje: R$ " + df.format(economiaHoje) + " 💰");
            System.out.println("Economia do Mês: R$ " + df.format(economiaMes) + " 💵");
            System.out.println("Projeção Anual: R$ " + df.format(projecaoAnual) + " 📊");
        }
    }

    /**
     * Displays the authenticated user's profile and provides options to update or delete the profile.
     *
     * This method fetches the current user's details, such as their name, email, and username.
     * It then presents the user with a menu to:
     * 1. Update the profile.
     * 2. Delete the profile.
     * 3. Return to the previous menu.
     */
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

    /**
     * Updates the user's profile with new information (name, email, username).
     *
     * Prompts the user for new values for their name, email, and username. If any field is left blank,
     * the current value is retained. The updated information is then sent to the server for processing.
     * If the update is successful, a success message is shown, otherwise, an error message is displayed.
     *
     * @param currentUser The current user's details to be updated.
     */
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

    /**
     * Changes the user's password.
     *
     * Prompts the user for their current password and the new password. Then, it sends the password change
     * request to the server. If successful, the user is logged out and asked to log in again with the new password.
     */
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
                    currentAuthentication = null;
                    System.out.println("Por favor, faça login novamente com sua nova senha.");
                } else {
                    System.out.println("\n[Erro ❌] Não foi possível alterar a senha: " + changeResponse.getBody());
                }
            } catch (Exception e) {
                System.out.println("\n[Erro ❌] Ocorreu um erro ao alterar senha: " + e.getMessage());
            }
        }
    }

    /**
     * Deletes the user's profile after a confirmation from the user.
     *
     * Asks the user to confirm whether they want to delete their profile. If confirmed, the profile is
     * deactivated on the server, and the user is logged out. If the operation fails, an error message is displayed.
     *
     * @param currentUser The current user's details to be deleted.
     */
    private void deleteProfile(UserDetailDTO currentUser) {
        System.out.println("\n=========== Deletar Perfil 🗑️ ===========");
        System.out.print("Tem certeza que deseja deletar seu perfil? (s/n): ");
        String confirmation = scanner.nextLine();

        if (confirmation.equalsIgnoreCase("s")) {
            try {
                ResponseEntity<String> response = userController.deactivateUser(currentUser.id());
                if (response.getStatusCode().is2xxSuccessful()) {
                    System.out.println("\n[Sucesso ✅] Perfil deletado com sucesso. Você será desconectado.");
                    currentAuthentication = null;
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

    /**
     * Generates a progress bar based on the given percentage.
     *
     * This method creates a simple visual representation of a progress bar by dividing the total bar size
     * (20 units) based on the provided percentage. Filled segments are represented by "█" and empty ones by "░".
     *
     * @param porcentagem The progress percentage (0 to 100).
     * @return A string representing the progress bar.
     */
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

    /**
     * Displays the maintenance status of the solar system components, including solar panels and battery.
     * For each component, it checks for the need for maintenance based on various factors such as
     * efficiency, age, and random conditions to simulate real-world maintenance needs.
     *
     * @param installation The solar system installation that contains the solar panels and battery.
     */
    private void mostrarManutencao(Installation installation) {
        if (installation != null) {
            System.out.println("\n=== Manutenção do Sistema ===");

            Random rand = new Random();

            for (SolarPanel panel : installation.getSolarPanels()) {
                int panelAge = rand.nextInt(10) + 1;
                double efficiency = rand.nextDouble() * 100;
                boolean needMaintenance = efficiency < 85 || panelAge > 5 || rand.nextDouble() < 0.2;

                System.out.println("Painel Solar " + panel.getId() + " (Idade: " + panelAge + " anos, Eficiência: " + df.format(efficiency) + "%):");
                if (needMaintenance) {
                    System.out.println("    ⚠️ Precisa de manutenção (Eficiência baixa ou desgaste excessivo).");
                } else {
                    System.out.println("    👍 Em bom estado.");
                }
            }

            Battery battery = installation.getBattery();
            if (battery != null) {
                int batteryAge = rand.nextInt(10) + 1;
                double batteryHealth = rand.nextDouble() * 100;
                boolean needsMaintenance = batteryHealth < 75 || batteryAge > 5 || rand.nextDouble() < 0.15;

                System.out.println("\nBateria (Idade: " + batteryAge + " anos, Saúde: " + df.format(batteryHealth) + "%):");
                if (needsMaintenance) {
                    System.out.println("    ⚠️ A bateria precisa de manutenção (Desgaste ou saúde comprometida).");
                } else {
                    System.out.println("    👍 Bateria em bom estado.");
                }
            }
        }
    }

    /**
     * Displays energy-saving tips to help users maximize their solar energy usage
     * and reduce overall energy consumption.
     */
     private void mostrarDicas() {
        System.out.println("\n=== Dicas para Economia de Energia ===");
        System.out.println("1. Aproveite ao máximo a energia solar durante o dia.");
        System.out.println("2. Evite picos de consumo de energia, distribuindo o uso de aparelhos ao longo do dia.");
        System.out.println("3. Realize a manutenção regular dos seus painéis solares para garantir alta eficiência.");
        System.out.println("4. Considere melhorar a eficiência energética da sua casa com melhores eletrodomésticos.");
    }

    /**
     * Displays the forecasted energy generation and consumption over the next 24 hours.
     * Also provides an estimate of potential savings based on solar energy usage.
     *
     * @param installation The installation containing the solar panels and battery.
     */
    private void mostrarPrevisaoEnergia(Installation installation) {
        if (installation != null) {
            System.out.println("\n=== Previsão de Energia ===");

            Random rand = new Random();

            double geraSolarDia = rand.nextDouble() * 30 + 20;
            double consomeEnergia = rand.nextDouble() * 20 + 10;

            double totalSolarGenerated = geraSolarDia * 24;
            double totalEnergyConsumed = consomeEnergia * 24;

            double economiaEsperada = (totalSolarGenerated - totalEnergyConsumed) > 0
                    ? (totalSolarGenerated - totalEnergyConsumed)
                    : 0;

            System.out.println("Previsão de energia gerada nas próximas 24 horas: " + df.format(totalSolarGenerated) + " kWh");
            System.out.println("Previsão de energia consumida nas próximas 24 horas: " + df.format(totalEnergyConsumed) + " kWh");
            System.out.println("Economia esperada (se você usar energia solar ao invés de rede elétrica): " + df.format(economiaEsperada) + " kWh");

            String weatherCondition = rand.nextDouble() < 0.5 ? "Ensolarado ☀️" : "Nublado ☁️";
            System.out.println("Condição climática prevista: " + weatherCondition);

            if ("Nublado ☁️".equals(weatherCondition)) {
                System.out.println("    ⛅ Geração solar será reduzida em 30% devido ao clima nublado.");
                totalSolarGenerated *= 0.7;
            }
        }
    }

    /**
     * Initializes predefined responses for different user queries.
     * These responses are tailored to give personalized information about the user's solar system status.
     */
    private void initializeResponses() {
        respostas = new HashMap<>();
        ResponseEntity<UserDetailDTO> userResponse = userController.getCurrentUser(currentAuthentication);
        UserDetailDTO user = userResponse.getBody();

        respostas.put("olá", "Olá, " + user.name() + "! 👋 Como posso ajudar você hoje? Estou aqui para responder suas dúvidas sobre energia solar e mostrar como você está economizando! ☀️");
        respostas.put("bom dia", "Bom dia, " + user.name() + "! ☀️ O dia está perfeito para energia solar! Sua geração já está 15% acima da média. Posso ajudar com algo específico?");
        respostas.put("boa tarde", "Boa tarde, " + user.name() + "! 🌤️ Seus painéis estão funcionando a todo vapor, já geraram 12.5 kWh hoje! Como posso ajudar?");
        respostas.put("boa noite", "Boa noite, " + user.name() + "! 🌙 Sua bateria está com 90% de carga, perfeita para o consumo noturno. Precisa de alguma informação?");
        respostas.put("como funciona a solaria", "🤖 Eu sou a SolarIA, a inteligência artificial por trás do sistema de energia solar da UniSolar! \nEu trabalho de forma integrada para otimizar o uso da energia solar em sua residência. Aqui está como eu funciono:\n\n1. **Painéis Solares**: Eu monitoro os painéis solares instalados no seu telhado ou outro local estratégico, capturando a energia solar durante o dia.\n2. **Bateria de Carro Elétrico Reutilizada**: Eu também gerencio a bateria que armazena a energia solar gerada para uso posterior, como à noite ou em dias nublados.\n3. **Minha Inteligência Artificial**: Eu analiso os dados em tempo real, como previsão do tempo, tarifas de energia e o consumo diário, para otimizar a utilização da energia solar e das baterias.");

        respostas.put("como está meu sistema agora", String.format("📊 Status atual (%s):\nGeração Solar: 2.8 kWh/h\nCarga da Bateria: 85%%\nConsumo Atual: 1.2 kWh\nEconomia Hoje: R$ 15,40",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))));
        respostas.put("qual minha economia hoje", "💰 Economia do dia:\nEconomia Atual: R$ 22,50\nPrevisão até final do dia: R$ 35,80\nVocê está 20% acima da meta diária! Continue assim! 🎯");
        respostas.put("mostre o status da bateria", "🔋 Status da Bateria:\nNível: 85%\nSaúde: 96%\nTemperatura: 25°C\nAutonomia: 6.5 horas\nPróxima recarga estimada: 22:30");
        respostas.put("como estão meus painéis", "☀️ Status dos Painéis:\nEficiência: 98%\nGeração Atual: 2.8 kWh\nLimpeza: Boa\nÚltima Manutenção: 15 dias atrás\nPróxima limpeza recomendada: 7 dias");

        respostas.put("como foi meu consumo essa semana", "📈 Análise Semanal:\nConsumo Total: 125 kWh\nEconomia: R$ 180,50\nRedução vs semana anterior: 15%\nMelhor dia: Terça (32 kWh)\nDica: Seus horários de consumo estão ótimos! 🌟");
        respostas.put("previsão para amanhã", "🔮 Previsão para amanhã:\nClima: Ensolarado ☀️\nGeração Estimada: 18.5 kWh\nMelhores horários: 9h-15h\nEconomia Prevista: R$ 28,90\nDica: Aproveite para usar eletrodomésticos entre 10h-14h!");
        respostas.put("mostre minha meta mensal", "🎯 Acompanhamento de Meta:\nMeta: R$ 300,00\nEconomizado: R$ 220,50\nFaltam: R$ 79,50\nVocê está 5% acima do planejado! 🏆");
        respostas.put("compare com mês passado", "📊 Comparativo Mensal:\nConsumo Atual: -15%\nGeração Solar: +20%\nEconomia: +25%\nUso da Bateria: +10%\nVocê está melhorando a cada mês! 🌟");

        respostas.put("dicas de economia", "💡 Dicas Personalizadas:\n1. Use a máquina de lavar às 14h (pico solar)\n2. Configure o ar-condicionado para 23°C\n3. Carregue dispositivos durante o dia\nSeguindo essas dicas, você pode economizar + R$ 45,00 esse mês!");
        respostas.put("melhor horário eletrodomésticos", "⏰ Horários Recomendados Hoje:\n9h-11h: Máquina de Lavar\n13h-15h: Aspirador\n10h-16h: Ar Condicionado\n12h-14h: Forno Elétrico\nAproveite o pico de geração solar! ☀️");
        respostas.put("sugestão de uso da bateria", "🔋 Recomendação de Uso:\nUse a bateria: 18h-21h\nRecarregue: 23h-5h\nEconomia estimada: R$ 18,50\nSua bateria está otimizada para seu padrão de consumo! ⚡");
        respostas.put("dicas do dia", "🌟 Dicas de Hoje:\n1. Dia ensolarado: aproveite para lavar roupas\n2. Bateria está cheia: ideal para usar à noite\n3. Tarifa alta às 18h: use a bateria\nSiga as dicas e economize + R$ 12,00 hoje!");

        respostas.put("preciso de manutenção", "🔧 Análise de Manutenção:\nPainéis: OK (98% eficiência)\nBateria: OK (85% saúde)\nInversor: OK (97% eficiência)\nPróxima manutenção preventiva: 15 dias\nSeu sistema está em ótimo estado! ✨");
        respostas.put("quando limpar painéis", "🧹 Recomendação de Limpeza:\nÚltima limpeza: 12 dias atrás\nEficiência atual: 96%\nPrevisão de chuva: Em 3 dias\nSugestão: Aguarde a chuva para avaliar necessidade de limpeza 👍");
        respostas.put("relatório de eficiência", "📋 Relatório Completo:\nEficiência Geral: 95%\nPainéis: 96%\nBateria: 94%\nInversor: 98%\nSeu sistema está entre os 10% mais eficientes! 🏆");
        respostas.put("histórico de manutenção", "📚 Histórico de Manutenções:\nÚltima geral: 60 dias atrás\nÚltima limpeza: 12 dias\nPróxima prevista: 20 dias\nTodas manutenções em dia! ✅");

        respostas.put("impacto ambiental", "🌱 Seu Impacto Ambiental:\nCO2 evitado: 180kg\nÁrvores equivalentes: 15\nEconomia de água: 1200L\nSua contribuição para o planeta é incrível! 🌍");
        respostas.put("benefícios ambientais", "🌿 Benefícios Ambientais:\nRedução de CO2: 180kg/mês\nEconomia de água: 1200L/mês\nEnergia limpa gerada: 450 kWh/mês\nVocê está fazendo a diferença! 💚");
        respostas.put("economia total", "💰 Economia Total:\nEste mês: R$ 280,50\nEste ano: R$ 2.850,00\nDesde a instalação: R$ 8.500,00\nRetorno do investimento: 45% concluído 📈");
        respostas.put("retorno financeiro", "💵 Análise de Retorno:\nInvestimento inicial: R$ 15.000\nEconomia total: R$ 8.500\nTempo restante: 2.5 anos\nSeu sistema está pagando-se mais rápido que o previsto! 🎉");

        respostas.put("problemas comuns", "❓ Problemas Mais Comuns:\n1. Baixa geração: Verifique sombras/sujeira\n2. Bateria não carrega: Verificar conexões\n3. App não conecta: Reiniciar roteador\nPrecisa de ajuda com algum desses? 🔧");
        respostas.put("contato suporte", "📞 Canais de Suporte:\nWhatsApp: (11) 99999-9999\nEmail: suporte@unisolar.com\nHorário: 8h-20h\nTempos médios de resposta: 5 minutos 👨‍💻");
        respostas.put("agendamento técnico", "👨‍🔧 Agendamento Técnico:\nPróxima visita disponível: 3 dias\nDuração: 1-2 horas\nCusto: Dentro da garantia\nDeseja agendar uma visita?");

        respostas.put("como economizar energia com o sistema solarIA", "Você pode economizar energia ajustando o uso de eletrodomésticos durante o dia, aproveitando a energia solar. O sistema também otimiza o uso da bateria para garantir que você use a energia armazenada quando for mais vantajoso. 💡");
        respostas.put("como você decide quando usar a bateria e quando usar a energia solar?", "Eu avalio o consumo, a previsão do tempo e as tarifas de energia.\nSe a previsão de tempo diz que vai chover ou ficar nublado, eu guardo a carga da bateria para quando realmente precisar.\nSe a previsão do tempo indica chuva ou céu nublado, eu guardo a carga da bateria para quando realmente precisar.\nEsses são alguns exemplos de como faço isso para você. 🤖");
        respostas.put("o que é net metering", "O Net Metering é um programa que permite que você envie a energia excedente gerada pelos seus painéis solares de volta para a rede elétrica, gerando créditos que podem ser usados posteriormente para reduzir sua conta de energia. 💚");
    }

    /**
     * Starts the interactive chat with the user.
     * This method initializes the responses and continuously listens for user input to provide information.
     * It allows the user to exit the chat or request help with commands at any time.
     */
    public void startChat() {
        initializeResponses();
        System.out.println("\n=========== Chat com SolarIA 🤖 ===========");
        System.out.println("SolarIA: ☀️ Olá! Sou a SolarIA, assistente virtual da Unisolar! Como posso ajudar? 💡");
        System.out.println("Digite 'sair' para voltar ao menu principal ou 'ajuda' para ver comandos disponíveis");

        while (true) {
            System.out.print("\nVocê: ");
            String question = scanner.nextLine();

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
                System.out.println("SolarIA: Hmmm, boa pergunta! Vou achar isso para você agora, só um instante! 🤗");

                try {
                    String response = chatbotService.answerQuestion(question);
                    System.out.println("SolarIA: " + response);
                } catch (Exception e) {
                    System.out.println("SolarIA: 😞 Desculpe, não consegui acessar a informação ou não entendi muito bem. Você pode tentar reformular a pergunta?\nSe o problema persistir, por favor, tente novamente mais tarde ou digite 'ajuda' para ver os comandos disponíveis. 🤔");
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Displays available commands to the user to guide them on how to interact with SolarIA.
     */
    private void showHelp() {
        System.out.println("\nSolarIA: Comandos disponíveis:");
        System.out.println("- 'como está meu sistema agora': Ver o estado atual do sistema");
        System.out.println("- 'economia total': Ver sua economia atual");
        System.out.println("- 'mostre o status da bateria': Ver status da bateria");
        System.out.println("- 'previsão para amanhã': Ver previsão para amanhã");
        System.out.println("- 'dicas de economia': Receber dicas de economia");
        System.out.println("- 'preciso de manutenção': Ver status de manutenção");
        System.out.println("- 'contato suporte': Contatar suporte técnico");
        System.out.println("- 'impacto ambiental': Ver seu impacto para a Terra");
        System.out.println("- 'sair': Voltar ao menu principal");
    }

    /**
     * Retrieves the response based on the user's input.
     * The input is normalized, and a matching response is returned from the predefined list.
     *
     * @param input The user’s input query.
     * @return The corresponding response or null if no match is found.
     */
    private static String getResposta(String input) {

        String normalizedInput = normalizeText(input.toLowerCase());

        for (Map.Entry<String, String> entry : respostas.entrySet()) {

            String normalizedKey = normalizeText(entry.getKey().toLowerCase());

            if (normalizedInput.contains(normalizedKey)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Normalizes the input text by removing special characters and accents for easier matching.
     *
     * @param text The text to normalize.
     * @return The normalized text.
     */
    private static String normalizeText(String text) {

        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return normalized.replaceAll("[^\\p{ASCII}]", "");
    }
}