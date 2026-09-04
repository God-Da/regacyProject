package kr.or.oti.b01.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReplyDTO {
	private Long rno;
	@NotNull
	private Long bno;
	@NotEmpty
	private String replyText;
	@NotEmpty
	private String replyer;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDateTime regDate;

	@JsonIgnore //사용하지 않는 건 ignore처리
	private LocalDateTime modDate;
}
