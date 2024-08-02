package com.example.demo.repository;

import com.example.demo.exception.MemberAlreadyExistsException;
import com.example.demo.model.Member;

import java.util.List;

public interface MemberRepository {
    Member findByUsername(String username);
    List<Member> findAll();
    void saveMember(Member member) throws MemberAlreadyExistsException;
    void update(Member member);
    void deleteByUsername(String username);
}