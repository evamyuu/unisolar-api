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
import unisolar.api.service.ChatbotService;
import unisolar.api.service.MaintenanceService;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

@Component
public class UniSolarCLI implements CommandLineRunner {
    private final Scanner scanner;
    private final AuthenticationManager authenticationManager;
    private final UserController userController;
    private final ChatbotService chatbotService;
    private Authentication currentAuthentication;
    private MaintenanceService maintenanceService;


    private final DecimalFormat df = new DecimalFormat("#,##0.00");

    public UniSolarCLI(AuthenticationManager authenticationManager,
                       UserController userController,
                       ChatbotService chatbotService) {
        this.scanner = new Scanner(System.in);
        this.authenticationManager = authenticationManager;
        this.userController = userController;
        this.chatbotService = chatbotService;
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
        System.out.println("1. Dashboard 📊");
        System.out.println("2. Perfil do Usuário 👤");
        System.out.println("3. Chat com SolarIA 🤖");
        System.out.println("4. Alterar Senha 🔒");
        System.out.println("5. Logout 🚶‍♂️");
        System.out.print("Escolha uma opção: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        switch (choice) {
            case 1:
                showDashboard();
                return true;
            case 2:
                showUserProfile();
                return true;
            case 3:
                startChat();
                return true;
            case 4:
                changePassword();
                return true;
            case 5:
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

        // Retrieve current user and installation data
        ResponseEntity<UserDetailDTO> userResponse = userController.getCurrentUser(currentAuthentication);
        if (userResponse.getStatusCode().is2xxSuccessful() && userResponse.getBody() != null) {
            UserDetailDTO user = userResponse.getBody();
            System.out.println("Bem-vindo, " + user.name() + "!");

            // Fetch installation details including energy consumption, battery, and solar panel data
            Installation installation = getInstallationDetails(user.id());

            if (installation != null) {
                // Main dashboard menu
                int option;
                do {
                    System.out.println("\nEscolha uma opção:");
                    System.out.println("1. Status do Sistema");
                    System.out.println("2. Economia");
                    System.out.println("3. Previsão de Energia");
                    System.out.println("4. Manutenção");
                    System.out.println("5. O Que a SolarIA Planejou para Você Hoje");
                    System.out.println("6. Dicas para Economia de Energia");
                    System.out.println("7. Voltar");

                    option = scanner.nextInt();

                    switch (option) {
                        case 1:
                            mostrarStatus(installation);
                            break;
                        case 2:
                            mostrarEconomia(installation);
                            break;
                        case 3:
                            mostrarPrevisaoEnergia(installation);
                            break;
                        case 4:
                            mostrarManutencao(installation);
                            break;
                        case 5:
                            mostrarDecisoesDaIA(installation);
                            break;
                        case 6:
                            mostrarDicas();
                            break;
                        case 7:
                            System.out.println("Voltando ao menu principal...");
                            break;
                        default:
                            System.out.println("Opção inválida. Tente novamente.");
                    }

                } while (option != 7);
            }
        }
        System.out.println("\nPressione ENTER para voltar ao menu principal...");
        scanner.nextLine();
    }

    private void mostrarDecisoesDaIA(Installation installation) {
        if (installation != null) {
            System.out.println("\n=== O Que a SolarIA planejou para você hoje? ===");

            // Definir os dados para a simulação
            double solarGenerationMorning = 100.0 + (Math.random() * 50.0);  // Geração solar maior pela manhã
            double batteryChargeMorning = 100.0;  // A bateria carrega para 100% durante o dia
            double batteryUsageMorning = solarGenerationMorning * 0.5;  // Usa 50% da geração solar para carregar a bateria
            double solarGenerationAfternoon = 50.0 + (Math.random() * 30.0);  // Geração solar menor devido às nuvens
            double batteryConsumptionNight = 20.0 + (Math.random() * 10.0);  // A bateria é utilizada para carregar os aparelhos essenciais
            double batteryDischargeAmount = 0.2 + (Math.random() * 0.3) * 100;  // Descarregar a bateria durante horários de pico (tarifa mais alta)

            // IA decide as ações
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
        // Assuming there is a method to retrieve the installation based on the user ID
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

            // Fonte de energia
            boolean usandoEnergiaSolar = installation.getSolarPanels().size() > 0; // Verifica se há painéis solares
            System.out.println("\nFonte de Energia Atual: " + (usandoEnergiaSolar ? "Solar ☀️" : "Rede Elétrica ⚡"));

            // Bateria
            Battery battery = installation.getBattery();
            int batteryCharge = battery != null ? (int) battery.getCurrentCharge() : 0;
            String statusBateria = batteryCharge > 50 ? "Carregada 👍" : "Baixa ⚠️";
            String batteryStatus = battery != null ? battery.getHealth() : "Desconhecido ❓";

            System.out.println("\nBateria:");
            System.out.println("Nível: " + batteryCharge + "% " + gerarBarraProgresso(batteryCharge));
            System.out.println("Status: " + statusBateria + " (" + batteryStatus + ")");

            // Painéis solares
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

            // Simulando consumos de energia (valores aleatórios)
            double totalSolarConsumption = Math.random() * 50 + 20;  // Consumo solar entre 20 e 70 kWh
            double totalGridConsumption = Math.random() * 50 + 10;   // Consumo da rede entre 10 e 60 kWh
            double totalBatteryConsumption = Math.random() * 30 + 5;  // Consumo de bateria entre 5 e 35 kWh

            // Simulando a economia com base no consumo solar
            double totalConsumption = totalSolarConsumption + totalGridConsumption + totalBatteryConsumption;
            double economiaHoje = totalSolarConsumption * 0.25; // Exemplo de economia (ajuste conforme necessário)
            double economiaMes = totalSolarConsumption * 7.5;   // Exemplo de economia mensal (ajuste conforme necessário)
            double projecaoAnual = economiaMes * 12;             // Projeção anual

            // Exibindo dados de consumo de energia e economia simulada
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
            scanner.nextLine(); // consume newline

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

            // Manutenção dos painéis solares
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


    private void startChat() {
        System.out.println("\n=========== Chat com SolarIA 🤖 ===========");
        System.out.println("SolarIA: ☀️ Olá, sou a SolarIA, assistente virtual da Unisolar! 🌱 Como posso ajudar? 💡");
        System.out.println("Digite 'sair' para voltar ao menu principal");

        while (true) {
            System.out.print("\nVocê: ");
            String question = scanner.nextLine();

            if (question.equalsIgnoreCase("sair")) {
                break;
            }
            if (question.toLowerCase().contains("previsão") || question.toLowerCase().contains("economia")) {
                simulateEnergyForecast();
            } else if (question.toLowerCase().contains("impacto") || question.toLowerCase().contains("bateria")) {
                simulateBatteryImpact();
            } else if (question.toLowerCase().contains("mensal") || question.toLowerCase().contains("estimativa")) {
                simulateMonthlyForecast();
            }
            // IA processa a pergunta e retorna uma previsão ou recomendação baseada em dados reais e simulações
            String response = chatbotService.answerQuestion(question);
            System.out.println("\nSolarIA: " + response);
        }
    }

    private void simulateEnergyForecast() {
        System.out.println("\nSolarIA: ☀️ Baseado nos dados de consumo e na previsão do tempo, aqui está sua previsão de economia:");

        // Simulando a previsão de consumo com base em padrões históricos
        double predictedSolarConsumption = 100.0 + (Math.random() * 50.0);  // Simulando aumento ou queda
        double predictedGridConsumption = 50.0 + (Math.random() * 30.0);
        double predictedBatteryConsumption = 20.0 + (Math.random() * 10.0);

        double totalConsumption = predictedSolarConsumption + predictedGridConsumption + predictedBatteryConsumption;
        double savingsToday = (predictedSolarConsumption - predictedGridConsumption) * 0.8;  // Suposição de economia com energia solar

        System.out.println("\nPrevisão para hoje:");
        System.out.println("Consumo Solar: " + df.format(predictedSolarConsumption) + " kWh");
        System.out.println("Consumo da Rede Elétrica: " + df.format(predictedGridConsumption) + " kWh");
        System.out.println("Consumo da Bateria: " + df.format(predictedBatteryConsumption) + " kWh");
        System.out.println("Consumo Total: " + df.format(totalConsumption) + " kWh");
        System.out.println("Economia estimada hoje: R$ " + df.format(savingsToday));

        System.out.println("\nCom base no clima e padrões de uso, sua economia mensal pode aumentar em até 25% se utilizar mais energia solar durante o dia.");
    }

    private String getWeatherCondition() {
        // Simulação do clima (pode ser substituído por dados reais de uma API)
        String[] conditions = {"Ensolarado", "Nublado", "Chuvoso"};
        int index = (int) (Math.random() * conditions.length);
        return conditions[index];
    }

    private int getCurrentHour() {
        // Simula a hora atual (de 0 a 23)
        return (int) (Math.random() * 24);  // Simulando uma hora aleatória
    }

    private void simulateBatteryImpact() {
        System.out.println("\nSolarIA: 🔋 Vamos simular o impacto do uso da bateria no seu consumo de energia:");

        // Simulando o consumo diário de energia da bateria
        double batteryDischargeRate = 0.1 + (Math.random() * 0.3);  // A taxa de descarregamento pode variar de 10% a 30% da carga da bateria
        double batteryLevel = 1.0;  // Iniciamos a bateria cheia

        // Simulando o impacto da descarga da bateria
        double batteryUsageToday = batteryLevel * batteryDischargeRate;
        double savingsFromBattery = batteryUsageToday * 0.75;  // Economia estimada ao usar a bateria

        // Exibindo o impacto
        System.out.println("Taxa de descarregamento da bateria hoje: " + df.format(batteryDischargeRate * 100) + "%");
        System.out.println("Energia utilizada da bateria hoje: " + df.format(batteryUsageToday * 100) + "%");
        System.out.println("Economia estimada ao usar a bateria: R$ " + df.format(savingsFromBattery) + " 💰");

        // Ajustando o nível da bateria após o uso
        batteryLevel -= batteryUsageToday;

        System.out.println("\nNível da bateria após o uso: " + df.format(batteryLevel * 100) + "%");
    }

    private void simulateMonthlyForecast() {
        System.out.println("\nSolarIA: 📊 Vamos simular a previsão mensal de consumo e economia.");

        // Estimando o consumo e economia mensal com base no consumo diário
        double dailySolarConsumption = 100.0 + (Math.random() * 50.0);  // Consumo solar diário simulado
        double dailyGridConsumption = 50.0 + (Math.random() * 30.0);    // Consumo de rede diário simulado
        double dailyBatteryConsumption = 20.0 + (Math.random() * 10.0);  // Consumo da bateria diário simulado

        // Economias com energia solar (ajustado para o mês)
        double dailySavings = (dailySolarConsumption - dailyGridConsumption) * 0.8;  // Economia diária
        double monthlySavings = dailySavings * 30;  // Estimando economia mensal

        // Estimativa de consumo total no mês
        double monthlyTotalConsumption = (dailySolarConsumption + dailyGridConsumption + dailyBatteryConsumption) * 30;

        // Exibindo a previsão mensal
        System.out.println("Consumo Solar Mensal: " + df.format(dailySolarConsumption * 30) + " kWh 🌞");
        System.out.println("Consumo da Rede Mensal: " + df.format(dailyGridConsumption * 30) + " kWh ⚡");
        System.out.println("Consumo da Bateria Mensal: " + df.format(dailyBatteryConsumption * 30) + " kWh 🔋");
        System.out.println("Consumo Total Mensal: " + df.format(monthlyTotalConsumption) + " kWh");

        System.out.println("\nEconomia Mensal Estimada: R$ " + df.format(monthlySavings) + " 💵");
    }

}

