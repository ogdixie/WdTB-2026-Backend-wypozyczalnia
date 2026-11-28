package pl.wsb.rules.lab04;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Historia Git projektu zespołu dla reguły R4.05: scalone Pull Requesty od początku LAB 04.
 * Czyta repozytorium poleceniem {@code git} w katalogu projektu.
 *
 * <p>W GitHub Actions etap „Ocena 5” pobiera pełną historię ({@code fetch-depth: 0}); stary plik workflow (przed Sync fork)
 * pobiera tylko ostatni commit i wtedy reguła prosi o Sync fork.
 */
final class TeamHistory {

    /** Od tej chwili liczą się scalenia (dzień przed zajęciami LAB 04). */
    static final String LAB04_START = "2026-11-27T00:00:00";
    /** Domyślny komunikat GitHuba przy „Create a merge commit”. */
    static final String PULL_REQUEST_MERGE = "Merge pull request #";

    private final Path dir;
    private final Optional<String> problem;

    private TeamHistory(Path dir, Optional<String> problem) {
        this.dir = dir;
        this.problem = problem;
    }

    static TeamHistory of(Path dir) {
        Optional<String> inside = run(dir, "rev-parse", "--is-inside-work-tree");
        if (inside.isEmpty() || !inside.get().strip().equals("true")) {
            return new TeamHistory(dir, Optional.of("Brak historii Git w katalogu projektu (" + dir + "). Reguła czyta historię commitów, "
                    + "więc uruchamiajcie ją w sklonowanym repozytorium (IntelliJ: Get from VCS) albo w GitHub Actions."));
        }
        Optional<String> shallow = run(dir, "rev-parse", "--is-shallow-repository");
        if (shallow.isPresent() && shallow.get().strip().equals("true")) {
            return new TeamHistory(dir, Optional.of("Historia repozytorium jest niepełna (pobrany tylko ostatni commit). Zróbcie Sync fork: "
                    + "aktualny plik workflow pobiera w etapie „Ocena 5” pełną historię (fetch-depth: 0)."));
        }
        return new TeamHistory(dir, Optional.empty());
    }

    Optional<String> problem() {
        return problem;
    }

    /** Tytuły scaleń Pull Requestów (commity „Merge pull request #…”) w historii HEAD od początku LAB 04. */
    List<String> mergedPullRequests() {
        List<String> merges = new ArrayList<>();
        run(dir, "log", "--merges", "--since=" + LAB04_START, "--format=%s", "HEAD").ifPresent(out -> out.lines()
                .filter(line -> line.startsWith(PULL_REQUEST_MERGE))
                .forEach(merges::add));
        return merges;
    }

    private static Optional<String> run(Path dir, String... args) {
        List<String> command = new ArrayList<>();
        command.add("git");
        command.addAll(List.of(args));
        try {
            Process process = new ProcessBuilder(command).directory(dir.toFile()).redirectErrorStream(false).start();
            process.getOutputStream().close();
            String out = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            if (!process.waitFor(30, TimeUnit.SECONDS) || process.exitValue() != 0) {
                return Optional.empty();
            }
            return Optional.of(out);
        } catch (IOException e) {
            return Optional.empty();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        }
    }
}
