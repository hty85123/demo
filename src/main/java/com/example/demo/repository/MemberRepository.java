package com.example.demo.repository;


import com.example.demo.exception.MemberAlreadyExistsException;
import com.example.demo.exception.MemberNotFoundException;
import com.example.demo.model.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    Member findByUsername(String username);
    List<Member> findAll();
    void insert(Member member) throws MemberAlreadyExistsException;
    void update(Member member) throws MemberNotFoundException;
    void deleteByUsername(String username) throws MemberNotFoundException;
}