package com.example.demo.repository;

import com.example.demo.exception.MemberAlreadyExistsException;
import com.example.demo.model.Member;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Primary
public class MapMemberRepository implements MemberRepository {
    private final Map<String, Member> memberMap = new HashMap<>();

    @Override
    public Member findByUsername(String username) {
        return memberMap.get(username);
    }

    @Override
    public List<Member> findAll() {
        return new ArrayList<>(memberMap.values());
    }

    @Override
    public void saveMember(Member member) throws MemberAlreadyExistsException {
        if (memberMap.containsKey(member.getUsername())) {
            throw new MemberAlreadyExistsException("Member already exists: " + member.getUsername());
        }
        memberMap.put(member.getUsername(), member);
    }

    @Override
    public void update(Member member) {
        memberMap.put(member.getUsername(), member);
    }

    @Override
    public void deleteByUsername(String username) {
        memberMap.remove(username);
    }
}