package com.esports.bigdata.common.page;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分页结果")
public class PageResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long total;
    private Long pageNum;
    private Long pageSize;
    private List<T> list;

    public static <T> PageResult<T> empty() {
        return new PageResult<>(0L, 1L, 10L, Collections.emptyList());
    }

    public static <E, T> PageResult<T> of(IPage<E> page, List<T> mapped) {
        return new PageResult<>(page.getTotal(), page.getCurrent(), page.getSize(), mapped);
    }
}
