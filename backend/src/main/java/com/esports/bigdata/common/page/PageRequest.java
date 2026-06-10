package com.esports.bigdata.common.page;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "分页请求")
public class PageRequest {

    @Schema(description = "当前页", defaultValue = "1")
    private Long pageNum = 1L;

    @Schema(description = "每页大小", defaultValue = "10")
    private Long pageSize = 10L;

    @Schema(description = "关键字")
    private String keyword;

    public Long getPageNum() {
        return pageNum == null || pageNum < 1 ? 1L : pageNum;
    }

    public Long getPageSize() {
        if (pageSize == null || pageSize < 1) return 10L;
        return pageSize > 200 ? 200L : pageSize;
    }
}
