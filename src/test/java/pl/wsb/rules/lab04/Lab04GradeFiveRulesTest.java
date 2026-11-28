package pl.wsb.rules.lab04;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pl.wsb.rules.core.Project;
import pl.wsb.rules.core.SinceLab;

/**
 * Reguły LAB 04 na ocenę 5: zmiana scalona do main przez Pull Request (czyta historię Git).
 * W GitHub Actions działają w etapie „Ocena 5”, uruchamianym po zielonym etapie „Ocena 4”; ten etap pobiera pełną historię.
 */
@SinceLab(4)
@Tag("ocena5")
@DisplayName("LAB 04 · Pull Request (ocena 5)")
class Lab04GradeFiveRulesTest {

    @Test
    @DisplayName("R4.05 Co najmniej jeden Pull Request scalony do main od początku LAB 04")
    void pullRequestMerged() {
        Lab04Checks.pullRequestMerged(TeamHistory.of(Project.projectDir()));
    }
}
