import java.util.ArrayList;
import java.util.List;

public class MemberList {
    private List<Member> members;

    public MemberList() {
        members = new ArrayList<>();
    }

    public void addMember(Member member) {
        members.add(member);
    }

    public Member findMember(String name, String password) {
        for(Member m : members) {
            if(m.login(name, password)) {
                return m;
            }
        }
        return null;
    }
}