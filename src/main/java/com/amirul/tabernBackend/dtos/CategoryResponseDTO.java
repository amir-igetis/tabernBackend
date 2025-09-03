package com.amirul.tabernBackend.dtos;

import lombok.*;

import java.util.List;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Integer displayOrder;
    private Boolean active;
    private CategorySimpleDTO parentCategory;
    private List<CategorySimpleDTO> subCategories;
    private Integer productCount;
}
