package com.ansh.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Product {

    @JsonProperty("field_1")
    private String field1;

    @JsonProperty("field_2")
    private String field2;

    @JsonProperty("field_3")
    private String field3;

    @JsonProperty("field_4")
    private long field4;
}
