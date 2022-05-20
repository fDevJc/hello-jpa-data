package hello.datajpa.dto;

import lombok.Data;

@Data
public class MemberDto {
    public Long id;
    public String username;
    public String teamName;

    public MemberDto(Long id, String username, String teamName) {
        this.id = id;
        this.username = username;
        this.teamName = teamName;
    }
}
