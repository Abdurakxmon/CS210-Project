package com.carrental.api.service;

import com.carrental.api.dto.request.CreateMemberRequest;
import com.carrental.api.entity.Member;
import java.util.List;

public interface MemberService {

    Member createMember(CreateMemberRequest request);

    List<Member> getAllMembers();

    Member getMemberById(Long id);
}
