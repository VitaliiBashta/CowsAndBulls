package logic;

import java.util.LinkedList;

import static logic.Brush.matchBull;
import static logic.Brush.matchCow;
import static logic.Brush.undefined;
import static logic.Player.players;

class Utils {
    static final LinkedList<String> ALL_VARIANTS = new LinkedList<>();

    static {
        for (int i = 123; i <= 9876; i++) {
            String number;
            if (i < 1000) number = "0" + i;
            else number = "" + i;
            if (isGoodNumber(number)) ALL_VARIANTS.add(number);
        }
    }

    static Brush digitStatus(Brush[] digits) {
        Brush result = digits[0];
        for (int i = 1; i < 4; i++) {
            if (digits[i] == undefined) result = undefined;
            if (digits[i] == matchCow) result = matchCow;
            if (digits[i] == matchBull) result = matchBull;
        }
        return result;
    }
    private static boolean isGoodNumber(String strShoot) {
        if (strShoot == null || strShoot.isEmpty()) return false;
        if (strShoot.length() != 4) return false;
        for (int i = 0; i < 3; i++)
            for (int j = i + 1; j < 4; j++)
                if (strShoot.charAt(i) == strShoot.charAt(j)) return false;
        return true;
    }

    static String getResponse(Player player, String action, String[] params) {
        switch (action) {
            case "register":
                player.setEnemy(null);
                return player.playersAsHTML();
            case "refresh":
                if (player.enemy != null) return player.thinkNumberAsHTML();
                return player.playersAsHTML();
            case "chooseEnemy":
                Player enemy = players.get(params[2]);
                player.setEnemy(enemy);
                for (Player play : players.values())
                    if (enemy == play) play.setEnemy(player);
                if (player == enemy) {
                    player.setNumber(player.generateNumber());
                    return player.getPlayerFieldHTML();
                } else
                    return player.thinkNumberAsHTML();
            case "checkEnemy":
                if (player !=player.enemy)return player.enemy.shoots2HTML(true);
                else return "";
            case "reset":
                player.reset();
                return player.getPlayerFieldHTML();
            case "save":
                player.saveBulls();
                return player.getPlayerFieldHTML();
            case "load":
                player.loadBulls();
                return player.getPlayerFieldHTML();
            case "matchCow":
            case "matchBull":
            case "unmatched":
                if (player.brush == Brush.valueOf(action)) player.brush = Brush.shoot;
                else player.brush = Brush.valueOf(action);
                return HTMLUtils.ActionTableAsHTML(player.brush);
            case "filter":
                player.filter = !player.filter;
                return player.getPlayerFieldHTML();
        }
        if (action.startsWith("bull") || action.startsWith("gues")) {
            if (player.brush == Brush.shoot) return "";
            int digit = Integer.parseInt(action.substring(4, 5));
            int pos = Integer.parseInt(action.substring(5, 6));
            if (action.startsWith("gues")) player.setCow(digit, pos);
            else player.setBull(digit, pos);
            return player.getPlayerFieldHTML();
        }
        if (action.startsWith("play") || action.startsWith("shot")) {
            String number = action.substring(4);
            if (number.equals("")) number = player.generateNumber();
            if (!isGoodNumber(number)) return "";
            if (action.startsWith("play")) player.setNumber(number);
            else player.addShoot(number);
            return player.getPlayerFieldHTML();
        }
        return player.getPlayerFieldHTML();
    }
}
