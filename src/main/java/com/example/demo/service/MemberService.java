package com.example.demo.service;

import com.example.demo.exception.MemberAlreadyExistsException;
import com.example.demo.exception.MemberNotFoundException;
import com.example.demo.model.Member;
import com.example.demo.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    // Add a new member
    public Member addMember(Member member) {
        if (memberRepository.findByUsername(member.getUsername()) != null) {
            throw new MemberAlreadyExistsException("Member already exists: " + member.getUsername());
        }
        memberRepository.save(member);
        return member;
    }

    // Get a member by username
    public Member getMemberByUsername(String username) {
        Member member = memberRepository.findByUsername(username);
        if (member == null) {
            throw new MemberNotFoundException("Member not found: " + username);
        }
        return member;
    }

    // Get all members
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    // Update a member's information
    public Member updateMember(String username, Member updatedMember) {
        Member member = getMemberByUsername(username);
        member.setNickname(updatedMember.getNickname());
        memberRepository.save(member);
        return member;
    }

    // Delete a member by username
    public void deleteMember(String username) {
        Member member = getMemberByUsername(username);
        memberRepository.deleteByUsername(member.getUsername());
    }
}