package symbolTable;

public class ListNode {
    String   symbol;
    ListNode next;

    public ListNode(String token, ListNode next) {
        this.symbol = token;
        this.next   = next;
    }
}
