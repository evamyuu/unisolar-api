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
import unisolar.api.service.ChatbotService;

import java.text.DecimalFormat;
import java.util.Scanner;

@Component
public class UniSolarCLI implements CommandLineRunner {
    private final Scanner scanner;
    private final AuthenticationManager authenticationManager;
    private final UserController userController;
    private final ChatbotService chatbotService;
    private Authentication currentAuthentication;
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
        System.out.println("1. Login 🔑");
        System.out.println("2. Cadastro 📝");
        System.out.print("Escolha uma opção: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        switch (choice) {
            case 1:
                doLogin();
                break;
            case 2:
                doRegister();
                break;
            default:
                System.out.println("\n[Erro ❌] Opção inválida! Tente novamente.");
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
        ResponseEntity<UserDetailDTO> userResponse = userController.getCurrentUser(currentAuthentication);
        if (userResponse.getStatusCode().is2xxSuccessful() && userResponse.getBody() != null) {
            UserDetailDTO user = userResponse.getBody();
            System.out.println("Bem-vindo, " + user.name() + "!");

            mostrarStatus();
            mostrarEconomia();
            mostrarDicas();
        }
        System.out.println("\nPressione ENTER para voltar ao menu principal...");
        scanner.nextLine();
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


    private void mostrarStatus() {
        System.out.println("\n=== Status do Sistema ===");

        boolean usandoEnergiaSolar = true;
        int nivelBateria = 75;
        String statusBateria = nivelBateria > 50 ? "Carregada 👍" : "Baixa ⚠️";
        String statusPaineis = "Operacional 🌞";

        System.out.println("\nFonte de Energia Atual: " + (usandoEnergiaSolar ? "Solar ☀" : "Rede Elétrica ⚡"));
        System.out.println("\nBateria:");
        System.out.println("Nível: " + nivelBateria + "% " + gerarBarraProgresso(nivelBateria));
        System.out.println("Status: " + statusBateria);
        System.out.println("\nPainéis Solares:");
        System.out.println("Status: " + statusPaineis);
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

    private void mostrarEconomia() {
        System.out.println("\n=== Economia ===");
        double economiaHoje = 5.25;
        double economiaMes = 150.00;
        double projecaoAnual = economiaMes * 12;

        System.out.println("Economia Hoje: R$ " + df.format(economiaHoje));
        System.out.println("Economia do Mês: R$ " + df.format(economiaMes));
        System.out.println("Projeção Anual: R$ " + df.format(projecaoAnual));
    }

    private void mostrarDicas() {
        System.out.println("\n=== Dicas de Hoje ===");
        System.out.println("1. Horários de Uso: Melhor horário para usar eletrodomésticos: após às 20h");
        System.out.println("2. Aproveitamento Solar: Dia ensolarado! Use aparelhos agora.");
        System.out.println("3. Economia de Energia: Desligue aparelhos não usados.");
    }

    private void startChat() {
        System.out.println("\n=========== Chat com SolarIA 🤖 ===========");
        System.out.println("☀️ Olá, sou a SolarIA, assistente virtual da Unisolar! 🌱 Como posso ajudar? 💡");
        System.out.println("Digite 'sair' para voltar ao menu principal");

        while (true) {
            System.out.print("\nVocê: ");
            String question = scanner.nextLine();

            if (question.equalsIgnoreCase("sair")) {
                break;
            }
            String response = chatbotService.answerQuestion(question);
            System.out.println("\nSolarIA: " + response);
        }
    }
}
