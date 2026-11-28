package pl.wsb.rules.lab04;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pl.wsb.rules.core.Project;
import pl.wsb.rules.core.SinceLab;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Autotest reguł LAB 04 (nie dotyczy projektu zespołu): reguły przechodzą dla przykładu kawiarni
 * ({@code pl.wsb.rules.examples.cafe}: repozytoria i wzorcowe testy pytań)
 * i wskazują błędy w celowo złym modelu ({@code pl.wsb.rules.examples.broken}). Sprawdza też pliki pytań zespołów.
 */
@SinceLab(4)
@DisplayName("Autotest reguł LAB 04 · przykład kawiarni")
class Lab04RulesSelfTest {

    private static final String CAFE = "pl.wsb.rules.examples.cafe";
    private static final String BROKEN = "pl.wsb.rules.examples.broken";
    private static final String NOTHING = "pl.wsb.rules.examples.nothing";

    @Test
    @DisplayName("Przykład kawiarni ma co najmniej 4 repozytoria i odpowiada na pytania P1–P3 (R4.01–R4.04)")
    void cafeSatisfiesAllRules() {
        Lab04Checks.Code cafe = Lab04Checks.Code.of(CAFE);
        assertThatCode(() -> {
            Lab04Checks.enoughRepositories(cafe, Lab04Checks.MIN_REPOSITORIES);
            for (Lab04Checks.Kind kind : Lab04Checks.Kind.values()) {
                Lab04Checks.answersQuestion(cafe, kind, Optional.empty());
            }
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Każda metoda przykładu spełnia warunek swojego rodzaju pytania i tylko jego")
    void kindsAreDistinct() {
        Lab04Checks.Code cafe = Lab04Checks.Code.of(CAFE);
        java.util.Map<Lab04Checks.Kind, String> expected = java.util.Map.of(
                Lab04Checks.Kind.P1, "findByGuest_FullNameOrderByStartsAtDesc",
                Lab04Checks.Kind.P2, "guestsOfRoom",
                Lab04Checks.Kind.P3, "tableLoad");
        for (Lab04Checks.Kind kind : Lab04Checks.Kind.values()) {
            List<String> matching = cafe.repositories().stream()
                    .flatMap(r -> r.getMethods().stream())
                    .filter(m -> Lab04Checks.matches(cafe, kind, m))
                    .map(m -> m.getName())
                    .toList();
            assertThat(matching).as(kind.name()).contains(expected.get(kind));
            // P1 spełniają też zapytania przez relację z LAB 03; pozostałe rodzaje mają w przykładzie po jednej metodzie
            if (kind != Lab04Checks.Kind.P1) {
                assertThat(matching).as(kind.name()).hasSize(1);
            }
        }
    }

    @Test
    @DisplayName("Celowo błędny model jest wskazywany z czytelnym komunikatem")
    void brokenModelIsRejected() {
        Lab04Checks.Code broken = Lab04Checks.Code.of(BROKEN);
        Lab04Checks.Code nothing = Lab04Checks.Code.of(NOTHING);
        assertThatThrownBy(() -> Lab04Checks.enoughRepositories(broken, Lab04Checks.MIN_REPOSITORIES))
                .hasMessageContaining("OrderRepository").hasMessageContaining("oczekiwany");
        assertThatThrownBy(() -> Lab04Checks.enoughRepositories(nothing, Lab04Checks.MIN_REPOSITORIES))
                .hasMessageContaining("co najmniej 4 repozytoria");
        // 5 repozytoriów kawiarni nie wystarcza na wymaganie 6: liczy się minimum, nie „każda encja”
        assertThatThrownBy(() -> Lab04Checks.enoughRepositories(Lab04Checks.Code.of(CAFE), 6))
                .hasMessageContaining("co najmniej 6");
        assertThatThrownBy(() -> Lab04Checks.answersQuestion(nothing, Lab04Checks.Kind.P2, Optional.empty()))
                .hasMessageContaining("Brak testu pytania P2");
        assertThatThrownBy(() -> Lab04Checks.answersQuestion(broken, Lab04Checks.Kind.P1, Optional.of("Zamówienia klienta")))
                .hasMessageContaining("Brak testu pytania P1").hasMessageContaining("„Zamówienia klienta”");
        assertThatThrownBy(() -> Lab04Checks.answersQuestion(broken, Lab04Checks.Kind.P2, Optional.empty()))
                .hasMessageContaining("OrderRepository.findByStatus").hasMessageContaining("JOIN");
        assertThatThrownBy(() -> Lab04Checks.answersQuestion(broken, Lab04Checks.Kind.P3, Optional.empty()))
                .hasMessageContaining("countPerStatus").hasMessageContaining("projekcji");
    }

    @Test
    @DisplayName("R4.05: scalenia Pull Requestów odczytane z historii Git; zwykłe scalenie przy pull się nie liczy")
    void pullRequestFromGitHistory(@TempDir Path dir) throws IOException, InterruptedException {
        git(dir, "init", "-q", "-b", "main");
        commitFile(dir, "Ola", "ola@example.com", "P1GuestQueriesTest.java");
        // zwykłe scalenie przy pull (bez Pull Requesta) się nie liczy
        git(dir, "checkout", "-q", "-b", "lab4-ola");
        commitFile(dir, "Ola", "ola@example.com", "notes.txt");
        git(dir, "checkout", "-q", "main");
        git(dir, "merge", "--no-ff", "-q", "-m", "Merge remote-tracking branch 'origin/main'", "lab4-ola");
        assertThat(TeamHistory.of(dir).mergedPullRequests()).isEmpty();
        assertThatThrownBy(() -> Lab04Checks.pullRequestMerged(TeamHistory.of(dir)))
                .hasMessageContaining("żadna zmiana nie trafiła do main przez Pull Request");

        git(dir, "checkout", "-q", "-b", "lab4-kuba");
        commitFile(dir, "Kuba", "kuba@example.com", "P2GuestQueriesTest.java");
        git(dir, "checkout", "-q", "main");
        git(dir, "merge", "--no-ff", "-q", "-m", "Merge pull request #1 from kuba/lab4-kuba", "lab4-kuba");

        TeamHistory history = TeamHistory.of(dir);
        assertThat(history.problem()).isEmpty();
        assertThat(history.mergedPullRequests()).containsExactly("Merge pull request #1 from kuba/lab4-kuba");
        assertThatCode(() -> Lab04Checks.pullRequestMerged(history)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("R4.05: katalog bez Git zgłasza brak historii")
    void missingHistory(@TempDir Path dir) {
        assertThat(TeamHistory.of(dir).problem()).hasValueSatisfying(p -> assertThat(p).contains("Brak historii Git"));
        assertThatThrownBy(() -> Lab04Checks.pullRequestMerged(TeamHistory.of(dir))).hasMessageContaining("Brak historii Git");
    }

    private static void commitFile(Path dir, String name, String email, String file) throws IOException, InterruptedException {
        Files.writeString(dir.resolve(file), file + "\n");
        git(dir, "add", file);
        git(dir, "-c", "user.name=" + name, "-c", "user.email=" + email, "commit", "-q", "-m", "dodaj " + file);
    }

    private static void git(Path dir, String... args) throws IOException, InterruptedException {
        List<String> command = new java.util.ArrayList<>(List.of("git", "-c", "user.name=Prowadzący",
                "-c", "user.email=prowadzacy@example.com", "-c", "commit.gpgsign=false", "-c", "init.defaultBranch=main"));
        command.addAll(List.of(args));
        ProcessBuilder builder = new ProcessBuilder(command).directory(dir.toFile()).redirectErrorStream(true);
        // stała data: scalenia mieszczą się w okresie LAB 04 niezależnie od dnia uruchomienia
        builder.environment().put("GIT_AUTHOR_DATE", "2026-11-28T10:00:00");
        builder.environment().put("GIT_COMMITTER_DATE", "2026-11-28T10:00:00");
        builder.environment().put("GIT_CONFIG_NOSYSTEM", "1");
        builder.environment().put("HOME", dir.toString());
        Process process = builder.start();
        String out = new String(process.getInputStream().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        assertThat(process.waitFor()).withFailMessage("git %s: %s", String.join(" ", args), out).isZero();
    }

    @Test
    @DisplayName("Nazwa zapytania pochodnego: warunek zaczyna się od pola relacji")
    void derivedQueryNames() {
        List<String> relations = List.of("guest", "table");
        assertThat(Lab04Checks.relationUsedBy("findByGuest_FullNameOrderByStartsAtDesc", relations)).contains("guest");
        assertThat(Lab04Checks.relationUsedBy("countByTable_Room", relations)).contains("table");
        assertThat(Lab04Checks.relationUsedBy("findByStatusOrderByStartsAtAsc", relations)).isEmpty();
        assertThat(Lab04Checks.relationUsedBy("tableLoad", relations)).isEmpty();
    }

    @Test
    @DisplayName("Plik pytań: tabela pytań, pakiet i adres repozytorium z .git/config")
    void questionFileFormat() {
        String markdown = """
                # Pytania inwestora · Kawiarnia

                Repozytorium: ola/WdTB-2026-Backend-kawiarnia · Pakiet: `pl.wsb.kawiarnia`

                | Id | Ocena | Pytanie inwestora | Tabele |
                |---|---|---|---|
                | P1 | 3 | Jakie rezerwacje ma gość? Od najnowszej. | reservation, guest |
                | P2 | 3 | Którzy goście \\| w sali? | guest, reservation |
                | P3 | 4 | ReservationRepository | Ile rezerwacji ma stolik? | reservation |
                """;
        InvestorQuestions q = InvestorQuestions.parse("x.md", markdown);
        assertThat(q.question("P1")).contains("Jakie rezerwacje ma gość? Od najnowszej.");
        assertThat(q.question("P2")).contains("Którzy goście | w sali?");
        assertThat(q.question("P3")).contains("Ile rezerwacji ma stolik?");
        assertThat(q.question("P4")).isEmpty();
        assertThat(InvestorQuestions.packageOf(markdown)).contains("pl.wsb.kawiarnia");
        assertThat(InvestorQuestions.fileName("Ola/WdTB-2026-Backend-Kawiarnia")).isEqualTo("ola__wdtb-2026-backend-kawiarnia.md");
        assertThat(InvestorQuestions.originOf("""
                [core]
                \tbare = false
                [remote "origin"]
                \turl = https://github.com/Ola/WdTB-2026-Backend-kawiarnia.git
                \tfetch = +refs/heads/*:refs/remotes/origin/*
                """)).contains("Ola/WdTB-2026-Backend-kawiarnia");
        assertThat(InvestorQuestions.originOf("[remote \"origin\"]\n\turl = git@github.com:ola/projekt.git\n")).contains("ola/projekt");
    }

    @Test
    @DisplayName("Każdy plik pytań zespołu ma pakiet i komplet pytań P1–P3")
    void everyTeamFileIsComplete() throws IOException {
        Path dir = Project.file(InvestorQuestions.DIR);
        assertThat(dir).isDirectory();
        assertThat(dir.resolve("README.md")).exists();
        List<Path> files = InvestorQuestions.teamFiles(dir);
        assertThat(files).isNotEmpty();
        for (Path file : files) {
            String markdown = Files.readString(file);
            InvestorQuestions q = InvestorQuestions.parse(file.getFileName().toString(), markdown);
            assertThat(q.all().keySet()).as(file.getFileName().toString()).containsExactly("P1", "P2", "P3");
            assertThat(InvestorQuestions.packageOf(markdown)).as(file.getFileName().toString()).isPresent();
            assertThat(file.getParent().getFileName().toString()).as(file.getFileName().toString()).startsWith("grupa-");
        }
    }
}
