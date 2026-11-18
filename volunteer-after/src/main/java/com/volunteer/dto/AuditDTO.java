package com.volunteer.dto;

import lombok.Data;

@Data
public class AuditDTO {
    /** approved 通过 / rejected 驳回 */
    private String status;
}
