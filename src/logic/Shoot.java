package logic;

import static logic.Brush.*;
import static logic.HTMLUtils.SEND_REQUEST;
import static logic.HTMLUtils.buttonWrap;

class Shoot {
    final int cow, bull;
    private final int[] guess = new int[4];

    Shoot(String guess, int[] number) {
        for (int i = 0; i < 4; i++)
            this.guess[i] = Character.getNumericValue(guess.charAt(i));
        int cow = 0, bull = 0;
        for (int i = 0; i < 4; i++) {
            if (this.guess[i] == number[i]) bull++;
            for (int j = 0; j < 4; j++)
                if (this.guess[i] == number[j]) cow++;
        }
        this.cow = cow;
        this.bull = bull;
    }

    int digit(int pos) {
        return guess[pos];
    }

    String html(final Brush[][] bulls, boolean isEnemy) {
        String result = "";
        boolean defined = true;
        int cow = 0, bull = 0;
        for (int j = 0; j < 4; j++) {
            int digit = digit(j);
            Brush brush = Utils.digitStatus(bulls[digit]);
            if (brush == matchBull && (bulls[digit][j] != matchBull)) brush = matchCow;
            if (brush == undefined) defined = false;
            if (brush == matchCow || brush == matchBull) cow++;
            if (brush == matchBull) bull++;
            if (this.bull == 4) brush = guessed;
            result += "<td>" + buttonWrap(digit + "", brush.toString(), "gues" + digit + j, isEnemy ? "" : SEND_REQUEST) + "</td>";
        }
        String resultStyle = "result";
        if (defined && (this.cow != cow || this.bull < bull)) resultStyle = "wrongResult";
        if (this.cow < cow || this.bull < bull) resultStyle = "wrongResult";
        if (this.bull == bull && this.cow == cow) resultStyle = "matchResult";
        if (this.bull == 4) resultStyle = "guessed";
        result += "<td>" + buttonWrap(this.cow + ":" + this.bull, resultStyle, null, null) + "</td>";

        return "<tr>" + result + "</tr>";
    }

    boolean same(String test) {
        Shoot shoot = new Shoot(test, this.guess);
        return shoot.cow == this.cow && shoot.bull == this.bull;
    }
}