package logic;

import java.util.List;

import static logic.Brush.*;

class Filter {

    static Brush digitStatus(Brush[] digits) {
        Brush result = digits[0];
        for (int i = 1; i < 4; i++) {
            if (digits[i] == undefined) result = undefined;
            if (digits[i] == matchCow) result = matchCow;
            if (digits[i] == matchBull) result = matchBull;
        }
        return result;
    }

    static void filter(List<Shoot> shoots, Brush[][] bulls) {
        if (shoots.size() > 0)
            for (int i = 0; i < 3; i++) {
                bullsEqualsCows(shoots, bulls);
                hideNonPlayed(shoots, bulls);
                markPlayed(shoots, bulls);
                markSingleBulls(bulls);
            }
    }


    private static void bullsEqualsCows(List<Shoot> shoots, Brush[][] bulls) {
        for (Shoot shoot : shoots) {
            if (shoot.cow == shoot.bull)
                for (int i = 0; i < 4; i++)
                    for (int j = 0; j < 4; j++)
                        if (i != j) bulls[shoot.digit(i)][j] = unmatched;
        }
    }

    private static void markPlayed(List<Shoot> shoots, Brush[][] bulls) {
        for (Shoot shoot : shoots) {
            int undefinedInGuess = 0;
            for (int j = 0; j < 4; j++)
                if (digitStatus(bulls[shoot.digit(j)]) != unmatched)
                    undefinedInGuess++;
            if (shoot.cow == undefinedInGuess) {
                for (int j = 0; j < 4; j++) {
                    for (int i = 0; i < 4; i++)
                        if (bulls[shoot.digit(j)][i] == undefined) bulls[shoot.digit(j)][i] = matchCow;
                }
            }
        }
    }

    private static void hideNonPlayed(List<Shoot> shoots, Brush[][] bulls) {
        for (Shoot shoot : shoots) {
            int cowsInGuess = 0;
            int bullsInGuess = 0;
            for (int j = 0; j < 4; j++) {
                int digit = shoot.digit(j);
                Brush digitStatus = digitStatus(bulls[digit]);
                if (digitStatus == matchCow || digitStatus == matchBull)
                    cowsInGuess++;
                if (bulls[digit][j] == matchBull)
                    bullsInGuess++;
            }
            for (int j = 0; j < 4; j++) {
                if (shoot.cow == cowsInGuess)
                    if (digitStatus(bulls[shoot.digit(j)]) == undefined)
                        for (int k = 0; k < 4; k++)
                            bulls[shoot.digit(j)][k] = unmatched;

                if (shoot.bull == bullsInGuess)
                    if (bulls[shoot.digit(j)][j] != matchBull)
                        bulls[shoot.digit(j)][j] = unmatched;
            }
        }
    }

    private static void markSingleBulls(Brush[][] bulls) {
        for (int digit = 0; digit < 10; digit++) {
            int cowCount = 0;
            for (Brush pos : bulls[digit])
                if (pos == matchCow) cowCount++;
            if (cowCount == 1)
                for (int j = 0; j < 4; j++)
                    if (bulls[digit][j] != unmatched) {
                        for (int m = 0; m < 4; m++) bulls[digit][m] = unmatched;
                        for (int m = 0; m < 10; m++) bulls[m][j] = unmatched;
                        bulls[digit][j] = matchBull;
                    }
        }
        for (int pos = 0; pos < 4; pos++) {
            int variants = 0, dig = -1;
            for (int digit = 0; digit < 10; digit++) {
                if (bulls[digit][pos] != unmatched) {
                    variants++;
                    dig = digit;
                }
            }
            if (variants == 1) {
                for (int m = 0; m < 4; m++) bulls[dig][m] = unmatched;
                for (int m = 0; m < 10; m++) bulls[m][pos] = unmatched;
                bulls[dig][pos] = matchBull;
            }

        }
    }
}