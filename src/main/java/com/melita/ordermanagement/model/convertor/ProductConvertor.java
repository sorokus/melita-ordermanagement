package com.melita.ordermanagement.model.convertor;
/*
 * @author sorokus.dev@gmail.com
 */

import com.melita.ordermanagement.model.dto.PackageDto;
import com.melita.ordermanagement.model.dto.ProductDto;
import com.melita.ordermanagement.model.entity.Package;
import com.melita.ordermanagement.model.entity.Product;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ProductConvertor {
    private final ModelMapper modelMapper;

    public ProductConvertor() {
        this.modelMapper = new ModelMapper();
        modelMapper.typeMap(Product.class, ProductDto.class);
        modelMapper.typeMap(Package.class, PackageDto.class);
    }

    public ProductDto convertToDto(Product entity) {
        return modelMapper.map(entity, ProductDto.class);
    }
}
