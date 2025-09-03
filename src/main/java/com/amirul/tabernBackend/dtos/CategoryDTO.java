package com.amirul.tabernBackend.dtos;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Integer displayOrder;
    private Boolean active;
    private Long parentCategoryId;
    private String parentCategoryName;
    private List<CategoryDTO> subCategories;
    private Integer productCount;
}
