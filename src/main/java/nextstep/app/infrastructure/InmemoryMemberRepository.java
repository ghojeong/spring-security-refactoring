package nextstep.app.infrastructure;

import nextstep.app.domain.Member;
import nextstep.app.domain.MemberRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class InmemoryMemberRepository implements MemberRepository {
    private static final Map<String, Member> members = new HashMap<>();

    @Override
    public Optional<Member> findByEmail(String email) {
        return Optional.ofNullable(members.get(email));
    }

    @Override
    public List<Member> findAll() {
        return members.values().stream().collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Member save(Member member) {
        members.put(member.getEmail(), member);
        return member;
    }

    @Override
    public void clear() {
        members.clear();
    }
}
