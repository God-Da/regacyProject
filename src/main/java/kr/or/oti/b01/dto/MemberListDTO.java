package kr.or.oti.b01.dto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberListDTO {

    private String mid;
    private String mpw;
    private String email;
    private boolean del;
    private boolean social;

    private LocalDateTime regDate;
    private LocalDateTime modDate;

    // domain.MemberRole 대신 단순 String Set 사용 ("USER", "ADMIN")
    @Builder.Default
    private Set<String> roleSet = new HashSet<>();
}