package logic;

import static logic.Brush.*;

class HTMLUtils {
    static final String SEND_REQUEST = "onClick = \"sendRequest(this.id, containerTable.id)\"";

    static String buttonWrap(String arg, String clazz, String id, String params) {
        StringBuilder result = new StringBuilder();
        result.append("<button ");
        if (clazz != null) result.append(" class=").append(clazz);
        if (id != null) result.append(" id=").append(id);
        if (params != null) result.append(" ").append(params);
        result.append(">").append(arg).append("</button>");
        return result.toString();
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

    static String tableWrapByColumns(String args[][], String id, String caption) {
        StringBuilder lines = new StringBuilder();
        for (int i = 0; i < args[0].length; i++) {
            lines.append("<td>");
            for (String[] arg : args) {
                lines.append(arg[i]);
            }
            lines.append("</td>");
        }
        lines.insert(0, "<tr>").append("</tr>");
        return tableHTML(lines.toString(), id, caption);
    }

    static String tableWrap(String args, String id, String caption) {
        return tableHTML("<tr><td>" + args + "</td></tr>", id, caption);
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

    private static String checkBoxWrap(String arg, boolean checked) {
        String SEND_REQUEST_ACTION = "onClick = \"sendRequest(this.id, actionTable.id)\"";
        return "<input id=" + arg + " name=action class=" + arg + " type=checkbox" + (checked ? " checked " : " ")
                + SEND_REQUEST_ACTION +"><label id=" + arg
                + "Label for=" + arg + " data-on=\"\"></label>";
    }
    private static String filterWrap(boolean checked) {
        return "<input id=filter name=action class=filter type=checkbox" + (checked ? " checked " : " ")
                + SEND_REQUEST+"><label id=filterLabel for=filter data-on=Filter></label>";
    }

    static String ActionTableAsHTML(Brush brush) {
        String result[][] = new String[1][4];
        result[0][0] = checkBoxWrap("matchCow", brush == matchCow);
        result[0][1] = checkBoxWrap("matchBull", brush == matchBull);
        result[0][2] = checkBoxWrap("unmatched", brush == unmatched);
        result[0][3] = buttonWrap("Reset", null, "reset", SEND_REQUEST);

        return tableWrap(result, "actionTable", null);
    }

    private static String tableHTML(String arg, String id, String caption) {
        StringBuilder result = new StringBuilder();
        result.append("<table id=").append(id);
        if (caption != null) result.append("><caption>").append(caption).append("</caption");
        result.append(">").append(arg).append("</table>");
        return result.toString();
    }

    static String bTable(Brush[][] bulls, boolean filter, boolean saved) {
        String[][] bTable = new String[2][2];
        bTable[0][0] = tableWrap(bullWrap(bulls), "bullTable", null);
        bTable[0][1] = buttonWrap("Save", null, "save", SEND_REQUEST);
        if (saved) bTable[0][1] += buttonWrap("Load", null, "load", SEND_REQUEST);
        bTable[1][0] = buttonWrap(" ? ", "select", "shot", "onClick = shot()");
        bTable[1][1] = filterWrap(filter);
        return tableWrap(bTable, "panel", null);
    }
}
