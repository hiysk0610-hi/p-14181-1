package com.back.domain.member.member;


import com.back.domain.member.member.service.MemberService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.back.domain.member.member.dto.MemberDto;
import com.back.domain.member.member.entity.Member;
import com.back.global.exception.ServiceException;
import com.back.global.rsData.RsData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@Tag(name = "ApiV1MemberController", description = "API 회원 컨트롤러")
public class ApiV1MemberController {
    private final MemberService memberService;

    record MemberJoinReqBody(
            @NotBlank
            @Size(min = 2, max = 30)
            String username,
            @NotBlank
            @Size(min = 2, max = 30)
            String password,
            @NotBlank
            @Size(min = 2, max = 30)
            String nickname
    ) {
    }
    @PostMapping
    public RsData<MemberDto> join(
            @Valid @RequestBody MemberJoinReqBody reqBody
    ) {
             Member member = memberService.join(
                reqBody.username(),
                reqBody.password(),
                reqBody.nickname()
        );

        return new RsData<>(
                "201-1",
                "%s님 환영합니다. 회원가입이 완료되었습니다.".formatted(member.getName()),
                new MemberDto(member)
        );
    }
    public record MemberLoginReqBody(
            @NotBlank
            @Size(min = 2, max = 30)
            String username,
            @NotBlank
            @Size(min = 2, max = 30)
            String password
    ) {
    }
    public record MemberLoginResBody(
            MemberDto item,
            String apiKey
    ) {
    }
    @PostMapping("/login")
    public RsData<MemberLoginResBody> login(
            @Valid @RequestBody MemberLoginReqBody reqBody
    ) {
        Member member = memberService.findByUsername(reqBody.username())
                .orElseThrow(() -> new ServiceException("401-1", "존재하지 않는 아이디입니다."));

        if (!member.getPassword().equals(reqBody.password()))
            throw new ServiceException("401-2", "비밀번호가 일치하지 않습니다.");

        return new RsData<>(
                "200-1",
                "%s님 환영합니다.".formatted(member.getName()),
                new MemberLoginResBody(
                        new MemberDto(member),
                        member.getApiKey()
                )
        );
    }
}