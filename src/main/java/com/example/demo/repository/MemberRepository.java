package com.example.demo.repository;


import com.example.demo.model.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    Optional<Member> findByUsername(String username);
    List<Member> findAll();
    void save(Member member);
    void deleteByUsername(String username);
}