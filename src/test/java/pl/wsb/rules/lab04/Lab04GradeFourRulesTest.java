package pl.wsb.rules.lab04;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pl.wsb.rules.core.SinceLab;

import static pl.wsb.rules.lab04.Lab04ProjectRulesTest.question;

/**
 * Reguły LAB 04 na ocenę 4: zestawienie GROUP BY (pytanie P3).
 * W GitHub Actions działają w etapie „Ocena 4”, uruchamianym po zielonym etapie „Ocena 3”.
 */
@SinceLab(4)
@Tag("ocena4")
@DisplayName("LAB 04 · zestawienie (ocena 4)")
class Lab04GradeFourRulesTest {

    @Test
    @DisplayName("R4.04 Pytanie P3: test @DataJpaTest wywołuje zestawienie GROUP BY zwracające projekcję")
    void questionThree() {
        Lab04Checks.answersQuestion(Lab04Checks.Code.ofProject(), Lab04Checks.Kind.P3, question("P3"));
    }
}
