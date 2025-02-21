package nextstep.app;

import nextstep.app.domain.Member;
import nextstep.app.domain.MemberRepository;

import java.util.Set;

public final class MemberFixture {
    private MemberFixture() {}

    public static final Member TEST_ADMIN_MEMBER = new Member("a@a.com", "password", "a", "", Set.of("ADMIN"));
    public static final Member TEST_USER_MEMBER = new Member("b@b.com", "password", "b", "", Set.of());

    public static void setUp(MemberRepository memberRepository) {
        memberRepository.clear();
        memberRepository.save(TEST_ADMIN_MEMBER);
        memberRepository.save(TEST_USER_MEMBER);
    }
}
