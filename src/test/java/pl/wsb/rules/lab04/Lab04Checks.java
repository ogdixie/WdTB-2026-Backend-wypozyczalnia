package pl.wsb.rules.lab04;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaCodeUnit;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaParameterizedType;
import com.tngtech.archunit.core.domain.JavaType;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.DisplayName;
import org.springframework.data.jpa.repository.Query;
import pl.wsb.rules.core.Project;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Sprawdzenia reguł LAB 04 (pytania inwestora jako zapytania repozytoriów, scalenie przez Pull Request). Działają na klasach projektu
 * razem z testami zespołu, bo pytanie jest zaliczone dopiero wtedy, gdy test {@code @DataJpaTest} wywołuje metodę repozytorium
 * odpowiedniego rodzaju. Ten sam kod sprawdza przykład kawiarni w autoteście ({@link Lab04RulesSelfTest}).
 */
final class Lab04Checks {

    static final String SPRING_DATA_REPOSITORY = "org.springframework.data.repository.Repository";
    static final String DATA_JPA_TEST = "org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest";
    static final List<String> TEST_ANNOTATIONS = List.of("org.junit.jupiter.api.Test",
            "org.junit.jupiter.params.ParameterizedTest", "org.junit.jupiter.api.RepeatedTest");
    static final List<String> RELATIONS = List.of("jakarta.persistence.ManyToOne", "jakarta.persistence.OneToOne",
            "jakarta.persistence.OneToMany", "jakarta.persistence.ManyToMany");

    private static final Pattern DERIVED_QUERY =
            Pattern.compile("^(find|read|get|query|search|stream|count|exists)(\\w*?)By([A-Z]\\w*)$");
    private static final Pattern JOIN = Pattern.compile("(?i)\\bjoin\\b");
    private static final Pattern GROUP_BY = Pattern.compile("(?i)\\bgroup\\s+by\\b");

    private Lab04Checks() {
    }

    /** Kod do sprawdzenia: klasy produkcyjne i testy zespołu w jednym imporcie, żeby było widać wywołania test → repozytorium. */
    record Code(JavaClasses classes, String rootPackage, Predicate<JavaClass> isTest) {

        /** Projekt zespołu: pakiet główny z src/main i src/test, bez reguł; test to klasa z katalogu test-classes. */
        static Code ofProject() {
            JavaClasses classes = new ClassFileImporter()
                    .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_JARS)
                    .withImportOption(location -> !location.contains("/pl/wsb/rules/"))
                    .importPackages(Project.rootPackage());
            return new Code(classes, Project.rootPackage(), c -> c.getSource()
                    .map(s -> s.getUri().toString().contains("/test-classes/"))
                    .orElse(false));
        }

        /** Przykład w testach reguł: wszystko leży w test-classes, więc test to klasa z metodą @Test. */
        static Code of(String rootPackage) {
            return new Code(new ClassFileImporter().importPackages(rootPackage), rootPackage, Lab04Checks::hasTestMethods);
        }

        List<JavaClass> production() {
            return classes.stream().filter(isTest.negate()).sorted(Comparator.comparing(JavaClass::getName)).toList();
        }

        List<JavaClass> tests() {
            return classes.stream().filter(isTest).sorted(Comparator.comparing(JavaClass::getName)).toList();
        }

        boolean isRepository(JavaClass c) {
            return c.isInterface() && c.isAssignableTo(SPRING_DATA_REPOSITORY) && c.getPackageName().startsWith(rootPackage + ".");
        }

        List<JavaClass> repositories() {
            return production().stream().filter(this::isRepository).toList();
        }
    }

    /** Rodzaje pytań inwestora: identyfikator, reguła, krótki opis i przykład metody repozytorium. */
    enum Kind {
        P1("R4.02", "zapytanie pochodne przez relację",
                "metoda bez @Query, której nazwa prowadzi przez pole relacji, np. findByGuest_FullNameOrderByStartsAtDesc(String fullName)"),
        P2("R4.03", "@Query (JPQL) z JOIN",
                "metoda z @Query(\"select distinct g from Reservation r join r.guest g join r.table t where t.room = :room …\"), "
                        + "bez GROUP BY (to rodzaj P3)"),
        P3("R4.04", "zestawienie GROUP BY do projekcji",
                "metoda z @Query(\"select t.number as tableNumber, count(r) as reservations … group by t.number\") zwracająca "
                        + "List<TableLoad>, gdzie TableLoad to interfejs z getterami albo rekord (nie encja, nie Object[])");

        final String rule;
        final String title;
        final String example;

        Kind(String rule, String title, String example) {
            this.rule = rule;
            this.title = title;
            this.example = example;
        }
    }

    // ---------------------------------------------------------------- repozytoria

    /** Na ocenę 3: tyle repozytoriów (każde dla innej encji), ile tabel wymagało LAB 02–03 na zaliczenie. */
    static final int MIN_REPOSITORIES = 4;

    /**
     * R4.01 (ocena 3): co najmniej {@code minimum} własnych encji (bez gotowego UserAccount) ma repozytorium Spring Data
     * w pakiecie {@code <moduł>.repository} tego samego modułu co encja. Więcej encji nie wymaga więcej repozytoriów.
     */
    static void enoughRepositories(Code code, int minimum) {
        List<JavaClass> entities = code.production().stream()
                .filter(c -> c.isAnnotatedWith("jakarta.persistence.Entity"))
                .filter(c -> !c.getSimpleName().equals("UserAccount"))
                .toList();
        List<String> covered = new ArrayList<>();
        List<String> without = new ArrayList<>();
        List<String> misplaced = new ArrayList<>();
        for (JavaClass entity : entities) {
            String expected = Project.moduleOf(entity, code.rootPackage()).map(m -> code.rootPackage() + "." + m + ".repository").orElse("");
            List<JavaClass> repositories = code.repositories().stream()
                    .filter(r -> managedEntity(r).filter(entity::equals).isPresent())
                    .toList();
            if (repositories.isEmpty()) {
                without.add(entity.getSimpleName());
            } else if (repositories.stream().anyMatch(r -> r.getPackageName().equals(expected))) {
                covered.add(entity.getSimpleName());
            } else {
                repositories.forEach(r -> misplaced.add(r.getSimpleName() + " (" + r.getPackageName() + ", oczekiwany " + expected + ")"));
            }
        }
        assertThat(misplaced)
                .withFailMessage("Repozytorium leży w pakiecie repository tego modułu co jego encja (encja w …booking.entity, "
                        + "repozytorium w …booking.repository). Przenieście (IntelliJ: Refactor → Move): %s", misplaced)
                .isEmpty();
        assertThat(covered)
                .withFailMessage("Wymagane co najmniej %d repozytoria, każde dla innej Waszej encji (bez UserAccount); są dla: %s. "
                                + "Encje bez repozytorium: %s. Wzór: public interface CafeTableRepository extends JpaRepository<CafeTable, Long> { } "
                                + "w pakiecie <moduł>.repository (bez własnych metod też wystarczy).",
                        minimum, covered, without)
                .hasSizeGreaterThanOrEqualTo(minimum);
    }

    /** Encja z pierwszego argumentu typowego {@code JpaRepository<Encja, Long>}. */
    static Optional<JavaClass> managedEntity(JavaClass repository) {
        for (JavaType type : repository.getInterfaces()) {
            if (type instanceof JavaParameterizedType parameterized
                    && parameterized.toErasure().isAssignableTo(SPRING_DATA_REPOSITORY)
                    && !parameterized.getActualTypeArguments().isEmpty()) {
                return Optional.of(parameterized.getActualTypeArguments().get(0).toErasure());
            }
        }
        return Optional.empty();
    }

    // ---------------------------------------------------------------- pytania inwestora

    /** R4.02–R4.04: test @DataJpaTest „Pn …” wywołuje metodę repozytorium wymaganego rodzaju. */
    static void answersQuestion(Code code, Kind kind, Optional<String> question) {
        String asked = question.map(q -> "Sugerowane pytanie " + kind.name() + " (albo Wasze własne tego rodzaju): „" + q + "”. ").orElse("");
        List<JavaMethod> tests = questionTests(code, kind.name());
        assertThat(tests)
                .withFailMessage("%sBrak testu pytania %s (%s). W klasie testowej z @DataJpaTest (src/test/java, pakiet repozytorium) "
                                + "dopiszcie metodę @Test z @DisplayName(\"%s …\"), która wywołuje metodę repozytorium i sprawdza wynik "
                                + "na danych z data.sql. Metoda repozytorium: %s.",
                        asked, kind.name(), kind.title, kind.name(), kind.example)
                .isNotEmpty();
        List<String> called = new ArrayList<>();
        List<String> matching = new ArrayList<>();
        for (JavaMethod test : tests) {
            for (JavaMethod m : repositoryMethodsCalledBy(code, test)) {
                String where = m.getOwner().getSimpleName() + "." + m.getName();
                called.add(where);
                if (matches(code, kind, m)) {
                    matching.add(where);
                }
            }
        }
        assertThat(called)
                .withFailMessage("%sTest %s nie wywołuje żadnej metody Waszego repozytorium. Test wywołuje metodę repozytorium "
                        + "bezpośrednio, np. reservations.findBy…(…), i sprawdza wynik.", asked, names(tests))
                .isNotEmpty();
        assertThat(matching)
                .withFailMessage("%sTest %s wywołuje %s, ale żadna z tych metod nie jest rodzaju „%s”. Wymagana: %s.",
                        asked, names(tests), called, kind.title, kind.example)
                .isNotEmpty();
    }

    static boolean matches(Code code, Kind kind, JavaMethod m) {
        Optional<Query> query = m.tryGetAnnotationOfType(Query.class);
        String jpql = query.map(Query::value).orElse("");
        return switch (kind) {
            case P1 -> query.isEmpty() && relationUsedBy(m.getName(), relationsOf(m.getOwner())).isPresent();
            // P2 to osobne pytanie: zestawienie (P3) nie liczy się jako P2
            case P2 -> query.isPresent() && !query.get().nativeQuery() && JOIN.matcher(jpql).find()
                    && !GROUP_BY.matcher(jpql).find();
            case P3 -> query.isPresent() && GROUP_BY.matcher(jpql).find() && returnsProjection(m);
        };
    }

    /** Testy pytania: metody @Test w klasach z @DataJpaTest, z @DisplayName „P1 …” albo nazwą {@code p1…}. */
    static List<JavaMethod> questionTests(Code code, String id) {
        Pattern display = Pattern.compile("^\\s*" + id + "\\b");
        Pattern name = Pattern.compile("^" + id.toLowerCase() + "($|_|[A-Z])");
        return code.tests().stream()
                .filter(c -> c.isAnnotatedWith(DATA_JPA_TEST))
                .flatMap(c -> c.getMethods().stream())
                .filter(m -> TEST_ANNOTATIONS.stream().anyMatch(m::isAnnotatedWith))
                .filter(m -> m.tryGetAnnotationOfType(DisplayName.class).map(d -> display.matcher(d.value()).find()).orElse(false)
                        || name.matcher(m.getName()).find())
                .sorted(Comparator.comparing(JavaMethod::getFullName))
                .toList();
    }

    /** Metody repozytoriów projektu wywołane w teście (także w lambdach w tej metodzie), zadeklarowane w repozytorium zespołu. */
    static List<JavaMethod> repositoryMethodsCalledBy(Code code, JavaCodeUnit test) {
        return test.getMethodCallsFromSelf().stream()
                .filter(call -> code.isRepository(call.getTargetOwner()))
                .flatMap(call -> call.getTarget().resolveMember().stream())
                .filter(m -> code.isRepository(m.getOwner()))
                .distinct()
                .sorted(Comparator.comparing(JavaMethod::getFullName))
                .toList();
    }

    /** Wynik zapytania to lista projekcji: element listy nie jest encją, Object, tablicą ani typem z JDK. */
    static boolean returnsProjection(JavaMethod m) {
        JavaType type = m.getReturnType();
        if (!(type instanceof JavaParameterizedType parameterized) || parameterized.getActualTypeArguments().isEmpty()) {
            return false;
        }
        JavaClass element = parameterized.getActualTypeArguments().get(0).toErasure();
        return !element.isArray()
                && !element.getName().equals(Object.class.getName())
                && !element.isAnnotatedWith("jakarta.persistence.Entity")
                && !element.getPackageName().startsWith("java.");
    }

    /** Pola relacji encji zarządzanej przez repozytorium ({@code JpaRepository<Encja, Long>}). */
    static List<String> relationsOf(JavaClass repository) {
        return managedEntity(repository).map(entity -> entity.getFields().stream()
                        .filter(f -> RELATIONS.stream().anyMatch(f::isAnnotatedWith))
                        .map(JavaField::getName)
                        .toList())
                .orElse(List.of());
    }

    /** Nazwa pola relacji, od którego zaczyna się warunek w nazwie metody, np. {@code findByGuest_FullName} → {@code guest}. */
    static Optional<String> relationUsedBy(String methodName, List<String> relations) {
        Matcher m = DERIVED_QUERY.matcher(methodName);
        if (!m.matches()) {
            return Optional.empty();
        }
        String condition = m.group(3);
        return relations.stream()
                .filter(r -> condition.startsWith(Character.toUpperCase(r.charAt(0)) + r.substring(1)))
                .findFirst();
    }

    // ---------------------------------------------------------------- Pull Request

    /**
     * R4.05 (ocena 5): co najmniej jedna zmiana trafiła do main przez Pull Request scalony przyciskiem „Create a merge commit”.
     * Nie zależy od liczby osób w zespole: gałąź, PR i scalenie da się przećwiczyć także w pojedynkę.
     */
    static void pullRequestMerged(TeamHistory history) {
        assertThat(history.problem()).withFailMessage(history.problem().orElse("")).isEmpty();
        assertThat(history.mergedPullRequests())
                .withFailMessage("Od początku LAB 04 żadna zmiana nie trafiła do main przez Pull Request. Zróbcie swoją część na gałęzi "
                        + "(np. feature/p1-samochody), wypchnijcie ją, na GitHubie otwórzcie Pull Request do main w swoim forku i scalcie go przyciskiem "
                        + "„Create a merge commit” (nie Squash i nie Rebase: reguła szuka commita „Merge pull request #…”).")
                .isNotEmpty();
    }

    // ---------------------------------------------------------------- pomocnicze

    static boolean hasTestMethods(JavaClass c) {
        return c.getMethods().stream().anyMatch(m -> TEST_ANNOTATIONS.stream().anyMatch(m::isAnnotatedWith));
    }

    private static List<String> names(List<JavaMethod> methods) {
        return methods.stream().map(m -> m.getOwner().getSimpleName() + "." + m.getName()).toList();
    }
}
