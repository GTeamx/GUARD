package guard.exempt;

import guard.data.GuardPlayer;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.function.Function;

@RequiredArgsConstructor
public class Exempt {

    private final GuardPlayer gp;

    public boolean isExempt(final ExemptType exemptType) {
        return exemptType.getException().apply(gp);
    }

    public boolean isExempt(final ExemptType... exemptTypes) {
        return Arrays.stream(exemptTypes).anyMatch(this::isExempt);
    }

    public boolean isExempt(final Function<GuardPlayer, Boolean> exception) {
        return exception.apply(gp);
    }
}
