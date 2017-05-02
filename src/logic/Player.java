package logic;

import java.util.*;

import static logic.Brush.*;
import static logic.Utils.*;

class Player {
    static final Map<String, Player> players = new HashMap<>();
    private final String nameAsHTML;
    private final String name;
    private final int[] number = new int[4];
    long lastActivity;
    Brush brush;
    Player enemy;
    boolean filter;
    private boolean saved;
    private List<Shoot> shoots;
    private Brush[][] bulls;
    private boolean guessed;
    private Brush[][] bulls_backup;
    private List<String> possibleVariants;


    Player(String name) {
        this.name = name;
        lastActivity = System.currentTimeMillis();
        nameAsHTML = "<input id=login value=" + name + ">";
    }

    void setEnemy(Player enemy) {
        this.enemy = enemy;
        shoots = new LinkedList<>();
        bulls = new Brush[10][4];
        bulls_backup = new Brush[10][4];
        reset();
        possibleVariants = new LinkedList<>(ALL_VARIANTS);
        guessed = false;
    }

    void setNumber(String number) {
        for (int i = 0; i < 4; i++) this.number[i] = Character.getNumericValue(number.charAt(i));
        brush = shoot;
        saved = false;
        reset();
    }

    void saveBulls() {
        for (int i = 0; i < 10; i++)
            System.arraycopy(bulls[i], 0, bulls_backup[i], 0, 4);
        saved = true;
    }

    void loadBulls() {
        for (int i = 0; i < 10; i++)
            System.arraycopy(bulls_backup[i], 0, bulls[i], 0, 4);
    }

    String thinkNumberAsHTML() {
        String[][] temp = new String[3][1];
        temp[0][0] = tableWrap(bullWrap(bulls), "thinkNumber", null);
        temp[1][0] = buttonWrap("Play", "select", "play", "onClick = play(this)");
        temp[2][0] = nameAsHTML;
        return tableWrap(temp, "thinkNumber", name + " vs " + enemy.name);
    }

    String playersAsHTML() {
        String result = nameAsHTML;
        for (String player : players.keySet())
            result += buttonWrap(player, "select", player, " onClick = chooseEnemy(this)") + "<br/>";
        return tableWrap(result, "players", name);
    }

    private String battleResult(int i1, int i2) {
        return i1 + ":" + i2 + (i1 > i2 ? "\n You lost :(" : i1 < i2 ? "\n You win :)" : " Withdraw!");
    }

    String getPlayerFieldHTML() {
        String text = "", action = "";
        if (filter) Filter.filter(this.shoots, this.bulls);

        if (enemy != null) {
            if (enemy.guessed) {
                text = battleResult(shoots.size(), enemy.shoots.size());
                if (this == enemy) text = shoots.size() + " shoots";
                action = " onClick = registerPlayer()";
            } else {
                text = " waiting for " + enemy.name + "...";
                action = SEND_REQUEST;
            }
        }

        String container[][] = {{""}, {""}, {""}, {""}, {""}};
        if (!guessed) {
            container[0][0] = ActionTableAsHTML(brush);
            container[2][0] = bTable(bulls, filter, saved);
        } else if (enemy != null) container[2][0] = buttonWrap(text, "select", enemy.name, action);
        container[1][0] = shoots2HTML(false);
        if (this != enemy) container[3][0] = enemy.shoots2HTML(true);
        container[4][0] = nameAsHTML;
        return tableWrap(container, "containerTable", null);
    }

    String generateNumber() {
        if (!filter) return ALL_VARIANTS.get((int) (Math.random() * ALL_VARIANTS.size()));
        else return possibleVariants.get((int) (Math.random() * possibleVariants.size()));
    }

    void addShoot(String guess) {
        shoots.add(new Shoot(guess, enemy.number));
        if (shoots.get(shoots.size() - 1).bull == 4) guessed = true;
        filterPossibleVariants();
    }

    private void filterPossibleVariants() {
        Iterator<String> iter = possibleVariants.iterator();
        while (iter.hasNext()) {
            String s = iter.next();
            for (Shoot shoot : shoots)
                if (!shoot.same(s)) iter.remove();
        }
    }

    String shoots2HTML(boolean isEnemy) {
        String result = "";
        for (Shoot shoot : shoots) result += shoot.html(bulls, isEnemy);
        return tableWrap(result, isEnemy ? "enemyTable" : "guessTable", name + " [" + possibleVariants.size() + "]");
    }

    void reset() {
        brush = shoot;
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 4; j++)
                bulls[i][j] = undefined;
    }

    void setBull(int digit, int pos) {
        if (brush == matchCow)
            for (int i = 0; i < 4; i++)
                if (bulls[digit][i] == undefined) bulls[digit][i] = matchCow;
        if (brush == matchBull) {
            for (int i = 0; i < 4; i++) bulls[digit][i] = unmatched;
            for (int i = 0; i < 10; i++) bulls[i][pos] = unmatched;
            bulls[digit][pos] = brush;
        }
        if (brush == unmatched) bulls[digit][pos] = brush;
    }

    void setCow(int digit, int pos) {
        if (brush == matchBull || brush == matchCow) setBull(digit, pos);
        if (brush == unmatched) {
            for (int i = 0; i < 4; i++)
                if (bulls[digit][i] == undefined) bulls[digit][i] = unmatched;
            bulls[digit][pos] = brush;
        }
        if (brush == unmatchedCow) bulls[digit][pos] = unmatched;
        if (brush == matchCow && bulls[digit][pos] == undefined) bulls[digit][pos] = brush;
    }
}