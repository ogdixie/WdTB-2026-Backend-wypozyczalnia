package pl.wsb.rules.lab04;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.wsb.rules.core.SinceLab;

import java.util.Optional;

/**
 * Reguły LAB 04 na ocenę 3 (minimum): co najmniej 4 repozytoria oraz odpowiedzi na pytania inwestora P1 i P2 jako metody repozytoriów z testami
 * {@code @DataJpaTest}. Pytania zespołu są w {@link InvestorQuestions#DIR}. Pytanie P3 (ocena 4) w {@link Lab04GradeFourRulesTest},
 * scalony Pull Request (ocena 5) w {@link Lab04GradeFiveRulesTest}.
 */
@SinceLab(4)
@DisplayName("LAB 04 · Repozytoria i pierwsze pytania inwestora (ocena 3)")
class Lab04ProjectRulesTest {

    @Test
    @DisplayName("R4.01 Co najmniej 4 repozytoria, każde dla innej encji, w pakiecie <moduł>.repository tego samego modułu")
    void enoughRepositories() {
        Lab04Checks.enoughRepositories(Lab04Checks.Code.ofProject(), Lab04Checks.MIN_REPOSITORIES);
    }

    @Test
    @DisplayName("R4.02 Pytanie P1: test @DataJpaTest wywołuje zapytanie pochodne przez relację")
    void questionOne() {
        Lab04Checks.answersQuestion(Lab04Checks.Code.ofProject(), Lab04Checks.Kind.P1, question("P1"));
    }

    @Test
    @DisplayName("R4.03 Pytanie P2: test @DataJpaTest wywołuje metodę z @Query (JPQL) z JOIN")
    void questionTwo() {
        Lab04Checks.answersQuestion(Lab04Checks.Code.ofProject(), Lab04Checks.Kind.P2, question("P2"));
    }

    static Optional<String> question(String id) {
        return InvestorQuestions.forProject().flatMap(q -> q.question(id));
    }
}
