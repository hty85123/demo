package com.example.demo.repository;

import com.example.demo.model.Member;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class MemberRepositoryImpl implements MemberRepository {
    private final Map<String, Member> memberMap = new HashMap<>();

    @Override
    public Optional<Member> findByUsername(String username) {
        return Optional.ofNullable(memberMap.get(username));
    }

    @Override
    public List<Member> findAll() {
        return new ArrayList<>(memberMap.values());
    }

    @Override
    public void save(Member member) {
        memberMap.put(member.getUsername(), member);
    }

    @Override
    public void deleteByUsername(String username) {
        memberMap.remove(username);
    }
}