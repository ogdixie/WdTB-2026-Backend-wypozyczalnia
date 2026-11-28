package pl.wsb.rules.lab04;

import pl.wsb.rules.core.Project;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Zestaw pytań inwestora zespołu (LAB 04). Każdy zespół ma własny plik w podkatalogu swojej grupy w {@link #DIR}, nazwany od repozytorium
 * na GitHubie: {@code <właściciel>__<repozytorium>.md} małymi literami. Katalog leży wśród reguł, więc GitHub Actions
 * pobiera go z szablonu razem z regułami, a lokalnie pojawia się po Sync fork.
 *
 * <p>Repozytorium zespołu: w GitHub Actions ze zmiennej {@code GITHUB_REPOSITORY}, lokalnie z adresu {@code origin}
 * w {@code .git/config}. Gdy pliku nie ma, zestaw jest szukany po pakiecie głównym projektu (linia „Pakiet:” w pliku).
 *
 * <p>Reguły nie sprawdzają treści odpowiedzi, tylko rodzaj zapytania; treść pytania pojawia się w komunikatach.
 */
final class InvestorQuestions {

    static final String DIR = "src/test/java/pl/wsb/rules/lab04/pytania";

    /** Wiersz tabeli pytań: {@code | P1 | 3 | CarRepository | treść | tabele |} (kolumna repozytorium opcjonalna). */
    private static final Pattern ROW = Pattern.compile("^\\|\\s*(P[1-5])\\s*\\|\\s*\\d\\s*\\|(?:\\s*\\w+\\s*\\|)?\\s*((?:\\\\\\||[^|])+?)\\s*\\|\\s*((?:\\\\\\||[^|])*?)\\s*\\|\\s*$");
    private static final Pattern PACKAGE = Pattern.compile("Pakiet:\\s*`?([\\w.]+)`?");
    private static final Pattern ORIGIN = Pattern.compile("github\\.com[:/]([^/\\s]+)/([^/\\s]+?)(?:\\.git)?/?\\s*$", Pattern.MULTILINE);

    private final String source;
    private final Map<String, String> questions;

    private InvestorQuestions(String source, Map<String, String> questions) {
        this.source = source;
        this.questions = questions;
    }

    /** Zestaw zespołu albo pusty (wtedy komunikaty reguł podają tylko rodzaj zapytania). */
    static Optional<InvestorQuestions> forProject() {
        Path dir = Project.file(DIR);
        if (!Files.isDirectory(dir)) {
            return Optional.empty();
        }
        List<Path> files = teamFiles(dir);
        Optional<String> repository = repository();
        if (repository.isPresent()) {
            String name = fileName(repository.get());
            Optional<Path> file = files.stream().filter(f -> f.getFileName().toString().equals(name)).findFirst();
            if (file.isPresent()) {
                return Optional.of(parse(name, read(file.get())));
            }
        }
        String rootPackage;
        try {
            rootPackage = Project.rootPackage();
        } catch (AssertionError | RuntimeException e) {
            return Optional.empty();
        }
        List<Path> matching = files.stream()
                .filter(f -> packageOf(read(f)).filter(rootPackage::equals).isPresent())
                .toList();
        return matching.size() == 1
                ? Optional.of(parse(matching.get(0).getFileName().toString(), read(matching.get(0))))
                : Optional.empty();
    }

    /** Pliki zespołów we wszystkich podkatalogach grup ({@code grupa-1/…}), bez spisu {@code README.md}. */
    static List<Path> teamFiles(Path dir) {
        try (Stream<Path> files = Files.walk(dir)) {
            return files.filter(Files::isRegularFile)
                    .filter(f -> f.toString().endsWith(".md"))
                    .filter(f -> !f.getFileName().toString().equalsIgnoreCase("README.md"))
                    .sorted()
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** {@code właściciel/repozytorium} → nazwa pliku zestawu. */
    static String fileName(String repository) {
        return repository.toLowerCase(Locale.ROOT).replace("/", "__") + ".md";
    }

    static InvestorQuestions parse(String source, String markdown) {
        Map<String, String> questions = new LinkedHashMap<>();
        for (String line : markdown.split("\\R")) {
            Matcher m = ROW.matcher(line);
            if (m.matches()) {
                questions.put(m.group(1), m.group(2).replace("\\|", "|"));
            }
        }
        return new InvestorQuestions(source, questions);
    }

    static Optional<String> packageOf(String markdown) {
        Matcher m = PACKAGE.matcher(markdown);
        return m.find() ? Optional.of(m.group(1)) : Optional.empty();
    }

    /** Właściciel i nazwa repozytorium z adresu w .git/config, np. {@code url = https://github.com/ola/WdTB-2026-Backend-x.git}. */
    static Optional<String> originOf(String gitConfig) {
        int remote = gitConfig.indexOf("[remote \"origin\"]");
        String section = remote < 0 ? gitConfig : gitConfig.substring(remote);
        Matcher m = ORIGIN.matcher(section);
        return m.find() ? Optional.of(m.group(1) + "/" + m.group(2)) : Optional.empty();
    }

    Optional<String> question(String id) {
        return Optional.ofNullable(questions.get(id));
    }

    Map<String, String> all() {
        return questions;
    }

    String source() {
        return source;
    }

    private static Optional<String> repository() {
        String env = System.getenv("GITHUB_REPOSITORY");
        if (env != null && !env.isBlank()) {
            return Optional.of(env.trim());
        }
        Path config = Project.file(".git/config");
        return Files.exists(config) ? originOf(read(config)) : Optional.empty();
    }

    private static String read(Path file) {
        try {
            return Files.readString(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
