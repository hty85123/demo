package com.example.demo.repository;

import com.example.demo.exception.MemberAlreadyExistsException;
import com.example.demo.exception.MemberNotFoundException;
import com.example.demo.model.Member;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class MemberRepositoryImpl implements MemberRepository {
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
    public void insert(Member member) throws MemberAlreadyExistsException {
        if (memberMap.containsKey(member.getUsername())) {
            throw new MemberAlreadyExistsException("Member already exists: " + member.getUsername());
        }
        memberMap.put(member.getUsername(), member);
    }

    @Override
    public void update(Member member) throws MemberNotFoundException {
        if (!memberMap.containsKey(member.getUsername())) {
            throw new MemberNotFoundException("Member not found: " + member.getUsername());
        }
        memberMap.put(member.getUsername(), member);
    }

    @Override
    public void deleteByUsername(String username) throws MemberNotFoundException {
        if (!memberMap.containsKey(username)) {
            throw new MemberNotFoundException("Member not found: " + username);
        }
        memberMap.remove(username);
    }
}