import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MemberList {
    private List<Member> members;
    private static final String DATA_FILE = "members.dat";

    public MemberList() {
        members = new ArrayList<>();
        loadMembers();
    }

    public void addMember(Member member) {
        members.add(member);
        saveMembers();
    }

    public Member findMember(String name, String password) {
        for(Member m : members) {
            if(m.login(name, password)) {
                return m;
            }
        }
        return null;
    }

    private void saveMembers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(members);
            System.out.println("Members saved successfully!");
        } catch (IOException e) {
            System.err.println("Error saving members: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadMembers() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
                members = (List<Member>) ois.readObject();
                System.out.println("Loaded " + members.size() + " member(s) from file");
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading members: " + e.getMessage());
                members = new ArrayList<>();
            }
        } else {
            System.out.println("No existing member data found. Starting fresh.");
        }
    }

    public List<Member> getAllMembers() {
        return new ArrayList<>(members);
    }
}