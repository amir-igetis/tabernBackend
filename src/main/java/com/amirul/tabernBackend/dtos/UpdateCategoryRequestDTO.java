package com.amirul.tabernBackend.dtos;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCategoryRequestDTO {
    private String name;
    private String description;
    private String imageUrl;
    private Integer displayOrder;
    private Boolean active;
    private Long parentCategoryId;
}