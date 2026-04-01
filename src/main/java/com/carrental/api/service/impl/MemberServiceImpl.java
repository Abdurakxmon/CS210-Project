package com.carrental.api.service.impl;

import com.carrental.api.dto.request.CreateMemberRequest;
import com.carrental.api.entity.Member;
import com.carrental.api.service.MemberService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MemberServiceImpl implements MemberService {

    @Override
    public Member createMember(CreateMemberRequest request) {
        throw new UnsupportedOperationException("Member creation is not implemented yet");
    }

    @Override
    public List<Member> getAllMembers() {
        throw new UnsupportedOperationException("Member lookup is not implemented yet");
    }

    @Override
    public Member getMemberById(Long id) {
        throw new UnsupportedOperationException("Member lookup is not implemented yet");
    }
}
