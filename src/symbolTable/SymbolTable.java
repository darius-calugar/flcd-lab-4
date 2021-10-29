package symbolTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SymbolTable {
    static public final int size = 10;

    private final ListNode[] listHeads;

    public SymbolTable() {
        listHeads = new ListNode[size];
    }

    public int put(String token) {
        var h    = hash(token);
        var node = listHeads[h];

        if (node == null) {
            listHeads[h] = new ListNode(token, null);
            return h;
        }

        var index = 1;
        while (node.next != null) {
            node = node.next;
            index += 1;
        }

        node.next = new ListNode(token, null);
        return index * size + h;
    }

    public String get(int position) {
        var h     = position % size;
        var index = position / size;
        var node  = listHeads[h];
        while (node != null) {
            if (index-- == 0)
                return node.symbol;
            node = node.next;
        }
        return null;
    }

    public int lookup(String token) {
        var h     = hash(token);
        var node  = listHeads[h];
        var index = 0;

        while (node != null) {
            if (Objects.equals(node.symbol, token))
                return index * size + h;
            index++;
            node = node.next;
        }
        return -1;
    }

    public List<Integer> positions() {
        var positions = new ArrayList<Integer>();
        for (var h = 0; h < listHeads.length; h++) {
            var index = 0;
            var node  = listHeads[h];
            while (node != null) {
                node = node.next;
                positions.add((index++) * size + h);
            }
        }
        return positions;
    }

    public List<String> symbols() {
        var tokens = new ArrayList<String>();
        for (ListNode listHead : listHeads) {
            var node = listHead;
            while (node != null) {
                tokens.add(node.symbol);
                node = node.next;
            }
        }
        return tokens;
    }

    private int hash(String token) {
        var h = 0;
        for (int i = 0; i < token.length(); i++) {
            h = h * 17 + token.charAt(i);
        }
        return (h > 0 ? h : -h) % size;
    }
}

