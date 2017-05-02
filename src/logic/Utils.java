package logic;

import java.util.LinkedList;

import static logic.Brush.*;
import static logic.Player.players;

class Utils {
    static final LinkedList<String> ALL_VARIANTS = new LinkedList<>();
    static final String SEND_REQUEST = "onClick = \"sendPost(this.id, containerTable.id)\"";

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
            case "unmatchedCow":
                if (player.brush == Brush.valueOf(action)) player.brush = Brush.shoot;
                else player.brush = Brush.valueOf(action);
                return ActionTableAsHTML(player.brush);
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

    static String bTable(Brush[][] bulls, boolean filter, boolean saved) {
        String[][] bTable = new String[1][2];
        bTable[0][0] = tableWrap(bullWrap(bulls), "bullTable", null);
        bTable[0][1] = buttonWrap("Save", null, "save", SEND_REQUEST);
        if (saved) bTable[0][1] += buttonWrap("Load", null, "load", SEND_REQUEST);
        bTable[0][1] += filterWrap(filter);
        return tableWrap(bTable, "panel", null) + buttonWrap(" ? ", "select", "shot", "onClick = shot()");
    }

    static String ActionTableAsHTML(Brush brush) {
        String result[][] = new String[1][4];
        result[0][0] = checkBoxWrap("matchCow", brush == matchCow);
        result[0][1] = checkBoxWrap("matchBull", brush == matchBull);
        result[0][2] = checkBoxWrap("unmatched", brush == unmatched);
        result[0][3] = buttonWrap("Reset", null, "reset", SEND_REQUEST);

        return tableWrap(result, "actionTable", null);
    }

    private static String checkBoxWrap(String arg, boolean checked) {
        String SEND_REQUEST_ACTION = "onClick = \"sendPost(this.id, actionTable.id)\"";
        return "<input id=" + arg + " name=action class=" + arg + " type=checkbox" + (checked ? " checked " : " ")
                + SEND_REQUEST_ACTION + "><label id=" + arg
                + "Label for=" + arg + " data-on=\"\"></label>";
    }

    static String buttonWrap(String arg, String clazz, String id, String params) {
        StringBuilder result = new StringBuilder();
        result.append("<button ");
        if (clazz != null) result.append(" class=").append(clazz);
        if (id != null) result.append(" id=").append(id);
        if (params != null) result.append(" ").append(params);
        result.append(">").append(arg).append("</button>");
        return result.toString();
    }

    static String tableWrap(String args, String id, String caption) {
        return tableHTML("<tr><td>" + args + "</td></tr>", id, caption);
    }

    static String tableWrap(String args[][], String id, String caption) {
        StringBuilder lines = new StringBuilder();
        for (String[] arg : args) {
            lines.append("<tr>");
            for (String ar : arg)
                lines.append("<td>").append(ar).append("</td>");
            lines.append("</tr>");
        }
        return tableHTML(lines.toString(), id, caption);
    }

    private static String tableHTML(String arg, String id, String caption) {
        StringBuilder result = new StringBuilder();
        result.append("<table id=").append(id);
        if (caption != null) result.append("><caption>").append(caption).append("</caption");
        result.append(">").append(arg).append("</table>");
        return result.toString();
    }


    static String[][] bullWrap(Brush args[][]) {
        String[][] result = new String[args.length][4];
        for (int i = 0; i < args.length; i++)
            for (int j = 0; j < 4; j++) {
                String checked = (args[i][j].equals(matchBull)) ? " checked " : "";
                result[i][j] = "<input id=bull" + i + j + " name=bullDigit" + j + " class=" + args[i][j]
                        + " type=radio " + checked + SEND_REQUEST
                        + "><label for=bull" + i + j + " data-on=" + i + "></label>";
            }
        return result;
    }

    private static String filterWrap(boolean checked) {
        return "<input id=filter name=action class=filter type=checkbox" + (checked ? " checked " : " ")
                + SEND_REQUEST + "><label id=filterLabel for=filter data-on=Filter></label>";
    }

    //    static String tableWrapByColumns(String args[][], String id, String caption) {
//        StringBuilder lines = new StringBuilder();
//        for (int i = 0; i < args[0].length; i++) {
//            lines.append("<td>");
//            for (String[] arg : args) {
//                lines.append(arg[i]);
//            }
//            lines.append("</td>");
//        }
//        lines.insert(0, "<tr>").append("</tr>");
//        return tableHTML(lines.toString(), id, caption);
//    }
}
