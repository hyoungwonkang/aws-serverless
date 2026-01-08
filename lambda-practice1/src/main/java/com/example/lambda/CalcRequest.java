package com.example.lambda;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CalcRequest {
    private String num1;
    private String num2;
    private String operation;
}
