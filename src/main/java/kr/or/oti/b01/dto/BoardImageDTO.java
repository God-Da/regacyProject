package kr.or.oti.b01.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardImageDTO {
    private String uuid;
    private String filename;
    private int ord;
    
    // S3 이미지 URL
    private String imageUrl;
    
    public String getFullName() {
        return this.uuid + "_" + this.filename;
    }

    // 대소문자 매핑 호환용 (MyBatis fileName 대응)
    public void setFileName(String fileName) {
        this.filename = fileName;
    }

    public String getFileName() {
        return this.filename;
    }
}