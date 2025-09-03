package com.amirul.tabernBackend.dtos;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategorySimpleDTO {
    private Long id;
    private String name;
    private String imageUrl;
    private Integer displayOrder;
}
